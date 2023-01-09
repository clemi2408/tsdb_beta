package de.cleem.bm.tsdb.adapter.victoriametrics;


import de.cleem.bm.tsdb.adapter.common.LineProtocolFormat;
import de.cleem.bm.tsdb.adapter.common.TSDBAdapterConfig;
import de.cleem.bm.tsdb.adapter.common.TSDBAdapterException;
import de.cleem.bm.tsdb.adapter.common.TSDBAdapterIF;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
public class VictoriaMetricsAdapter implements TSDBAdapterIF {

    private static final String MEASUREMENT_NAME= "victoria_measurement";

    private static final String RUN_LABEL_KEY= "run";
    private static final String RUN_LABEL_VALUE= "victoria";
    private static final String WRITE_ENDPOINT = "/write";
    private static final String LABEL_ENDPOINT = "/api/v1/label/__name__/values";

    private static final String DELETE_ENDPOINT = "/api/v1/admin/tsdb/delete_series?match[]=%s";

    private VictoriaMetricsAdapterConfig config;

    private HttpClient httpClient;
    private URI labelUri;

    private LineProtocolFormat lineProtocolFormat;

    @Override
    public void setup(TSDBAdapterConfig tsdbAdapterConfig) throws TSDBAdapterException {

        if(!(tsdbAdapterConfig instanceof VictoriaMetricsAdapterConfig)){

            throw new TSDBAdapterException("Setup error - tsdbConfig is not instance of VictoriaMetricsAdapterConfig - " + getConnectionInfo());

        }

        httpClient = HttpClient.newHttpClient();
        config = (VictoriaMetricsAdapterConfig) tsdbAdapterConfig;
        labelUri = URI.create(config.getVictoriaMetricsUrl()+LABEL_ENDPOINT);

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
        // NOT IMPLEMENTED

    }

    @Override
    public void cleanup() throws TSDBAdapterException {

        try {
            // :(
            Thread.sleep(2000);
            // :((((
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        final String[] seriesArray = getSeriesByLabelKV(RUN_LABEL_KEY, RUN_LABEL_VALUE);

        for(String series : seriesArray){

             deleteSeries(series);

        }

    }

    @Override
    public void write(final HashMap<String, Number> record) throws TSDBAdapterException {

        if (httpClient == null) {
            throw new TSDBAdapterException("Can not write to Storage - client is NULL - "+getConnectionInfo());
        }

        final URI writeUri = URI.create(config.getVictoriaMetricsUrl()+WRITE_ENDPOINT);

        final String metricLine = lineProtocolFormat.getLine(record);
        try {

            // curl -d 'measurement,tag1=value1,tag2=value2 field1=123,field2=1.23' -X POST 'http://localhost:8428/write'

            final HttpRequest writeRequest = HttpRequest.newBuilder()
                    .uri(writeUri)
                    .POST(HttpRequest.BodyPublishers.ofString(metricLine))
                    .build();

            final HttpResponse<String> writeResponse = httpClient.send(writeRequest, HttpResponse.BodyHandlers.ofString());


            if(writeResponse.statusCode()!=204){
                throw new TSDBAdapterException("Can not write to Storage - "+writeResponse.statusCode()+" invalid response code - "+getConnectionInfo());
            }

            log.info("Wrote Data - "+getConnectionInfo()+" - "+record.toString());

        } catch (IOException |InterruptedException e) {
            throw new TSDBAdapterException(e);
        }


    }

    ////
    private String[] getSeriesByLabelKV(final String key, final String value) throws TSDBAdapterException {

        final String requestBody = "match[]={__name__=~\".+\", "+key+"=\""+value+"\"}";

        try {

            final String encodedRequestBody = URLEncoder.encode(requestBody, "UTF-8");

            // curl -XGET 'http://localhost:8428/api/v1/label/__name__/values' --data-urlencode 'match[]={__name__=~".+", run="victoria"}'

            final HttpRequest request = HttpRequest.newBuilder(labelUri)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .method("GET", HttpRequest.BodyPublishers.ofString(encodedRequestBody))
                    .build();

            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode()!=200){
                throw new TSDBAdapterException("Can not export Series - "+response.statusCode()+" invalid response code - "+getConnectionInfo());
            }

            final JSONObject jObject = new JSONObject(response.body());

            final JSONArray seriesArray = jObject.getJSONArray("data");

            final ArrayList<String> seriesList = new ArrayList<>();
            for(int i = 0; i < seriesArray.length(); i++){

                seriesList.add(seriesArray.getString(i));


            }

            return seriesList.toArray(new String[seriesList.size()]);


        } catch (IOException | InterruptedException e) {
            throw new TSDBAdapterException(e);
        }

    }

    private void deleteSeries(final String seriesName) throws TSDBAdapterException {

        try {

            final URI deleteUri = URI.create(config.getVictoriaMetricsUrl()+ String.format(DELETE_ENDPOINT, seriesName));

            // curl -s 'http://localhost:8428/api/v1/admin/tsdb/delete_series?match[]=victoria_measurement_PEp'

            final HttpRequest request =HttpRequest.newBuilder()
                    .uri(deleteUri).build();

            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode()!=204){
                throw new TSDBAdapterException("Can not delete Series "+seriesName+" - "+response.statusCode()+" invalid response code - "+getConnectionInfo());
            }

            log.info("Deleted series: "+seriesName+" - "+getConnectionInfo());

        } catch (IOException | InterruptedException e) {
            throw new TSDBAdapterException(e);
        }

    }

    private String getConnectionInfo() {

        final StringBuffer infoBuffer = new StringBuffer();

        if(config.getVictoriaMetricsUrl()!=null){
            infoBuffer.append(config.getVictoriaMetricsUrl());
        }

        return infoBuffer.toString();
    }
    ////



}
