package de.cleem.tub.tsdbb.commons.examplejson;

import de.cleem.tub.tsdbb.api.model.Record;
import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.commons.file.FileException;
import de.cleem.tub.tsdbb.commons.file.FileHelper;
import de.cleem.tub.tsdbb.commons.json.JsonException;
import de.cleem.tub.tsdbb.commons.json.JsonHelper;

import java.io.File;
import java.math.BigDecimal;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ExampleJsonGenerator {

    private static final String TARGET_FOLDER_STRING = "/Users/kuenzelc/DEV/workspaces/priv/tsdb_beta/schema/src/main/resources/api/model/examples/generated";
    private static final String GENERATED_WORKLOAD_FILE_STRING = "/Users/kuenzelc/DEV/workspaces/priv/tsdb_beta/schema/src/main/resources/api/model/examples/manual/workload_generated.json";
    private static final String PREPARED_WORKLOAD_FILE_STRING = "/Users/kuenzelc/DEV/workspaces/priv/tsdb_beta/schema/src/main/resources/api/model/examples/manual/workload_prepared.json";

    private static final URI ORCHESTRATOR_URL = URI.create("http://localhost:8081");
    private static final URI GENERATOR_URL = URI.create("http://localhost:8080");


    public static <T> void write(final T instance, final int index) throws JsonException, FileException {

        final String fileName = instance.getClass().getSimpleName()+"_"+index+".json";

        final File outputFile = new File(TARGET_FOLDER_STRING +"/"+fileName);

        FileHelper.write(outputFile,JsonHelper.toByteArray(instance,true));

    }

    public static void main(String[] args) throws FileException, JsonException {

        final Workload generatedWorkload = JsonHelper.objectFromByteArray(FileHelper.read(new File(GENERATED_WORKLOAD_FILE_STRING)),Workload.class);
        final Workload preparedWorkload = JsonHelper.objectFromByteArray(FileHelper.read(new File(PREPARED_WORKLOAD_FILE_STRING)),Workload.class);

        final WorkerGeneralProperties workerGeneralPropertiesVictoria = getVictoriaGeneralProperties();
        final WorkerConfiguration workerConfigurationVictoria = getVictoriaWorkerConfiguration();
        final WorkerSetupRequest workerSetupRequestVictoria = getWorkerSetupRequest(workerGeneralPropertiesVictoria, workerConfigurationVictoria,generatedWorkload);
        write(workerSetupRequestVictoria,1);


        final WorkerGeneralProperties workerGeneralPropertiesInflux = getInfluxGeneralProperties();
        final WorkerConfiguration workerConfigurationInflux = getInfluxWorkerConfiguration();
        final WorkerSetupRequest workerSetupRequestInflux = getWorkerSetupRequest(workerGeneralPropertiesInflux,workerConfigurationInflux,generatedWorkload);
        write(workerSetupRequestInflux,2);


        write(workerGeneralPropertiesVictoria,1);
        write(workerGeneralPropertiesInflux,2);


        write(workerConfigurationVictoria,1);
        write(workerConfigurationInflux,2);

        write(createGenerateRequest(),1);


        final OrchestratorSetupRequest orchestratorSetupRequestGeneratorInflux = getOrchestratorSetupRequest(
                createConnection(workerGeneralPropertiesInflux,workerConfigurationInflux),
                null,
                createGenerateRequest(),
                GENERATOR_URL);

        write(orchestratorSetupRequestGeneratorInflux,1);


        final OrchestratorSetupRequest orchestratorSetupRequestGeneratorVictoria = getOrchestratorSetupRequest(
                createConnection(workerGeneralPropertiesVictoria,workerConfigurationVictoria),
                null,
                createGenerateRequest(),
                GENERATOR_URL);

        write(orchestratorSetupRequestGeneratorVictoria,2);

        final OrchestratorSetupRequest orchestratorSetupRequestWorkloadInflux = getOrchestratorSetupRequest(
                createConnection(workerGeneralPropertiesInflux,workerConfigurationInflux),
                preparedWorkload,
                null,null);

        write(orchestratorSetupRequestWorkloadInflux,3);


        final OrchestratorSetupRequest orchestratorSetupRequestWorkloadVictoria = getOrchestratorSetupRequest(
                createConnection(workerGeneralPropertiesVictoria,workerConfigurationVictoria),
                preparedWorkload,
                null,null);


        write(orchestratorSetupRequestWorkloadVictoria,4);

        write(getPingResponse(),1);

        write(getSourceInformation(),1);

        write(getResetResponse(false),1);

        write(getResetResponse(true),2);

        write(getTimeFrame(),1);

        write(dynamicKeyWithDynamicStringValue(),1);
        write(staticKeyWithDynamicStringValue(),2);
        write(dynamicKeyWithDynamicStringEnumCountAndDynamicValue(),3);
        write(staticKeyWithDynamicStringEnumCountAndDynamicValue(),4);
        write(dynamicKeyWithStaticStringEnumValues(),5);
        write(staticKeyWithStaticStringEnumValues(),6);
        write(dynamicKeyWithUniformIntegerValue(),7);
        write(staticKeyWithUniformIntegerValue(),8);
        write(staticKeyWithUniformDoubleValue(),9);
        write(dynamicKeyWithTriangleIntegerValue(),10);
        write(staticKeyWithTriangleIntegerValue(),11);
        write(staticKeyWithTriangleDoubleValue(),12);
        write(dynamicKeyWithGaussIntegerValue(),13);
        write(staticKeyWithGaussIntegerValue(),14);
        write(staticKeyWithGaussDoubleValue(),15);

        write(getKvPair(),1);

        write(getRecord(),1);

        write(getTaskResult(),1);

        write(getBenchmarkResultRequest(),1);

        write(getBenchmarkResultResponse(),1);

    }

    private static BenchmarkResultResponse getBenchmarkResultResponse(){

        final BenchmarkResultResponse resultResponse = new BenchmarkResultResponse();
        resultResponse.setDeliveryCount(BigDecimal.valueOf(10));

        return resultResponse;

    }

    private static KvPair getKvPair(){

        final KvPair kvPair = new KvPair();
        kvPair.setKey("key");
        kvPair.setValue(List.of("value"));

        return kvPair;


    }

    private static List<KvPair> getKvPairs(){

        final List<KvPair> kvPairs = new ArrayList<>();
        kvPairs.add(getKvPair());
        kvPairs.add(getKvPair());
        return kvPairs;

    }

    private static Record getRecord(){
        final Record record = new Record();
        record.setRecordId(UUID.randomUUID());
        record.setKvPairs(getKvPairs());

        return record;
    }

    private static TaskResult getTaskResult(){

        final TaskResult taskResult = new TaskResult();
        taskResult.setTaskName("taskName");
        taskResult.setDurationInMs(BigDecimal.valueOf(100));
        taskResult.setThreadName("threadName");
        taskResult.setTimeFrame(getTimeFrame());
        taskResult.setSourceInformation(getSourceInformation());
        taskResult.setRequestSizeInBytes(BigDecimal.valueOf(10));

        return taskResult;
    }
    private static List<TaskResult> getTaskResults(){

        final List<TaskResult> taskResultList = new ArrayList<>();

        taskResultList.add(getTaskResult());

        return taskResultList;

    }

    private static BenchmarkResultRequest getBenchmarkResultRequest(){

        final BenchmarkResultRequest request = new BenchmarkResultRequest();
        request.setSourceInformation(getSourceInformation());

        request.setTaskResults(getTaskResults());

        return request;


    }

    private static SourceInformation getSourceInformation(){
        final SourceInformation sourceInformation = new SourceInformation();
        sourceInformation.setUrl(URI.create("http://example.org"));
        return sourceInformation;
    }

    private static TimeFrame getTimeFrame(){
        final TimeFrame timeFrame = new TimeFrame();
        timeFrame.setEndTimestamp(OffsetDateTime.now());
        timeFrame.setStartTimestamp(OffsetDateTime.now());
        return timeFrame;
    }

    private static ResetResponse getResetResponse(final boolean nested){

        final ResetResponse resetResponse = new ResetResponse();
        resetResponse.setTimeFrame(getTimeFrame());
        resetResponse.setReset(true);

        if(nested) {

            resetResponse.setNestedResponses(new ArrayList<>());
            resetResponse.getNestedResponses().add(getResetResponse(false));
            resetResponse.getNestedResponses().add(getResetResponse(false));

        }

        resetResponse.setSourceInformation(getSourceInformation());

        return resetResponse;

    }

    private static PingResponse getPingResponse(){

        final PingResponse pingResponse = new PingResponse();
        pingResponse.setStatus(PingResponse.StatusEnum.OK);
        return pingResponse;

    }

    private static OrchestratorSetupRequest getOrchestratorSetupRequest(final WorkerConnectionSettings workerConnectionSettings, final Workload workload, final GeneratorGenerateRequest generatorGenerateRequest, final URI generatorUrl){

        final OrchestratorSetupRequest orchestratorSetupRequest = new OrchestratorSetupRequest();

        orchestratorSetupRequest.setWorkerConnectionSettings(workerConnectionSettings);

        if(generatorGenerateRequest!=null) {
            orchestratorSetupRequest.setGenerateRequest(generatorGenerateRequest);
        }
        if(workload!=null){
            orchestratorSetupRequest.setWorkload(workload);
        }

        if(generatorUrl!=null) {
            orchestratorSetupRequest.setGeneratorUrl(generatorUrl);
        }
        return orchestratorSetupRequest;
    }

    private static WorkerSetupRequest getWorkerSetupRequest(final WorkerGeneralProperties workerGeneralProperties, final WorkerConfiguration workerConfiguration, final Workload workload){

        final WorkerSetupRequest workerSetupRequest = new WorkerSetupRequest();
        workerSetupRequest.setWorkerConfiguration(workerConfiguration);
        workerSetupRequest.setWorkerGeneralProperties(workerGeneralProperties);
        workerSetupRequest.setOrchestratorUrl(ORCHESTRATOR_URL);
        workerSetupRequest.setBenchmarkWorkload(workload);
        return workerSetupRequest;

    }

    private static GeneratorRecordConfig dynamicKeyWithDynamicStringValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.STRING);
        generatorRecordConfig.setMinKeyLength(1);
        generatorRecordConfig.setMaxKeyLength(1);
        generatorRecordConfig.setMinStringValueLength(1);
        generatorRecordConfig.setMaxStringValueLength(3);

        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig staticKeyWithDynamicStringValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setKeyValue("staticKeyDynStringValue");
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.STRING);
        generatorRecordConfig.setMinStringValueLength(3);
        generatorRecordConfig.setMaxStringValueLength(5);

        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig dynamicKeyWithDynamicStringEnumCountAndDynamicValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.STRING);
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
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.STRING);
        generatorRecordConfig.setKeyValue("staticKeyDynStringEnumDynValue");
        generatorRecordConfig.setMinStringValueLength(1);
        generatorRecordConfig.setMaxStringValueLength(3);
        generatorRecordConfig.setMinStringEnumValues(1);
        generatorRecordConfig.setMaxStringEnumValues(5);

        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig dynamicKeyWithStaticStringEnumValues(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.STRING);
        generatorRecordConfig.setMinKeyLength(1);
        generatorRecordConfig.setMaxKeyLength(1);
        generatorRecordConfig.setStringEnumValues(List.of("s-val1","s-val2"));
        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig staticKeyWithStaticStringEnumValues(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.STRING);
        generatorRecordConfig.setKeyValue("staticKeyStaticStringEnumValues");
        generatorRecordConfig.setStringEnumValues(List.of("s-val1","s-val2"));
        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig dynamicKeyWithUniformIntegerValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.INTEGER);
        generatorRecordConfig.minKeyLength(2);
        generatorRecordConfig.maxKeyLength(5);
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.UNIFORM);
        generatorRecordConfig.setMinValue(BigDecimal.valueOf(0));
        generatorRecordConfig.setMaxValue(BigDecimal.valueOf(100));

        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig staticKeyWithUniformIntegerValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.INTEGER);
        generatorRecordConfig.setKeyValue("staticKeyUniformIntegerValue");
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.UNIFORM);
        generatorRecordConfig.setMinValue(BigDecimal.valueOf(0));
        generatorRecordConfig.setMaxValue(BigDecimal.valueOf(100));

        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig staticKeyWithUniformDoubleValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.DOUBLE);
        generatorRecordConfig.setKeyValue("staticKeyUniformDoubleValue");
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.UNIFORM);
        generatorRecordConfig.setMinValue(BigDecimal.valueOf(5.5d));
        generatorRecordConfig.setMaxValue(BigDecimal.valueOf(55.3d));

        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig dynamicKeyWithTriangleIntegerValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.INTEGER);
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
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.INTEGER);
        generatorRecordConfig.setKeyValue("staticKeyTriangleIntegerValue");
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.TRIANGLE);
        generatorRecordConfig.setMinValue(BigDecimal.valueOf(0));
        generatorRecordConfig.setMaxValue(BigDecimal.valueOf(100));
        generatorRecordConfig.setTriangleSpike(BigDecimal.valueOf(50));


        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig staticKeyWithTriangleDoubleValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.DOUBLE);
        generatorRecordConfig.setKeyValue("staticKeyTriangleDoubleValue");
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.TRIANGLE);
        generatorRecordConfig.setMinValue(BigDecimal.valueOf(5.5d));
        generatorRecordConfig.setMaxValue(BigDecimal.valueOf(55.3d));
        generatorRecordConfig.setTriangleSpike(BigDecimal.valueOf(10));

        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig dynamicKeyWithGaussIntegerValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.INTEGER);
        generatorRecordConfig.minKeyLength(2);
        generatorRecordConfig.maxKeyLength(5);
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.GAUSS);
        generatorRecordConfig.setGaussMiddle(BigDecimal.valueOf(10));
        generatorRecordConfig.setGaussRange(BigDecimal.valueOf(10));


        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig staticKeyWithGaussIntegerValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.INTEGER);
        generatorRecordConfig.setKeyValue("staticKeyGaussIntegerValue");
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.GAUSS);
        generatorRecordConfig.setGaussMiddle(BigDecimal.valueOf(10));
        generatorRecordConfig.setGaussRange(BigDecimal.valueOf(10));


        return generatorRecordConfig;
    }

    private static GeneratorRecordConfig staticKeyWithGaussDoubleValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();

        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.DOUBLE);
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


    public static WorkerGeneralProperties getWorkerGeneralProperties(){
        final WorkerGeneralProperties workerGeneralProperties = new WorkerGeneralProperties();
        workerGeneralProperties.setCreateStorage(true);
        workerGeneralProperties.setCleanupStorage(true);

        return workerGeneralProperties;

    }

    private static WorkerGeneralProperties getInfluxGeneralProperties(){

        final WorkerGeneralProperties workerGeneralProperties = getWorkerGeneralProperties();

        workerGeneralProperties.setTsdbType(WorkerGeneralProperties.TsdbTypeEnum.INFLUX);
        workerGeneralProperties.setCustomProperties(new HashMap<>());
        workerGeneralProperties.getCustomProperties().put("bucketName","testbucket");
        workerGeneralProperties.getCustomProperties().put("organisationName","testorg");

        return workerGeneralProperties;

    }

    private static WorkerConnectionSettings createConnection(final WorkerGeneralProperties workerGeneralProperties, final WorkerConfiguration workerConfiguration){

        final WorkerConnectionSettings workerConnectionSettings = new WorkerConnectionSettings();
        workerConnectionSettings.setWorkerGeneralProperties(workerGeneralProperties);
        workerConnectionSettings.setWorkerConfigurations(List.of(workerConfiguration));

        return workerConnectionSettings;


    }

    private static WorkerGeneralProperties getVictoriaGeneralProperties(){

        final WorkerGeneralProperties workerGeneralProperties = getWorkerGeneralProperties();

        workerGeneralProperties.setTsdbType(WorkerGeneralProperties.TsdbTypeEnum.VICTORIA);

        return workerGeneralProperties;

    }


    private static WorkerConfiguration getInfluxWorkerConfiguration(){
        final WorkerTsdbEndpoint workerTsdbEndpoint = new WorkerTsdbEndpoint();
        workerTsdbEndpoint.setEndpointName("influx-zoneA");
        workerTsdbEndpoint.setEndpointPercentage(100);
        workerTsdbEndpoint.setTsdbToken("YcqeMSEqzrvDsPevnsPA9fuES38RU-R9_y9UZ2gefEYaNJfVdUOVdj5NvrPpwNnmVuGoZSsqZ_jxnKx9dhdHYw==");
        workerTsdbEndpoint.setTsdbUrl(URI.create("http://127.0.0.1:8086"));

        final WorkerConfiguration workerConfiguration = new WorkerConfiguration();
        workerConfiguration.setWorkerName("influx-worker-1");
        workerConfiguration.setWorkerUrl(URI.create("http://localhost:8082"));
        workerConfiguration.setWorkerThreads(3);
        workerConfiguration.setWorkerPercentage(100);
        workerConfiguration.setTsdbEndpoints(List.of(workerTsdbEndpoint));

        return workerConfiguration;
    }

    private static WorkerConfiguration getVictoriaWorkerConfiguration(){

        final WorkerTsdbEndpoint workerTsdbEndpoint = new WorkerTsdbEndpoint();
        workerTsdbEndpoint.setEndpointName("victora-zoneA");
        workerTsdbEndpoint.setEndpointPercentage(100);
        workerTsdbEndpoint.setTsdbUrl(URI.create("http://127.0.0.1:8428"));

        final WorkerConfiguration workerConfiguration = new WorkerConfiguration();
        workerConfiguration.setWorkerName("victoria-worker-1");
        workerConfiguration.setWorkerUrl(URI.create("http://localhost:8082"));
        workerConfiguration.setWorkerThreads(3);
        workerConfiguration.setWorkerPercentage(100);
        workerConfiguration.setTsdbEndpoints(List.of(workerTsdbEndpoint));

        return workerConfiguration;


    }

}
