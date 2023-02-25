package de.cleem.bm.tsdb.adapter.victoriametrics;


import de.cleem.bm.tsdb.adapter.TSDBAdapterIF;
import de.cleem.bm.tsdb.adapter.exception.TSDBAdapterException;
import de.cleem.bm.tsdb.common.http.HttpHelper;
import de.cleem.bm.tsdb.common.lineprotocolformat.LineProtocolFormat;
import de.cleem.bm.tsdb.model.config.adapter.TSDBAdapterConfig;
import de.cleem.tub.tsdbb.commons.exception.TSDBBException;
import de.cleem.tub.tsdbb.commons.model.workload.Record;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
public class VictoriaMetricsAdapter implements TSDBAdapterIF {

    private static final String MEASUREMENT_NAME = "victoria_measurement";

    private static final String RUN_LABEL_KEY = "run";
    private static final String RUN_LABEL_VALUE = "victoria";
    private static final String WRITE_ENDPOINT = "/write";
    private static final String LABEL_ENDPOINT = "/api/v1/label/__name__/values";

    private static final String DELETE_ENDPOINT = "/api/v1/admin/tsdb/delete_series?match[]=%s";

    private VictoriaMetricsAdapterConfig config;

    private HttpClient httpClient;
    private URI labelUri;

    private LineProtocolFormat lineProtocolFormat;

    @Override
    public void setup(TSDBAdapterConfig tsdbAdapterConfig) throws TSDBAdapterException {

        if (!(tsdbAdapterConfig instanceof VictoriaMetricsAdapterConfig)) {

            throw new TSDBAdapterException("Setup error - tsdbConfig is not instance of VictoriaMetricsAdapterConfig - " + getConnectionInfo());

        }

        httpClient = HttpClient.newHttpClient();
        config = (VictoriaMetricsAdapterConfig) tsdbAdapterConfig;
        labelUri = URI.create(config.getVictoriaMetricsUrl() + LABEL_ENDPOINT);

        lineProtocolFormat = LineProtocolFormat.builder()
                .measurementName(MEASUREMENT_NAME)
                .labelKey(RUN_LABEL_KEY)
                .labelValue(RUN_LABEL_VALUE)
                .build();

    }

    @Override
    public void createStorage() throws TSDBAdapterException {

        // NOT IMPLEMENTED

    }

    @Override
    public void close() {

        httpClient = null;

    }

    @Override
    public void cleanup() throws TSDBBException {

        try {
            // :(
            Thread.sleep(2000);
            // :((((
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        final String[] seriesArray = getSeriesByLabelKV(RUN_LABEL_KEY, RUN_LABEL_VALUE);

        for (String series : seriesArray) {

            deleteSeries(series);

        }

    }

    @Override
    public void write(final Record record) throws TSDBBException {

        log.info("Processing record: " + record.toString());

        final Instant instant = Instant.now();

        final String metricLine = lineProtocolFormat.getLine(record, instant);

        // WRITE
        // curl -X POST \
        //'http://localhost:8428/write' \
        //-d 'census,location=klamath,scientist=anderson bees=23 1673176214'


        final URI writeUri = URI.create(config.getVictoriaMetricsUrl() + WRITE_ENDPOINT);

        final HashMap<String, String> headers = new HashMap<>();

        HttpHelper.executePost(httpClient, writeUri, metricLine, headers, 204);

        log.info("Wrote Line: " + metricLine + " to: " + getConnectionInfo());

    }

    ////
    private String[] getSeriesByLabelKV(final String key, final String value) throws TSDBBException {

        final String requestBody = "match[]={__name__=~\".+\", " + key + "=\"" + value + "\"}";

        try {

            final String encodedRequestBody = URLEncoder.encode(requestBody, "UTF-8");

            // curl -XGET 'http://localhost:8428/api/v1/label/__name__/values' --data-urlencode 'match[]={__name__=~".+", run="victoria"}'

            final HashMap<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/x-www-form-urlencoded");


            final String response = HttpHelper.executeGet(httpClient, labelUri, encodedRequestBody, headers, 200);

            final JSONObject jObject = new JSONObject(response);

            final JSONArray seriesArray = jObject.getJSONArray("data");

            final ArrayList<String> seriesList = new ArrayList<>();
            for (int i = 0; i < seriesArray.length(); i++) {

                seriesList.add(seriesArray.getString(i));

            }

            return seriesList.toArray(new String[seriesList.size()]);


        } catch (UnsupportedEncodingException e) {
            throw new TSDBAdapterException(e);
        }

    }

    private void deleteSeries(final String seriesName) throws TSDBBException {

        final URI deleteUri = URI.create(config.getVictoriaMetricsUrl() + String.format(DELETE_ENDPOINT, seriesName));

        // curl 'http://localhost:8428/api/v1/admin/tsdb/delete_series?match[]=victoria_measurement_PEp'

        HttpHelper.executeGet(httpClient, deleteUri, null, null, 204);

        log.info("Deleted series: " + seriesName + " - " + getConnectionInfo());

    }

    private String getConnectionInfo() {

        final StringBuffer infoBuffer = new StringBuffer();

        if (config.getVictoriaMetricsUrl() != null) {
            infoBuffer.append(config.getVictoriaMetricsUrl());
        }

        return infoBuffer.toString();
    }

    ////


}
