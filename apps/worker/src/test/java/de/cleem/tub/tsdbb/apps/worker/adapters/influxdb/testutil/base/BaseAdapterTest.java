package de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.testutil.base;

import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.apps.worker.adapters.InsertResponse;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterException;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterIF;
import de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.InfluxDbAdapter;
import de.cleem.tub.tsdbb.apps.worker.adapters.victoriametrics.VictoriaMetricsAdapter;
import de.cleem.tub.tsdbb.commons.examplejson.ExampleDataGenerator;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class BaseAdapterTest {

    protected static final long DEFAULT_POST_INSERT_SLEEP_MS = 1000;
    protected static final int DEFAULT_INSERT_COUNT = 1000;
    protected static final String DEFAULT_START_VALUE = "-1d";
    protected static final String DEFAULT_LABEL_NAME = "run";
    protected static final String DEFAULT_FIELD_NAME = "field1";
    private static final String URL_PATTERN = "http://localhost:%s";
    private static final String DEFAULT_MEASUREMENT_SUFFIX = "_measurement";
    protected TSDBAdapterIF adapter;
    protected Class<?> adapterClass = null;
    protected WorkerGeneralProperties.TsdbTypeEnum type;
    protected WorkerSetupRequest workerSetupRequest;
    protected WorkerTsdbEndpoint endpoint;
    protected String token = null;
    protected int databasePort;
    protected HashMap<String, String> customDatabaseProperties = null;

    public void setUpAdapter() {

        try {

            log.info("Setting up Adapter: " + adapterClass);
            adapter = (TSDBAdapterIF) adapterClass.getDeclaredConstructor().newInstance();

            if (adapterClass == InfluxDbAdapter.class) {
                type = WorkerGeneralProperties.TsdbTypeEnum.INFLUX;

            } else if (adapterClass == VictoriaMetricsAdapter.class) {
                type = WorkerGeneralProperties.TsdbTypeEnum.VICTORIA;
            }

            this.workerSetupRequest = getWorkerSetupRequest(databasePort, token, type, customDatabaseProperties);
            this.endpoint = getWorkerTsdbEndpoint(databasePort, token);

            adapter.setup(workerSetupRequest);
            adapter.createStorage(endpoint);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void tearDownAdapter() {
        log.info("Tearing down Adapter: " + adapterClass);
        adapter = null;
    }

    private WorkerTsdbEndpoint getWorkerTsdbEndpoint(final int containerPort, final String token) {
        final WorkerTsdbEndpoint workerTsdbEndpoint = new WorkerTsdbEndpoint();
        workerTsdbEndpoint.setTsdbUrl(URI.create(String.format(URL_PATTERN, containerPort)));

        if (token != null) {
            workerTsdbEndpoint.setTsdbToken(token);
        }

        log.info("Using Worker endpoint: " + workerTsdbEndpoint.getTsdbUrl());

        return workerTsdbEndpoint;
    }

    private WorkerSetupRequest getWorkerSetupRequest(final int containerPort, final String token, final WorkerGeneralProperties.TsdbTypeEnum tsdbType, final HashMap<String, String> customProperties) {

        final WorkerSetupRequest workerSetupRequest = new WorkerSetupRequest();

        final WorkerConfiguration workerConfiguration = new WorkerConfiguration();
        workerSetupRequest.setWorkerConfiguration(workerConfiguration);

        workerConfiguration.setTsdbEndpoints(List.of(getWorkerTsdbEndpoint(containerPort, token)));

        final WorkerGeneralProperties workerGeneralProperties = new WorkerGeneralProperties();
        workerGeneralProperties.setTsdbType(tsdbType);

        workerGeneralProperties.setCustomProperties(customProperties);

        workerSetupRequest.setWorkerGeneralProperties(workerGeneralProperties);

        return workerSetupRequest;

    }

    public List<InsertResponse> insert() throws TSDBAdapterException {
        return insert(DEFAULT_INSERT_COUNT, DEFAULT_POST_INSERT_SLEEP_MS);
    }

    public void sleep(long ms) {
        final int sleepSeconds = (int) (ms/1000);

        log.info("Sleeping " + sleepSeconds + "s (" + ms + "ms)");
        try {

            for(int i = 0; i < sleepSeconds; i++){

                if(i%10==0){
                    log.info("Sleeping " + (sleepSeconds-i)+"s");
                }

                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            log.error("Error sleeping " + (ms / 1000) + "s (" + ms + "ms)");
            throw new RuntimeException(e);
        }

    }

    public List<InsertResponse> insert(final int count, final Long postInsertSleepMs) throws TSDBAdapterException {

        final List<InsertResponse> responses = new ArrayList<>();

        final WorkerTsdbEndpoint workerTsdbEndpoint = getWorkerTsdbEndpoint(databasePort, token);

        for (int i = 0; i < count; i++) {

            responses.add(adapter.write(getInsert(), workerTsdbEndpoint));

        }

        if (postInsertSleepMs != null) {
            try {
                Thread.sleep(postInsertSleepMs);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return responses;

    }

    public Insert getInsert() {
        return ExampleDataGenerator.getInsert();
    }

    public Select getSelect() {
        return new Select().startValue(DEFAULT_START_VALUE);
    }

    public String getMeasurementName() {

        return type.getValue().concat(DEFAULT_MEASUREMENT_SUFFIX);

    }






}
