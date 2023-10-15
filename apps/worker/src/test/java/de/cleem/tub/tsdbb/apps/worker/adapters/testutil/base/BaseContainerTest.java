package de.cleem.tub.tsdbb.apps.worker.adapters.testutil.base;

import de.cleem.tub.tsdbb.api.model.WorkerGeneralProperties;
import de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.InfluxDbAdapter;
import de.cleem.tub.tsdbb.apps.worker.adapters.testutil.container.InfluxContainer;
import de.cleem.tub.tsdbb.apps.worker.adapters.testutil.container.VictoriaContainerFactory;
import de.cleem.tub.tsdbb.apps.worker.adapters.victoriametrics.VictoriaMetricsAdapter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.testcontainers.containers.GenericContainer;

import java.util.HashMap;

@Slf4j
public class BaseContainerTest extends BaseAdapterTest {

    protected WorkerGeneralProperties.TsdbTypeEnum tsdbTypeEnum;
    protected GenericContainer<?> container;

    public void initialize(final Class<?> adapterClass) {

        this.adapterClass = adapterClass;

        setUpContainer();
        startContainer();
        setUpAdapter();

    }
    private void setUpContainer() {

        log.info("Setting up Container: "+this.adapterClass);

        if(adapterClass == InfluxDbAdapter.class){

            token = "s-kr1M8PTAYyylFNE8KJ2bG2foRf0c2_c9ECxOT1VbMeLK15bBT9xsUCglGKqcVSEZBPqrtKdVR5IpcA1L-aTA==";
            final String orgName = "testorg";

            customDatabaseProperties = new HashMap<>();
            customDatabaseProperties.put("organisationName", orgName);
            customDatabaseProperties.put("bucketName", "testbucket");

            tsdbTypeEnum= WorkerGeneralProperties.TsdbTypeEnum.INFLUX;
            container= InfluxContainer.getContainer(token,orgName);

        }
        else if(adapterClass == VictoriaMetricsAdapter.class){
            tsdbTypeEnum= WorkerGeneralProperties.TsdbTypeEnum.VICTORIA;
            container= VictoriaContainerFactory.getContainer();
        }


    }
    private void startContainer() {
        log.info("Starting container");
        container.start();
        databasePort =container.getFirstMappedPort();
    }
    private void stopContainer() {
        log.info("Stopping container");
        container.stop();
    }

    @AfterEach
    public void finishTest(){
        log.info("Finishing test");
        tearDownAdapter();
        stopContainer();
    }

}