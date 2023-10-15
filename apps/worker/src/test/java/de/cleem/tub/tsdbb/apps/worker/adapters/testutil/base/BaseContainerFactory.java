package de.cleem.tub.tsdbb.apps.worker.adapters.testutil.base;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;

@Slf4j
public class BaseContainerFactory {

    protected GenericContainer container;

    public static GenericContainer getContainer(final String dockerImage, final int dockerPort, final HashMap<String, String> envVariables) {

        final GenericContainer container = new GenericContainer(DockerImageName.parse(dockerImage));
        container.withExposedPorts(dockerPort);

        if(envVariables!=null){
            container.withEnv(envVariables);
        }

        return container;

    }

}
