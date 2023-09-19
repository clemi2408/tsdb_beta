package de.cleem.tub.tsdbb.apps.worker.adapters.victoriametrics;


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
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
public class VictoriaMetricsAdapter implements TSDBAdapterIF {

    private static final String MEASUREMENT_SUFFIX_STRING = "_measurement";
    private static final String RUN_LABEL_KEY = "run";
    private static final int PRE_CLEANUP_SLEEP_IN_MS = 2000;
    private static final String WRITE_ENDPOINT = "/write";
    private static final String LABEL_VALUE_ENDPOINT = "/api/v1/label/%s/values";
    private static final String SERIES_ENDPOINT = "/api/v1/series";
    private static final String SERIES_COUNT_ENDPOINT = "/api/v1/label/series/count";
    private static final String LABELS_ENDPOINT ="/api/v1/labels";
    private static final String LABEL_ENDPOINT = "/api/v1/label/__name__/values";
    private static final String DELETE_ENDPOINT = "/api/v1/admin/tsdb/delete_series?match[]=%s";
    // health==metrics? https://github.com/VictoriaMetrics/VictoriaMetrics/issues/3539
    private static final String HEALTH_ENDPOINT = "/metrics";
    private static final String EXPORT_ENDPOINT = "/api/v1/export";
    private static final String QUERY_ENDPOINT_INSTANT = "/api/v1/query";

    private static final String QUERY_ENDPOINT_RANGE = "/api/v1/query_range";

    private HttpClient httpClient;
    private LineProtocolFormat lineProtocolFormat;
    private WorkerGeneralProperties.TsdbTypeEnum tsdbType;

    @Override
    public void setup(final WorkerSetupRequest workerSetupRequest) throws TSDBAdapterException {

        tsdbType = workerSetupRequest.getWorkerGeneralProperties().getTsdbType();

        if (!(tsdbType.equals(WorkerGeneralProperties.TsdbTypeEnum.VICTORIA))) {

            throw new TSDBAdapterException("Setup error - workerTsdbConnection is of type "+ WorkerGeneralProperties.TsdbTypeEnum.VICTORIA);

        }

        httpClient = HttpClient.newHttpClient();

        for(WorkerTsdbEndpoint endpoint : workerSetupRequest.getWorkerConfiguration().getTsdbEndpoints()) {

            healthCheck(endpoint);

        }

        httpClient = HttpClient.newHttpClient();

        lineProtocolFormat = LineProtocolFormat.builder()
                .measurementName(tsdbType.getValue()+MEASUREMENT_SUFFIX_STRING)
                .labelKey(RUN_LABEL_KEY)
                .labelValue(tsdbType.getValue())
                .build();

    }
    @Override
    public void healthCheck(final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        // HEALTH
        // curl "http://localhost:8428/metrics"

        callHttp(endpoint,HEALTH_ENDPOINT,new HashMap<>(),null,HttpMethod.GET,200);

    }
    @Override
    public void createStorage(final WorkerTsdbEndpoint endpoint) {

        // NOT IMPLEMENTED

    }
    @Override
    public void close() {

        httpClient = null;

    }
    @Override
    public void cleanup(final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        try {
            // :(
            Thread.sleep(PRE_CLEANUP_SLEEP_IN_MS);
            // :((((
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        final String[] seriesArray = getSeriesByLabelKV(endpoint);

        for (String series : seriesArray) {

            deleteSeries(series, endpoint);

        }

    }
    @Override
    public InsertResponse write(final Insert insert, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        // WRITE
        // curl -X POST \
        //'http://localhost:8428/write' \
        //-d 'census,location=klamath,scientist=anderson bees=23 1673176214'

        if(insert==null){
            throw new TSDBAdapterException("Can not write - insert is NULL");
        }

        final String metricLine = lineProtocolFormat.getLine(insert);
        log.info("Created metricLine: "+metricLine);

        try {

            final URI uri = URI.create(endpoint.getTsdbUrl() + WRITE_ENDPOINT);

            HttpHelper.executePost(httpClient, uri, metricLine, new HashMap<>(), 204);

            log.debug("Wrote Line: " + metricLine + " to: " + uri);

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

        // curl -g "http://localhost:8428/api/v1/labels"

        if(select==null){
            throw new TSDBAdapterException("Can not get AllLabels - select is NULL");
        }

        return doQuery(endpoint,select, LABELS_ENDPOINT,null, HttpMethod.GET);

    }
    @Override
    public SelectResponse getSingleLabelValue(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException{

        // curl -G "http://localhost:8428/api/v1/label/${LABEL1_NAME}/values"

        if(select==null){
            throw new TSDBAdapterException("Can not get SingleLabelValue - select is NULL");
        }
        if(select.getLabelName()==null){
            throw new TSDBAdapterException("Can not get SingleLabelValue - select.getLabelName is NULL");
        }

        return doQuery(endpoint,select, String.format(LABEL_VALUE_ENDPOINT, select.getLabelName()),null, HttpMethod.GET);


    }
    @Override
    public SelectResponse getMeasurementLabels(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException{

        // curl -X GET "http://localhost:8428/api/v1/label/__name__/values" \
        // --data-urlencode "match[]={__name__=~".+", run="${MEASUREMENT_NAME}"}"

        if(select==null){
            throw new TSDBAdapterException("Can not get MeasurementLabels - select is NULL");
        }
        if(select.getMeasurementName()==null){
            throw new TSDBAdapterException("Can not get MeasurementLabels - select.getMeasurementName is NULL");
        }

        final String requestBody = "match[]={__name__=~\".+\", " + RUN_LABEL_KEY + "=\"" + select.getMeasurementName() + "\"}";

        return doQuery(endpoint,select,LABEL_ENDPOINT,requestBody,HttpMethod.GET);

    }
    @Override
    public SelectResponse countSeries(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        // curl -s "http://localhost:8428/api/v1/series/count"

        if(select==null){
            throw new TSDBAdapterException("Can not countSeries - select is NULL");
        }

        return doQuery(endpoint,select,SERIES_COUNT_ENDPOINT,null,HttpMethod.GET);

    }
    @Override
    public SelectResponse getAllSeries(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        // curl -G "http://localhost:8428/api/v1/series" -d "match[]={__name__=~".*"}"

        if(select==null){
            throw new TSDBAdapterException("Can not get AllSeries - select is NULL");
        }

        final String requestBody = "match[]={__name__=~\".*\"}";

        return doQuery(endpoint,select,SERIES_ENDPOINT,requestBody,HttpMethod.GET);

    }
    @Override
    public SelectResponse getMeasurementSeries(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        // curl -G "http://localhost:8428/api/v1/series" -d "match[]={__name__=~"${MEASUREMENT_NAME}.*"}"

        if(select==null){
            throw new TSDBAdapterException("Can not get MeasurementSeries - select is NULL");
        }
        if(select.getMeasurementName()==null){
            throw new TSDBAdapterException("Can not get MeasurementSeries - select.getMeasurementName is NULL");
        }

        final String requestBody = "match[]={__name__=~\""+select.getMeasurementName()+".*\"}";

        return doQuery(endpoint,select,SERIES_ENDPOINT,requestBody, HttpMethod.GET);

    }
    @Override
    public SelectResponse exportSeries(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        // curl -X POST "http://localhost:8428/api/v1/export" -d "match[]={__name__=~"${MEASUREMENT_NAME}_${FIELD1_NAME}"}"

        if(select==null){
            throw new TSDBAdapterException("Can not exportSeries - select is NULL");
        }
        if(select.getMeasurementName()==null){
            throw new TSDBAdapterException("Can not exportSeries - select.getMeasurementName is NULL");
        }
        if(select.getFieldName()==null){
            throw new TSDBAdapterException("Can not exportSeries - select.getFieldName is NULL");
        }

        final String requestBody = "match[]={__name__=~\""+select.getMeasurementName()+"_"+select.getFieldName()+"\"}";

        return doQuery(endpoint,select,EXPORT_ENDPOINT,requestBody, HttpMethod.POST);
    }
    @Override
    public SelectResponse getFieldValue(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        // curl -G "http://localhost:8428/api/v1/series" -d "match[]={__name__=~".*"}"

        if(select==null){
            throw new TSDBAdapterException("Can not get value - select is NULL");
        }
        if(select.getMeasurementName()==null){
            throw new TSDBAdapterException("Can not get value - select.getMeasurementName is NULL");
        }
        if(select.getFieldName()==null){
            throw new TSDBAdapterException("Can not get value - select.getFieldName is NULL");
        }

        final String requestBody = "query="+select.getMeasurementName()+"_"+select.getFieldName();

        return doQuery(endpoint,select, QUERY_ENDPOINT_INSTANT,requestBody,HttpMethod.GET);

    }
    @Override
    public SelectResponse getFieldValueSum(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        // curl "http://localhost:8428/api/v1/query_range" -d "query=sum_over_time(${MEASUREMENT_NAME}_${FIELD1_NAME})" -d "start=${START_VALUE}" -d "step=${STEP_VALUE}"

        if(select==null){
            throw new TSDBAdapterException("Can not get FieldValueSum - select is NULL");
        }
        if(select.getStartValue()==null){
            throw new TSDBAdapterException("Can not get FieldValueSum - select.getStartValue is NULL");
        }
        if(select.getStepValue()==null){
            throw new TSDBAdapterException("Can not get FieldValueSum - select.getStepValue is NULL");
        }
        if(select.getMeasurementName()==null){
            throw new TSDBAdapterException("Can not get FieldValueSum - select.getMeasurementName is NULL");
        }
        if(select.getFieldName()==null){
            throw new TSDBAdapterException("Can not get FieldValueSum - select.getFieldName is NULL");
        }

        final String requestBody = "query=sum_over_time("+select.getMeasurementName()+"_"+select.getFieldName()+")" +
                "&start="+select.getStartValue()+
                "&step="+select.getStepValue();

        return doQuery(endpoint,select, QUERY_ENDPOINT_RANGE,requestBody,HttpMethod.GET);

    }
    @Override
    public SelectResponse getFieldValueAvg(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        // curl "http://localhost:8428/api/v1/query_range" -d "query=avg_over_time(${MEASUREMENT_NAME}_${FIELD1_NAME})" -d "start=${START_VALUE}" -d "step=${STEP_VALUE}"

        if(select==null){
            throw new TSDBAdapterException("Can not get FieldValueAvg - select is NULL");
        }
        if(select.getStartValue()==null){
            throw new TSDBAdapterException("Can not get FieldValueAvg - select.getStartValue is NULL");
        }
        if(select.getStepValue()==null){
            throw new TSDBAdapterException("Can not get FieldValueAvg - select.getStepValue is NULL");
        }
        if(select.getMeasurementName()==null){
            throw new TSDBAdapterException("Can not get FieldValueAvg - select.getMeasurementName is NULL");
        }
        if(select.getFieldName()==null){
            throw new TSDBAdapterException("Can not get FieldValueAvg - select.getFieldName is NULL");
        }

        final String requestBody = "query=avg_over_time("+select.getMeasurementName()+"_"+select.getFieldName()+")" +
                "&start="+select.getStartValue()+
                "&step="+select.getStepValue();

        return doQuery(endpoint,select, QUERY_ENDPOINT_RANGE,requestBody,HttpMethod.GET);
    }
    @Override
    public SelectResponse getFieldValueMin(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        // curl "http://localhost:8428/api/v1/query_range" -d "query=min_over_time(${MEASUREMENT_NAME}_${FIELD1_NAME})" -d "start=${START_VALUE}" -d "step=${STEP_VALUE}"

        if(select==null){
            throw new TSDBAdapterException("Can not get FieldValueMin - select is NULL");
        }
        if(select.getStartValue()==null){
            throw new TSDBAdapterException("Can not get FieldValueMin - select.getStartValue is NULL");
        }
        if(select.getStepValue()==null){
            throw new TSDBAdapterException("Can not get FieldValueMin - select.getStepValue is NULL");
        }
        if(select.getMeasurementName()==null){
            throw new TSDBAdapterException("Can not get FieldValueMin - select.getMeasurementName is NULL");
        }
        if(select.getFieldName()==null){
            throw new TSDBAdapterException("Can not get FieldValueMin - select.getFieldName is NULL");
        }

        final String requestBody = "query=min_over_time("+select.getMeasurementName()+"_"+select.getFieldName()+")" +
                "&start="+select.getStartValue()+
                "&step="+select.getStepValue();

        return doQuery(endpoint,select, QUERY_ENDPOINT_RANGE,requestBody,HttpMethod.GET);

    }
    @Override
    public SelectResponse getFieldValueMax(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        // curl "http://localhost:8428/api/v1/query_range" -d "query=max_over_time(${MEASUREMENT_NAME}_${FIELD1_NAME})" -d "start=${START_VALUE}" -d "step=${STEP_VALUE}"

        if(select==null){
            throw new TSDBAdapterException("Can not get FieldValueMax - select is NULL");
        }
        if(select.getStartValue()==null){
            throw new TSDBAdapterException("Can not get FieldValueMax - select.getStartValue is NULL");
        }
        if(select.getStepValue()==null){
            throw new TSDBAdapterException("Can not get FieldValueMax - select.getStepValue is NULL");
        }
        if(select.getMeasurementName()==null){
            throw new TSDBAdapterException("Can not get FieldValueMax - select.getMeasurementName is NULL");
        }
        if(select.getFieldName()==null){
            throw new TSDBAdapterException("Can not get FieldValueMax - select.getFieldName is NULL");
        }

        final String requestBody = "query=max_over_time("+select.getMeasurementName()+"_"+select.getFieldName()+")" +
                "&start="+select.getStartValue()+
                "&step="+select.getStepValue();

        return doQuery(endpoint,select, QUERY_ENDPOINT_RANGE,requestBody,HttpMethod.GET);

    }

    ////
    private String callHttp(final WorkerTsdbEndpoint endpoint, final String path, final HashMap<String, String> headers, final String requestBody, final HttpMethod method, int expectedCode) throws TSDBAdapterException {

        if(endpoint==null){
            throw new TSDBAdapterException("Can not callHttp - endpoint is NULL");
        }
        if(path==null){
            throw new TSDBAdapterException("Can not callHttp - path is NULL");
        }
        if(headers==null){

            throw new TSDBAdapterException("Can not callHttp - headers is NULL");

        }

        String encodedRequestBody = null;

        if(requestBody!=null) {
            encodedRequestBody = URLEncoder.encode(requestBody, StandardCharsets.UTF_8);
            headers.put(HttpHelper.HEADER_KEY_CONTENT_TYPE, HttpHelper.HEADER_KEY_CONTENT_TYPE_VALUE_FORM_URLENCODED);
        }

        final URI uri = URI.create(endpoint.getTsdbUrl() + path);

        try {

            if(method==HttpMethod.GET){
                return HttpHelper.executeGet(httpClient, uri, encodedRequestBody, headers, expectedCode);

            }
            else if(method==HttpMethod.POST){

                return HttpHelper.executePost(httpClient,uri,encodedRequestBody,headers,expectedCode);

            }
            else {
                throw new TSDBAdapterException("Invalid HTTP Method");

            }

        } catch (HttpException e) {
            throw new TSDBAdapterException(e);
        }

    }
    private SelectResponse doQuery(final WorkerTsdbEndpoint endpoint, final Select select, final String path, final String requestBody, final HttpMethod method) throws TSDBAdapterException {

        // curl -XGET 'http://localhost:8428/$PATH' --data-urlencode '$REQUEST_BODY'

        if(select==null){

           throw new TSDBAdapterException("Can not do Query - select is NULL");

        }

        String responseBody =  callHttp(endpoint,path,new HashMap<>(), requestBody,method,200);

        return SelectResponse.builder()
                .requestLength(requestBody!=null?requestBody.length():0)
                .responseLength(responseBody.length())
                .select(select)
                .build();

    }
    private String getSeriesByLabelKVString(final WorkerTsdbEndpoint endpoint, final String measurement) throws TSDBAdapterException {

        if(measurement==null){

            throw new TSDBAdapterException("Can not get SeriesByLabelKVString - measurement is NULL");

        }

        // curl -XGET 'http://localhost:8428/api/v1/label/__name__/values' --data-urlencode 'match[]={__name__=~".+", run="victoria"}'

        final String requestBody = "match[]={__name__=~\".+\", " + RUN_LABEL_KEY + "=\"" + measurement + "\"}";

        return callHttp(endpoint,LABEL_ENDPOINT,new HashMap<>(),requestBody,HttpMethod.GET,200);

    }
    private String[] getSeriesByLabelKV(final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        final String response = getSeriesByLabelKVString(endpoint, tsdbType.getValue());

        final JSONObject jObject = new JSONObject(response);

        final JSONArray seriesArray = jObject.getJSONArray("data");

        final ArrayList<String> seriesList = new ArrayList<>();
        for (int i = 0; i < seriesArray.length(); i++) {

            seriesList.add(seriesArray.getString(i));

        }

        return seriesList.toArray(new String[0]);


    }
    private void deleteSeries(final String seriesName, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        // curl 'http://localhost:8428/api/v1/admin/tsdb/delete_series?match[]=victoria_measurement_PEp'

        if(seriesName==null){

            throw new TSDBAdapterException("Can not get deleteSeries - seriesName is NULL");

        }

        callHttp(endpoint,String.format(DELETE_ENDPOINT, seriesName),new HashMap<>(),null,HttpMethod.GET,204);
        log.info("Deleted series: " + seriesName);

    }

}
