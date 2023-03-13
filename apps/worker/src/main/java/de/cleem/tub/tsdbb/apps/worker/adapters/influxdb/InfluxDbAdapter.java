package de.cleem.tub.tsdbb.apps.worker.adapters.influxdb;

import de.cleem.tub.tsdbb.api.model.WorkerTsdbConnection;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterIF;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterException;
import de.cleem.tub.tsdbb.apps.worker.formats.LineProtocolFormat;
import de.cleem.tub.tsdbb.api.model.Record;

import de.cleem.tub.tsdbb.commons.http.HttpException;
import de.cleem.tub.tsdbb.commons.http.HttpHelper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Instant;
import java.util.HashMap;

@Slf4j
public class InfluxDbAdapter implements TSDBAdapterIF {

    private static final String PROPERTY_KEY_BUCKET_NAME="bucketName";
    private static final String PROPERTY_KEY_ORGANISATION_NAME="organisationName";
    private static final String ORGS_STRING = "orgs";
    private static final String BUCKETS_STRING = "buckets";
    private static final String ORGID_STRING = "orgID";
    private static final String NAME_STRING = "name";
    private static final String ID_STRING = "id";
    private static final String MEASUREMENT_SUFFIX_STRING = "_measurement";
    private static final String ORGS_ENDPOINT = "/api/v2/orgs";
    private static final String BUCKETS_ENDPOINT = "/api/v2/buckets";
    private static final String HEALTH_ENDPOINT = "/health";
    private static final String WRITE_ENDPOINT = "/api/v2/write?org=%s&bucket=%s&precision=%s";
    private static final String WRITE_PRECISION = "ms";
    private static final String RUN_LABEL_KEY = "run";

    private HttpClient httpClient;
    private LineProtocolFormat lineProtocolFormat;

    private URI baseUrl;
    private URI orgsUri;
    private URI bucketsUri;
    private URI healthUri;

    private String orgName;
    private String orgId;
    private String bucketName;
    private String bucketId;
    private String token;

    @Override
    public void setup(final WorkerTsdbConnection workerTsdbConnection) throws TSDBAdapterException {

        if (!(workerTsdbConnection.getTsdbType().equals(WorkerTsdbConnection.TsdbTypeEnum.INFLUX))) {

            throw new TSDBAdapterException("Setup error - workerTsdbConnection is of type "+ WorkerTsdbConnection.TsdbTypeEnum.INFLUX+" - " + getConnectionInfo());

        }

        httpClient = HttpClient.newHttpClient();
        
        final WorkerTsdbConnection.TsdbTypeEnum tsdbType = workerTsdbConnection.getTsdbType();

        baseUrl = workerTsdbConnection.getUrl();
        orgsUri = URI.create(baseUrl + ORGS_ENDPOINT);
        bucketsUri = URI.create(baseUrl + BUCKETS_ENDPOINT);
        healthUri = URI.create(baseUrl + HEALTH_ENDPOINT);

        token = workerTsdbConnection.getToken();


        healthCheck();


        lineProtocolFormat = LineProtocolFormat.builder()
                .measurementName(tsdbType.getValue()+ MEASUREMENT_SUFFIX_STRING)
                .labelKey(RUN_LABEL_KEY)
                .labelValue(tsdbType.getValue())
                .build();

        if(!workerTsdbConnection.getConnectionProperties().containsKey(PROPERTY_KEY_BUCKET_NAME)){
            throw new TSDBAdapterException("Connection Property "+PROPERTY_KEY_BUCKET_NAME+" not set");
        }

        bucketName = workerTsdbConnection.getConnectionProperties().get(PROPERTY_KEY_BUCKET_NAME);

        if(!workerTsdbConnection.getConnectionProperties().containsKey(PROPERTY_KEY_ORGANISATION_NAME)){
            throw new TSDBAdapterException("Connection Property "+PROPERTY_KEY_ORGANISATION_NAME+" not set");
        }

        orgName = workerTsdbConnection.getConnectionProperties().get(PROPERTY_KEY_ORGANISATION_NAME);

        orgId = lookupId(ORGS_STRING, orgName,listOrganisations());

    }
    @Override
    public void createStorage() throws TSDBAdapterException {

        // CREATE BUCKET
        // curl --request POST \
        //	"http://localhost:8086/api/v2/buckets" \
        //	--header "Authorization: Token ${INFLUX_TOKEN}" \
        //  --header "Content-type: application/json" \
        //  --data '{
        //    "orgID": "'"${INFLUX_ORG_ID}"'",
        //    "name": "'"${INFLUX_BUCKET}"'"
        //  }'


        final HashMap<String, String> headerMap = HttpHelper.getTokenAuthHeaderMap(token);
        headerMap.put(HttpHelper.HEADER_KEY_CONTENT_TYPE, HttpHelper.HEADER_KEY_CONTENT_TYPE_VALUE_JSON);

        final String createBucketRequestBody = buildCreateBucketJson(orgId, bucketName);


        try {
            HttpHelper.executePost(httpClient, bucketsUri, createBucketRequestBody, headerMap, 201);
        } catch (HttpException e) {
            throw new TSDBAdapterException(e);
        }

        bucketId = lookupId(BUCKETS_STRING, bucketName, listBuckets());


    }
    @Override
    public void write(final Record record) throws TSDBAdapterException {

        if (bucketId == null) {
            throw new TSDBAdapterException("Can not write to Storage - bucketId is NULL - " + getConnectionInfo());
        }

        final String metricLine = lineProtocolFormat.getLine(record,  Instant.now());

        // WRITE
        // curl -v --request POST \
        //"http://localhost:8086/api/v2/write?org=${INFLUX_ORG}&bucket=${INFLUX_BUCKET}&precision=s" \
        //  --header "Authorization: Token ${INFLUX_TOKEN}" \
        //  --data-binary 'testtesttest1,sensor_id=TLM0201 temperature=73.97038159354763,humidity=35.23103248356096,co=0.48445310567793615 1673369771'

        final URI writeUri = URI.create(baseUrl + String.format(WRITE_ENDPOINT, orgName, bucketName, WRITE_PRECISION));

        final HashMap<String, String> headers = HttpHelper.getTokenAuthHeaderMap(token);

        try {
            HttpHelper.executePost(httpClient, writeUri, metricLine, headers, 204);
            log.debug("Wrote Line: " + metricLine + " to: " + getConnectionInfo());
        } catch (HttpException e) {
            throw new TSDBAdapterException(e);
        }



    }
    @Override
    public void cleanup() throws TSDBAdapterException {

        if (bucketId == null) {

            throw new TSDBAdapterException("Can not delete to Storage " + bucketName + " - bucketId is NULL - " + getConnectionInfo());

        }

        // DELETE BUCKET
        // curl --request DELETE "http://localhost:8086/api/v2/buckets/${INFLUX_BUCKET_ID}" \
        //  --header "Authorization: Token ${INFLUX_TOKEN}" \
        //  --header 'Accept: application/json'


        final URI deleteBucketUri = URI.create(baseUrl + BUCKETS_ENDPOINT + "/" + bucketId);

        final HashMap<String, String> headerMap = HttpHelper.getTokenAuthHeaderMap(token);
        headerMap.put(HttpHelper.HEADER_KEY_CONTENT_TYPE, HttpHelper.HEADER_KEY_CONTENT_TYPE_VALUE_JSON);

        try {
            HttpHelper.executeDelete(httpClient, deleteBucketUri, null, headerMap, 204);
            log.info("Deleted bucket: " + bucketName + " - " + getConnectionInfo());
        } catch (HttpException e) {
            throw new TSDBAdapterException(e);
        }




    }
    @Override
    public void close() {

        httpClient = null;

    }
    @Override
    public String getConnectionInfo() {

        final StringBuilder infoBuffer = new StringBuilder();

        if (baseUrl != null) {
            infoBuffer.append(baseUrl);
        }
        if (orgName != null) {
            infoBuffer
                    .append(" Organisation: ")
                    .append(orgName);
        }
        if (orgId != null) {
            infoBuffer.append(" (")
                    .append(orgId)
                    .append(")");
        }
        if (bucketName != null) {
            infoBuffer
                    .append(" Bucket: ")
                    .append(bucketName);
        }
        if (bucketId != null) {
            infoBuffer.append(" (")
                    .append(bucketId)
                    .append(")");
        }


        return infoBuffer.toString();
    }

    ////
    private void healthCheck() throws TSDBAdapterException {

        // HEALTH
        // curl "http://localhost:8086/health"
        try {
            HttpHelper.executeGet(httpClient, healthUri, null, null, 200);
        } catch (HttpException e) {
            throw new TSDBAdapterException(e);
        }

    }
    private String buildCreateBucketJson(final String orgId, final String bucketName) {

        //  {
        //    "orgID": "'"${INFLUX_ORG_ID}"'",
        //    "name": "'"${INFLUX_BUCKET}"'"
        //  }

        final JSONObject createBucketJson = new JSONObject();
        createBucketJson.put(ORGID_STRING, orgId);
        createBucketJson.put(NAME_STRING, bucketName);

        return createBucketJson.toString();

    }
    private String listBuckets() throws TSDBAdapterException {

        // LIST BUCKETS
        // curl "http://localhost:8086/api/v2/buckets" \
        // --header "Authorization: Token ${INFLUX_TOKEN}"
        final HashMap<String, String> headerMap = HttpHelper.getTokenAuthHeaderMap(token);
        
        try {
            return HttpHelper.executeGet(httpClient, bucketsUri, null, headerMap, 200);
        } catch (HttpException e) {
            throw new TSDBAdapterException(e);
        }

    }
    private String listOrganisations() throws TSDBAdapterException {

        // LIST ORG
        // curl "http://localhost:8086/api/v2/orgs" \
        // --header "Authorization: Token ${INFLUX_TOKEN}"

        final HashMap<String, String> headers = HttpHelper.getTokenAuthHeaderMap(token);
        
        try {
            return HttpHelper.executeGet(httpClient, orgsUri, null, headers, 200);
        } catch (HttpException e) {
            throw new TSDBAdapterException(e);
        }

    }
    private String lookupId(final String type, final String name, final String response) throws TSDBAdapterException {

        final JSONObject orgList = new JSONObject(response);

        final JSONArray bucketList = orgList.getJSONArray(type);

        JSONObject currentBucket;
        for (int i = 0; i < bucketList.length(); i++) {

            currentBucket = (JSONObject) bucketList.get(i);

            if (currentBucket.getString(NAME_STRING).equals(name)) {
                return currentBucket.getString(ID_STRING);
            }


        }

        throw new TSDBAdapterException(type + " ID not found for: " + name);

    }

}
