package de.cleem.bm.tsdb.adapter.influxdb;

import de.cleem.bm.tsdb.adapter.TSDBAdapterIF;
import de.cleem.bm.tsdb.adapter.exception.TSDBAdapterException;
import de.cleem.bm.tsdb.common.http.HttpException;
import de.cleem.bm.tsdb.common.http.HttpHelper;
import de.cleem.bm.tsdb.common.lineprotocolformat.LineProtocolFormat;
import de.cleem.bm.tsdb.model.config.adapter.TSDBAdapterConfig;
import de.cleem.tub.tsdbb.commons.exception.TSDBBException;
import de.cleem.tub.tsdbb.commons.model.workload.Record;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Instant;
import java.util.HashMap;

@Slf4j
public class InfluxDbAdapter implements TSDBAdapterIF {

    private static final String HEADER_KEY_AUTH = "Authorization";
    private static final String HEADER_KEY_VALUE_TOKEN = "Token ";

    private static final String ORGS_ENDPOINT = "/api/v2/orgs";
    private static final String BUCKETS_ENDPOINT = "/api/v2/buckets";
    private static final String HEALTH_ENDPOINT = "/health";
    private static final String WRITE_ENDPOINT = "/api/v2/write?org=%s&bucket=%s&precision=%s";

    private static final String WRITE_PRECISION = "ms";
    private static final String MEASUREMENT_NAME = "influx_measurement";
    private static final String RUN_LABEL_KEY = "run";
    private static final String RUN_LABEL_VALUE = "influx";


    private InfluxDbAdapterConfig config;
    private HttpClient httpClient;
    private LineProtocolFormat lineProtocolFormat;

    private URI orgsUri;
    private URI bucketsUri;
    private URI healthUri;

    @Override
    public void setup(final TSDBAdapterConfig tsdbAdapterConfig) throws TSDBBException {

        if (!(tsdbAdapterConfig instanceof InfluxDbAdapterConfig)) {

            throw new TSDBAdapterException("Setup error - tsdbConfig is not instance of InfluxDbPlainAdapterConfig - " + getConnectionInfo());

        }

        config = (InfluxDbAdapterConfig) tsdbAdapterConfig;

        httpClient = HttpClient.newHttpClient();

        orgsUri = URI.create(config.getInfluxDbUrl() + ORGS_ENDPOINT);
        bucketsUri = URI.create(config.getInfluxDbUrl() + BUCKETS_ENDPOINT);
        healthUri = URI.create(config.getInfluxDbUrl() + HEALTH_ENDPOINT);

        healthCheck();

        lineProtocolFormat = LineProtocolFormat.builder()
                .measurementName(MEASUREMENT_NAME)
                .labelKey(RUN_LABEL_KEY)
                .labelValue(RUN_LABEL_VALUE)
                .build();


        if (config.getOrganisationId() == null) {

            final String orgId = lookupId("orgs", config.getOrganisation(), listOrganisations());
            config.setOrganisationId(orgId);

        }

    }

    @Override
    public void createStorage() throws TSDBBException {

        // CREATE BUCKET
        // curl --request POST \
        //	"http://localhost:8086/api/v2/buckets" \
        //	--header "Authorization: Token ${INFLUX_TOKEN}" \
        //  --header "Content-type: application/json" \
        //  --data '{
        //    "orgID": "'"${INFLUX_ORG_ID}"'",
        //    "name": "'"${INFLUX_BUCKET}"'"
        //  }'

        final HashMap<String, String> headerMap = getAuthHeaderMap();
        headerMap.put("Content-type", "application/json");

        final String createBucketRequestBody = buildCreateBucketJson(config.getOrganisationId(), config.getBucket());
        HttpHelper.executePost(httpClient, bucketsUri, createBucketRequestBody, headerMap, 201);

        final String listBucketsResponse = listBuckets();

        final String bucketId = lookupId("buckets", config.getBucket(), listBucketsResponse);

        config.setBucketId(bucketId);

    }

    @Override
    public void write(final Record record) throws TSDBBException {

        if (config.getBucketId() == null) {
            throw new TSDBAdapterException("Can not write to Storage - bucketId is NULL - " + getConnectionInfo());
        }

        log.info("Processing record: " + record.toString());

        final Instant instant = Instant.now();

        final String metricLine = lineProtocolFormat.getLine(record, instant);

        // WRITE
        // curl -v --request POST \
        //"http://localhost:8086/api/v2/write?org=${INFLUX_ORG}&bucket=${INFLUX_BUCKET}&precision=s" \
        //  --header "Authorization: Token ${INFLUX_TOKEN}" \
        //  --data-binary 'testtesttest1,sensor_id=TLM0201 temperature=73.97038159354763,humidity=35.23103248356096,co=0.48445310567793615 1673369771'

        final URI writeUri = URI.create(config.getInfluxDbUrl() + String.format(WRITE_ENDPOINT, config.getOrganisation(), config.getBucket(), WRITE_PRECISION));

        final HashMap<String, String> headers = getAuthHeaderMap();

        HttpHelper.executePost(httpClient, writeUri, metricLine, headers, 204);

        log.info("Wrote Line: " + metricLine + " to: " + getConnectionInfo());

    }

    @Override
    public void cleanup() throws TSDBBException {

        if (config.getBucketId() == null) {
            throw new TSDBAdapterException("Can not delete to Storage " + config.getBucket() + " - bucketId is NULL - " + getConnectionInfo());

        }

        // DELETE BUCKET
        // curl --request DELETE "http://localhost:8086/api/v2/buckets/${INFLUX_BUCKET_ID}" \
        //  --header "Authorization: Token ${INFLUX_TOKEN}" \
        //  --header 'Accept: application/json'


        final URI deleteBucketUri = URI.create(config.getInfluxDbUrl() + BUCKETS_ENDPOINT + "/" + config.getBucketId());

        final HashMap<String, String> headerMap = getAuthHeaderMap();
        headerMap.put("Content-type", "application/json");

        HttpHelper.executeDelete(httpClient, deleteBucketUri, null, headerMap, 204);

        log.info("Deleted bucket: " + config.getBucket() + " - " + getConnectionInfo());


    }

    @Override
    public void close() {

        httpClient = null;

    }

    ////

    private void healthCheck() throws HttpException {

        // HEALTH
        // curl "http://localhost:8086/health"
        HttpHelper.executeGet(httpClient, healthUri, null, null, 200);

    }

    private HashMap<String, String> getAuthHeaderMap() {

        final HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put(HEADER_KEY_AUTH, HEADER_KEY_VALUE_TOKEN + config.getToken());

        return headerMap;
    }

    private String buildCreateBucketJson(final String orgId, final String bucketName) {

        //  {
        //    "orgID": "'"${INFLUX_ORG_ID}"'",
        //    "name": "'"${INFLUX_BUCKET}"'"
        //  }

        final JSONObject createBucketJson = new JSONObject();
        createBucketJson.put("orgID", orgId);
        createBucketJson.put("name", bucketName);

        return createBucketJson.toString();

    }

    private String listBuckets() throws HttpException {

        // LIST BUCKETS
        // curl "http://localhost:8086/api/v2/buckets" \
        // --header "Authorization: Token ${INFLUX_TOKEN}"
        final HashMap<String, String> headerMap = getAuthHeaderMap();
        return HttpHelper.executeGet(httpClient, bucketsUri, null, headerMap, 200);

    }

    private String listOrganisations() throws HttpException {

        // LIST ORG
        // curl "http://localhost:8086/api/v2/orgs" \
        // --header "Authorization: Token ${INFLUX_TOKEN}"

        final HashMap<String, String> headers = getAuthHeaderMap();
        return HttpHelper.executeGet(httpClient, orgsUri, null, headers, 200);

    }

    private String lookupId(final String type, final String name, final String response) throws TSDBBException {

        final JSONObject orgList = new JSONObject(response);

        final JSONArray bucketList = orgList.getJSONArray(type);

        JSONObject currentBucket;
        for (int i = 0; i < bucketList.length(); i++) {

            currentBucket = (JSONObject) bucketList.get(i);

            if (currentBucket.getString("name").equals(name)) {
                return currentBucket.getString("id");
            }


        }

        throw new TSDBBException(type + " ID not found for: " + name);

    }

    private String getConnectionInfo() {

        final StringBuilder infoBuffer = new StringBuilder();

        if (config.getInfluxDbUrl() != null) {
            infoBuffer.append(config.getInfluxDbUrl());
        }
        if (config.getOrganisation() != null) {
            infoBuffer
                    .append(" Organisation: ")
                    .append(config.getOrganisation());
        }
        if (config.getOrganisationId() != null) {
            infoBuffer.append(" (")
                    .append(config.getOrganisationId())
                    .append(")");
        }
        if (config.getBucket() != null) {
            infoBuffer
                    .append(" Bucket: ")
                    .append(config.getBucket());
        }
        if (config.getBucketId() != null) {
            infoBuffer.append(" (")
                    .append(config.getBucketId())
                    .append(")");
        }


        return infoBuffer.toString();
    }

}
