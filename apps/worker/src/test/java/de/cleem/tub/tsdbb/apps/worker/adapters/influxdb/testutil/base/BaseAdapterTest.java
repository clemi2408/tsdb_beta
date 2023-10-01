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

    private static final String URL_PATTERN = "http://localhost:%s";
    //private final HashMap<String, String> customProperties;
    protected TSDBAdapterIF tsdbAdapter;
    protected WorkerSetupRequest workerSetupRequest;
    protected WorkerTsdbEndpoint tsdbEndpoint;
    public void setUpAdapter(final Class adapterClass,final int containerPort, final String token, final HashMap<String,String> customProperties) {

        try {
            log.info("Setting up Adapter: "+adapterClass);
            this.tsdbAdapter = (TSDBAdapterIF) adapterClass.getDeclaredConstructor().newInstance();

            WorkerGeneralProperties.TsdbTypeEnum type = null;
            if(adapterClass == InfluxDbAdapter.class){
                type= WorkerGeneralProperties.TsdbTypeEnum.INFLUX;

            }
            else if(adapterClass == VictoriaMetricsAdapter.class){
                type= WorkerGeneralProperties.TsdbTypeEnum.VICTORIA;
            }

            this.workerSetupRequest = getWorkerSetupRequest(containerPort,token,type,customProperties);
            this.tsdbEndpoint = getWorkerTsdbEndpoint(containerPort,token);

            tsdbAdapter.setup(workerSetupRequest);
            tsdbAdapter.createStorage(tsdbEndpoint);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    protected void tearDownAdapter() {
        tsdbAdapter = null;
    }
    private WorkerTsdbEndpoint getWorkerTsdbEndpoint(final int containerPort, final String token) {
        final WorkerTsdbEndpoint workerTsdbEndpoint = new WorkerTsdbEndpoint();
        workerTsdbEndpoint.setTsdbUrl(URI.create(String.format(URL_PATTERN, containerPort)));

        if (token != null) {
            workerTsdbEndpoint.setTsdbToken(token);
        }

        return workerTsdbEndpoint;
    }
    private WorkerSetupRequest getWorkerSetupRequest(final int containerPort, final String token, final WorkerGeneralProperties.TsdbTypeEnum tsdbType, final HashMap<String,String> customProperties) {

        final WorkerSetupRequest workerSetupRequest = new WorkerSetupRequest();

        final WorkerConfiguration workerConfiguration = new WorkerConfiguration();
        workerSetupRequest.setWorkerConfiguration(workerConfiguration);

        workerConfiguration.setTsdbEndpoints(List.of(getWorkerTsdbEndpoint(containerPort,token)));

        final WorkerGeneralProperties workerGeneralProperties = new WorkerGeneralProperties();
        workerGeneralProperties.setTsdbType(tsdbType);

        workerGeneralProperties.setCustomProperties(customProperties);

        workerSetupRequest.setWorkerGeneralProperties(workerGeneralProperties);

        return workerSetupRequest;

    }
    public List<InsertResponse> writeInserts(final int containerPort, final int count, final Long postInsertSleep, final String token) throws TSDBAdapterException {

        final List<InsertResponse> responses = new ArrayList<>();

        final WorkerTsdbEndpoint workerTsdbEndpoint = getWorkerTsdbEndpoint(containerPort,token);

        for (int i = 0; i < count; i++) {

            responses.add(tsdbAdapter.write(getInsert(), workerTsdbEndpoint));

        }

        if (postInsertSleep != null) {
            try {
                Thread.sleep(postInsertSleep);
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
        final Select select = new Select();
        select.setStartValue("-1d");
        select.setLabelName("run");
        return select;
    }


}
