package de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.testutil.base;

import de.cleem.tub.tsdbb.api.model.WorkerGeneralProperties;
import de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.InfluxDbAdapter;
import de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.testutil.container.InfluxContainer;
import de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.testutil.container.VictoriaContainerFactory;
import de.cleem.tub.tsdbb.apps.worker.adapters.victoriametrics.VictoriaMetricsAdapter;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.GenericContainer;

import java.util.HashMap;

@Slf4j
public class BaseContainerTest extends BaseAdapterTest {

    protected WorkerGeneralProperties.TsdbTypeEnum tsdbTypeEnum;
    protected GenericContainer container;
    protected int containerPort;
    private HashMap<String,String> customProperties = null;
    private String token = null;
    private String orgName = null;

    public void startTest(final Class adapterClass) {

        if(adapterClass==InfluxDbAdapter.class){

            token = "s-kr1M8PTAYyylFNE8KJ2bG2foRf0c2_c9ECxOT1VbMeLK15bBT9xsUCglGKqcVSEZBPqrtKdVR5IpcA1L-aTA==";
            orgName = "testorg";

            customProperties = new HashMap<>();
            customProperties.put("organisationName", orgName);
            customProperties.put("bucketName", "testbucket");

        }

        setUpContainer(adapterClass, token);
        startContainer();
        setUpAdapter(adapterClass,containerPort,token, customProperties);


    }
    private void setUpContainer(final Class adapterClass, final String token) {

        log.info("Setting up Container: "+adapterClass);

        if(adapterClass == InfluxDbAdapter.class){
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
        containerPort=container.getFirstMappedPort();
    }
    private void stopContainer() {
        log.info("Stopping container");
        container.stop();
    }
    public void endTest(){
        tearDownAdapter();
        stopContainer();
    }

}