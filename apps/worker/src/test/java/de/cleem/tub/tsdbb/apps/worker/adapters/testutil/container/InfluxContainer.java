package de.cleem.tub.tsdbb.apps.worker.adapters.testutil.container;

import de.cleem.tub.tsdbb.apps.worker.adapters.testutil.base.BaseContainerFactory;
import org.testcontainers.containers.GenericContainer;

import java.util.HashMap;

public class InfluxContainer{

    private static final String DOCKER_IMAGE = "influxdb:2.0.7";
    private static final int DOCKER_PORT = 8086;
    private static final String INIT_BUCKET_NAME = "init_testbucket";

    public static GenericContainer getContainer(final String token, final String orgName){

        final HashMap<String, String> dockerEnvVars = new HashMap<>();
        dockerEnvVars.put("DOCKER_INFLUXDB_INIT_MODE", "setup");
        dockerEnvVars.put("DOCKER_INFLUXDB_INIT_USERNAME", "admin");
        dockerEnvVars.put("DOCKER_INFLUXDB_INIT_PASSWORD", "adminadmin");
        dockerEnvVars.put("DOCKER_INFLUXDB_INIT_ORG", orgName);
        dockerEnvVars.put("DOCKER_INFLUXDB_INIT_BUCKET", INIT_BUCKET_NAME);
        dockerEnvVars.put("DOCKER_INFLUXDB_INIT_RETENTION", "0");
        dockerEnvVars.put("DOCKER_INFLUXDB_INIT_ADMIN_TOKEN", token);

        return BaseContainerFactory.getContainer(DOCKER_IMAGE,DOCKER_PORT, dockerEnvVars);
    }
}
