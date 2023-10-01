package de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.testutil.container;

import de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.testutil.base.BaseContainerFactory;
import org.testcontainers.containers.GenericContainer;

public class VictoriaContainerFactory extends BaseContainerFactory {

    private static final String DOCKER_IMAGE = "victoriametrics/victoria-metrics:v1.93.5";
    private static final int DOCKER_PORT = 8428;

    public static GenericContainer getContainer(){

        return BaseContainerFactory.getContainer(DOCKER_IMAGE,DOCKER_PORT, null);

    }
}
