package de.cleem.tub.tsdbb.apps.worker.adapters.victoriametrics;


import de.cleem.tub.tsdbb.api.model.Record;
import de.cleem.tub.tsdbb.api.model.WorkerTsdbConnection;
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
    private HttpClient httpClient;
    private URI baseUrl;
    private URI labelUri;
    private URI writeUri;
    private LineProtocolFormat lineProtocolFormat;
    private WorkerTsdbConnection.TsdbTypeEnum tsdbType;

    @Override
    public void setup(final WorkerTsdbConnection workerTsdbConnection) throws TSDBAdapterException {

        if (!(workerTsdbConnection.getTsdbType().equals(WorkerTsdbConnection.TsdbTypeEnum.VICTORIA))) {

            throw new TSDBAdapterException("Setup error - workerTsdbConnection is of type "+ WorkerTsdbConnection.TsdbTypeEnum.VICTORIA+" - " + getConnectionInfo());

        }

        httpClient = HttpClient.newHttpClient();

        tsdbType = workerTsdbConnection.getTsdbType();

        baseUrl = workerTsdbConnection.getUrl();
        labelUri = URI.create(baseUrl + LABEL_ENDPOINT);
        writeUri = URI.create(baseUrl + WRITE_ENDPOINT);

        lineProtocolFormat = LineProtocolFormat.builder()
                .measurementName(tsdbType.getValue()+MEASUREMENT_SUFFIX_STRING)
                .labelKey(RUN_LABEL_KEY)
                .labelValue(tsdbType.getValue())
                .build();

    }
    @Override
    public void createStorage() {

        // NOT IMPLEMENTED

    }
    @Override
    public void close() {

        httpClient = null;

    }
    @Override
    public void cleanup() throws TSDBAdapterException {

        try {
            // :(
            Thread.sleep(PRE_CLEANUP_SLEEP_IN_MS);
            // :((((
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        final String[] seriesArray = getSeriesByLabelKV();

        for (String series : seriesArray) {

            deleteSeries(series);

        }

    }
    @Override
    public void write(final Record record) throws TSDBAdapterException {

        final String metricLine = lineProtocolFormat.getLine(record, Instant.now());

        // WRITE
        // curl -X POST \
        //'http://localhost:8428/write' \
        //-d 'census,location=klamath,scientist=anderson bees=23 1673176214'


        try {
            HttpHelper.executePost(httpClient, writeUri, metricLine, new HashMap<>(), 204);
        } catch (HttpException e) {
            throw new TSDBAdapterException(e);
        }

        log.debug("Wrote Line: " + metricLine + " to: " + getConnectionInfo());

    }
    @Override
    public String getConnectionInfo() {

        final StringBuilder stringBuilder = new StringBuilder();

        if (baseUrl != null) {
            stringBuilder.append(baseUrl);
        }

        return stringBuilder.toString();
    }

    ////
    private String[] getSeriesByLabelKV() throws TSDBAdapterException {

        final String requestBody = "match[]={__name__=~\".+\", " + RUN_LABEL_KEY + "=\"" + tsdbType.getValue() + "\"}";

        final String encodedRequestBody = URLEncoder.encode(requestBody, StandardCharsets.UTF_8);

        // curl -XGET 'http://localhost:8428/api/v1/label/__name__/values' --data-urlencode 'match[]={__name__=~".+", run="victoria"}'

        final HashMap<String, String> headers = new HashMap<>();
        headers.put(HttpHelper.HEADER_KEY_CONTENT_TYPE, HttpHelper.HEADER_KEY_CONTENT_TYPE_VALUE_FORM_URLENCODED);

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
    private void deleteSeries(final String seriesName) throws TSDBAdapterException {

        final URI deleteUri = URI.create(baseUrl + String.format(DELETE_ENDPOINT, seriesName));

        // curl 'http://localhost:8428/api/v1/admin/tsdb/delete_series?match[]=victoria_measurement_PEp'

        try {

            HttpHelper.executeGet(httpClient, deleteUri, null, null, 204);
            log.info("Deleted series: " + seriesName + " - " + getConnectionInfo());

        } catch (HttpException e) {
            throw new TSDBAdapterException(e);
        }



    }

}
