package de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.base.adapter;

import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.apps.worker.adapters.InsertResponse;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterException;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterIF;
import de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.base.container.BaseContainerTest;
import de.cleem.tub.tsdbb.commons.examplejson.ExampleDataGenerator;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseTsdbAdapterTest extends BaseContainerTest {

    private static final String URL_PATTERN = "http://localhost:%s";
    private final HashMap<String, String> customProperties;
    private final WorkerGeneralProperties.TsdbTypeEnum tsdbType;
    private final String token;
    private final Long postInsertSleep;
    protected TSDBAdapterIF tsdbAdapter;
    protected WorkerSetupRequest workerSetupRequest;
    protected WorkerTsdbEndpoint tsdbEndpoint;

    public BaseTsdbAdapterTest(final String dockerImage, final HashMap<String, String> dockerEnvVars, final int dockerPort, final WorkerGeneralProperties.TsdbTypeEnum tsdbType, final String token, final HashMap<String, String> customProperties, final Long postInsertSleep) {
        super(dockerImage, dockerPort, dockerEnvVars);
        this.customProperties = customProperties;
        this.tsdbType = tsdbType;
        this.token = token;
        this.postInsertSleep = postInsertSleep;
    }


    protected void setupAdapter(final TSDBAdapterIF tsdbAdapter) throws TSDBAdapterException {
        this.workerSetupRequest = getWorkerSetupRequest();
        this.tsdbEndpoint = getWorkerTsdbEndpoint();
        this.tsdbAdapter = tsdbAdapter;

        tsdbAdapter.setup(workerSetupRequest);
        tsdbAdapter.createStorage(tsdbEndpoint);
    }

    protected void tearDownAdapter() {
        tsdbAdapter = null;
    }

    public WorkerTsdbEndpoint getWorkerTsdbEndpoint() {
        final WorkerTsdbEndpoint workerTsdbEndpoint = new WorkerTsdbEndpoint();
        workerTsdbEndpoint.setTsdbUrl(URI.create(String.format(URL_PATTERN, container.getFirstMappedPort())));

        if (token != null) {
            workerTsdbEndpoint.setTsdbToken(token);
        }

        return workerTsdbEndpoint;
    }

    public WorkerSetupRequest getWorkerSetupRequest() {


        final WorkerSetupRequest workerSetupRequest = new WorkerSetupRequest();

        final WorkerConfiguration workerConfiguration = new WorkerConfiguration();
        workerSetupRequest.setWorkerConfiguration(workerConfiguration);

        workerConfiguration.setTsdbEndpoints(List.of(getWorkerTsdbEndpoint()));

        final WorkerGeneralProperties workerGeneralProperties = new WorkerGeneralProperties();
        workerGeneralProperties.setTsdbType(tsdbType);

        workerGeneralProperties.setCustomProperties(customProperties);

        workerSetupRequest.setWorkerGeneralProperties(workerGeneralProperties);

        return workerSetupRequest;

    }

    public List<InsertResponse> writeInserts(final int count) throws TSDBAdapterException {

        final List<InsertResponse> responses = new ArrayList<>();

        final WorkerTsdbEndpoint workerTsdbEndpoint = getWorkerTsdbEndpoint();

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
