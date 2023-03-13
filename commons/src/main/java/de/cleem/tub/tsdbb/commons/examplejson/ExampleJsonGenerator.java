package de.cleem.tub.tsdbb.apps.worker;

import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.commons.file.FileException;
import de.cleem.tub.tsdbb.commons.file.FileHelper;
import de.cleem.tub.tsdbb.commons.json.JsonException;
import de.cleem.tub.tsdbb.commons.json.JsonHelper;

import java.io.File;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class main {

    private static final String TARGET_FOLDER_STRING = "/Users/clemens/IdeaProjects/tsdb_beta/schema/src/main/resources/api/model/examples/generated";
    private static final String WORKLOAD_FILE_STRING = "/Users/clemens/IdeaProjects/tsdb_beta/schema/src/main/resources/api/model/examples/workload_1.json";
    private static final String ORCHESTRATOR_URL = "http://localhost:8081";
    private static final String GENERATOR_URL = "http://localhost:8080";


    public static <T> void write(final T instance, final int index) throws JsonException, FileException {

        final String fileName = instance.getClass().getSimpleName()+"_"+index+".json";

        final File outputFile = new File(TARGET_FOLDER_STRING +"/"+fileName);

        FileHelper.write(outputFile,JsonHelper.toByteArray(instance,true));

    }

    public static void main(String[] args) throws FileException, JsonException {


        final WorkerConfig influxWorkerConfig = createWorkerConfig(createInfluxConnection());
        write(influxWorkerConfig,1);

        final WorkerConfig victoriaWorkerConfig = createWorkerConfig(createVictoriaConnection());
        write(victoriaWorkerConfig,2);

        final GeneratorGenerateRequest generatorGenerateRequest = createGenerateRequest();
        write(generatorGenerateRequest,1);


        final OrchestratorPreloadRequest orchestratorPreloadRequestInflux = getOrchestratorPreloadRequest(
                createWorkerConfig(createInfluxConnection()),
                createGenerateRequest()
        );
        write(orchestratorPreloadRequestInflux,1);


        final OrchestratorPreloadRequest orchestratorPreloadRequestVictoria = getOrchestratorPreloadRequest(
                createWorkerConfig(createVictoriaConnection()),
                createGenerateRequest()
        );
        write(orchestratorPreloadRequestVictoria,2);

        final Workload workload = JsonHelper.objectFromByteArray(FileHelper.read(new File(WORKLOAD_FILE_STRING)),Workload.class);

        final WorkerPreloadRequest workerPreloadRequestVictoria = getWorkerPreloadRequest(createWorkerConfig(createVictoriaConnection()),workload);
        write(workerPreloadRequestVictoria,1);

        final WorkerPreloadRequest workerPreloadRequestInflux = getWorkerPreloadRequest(createWorkerConfig(createInfluxConnection()),workload);
        write(workerPreloadRequestInflux,2);


    }

    private static OrchestratorPreloadRequest getOrchestratorPreloadRequest(final WorkerConfig workerConfig,final GeneratorGenerateRequest generatorGenerateRequest){

        final OrchestratorPreloadRequest orchestratorPreloadRequest = new OrchestratorPreloadRequest();
        orchestratorPreloadRequest.setWorkerConfigs(List.of(workerConfig));
        orchestratorPreloadRequest.setGenerateRequest(generatorGenerateRequest);
        orchestratorPreloadRequest.setGeneratorUrl(GENERATOR_URL);

        return orchestratorPreloadRequest;
    }

    private static WorkerPreloadRequest getWorkerPreloadRequest(final WorkerConfig workerConfig, final Workload workload){

        final WorkerPreloadRequest workerPreloadRequest = new WorkerPreloadRequest();
        workerPreloadRequest.setWorkerConfig(workerConfig);
        workerPreloadRequest.setOrchestratorUrl(ORCHESTRATOR_URL);
        workerPreloadRequest.setBenchmarkWorkload(workload);

        return workerPreloadRequest;

    }


    private static GeneratorRecordConfig dynamicKeyWithDynamicStringValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType("String");
        generatorRecordConfig.setMinKeyLength(1);
        generatorRecordConfig.setMaxKeyLength(1);
        generatorRecordConfig.setMinStringValueLength(1);
        generatorRecordConfig.setMaxStringValueLength(3);

        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig staticKeyWithDynamicStringValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setKeyValue("staticKeyDynStringValue");
        generatorRecordConfig.setValueType("String");
        generatorRecordConfig.setMinStringValueLength(3);
        generatorRecordConfig.setMaxStringValueLength(5);

        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig dynamicKeyWithDynamicStringEnumCountAndDynamicValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType("String");
        generatorRecordConfig.setMinKeyLength(6);
        generatorRecordConfig.setMaxKeyLength(8);
        generatorRecordConfig.setMinStringValueLength(1);
        generatorRecordConfig.setMaxStringValueLength(3);
        generatorRecordConfig.setMinStringEnumValues(1);
        generatorRecordConfig.setMaxStringEnumValues(5);

        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig staticKeyWithDynamicStringEnumCountAndDynamicValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType("String");
        generatorRecordConfig.setKeyValue("staticKeyDynStringEnumDynValue");
        generatorRecordConfig.setMinStringValueLength(1);
        generatorRecordConfig.setMaxStringValueLength(3);
        generatorRecordConfig.setMinStringEnumValues(1);
        generatorRecordConfig.setMaxStringEnumValues(5);

        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig dynamicKeyWithStaticStringEnumValues(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType("String");
        generatorRecordConfig.setMinKeyLength(1);
        generatorRecordConfig.setMaxKeyLength(1);
        generatorRecordConfig.setStringEnumValues(List.of("s-val1","s-val2"));
        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig staticKeyWithStaticStringEnumValues(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType("String");
        generatorRecordConfig.setKeyValue("staticKeyStaticStringEnumValues");
        generatorRecordConfig.setStringEnumValues(List.of("s-val1","s-val2"));
        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig dynamicKeyWithUniformIntegerValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType("Integer");
        generatorRecordConfig.minKeyLength(2);
        generatorRecordConfig.maxKeyLength(5);
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.UNIFORM);
        generatorRecordConfig.setMinValue(BigDecimal.valueOf(0));
        generatorRecordConfig.setMaxValue(BigDecimal.valueOf(100));

        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig staticKeyWithUniformIntegerValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType("Integer");
        generatorRecordConfig.setKeyValue("staticKeyUniformIntegerValue");
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.UNIFORM);
        generatorRecordConfig.setMinValue(BigDecimal.valueOf(0));
        generatorRecordConfig.setMaxValue(BigDecimal.valueOf(100));

        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig staticKeyWithUniformDoubleValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType("Double");
        generatorRecordConfig.setKeyValue("staticKeyUniformDoubleValue");
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.UNIFORM);
        generatorRecordConfig.setMinValue(BigDecimal.valueOf(5.5d));
        generatorRecordConfig.setMaxValue(BigDecimal.valueOf(55.3d));

        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig dynamicKeyWithTriangleIntegerValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType("Integer");
        generatorRecordConfig.minKeyLength(2);
        generatorRecordConfig.maxKeyLength(5);
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.TRIANGLE);
        generatorRecordConfig.setMinValue(BigDecimal.valueOf(0));
        generatorRecordConfig.setMaxValue(BigDecimal.valueOf(100));
        generatorRecordConfig.setTriangleSpike(BigDecimal.valueOf(50));

        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig staticKeyWithTriangleIntegerValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType("Integer");
        generatorRecordConfig.setKeyValue("staticKeyTriangleIntegerValue");
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.TRIANGLE);
        generatorRecordConfig.setMinValue(BigDecimal.valueOf(0));
        generatorRecordConfig.setMaxValue(BigDecimal.valueOf(100));
        generatorRecordConfig.setTriangleSpike(BigDecimal.valueOf(50));


        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig staticKeyWithTriangleDoubleValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType("Double");
        generatorRecordConfig.setKeyValue("staticKeyTriangleDoubleValue");
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.TRIANGLE);
        generatorRecordConfig.setMinValue(BigDecimal.valueOf(5.5d));
        generatorRecordConfig.setMaxValue(BigDecimal.valueOf(55.3d));
        generatorRecordConfig.setTriangleSpike(BigDecimal.valueOf(10));

        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig dynamicKeyWithGaussIntegerValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType("Integer");
        generatorRecordConfig.minKeyLength(2);
        generatorRecordConfig.maxKeyLength(5);
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.GAUSS);
        generatorRecordConfig.setGaussMiddle(BigDecimal.valueOf(10));
        generatorRecordConfig.setGaussRange(BigDecimal.valueOf(10));


        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig staticKeyWithGaussIntegerValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType("Integer");
        generatorRecordConfig.setKeyValue("staticKeyGaussIntegerValue");
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.GAUSS);
        generatorRecordConfig.setGaussMiddle(BigDecimal.valueOf(10));
        generatorRecordConfig.setGaussRange(BigDecimal.valueOf(10));


        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig staticKeyWithGaussDoubleValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType("Double");
        generatorRecordConfig.setKeyValue("staticKeyGaussDoubleValue");
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.GAUSS);
        generatorRecordConfig.setGaussMiddle(BigDecimal.valueOf(10));
        generatorRecordConfig.setGaussRange(BigDecimal.valueOf(10));

        return generatorRecordConfig;
    }


    private static GeneratorGenerateRequest createGenerateRequest(){

        final GeneratorGenerateRequest request = new GeneratorGenerateRequest();
        request.setRecordCount(10);

        final List<GeneratorRecordConfig> generatorRecordConfigList = new ArrayList<>();

        generatorRecordConfigList.add(dynamicKeyWithDynamicStringValue());
        generatorRecordConfigList.add(staticKeyWithDynamicStringValue());
        generatorRecordConfigList.add(dynamicKeyWithDynamicStringEnumCountAndDynamicValue());
        generatorRecordConfigList.add(staticKeyWithDynamicStringEnumCountAndDynamicValue());
        generatorRecordConfigList.add(dynamicKeyWithStaticStringEnumValues());
        generatorRecordConfigList.add(staticKeyWithStaticStringEnumValues());
        generatorRecordConfigList.add(dynamicKeyWithUniformIntegerValue());
        generatorRecordConfigList.add(staticKeyWithUniformIntegerValue());
        generatorRecordConfigList.add(staticKeyWithUniformDoubleValue());
        generatorRecordConfigList.add(dynamicKeyWithTriangleIntegerValue());
        generatorRecordConfigList.add(staticKeyWithTriangleIntegerValue());
        generatorRecordConfigList.add(staticKeyWithTriangleDoubleValue());
        generatorRecordConfigList.add(dynamicKeyWithGaussIntegerValue());
        generatorRecordConfigList.add(staticKeyWithGaussIntegerValue());
        generatorRecordConfigList.add(staticKeyWithGaussDoubleValue());


        request.setRecordConfigs(generatorRecordConfigList);

        return request;

    }


    private static WorkerConfig createWorkerConfig(final WorkerTsdbConnection tsdbConnection){

        final WorkerConfig workerConfig = new WorkerConfig();
        workerConfig.setWorkerThreads(3);
        workerConfig.setCleanupStorage(true);
        workerConfig.setCreateStorage(true);
        workerConfig.setWorkerUrl("http://localhost:8082");
        workerConfig.setTsdbConnection(tsdbConnection);

        return workerConfig;

    }

    private static WorkerTsdbConnection createInfluxConnection(){

        final WorkerTsdbConnection influxConnection = new WorkerTsdbConnection();
        influxConnection.setTsdbType(WorkerTsdbConnection.TsdbTypeEnum.INFLUX);
        influxConnection.setToken("YcqeMSEqzrvDsPevnsPA9fuES38RU-R9_y9UZ2gefEYaNJfVdUOVdj5NvrPpwNnmVuGoZSsqZ_jxnKx9dhdHYw==");
        influxConnection.setUrl(URI.create("http://127.0.0.1:8086"));
        influxConnection.setConnectionProperties(new HashMap<>());
        influxConnection.getConnectionProperties().put("bucketName","testbucket");
        influxConnection.getConnectionProperties().put("organisationName","testorg");


        return influxConnection;


    }

    private static WorkerTsdbConnection createVictoriaConnection(){

        final WorkerTsdbConnection influxConnection = new WorkerTsdbConnection();
        influxConnection.setTsdbType(WorkerTsdbConnection.TsdbTypeEnum.VICTORIA);
        influxConnection.setUrl(URI.create("http://127.0.0.1:8428"));
        return influxConnection;


    }


}
