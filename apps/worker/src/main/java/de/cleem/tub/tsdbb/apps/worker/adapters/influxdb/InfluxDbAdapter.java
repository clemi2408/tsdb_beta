package de.cleem.tub.tsdbb.apps.worker.adapters.influxdb;

import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.apps.worker.adapters.InsertResponse;
import de.cleem.tub.tsdbb.apps.worker.adapters.SelectResponse;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterException;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterIF;
import de.cleem.tub.tsdbb.apps.worker.formats.LineProtocolFormat;
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

    private static final String QUERY_ENDPOINT = "/api/v2/query?orgID=%s";
    private static final String WRITE_PRECISION = "ms";
    private static final String RUN_LABEL_KEY = "run";
    private static final String MIME_CSV = "application/csv";
    private static final String MIME_FLUX = "application/vnd.flux";

    private HttpClient httpClient;
    private LineProtocolFormat lineProtocolFormat;

    private String orgName;
    private String orgId;
    private String bucketName;
    private String bucketId;

    private WorkerGeneralProperties.TsdbTypeEnum tsdbType;

    @Override
    public void setup(final WorkerSetupRequest workerSetupRequest) throws TSDBAdapterException {

        tsdbType = workerSetupRequest.getWorkerGeneralProperties().getTsdbType();

        if (!(tsdbType.equals(WorkerGeneralProperties.TsdbTypeEnum.INFLUX))) {

            throw new TSDBAdapterException("Setup error - workerTsdbConnection is of type "+ WorkerGeneralProperties.TsdbTypeEnum.INFLUX);

        }

        httpClient = HttpClient.newHttpClient();

        for(WorkerTsdbEndpoint endpoint : workerSetupRequest.getWorkerConfiguration().getTsdbEndpoints()) {

            healthCheck(endpoint);

        }


        lineProtocolFormat = LineProtocolFormat.builder()
                .measurementName(tsdbType.getValue()+ MEASUREMENT_SUFFIX_STRING)
                .labelKey(RUN_LABEL_KEY)
                .labelValue(tsdbType.getValue())
                .build();

        if(!workerSetupRequest.getWorkerGeneralProperties().getCustomProperties().containsKey(PROPERTY_KEY_BUCKET_NAME)){
            throw new TSDBAdapterException("Connection Property "+PROPERTY_KEY_BUCKET_NAME+" not set");
        }

        bucketName = workerSetupRequest.getWorkerGeneralProperties().getCustomProperties().get(PROPERTY_KEY_BUCKET_NAME);

        if(!workerSetupRequest.getWorkerGeneralProperties().getCustomProperties().containsKey(PROPERTY_KEY_ORGANISATION_NAME)){
            throw new TSDBAdapterException("Connection Property "+PROPERTY_KEY_ORGANISATION_NAME+" not set");
        }

        orgName = workerSetupRequest.getWorkerGeneralProperties().getCustomProperties().get(PROPERTY_KEY_ORGANISATION_NAME);

        orgId = lookupId(ORGS_STRING, orgName,listOrganisations(workerSetupRequest.getWorkerConfiguration().getTsdbEndpoints().get(0)));

    }
    @Override
    public void healthCheck(final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        // HEALTH
        // curl "http://localhost:8086/health"
        try {

            final URI uri = URI.create(endpoint.getTsdbUrl() + HEALTH_ENDPOINT);

            HttpHelper.executeGet(httpClient, uri, null, null, 200);
        } catch (HttpException e) {
            throw new TSDBAdapterException(e);
        }

    }
    @Override
    public void createStorage(final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        // CREATE BUCKET
        // curl --request POST \
        //	"http://localhost:8086/api/v2/buckets" \
        //	--header "Authorization: Token ${INFLUX_TOKEN}" \
        //  --header "Content-type: application/json" \
        //  --data '{
        //    "orgID": "'"${INFLUX_ORG_ID}"'",
        //    "name": "'"${INFLUX_BUCKET}"'"
        //  }'


        final HashMap<String, String> headerMap = HttpHelper.getTokenAuthHeaderMap(endpoint.getTsdbToken());
        headerMap.put(HttpHelper.HEADER_KEY_CONTENT_TYPE, HttpHelper.HEADER_KEY_CONTENT_TYPE_VALUE_JSON);

        final String createBucketRequestBody = buildCreateBucketJson(orgId, bucketName);

        final URI uri = URI.create(endpoint.getTsdbUrl() + BUCKETS_ENDPOINT);

        try {
            HttpHelper.executePost(httpClient, uri, createBucketRequestBody, headerMap, 201);
        } catch (HttpException e) {
            throw new TSDBAdapterException(e);
        }

        bucketId = lookupId(BUCKETS_STRING, bucketName, listBuckets(endpoint));


    }
    @Override
    public void close() {

        httpClient = null;

    }
    @Override
    public void cleanup(final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        if (bucketId == null) {

            throw new TSDBAdapterException("Can not delete to Storage " + bucketName + " - bucketId is NULL");

        }

        // DELETE BUCKET
        // curl --request DELETE "http://localhost:8086/api/v2/buckets/${INFLUX_BUCKET_ID}" \
        //  --header "Authorization: Token ${INFLUX_TOKEN}" \
        //  --header 'Accept: application/json'


        final URI uri = URI.create(endpoint.getTsdbUrl() + BUCKETS_ENDPOINT + "/" + bucketId);

        final HashMap<String, String> headerMap = HttpHelper.getTokenAuthHeaderMap(endpoint.getTsdbToken());
        headerMap.put(HttpHelper.HEADER_KEY_CONTENT_TYPE, HttpHelper.HEADER_KEY_CONTENT_TYPE_VALUE_JSON);

        try {
            HttpHelper.executeDelete(httpClient, uri, null, headerMap, 204);
            log.info("Deleted bucket: " + bucketName);
        } catch (HttpException e) {
            throw new TSDBAdapterException(e);
        }




    }
    @Override
    public InsertResponse write(final Insert insert, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        if (bucketId == null) {
            throw new TSDBAdapterException("Can not write to Storage - bucketId is NULL");
        }

        final String metricLine = lineProtocolFormat.getLine(insert);

        // WRITE
        // curl -v --request POST \
        //"http://localhost:8086/api/v2/write?org=${INFLUX_ORG}&bucket=${INFLUX_BUCKET}&precision=s" \
        //  --header "Authorization: Token ${INFLUX_TOKEN}" \
        //  --data-binary 'testtesttest1,sensor_id=TLM0201 temperature=73.97038159354763,humidity=35.23103248356096,co=0.48445310567793615 1673369771'

        final URI uri = URI.create(endpoint.getTsdbUrl() + String.format(WRITE_ENDPOINT, orgName, bucketName, WRITE_PRECISION));

        final HashMap<String, String> headers = HttpHelper.getTokenAuthHeaderMap(endpoint.getTsdbToken());

        try {

            HttpHelper.executePost(httpClient, uri, metricLine, headers, 204);

            log.debug("Wrote Line: " + metricLine);

            return InsertResponse.builder().requestLength(metricLine.length()).insert(insert).build();

        } catch (HttpException e) {
            throw new TSDBAdapterException(e);
        }

    }
    @Override
    public SelectResponse read(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        return null;
    }
    @Override
    public SelectResponse getAllLabels(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        if (bucketName == null) {
            throw new TSDBAdapterException("Can not get all labels - bucketName is NULL");
        }

        if (orgId == null) {
            throw new TSDBAdapterException("Can not get all labels - orgId is NULL");
        }

        /*
        curl --request POST \
        "http://localhost:8086/api/v2/query?orgID=${INFLUX_ORG_ID}"  \
        --header "Authorization: Token ${INFLUX_TOKEN}" \
        --header 'Accept: application/csv' \
        --header 'Content-type: application/vnd.flux' \
        --data '
        import "influxdata/influxdb/schema"
        schema.tagKeys(
                bucket: "'$INFLUX_BUCKET'",
                predicate: (r) => true,
                start: '$START_VALUE'
        )
        '
        */

        final URI uri = URI.create(endpoint.getTsdbUrl() + String.format(QUERY_ENDPOINT, orgId));

        final HashMap<String, String> headers = HttpHelper.getAcceptContentTypeTokenHeaderMap(endpoint.getTsdbToken(),MIME_CSV,MIME_FLUX);

        String requestBody = "import \"influxdata/influxdb/schema\"\n" +
                "        schema.tagKeys(\n" +
                "                bucket: \"'"+bucketName+"'\",\n" +
                "                predicate: (r) => true,\n" +
                "                start: '"+select.getStartValue()+"'\n" +
                "        )";

        try {

            String responseBody = HttpHelper.executePost(httpClient, uri, requestBody, headers, 200);

            log.debug("Queried: " + requestBody);

            return SelectResponse.builder()
                    .requestLength(requestBody.length())
                    .responseLength(responseBody.length())
                    .select(select)
                    .build();

        } catch (HttpException e) {
            throw new TSDBAdapterException(e);
        }

    }
    @Override
    public SelectResponse getSingleLabelValue(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException{


        if (bucketName == null) {
            throw new TSDBAdapterException("Can not get single label value - bucketName is NULL");
        }

        if (orgId == null) {
            throw new TSDBAdapterException("Can not get single label value - orgId is NULL");
        }

        /*
            curl --request POST \
            "http://localhost:8086/api/v2/query?orgID=${INFLUX_ORG_ID}"  \
            --header "Authorization: Token ${INFLUX_TOKEN}" \
            --header 'Accept: application/csv' \
            --header 'Content-type: application/vnd.flux' \
            --data '
            from(bucket: "'$INFLUX_BUCKET'")
            |> range(start: '$START_VALUE')
            |> group(columns: ["'LABEL1_NAME'"])
            |> distinct(column: "'LABEL1_NAME'")
            |> keep(columns: ["_value"])
            '
        */

        final URI uri = URI.create(endpoint.getTsdbUrl() + String.format(QUERY_ENDPOINT, orgId));

        final HashMap<String, String> headers = HttpHelper.getAcceptContentTypeTokenHeaderMap(endpoint.getTsdbToken(),MIME_CSV,MIME_FLUX);

        String requestBody = "from(bucket: \"'"+bucketName+"'\")\n" +
                "|> range(start: '"+select.getStartValue()+"')\n" +
                "|> group(columns: [\"'"+select.getLabelName()+"'\"])\n" +
                "|> distinct(column: \"'"+select.getLabelName()+"'\")\n" +
                "|> keep(columns: [\"_value\"])";

        try {

            String responseBody = HttpHelper.executePost(httpClient, uri, requestBody, headers, 200);

            log.debug("Queried: " + requestBody);

            return SelectResponse.builder()
                    .requestLength(requestBody.length())
                    .responseLength(responseBody.length())
                    .select(select)
                    .build();

        } catch (HttpException e) {
            throw new TSDBAdapterException(e);
        }

    }
    @Override
    public SelectResponse getMeasurementLabels(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException{
        if (bucketName == null) {
            throw new TSDBAdapterException("Can not get measurement labels - bucketName is NULL");
        }

        if (orgId == null) {
            throw new TSDBAdapterException("Can not get measurement labels - orgId is NULL");
        }

        /*
            curl --request POST \
            "http://localhost:8086/api/v2/query?orgID=${INFLUX_ORG_ID}"  \
            --header "Authorization: Token ${INFLUX_TOKEN}" \
            --header 'Accept: application/csv' \
            --header 'Content-type: application/vnd.flux' \
            --data '
            from(bucket: "'$INFLUX_BUCKET'")
            |> range('$START_VALUE'))
            |> filter(fn: (r) => r["_measurement"] == "'$MEASUREMENT_NAME'")
            |> distinct(column: "_field")
            |> keep(columns: ["_field"])
            '
        */

        final URI uri = URI.create(endpoint.getTsdbUrl() + String.format(QUERY_ENDPOINT, orgId));

        final HashMap<String, String> headers = HttpHelper.getAcceptContentTypeTokenHeaderMap(endpoint.getTsdbToken(),MIME_CSV,MIME_FLUX);

        String requestBody = "from(bucket: \"'"+bucketName+"'\")\n" +
                "|> range(start: '"+select.getStartValue()+"')\n" +
                "|> filter(fn: (r) => r[\"_measurement\"] == \"'"+select.getMeasurementName()+"'\")\n" +
                "|> distinct(column: \"_field\")\n" +
                "|> keep(columns: [\"_field\"])";

        try {

            String responseBody = HttpHelper.executePost(httpClient, uri, requestBody, headers, 200);

            log.debug("Queried: " + requestBody);

            return SelectResponse.builder()
                    .requestLength(requestBody.length())
                    .responseLength(responseBody.length())
                    .select(select)
                    .build();

        } catch (HttpException e) {
            throw new TSDBAdapterException(e);
        }
    }
    ////
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
    private String listBuckets(final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        // LIST BUCKETS
        // curl "http://localhost:8086/api/v2/buckets" \
        // --header "Authorization: Token ${INFLUX_TOKEN}"
        final HashMap<String, String> headerMap = HttpHelper.getTokenAuthHeaderMap(endpoint.getTsdbToken());

        final URI uri = URI.create(endpoint.getTsdbUrl() + BUCKETS_ENDPOINT);

        try {
            return HttpHelper.executeGet(httpClient, uri, null, headerMap, 200);
        } catch (HttpException e) {
            throw new TSDBAdapterException(e);
        }

    }
    private String listOrganisations(final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        // LIST ORG
        // curl "http://localhost:8086/api/v2/orgs" \
        // --header "Authorization: Token ${INFLUX_TOKEN}"


        final HashMap<String, String> headers = HttpHelper.getTokenAuthHeaderMap(endpoint.getTsdbToken());

        final URI uri = URI.create(endpoint.getTsdbUrl() + ORGS_ENDPOINT);

        try {
            return HttpHelper.executeGet(httpClient, uri, null, headers, 200);
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
