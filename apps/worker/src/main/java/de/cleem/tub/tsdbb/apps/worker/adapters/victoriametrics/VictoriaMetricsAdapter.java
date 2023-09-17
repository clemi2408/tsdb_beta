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

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
public class VictoriaMetricsAdapter implements TSDBAdapterIF {

    private static final String MEASUREMENT_SUFFIX_STRING = "_measurement";
    private static final String RUN_LABEL_KEY = "run";
    private static final String WRITE_ENDPOINT = "/write";
    private static final String LABEL_ENDPOINT = "/api/v1/label/__name__/values";
    private static final String DELETE_ENDPOINT = "/api/v1/admin/tsdb/delete_series?match[]=%s";
    private static final int PRE_CLEANUP_SLEEP_IN_MS = 2000;

    // health==metrics? https://github.com/VictoriaMetrics/VictoriaMetrics/issues/3539
    private static final String HEALTH_ENDPOINT = "/metrics";
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
    public void healthCheck(WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        // HEALTH
        // curl "http://localhost:8428/metrics"
        try {

            final URI healthUri = URI.create(endpoint.getTsdbUrl() + HEALTH_ENDPOINT);

            HttpHelper.executeGet(httpClient, healthUri, null, null, 200);
        } catch (HttpException e) {
            throw new TSDBAdapterException(e);
        }

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

        final String metricLine = lineProtocolFormat.getLine(insert);
        log.info("Created metricLine: "+metricLine);


        // WRITE
        // curl -X POST \
        //'http://localhost:8428/write' \
        //-d 'census,location=klamath,scientist=anderson bees=23 1673176214'

        try {

            final URI writeUri = URI.create(endpoint.getTsdbUrl() + WRITE_ENDPOINT);

            HttpHelper.executePost(httpClient, writeUri, metricLine, new HashMap<>(), 204);

            log.debug("Wrote Line: " + metricLine + " to: " + writeUri);

            return InsertResponse.builder().requestLength(metricLine.length()).insert(insert).build();

        } catch (HttpException e) {
            throw new TSDBAdapterException(e);
        }



    }

    @Override
    public SelectResponse read(final Select select, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

    }

    ////
    private String[] getSeriesByLabelKV(final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        final String requestBody = "match[]={__name__=~\".+\", " + RUN_LABEL_KEY + "=\"" + tsdbType.getValue() + "\"}";

        final String encodedRequestBody = URLEncoder.encode(requestBody, StandardCharsets.UTF_8);

        // curl -XGET 'http://localhost:8428/api/v1/label/__name__/values' --data-urlencode 'match[]={__name__=~".+", run="victoria"}'

        final HashMap<String, String> headers = new HashMap<>();
        headers.put(HttpHelper.HEADER_KEY_CONTENT_TYPE, HttpHelper.HEADER_KEY_CONTENT_TYPE_VALUE_FORM_URLENCODED);

        final URI labelUri = URI.create(endpoint.getTsdbUrl() + LABEL_ENDPOINT);

        final String response;

        try {

            response = HttpHelper.executeGet(httpClient, labelUri, encodedRequestBody, headers, 200);

        } catch (HttpException e) {
            throw new TSDBAdapterException(e);
        }

        final JSONObject jObject = new JSONObject(response);

        final JSONArray seriesArray = jObject.getJSONArray("data");

        final ArrayList<String> seriesList = new ArrayList<>();
        for (int i = 0; i < seriesArray.length(); i++) {

            seriesList.add(seriesArray.getString(i));

        }

        return seriesList.toArray(new String[0]);


    }
    private void deleteSeries(final String seriesName, final WorkerTsdbEndpoint endpoint) throws TSDBAdapterException {

        final URI deleteUri = URI.create(endpoint.getTsdbUrl() + String.format(DELETE_ENDPOINT, seriesName));

        // curl 'http://localhost:8428/api/v1/admin/tsdb/delete_series?match[]=victoria_measurement_PEp'

        try {

            HttpHelper.executeGet(httpClient, deleteUri, null, null, 204);
            log.info("Deleted series: " + seriesName);

        } catch (HttpException e) {
            throw new TSDBAdapterException(e);
        }



    }

}
