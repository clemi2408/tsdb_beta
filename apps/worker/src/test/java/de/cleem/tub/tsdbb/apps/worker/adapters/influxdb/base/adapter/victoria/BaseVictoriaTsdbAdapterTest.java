package de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.base.adapter.victoria;

import de.cleem.tub.tsdbb.api.model.WorkerGeneralProperties.TsdbTypeEnum;
import de.cleem.tub.tsdbb.api.model.WorkerSetupRequest;
import de.cleem.tub.tsdbb.api.model.WorkerTsdbEndpoint;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterException;
import de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.base.adapter.BaseTsdbAdapterTest;
import de.cleem.tub.tsdbb.apps.worker.adapters.victoriametrics.VictoriaMetricsAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;

public class BaseVictoriaTsdbAdapterTest extends BaseTsdbAdapterTest {

    private static final String DOCKER_IMAGE = "victoriametrics/victoria-metrics:v1.93.5";
    private static final int DOCKER_PORT = 8428;

    public BaseVictoriaTsdbAdapterTest() {
        super(DOCKER_IMAGE, null, DOCKER_PORT, TsdbTypeEnum.VICTORIA, null, null,1000l);
    }

    @BeforeEach
    protected void setUp() throws TSDBAdapterException {
        setUpContainer();
        setupAdapter(new VictoriaMetricsAdapter());
    }

    @AfterEach
    protected void tearDown() {
        tearDownAdapter();
        tearDownContainer();
    }

}