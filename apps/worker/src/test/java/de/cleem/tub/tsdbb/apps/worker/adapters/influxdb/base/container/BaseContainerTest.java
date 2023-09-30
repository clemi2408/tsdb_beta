package de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.base.container;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;

@Slf4j
public class BaseContainerTest {

    protected GenericContainer container;
    protected final String dockerImage;
    protected final int dockerPort;
    protected final HashMap<String,String> envVariables;

    public BaseContainerTest(final String dockerImage, final int dockerPort, final HashMap<String,String> envVariables){
        this.dockerImage = dockerImage;
        this.dockerPort=dockerPort;
        this.envVariables =envVariables;
    }

    protected void setUpContainer() {
        log.info("Starting {} on port {} with env variables {}", dockerImage,dockerPort,envVariables);
        this.container=new GenericContainer(DockerImageName.parse(dockerImage));
        container.withExposedPorts(dockerPort);
        container.withEnv(envVariables);
        container.start();

    }

    protected void tearDownContainer() {
        log.info("Stopping {} on port {} with env variables {}", dockerImage,dockerPort,envVariables);
        container.stop();
    }


}
