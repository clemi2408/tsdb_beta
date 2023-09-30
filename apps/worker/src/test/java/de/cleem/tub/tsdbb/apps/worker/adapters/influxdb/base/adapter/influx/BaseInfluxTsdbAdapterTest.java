package de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.base.adapter.influx;

import de.cleem.tub.tsdbb.api.model.Insert;
import de.cleem.tub.tsdbb.api.model.WorkerGeneralProperties.TsdbTypeEnum;
import de.cleem.tub.tsdbb.api.model.WorkerSetupRequest;
import de.cleem.tub.tsdbb.api.model.WorkerTsdbEndpoint;
import de.cleem.tub.tsdbb.apps.worker.adapters.InsertResponse;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterException;
import de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.InfluxDbAdapter;
import de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.base.adapter.BaseTsdbAdapterTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseInfluxTsdbAdapterTest extends BaseTsdbAdapterTest {

    private static final String DOCKER_IMAGE = "influxdb:2.0.7";
    private static final HashMap<String,String> DOCKER_ENV_VARS = new HashMap<>();
    private static final int DOCKER_PORT = 8086;
    private static final String TOKEN = "s-kr1M8PTAYyylFNE8KJ2bG2foRf0c2_c9ECxOT1VbMeLK15bBT9xsUCglGKqcVSEZBPqrtKdVR5IpcA1L-aTA==";
    private static final String ORG_NAME="testorg";
    private static final String BUCKET_NAME="testbucket";
    private static final String INIT_BUCKET_NAME="init_testbucket";


    private static final HashMap<String,String> TSDB_CUSTOM_PROPERTIES = new HashMap<>();

    static{
        TSDB_CUSTOM_PROPERTIES.put("bucketName", BUCKET_NAME);
        TSDB_CUSTOM_PROPERTIES.put("organisationName",ORG_NAME);

        DOCKER_ENV_VARS.put("DOCKER_INFLUXDB_INIT_MODE","setup");
        DOCKER_ENV_VARS.put("DOCKER_INFLUXDB_INIT_USERNAME","admin");
        DOCKER_ENV_VARS.put("DOCKER_INFLUXDB_INIT_PASSWORD","adminadmin");
        DOCKER_ENV_VARS.put("DOCKER_INFLUXDB_INIT_ORG",ORG_NAME);
        DOCKER_ENV_VARS.put("DOCKER_INFLUXDB_INIT_BUCKET",INIT_BUCKET_NAME);
        DOCKER_ENV_VARS.put("DOCKER_INFLUXDB_INIT_RETENTION","0");
        DOCKER_ENV_VARS.put("DOCKER_INFLUXDB_INIT_ADMIN_TOKEN", TOKEN);
    }

    protected WorkerSetupRequest workerSetupRequest;
    protected WorkerTsdbEndpoint tsdbEndpoint;

    public BaseInfluxTsdbAdapterTest() {
        super(DOCKER_IMAGE, DOCKER_ENV_VARS,DOCKER_PORT,TsdbTypeEnum.INFLUX, TOKEN, TSDB_CUSTOM_PROPERTIES);
    }


    @BeforeEach
    protected void setUp() throws TSDBAdapterException {
        setUpContainer();
        workerSetupRequest = getWorkerSetupRequest();
        tsdbEndpoint = getWorkerTsdbEndpoint();
        tsdbAdapter = new InfluxDbAdapter();
        tsdbAdapter.setup(workerSetupRequest);
        tsdbAdapter.createStorage(tsdbEndpoint);
    }

    @AfterEach
    protected void tearDown() {
        tearDownContainer();
        tsdbAdapter=null;
    }

}