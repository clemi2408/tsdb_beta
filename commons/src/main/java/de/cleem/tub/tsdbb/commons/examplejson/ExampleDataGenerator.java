package de.cleem.tub.tsdbb.commons.examplejson;

import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.api.model.Insert;
import de.cleem.tub.tsdbb.commons.duration.DurationException;

import java.math.BigDecimal;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ExampleDataGenerator {
    public static BenchmarkResultResponse getBenchmarkResultResponse(){

        final BenchmarkResultResponse resultResponse = new BenchmarkResultResponse();
        resultResponse.setDeliveryCount(BigDecimal.valueOf(10));

        return resultResponse;

    }

    public static KvPair getKvPair(){

        final KvPair kvPair = new KvPair();
        kvPair.setKey("key");
        kvPair.setValue(List.of("value"));

        return kvPair;


    }

    public static List<Insert> getInserts(final int count){

        final List<Insert> inserts = new ArrayList<>();

        for(int i = 0; i < count; i++){

            inserts.add(getInsert());

        }


        return inserts;
    }

    private static List<KvPair> getKvPairs(){

        final List<KvPair> kvPairs = new ArrayList<>();
        kvPairs.add(getKvPair());
        kvPairs.add(getKvPair());
        return kvPairs;

    }

    static Insert getInsert(){
        final Insert insert = new Insert();
        insert.setId(UUID.randomUUID());
        insert.setKvPairs(getKvPairs());

        return insert;
    }

    static TaskResult getTaskResult(){

        final TaskResult taskResult = new TaskResult();
        taskResult.setTaskName("taskName");
        taskResult.setDurationInMs(BigDecimal.valueOf(100));
        taskResult.setThreadName("threadName");
        taskResult.setTimeFrame(getTimeFrame());
        taskResult.setSourceInformation(getSourceInformation());
        taskResult.setRequestSizeInBytes(BigDecimal.valueOf(10));

        return taskResult;
    }

    public static List<TaskResult> getTaskResults(){

        final List<TaskResult> taskResultList = new ArrayList<>();

        taskResultList.add(getTaskResult());

        return taskResultList;

    }

    public static BenchmarkResultRequest getBenchmarkResultRequest(){

        final BenchmarkResultRequest request = new BenchmarkResultRequest();
        request.setSourceInformation(getSourceInformation());

        request.setTaskResults(getTaskResults());

        return request;


    }

    public static SourceInformation getSourceInformation(){
        final SourceInformation sourceInformation = new SourceInformation();
        sourceInformation.setUrl(URI.create("http://example.org"));
        return sourceInformation;
    }

    public static TimeFrame getTimeFrame(){
        final TimeFrame timeFrame = new TimeFrame();
        timeFrame.setEndTimestamp(OffsetDateTime.now());
        timeFrame.setStartTimestamp(OffsetDateTime.now());
        return timeFrame;
    }

    public static ResetResponse getResetResponse(final boolean nested){

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

    public static PingResponse getPingResponse(){

        final PingResponse pingResponse = new PingResponse();
        pingResponse.setStatus(PingResponse.StatusEnum.OK);
        return pingResponse;

    }

    public static OrchestratorSetupRequest getOrchestratorSetupRequest(final WorkerConnectionSettings workerConnectionSettings, final Workload workload, final GeneratorGenerateRequest generatorGenerateRequest, final URI generatorUrl){

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

    public static WorkerSetupRequest getWorkerSetupRequest(final WorkerGeneralProperties workerGeneralProperties, final WorkerConfiguration workerConfiguration, final Workload workload, final URI orchestratorUrl){

        final WorkerSetupRequest workerSetupRequest = new WorkerSetupRequest();
        workerSetupRequest.setWorkerConfiguration(workerConfiguration);
        workerSetupRequest.setWorkerGeneralProperties(workerGeneralProperties);
        workerSetupRequest.setOrchestratorUrl(orchestratorUrl);
        workerSetupRequest.setBenchmarkWorkload(workload);
        return workerSetupRequest;

    }

    public static GeneratorInsertConfig getGeneratorInsertConfig(){
        final GeneratorInsertConfig generatorInsertConfig = new GeneratorInsertConfig();
        generatorInsertConfig.setId(UUID.randomUUID().toString());
        return generatorInsertConfig;
    }

    public static GeneratorInsertConfig dynamicKeyWithDynamicStringValueIndividualKey(){

        final GeneratorInsertConfig generatorInsertConfig = getGeneratorInsertConfig();

        generatorInsertConfig.setValueType(GeneratorInsertConfig.ValueTypeEnum.STRING);
        generatorInsertConfig.setMinKeyLength(5);
        generatorInsertConfig.setMaxKeyLength(5);
        generatorInsertConfig.setMinStringValueLength(5);
        generatorInsertConfig.setMaxStringValueLength(5);
        generatorInsertConfig.setIndividualKey(true);

        return generatorInsertConfig;
    }
    public static GeneratorInsertConfig dynamicKeyWithDynamicStringValueSharedKey(){

        final GeneratorInsertConfig generatorInsertConfig = getGeneratorInsertConfig();

        generatorInsertConfig.setValueType(GeneratorInsertConfig.ValueTypeEnum.STRING);
        generatorInsertConfig.setMinKeyLength(5);
        generatorInsertConfig.setMaxKeyLength(5);
        generatorInsertConfig.setMinStringValueLength(5);
        generatorInsertConfig.setMaxStringValueLength(5);
        generatorInsertConfig.setIndividualKey(false);

        return generatorInsertConfig;
    }

    public static GeneratorInsertConfig staticKeyWithDynamicStringValue(){

        final GeneratorInsertConfig generatorInsertConfig = getGeneratorInsertConfig();
        generatorInsertConfig.setKeyValue("staticKeyDynStringValue");
        generatorInsertConfig.setValueType(GeneratorInsertConfig.ValueTypeEnum.STRING);
        generatorInsertConfig.setMinStringValueLength(3);
        generatorInsertConfig.setMaxStringValueLength(5);

        return generatorInsertConfig;
    }

    public static GeneratorInsertConfig dynamicKeyWithDynamicStringEnumCountAndDynamicValue(){

        final GeneratorInsertConfig generatorInsertConfig = getGeneratorInsertConfig();
        generatorInsertConfig.setValueType(GeneratorInsertConfig.ValueTypeEnum.STRING);
        generatorInsertConfig.setMinKeyLength(6);
        generatorInsertConfig.setMaxKeyLength(8);
        generatorInsertConfig.setMinStringValueLength(1);
        generatorInsertConfig.setMaxStringValueLength(3);
        generatorInsertConfig.setMinStringEnumValues(1);
        generatorInsertConfig.setMaxStringEnumValues(5);

        return generatorInsertConfig;
    }

    public static GeneratorInsertConfig staticKeyWithDynamicStringEnumCountAndDynamicValue(){

        final GeneratorInsertConfig generatorInsertConfig = getGeneratorInsertConfig();
        generatorInsertConfig.setValueType(GeneratorInsertConfig.ValueTypeEnum.STRING);
        generatorInsertConfig.setKeyValue("staticKeyDynStringEnumDynValue");
        generatorInsertConfig.setMinStringValueLength(1);
        generatorInsertConfig.setMaxStringValueLength(3);
        generatorInsertConfig.setMinStringEnumValues(1);
        generatorInsertConfig.setMaxStringEnumValues(5);

        return generatorInsertConfig;
    }

    public static GeneratorInsertConfig dynamicKeyWithStaticStringEnumValues(){

        final GeneratorInsertConfig generatorInsertConfig = getGeneratorInsertConfig();
        generatorInsertConfig.setValueType(GeneratorInsertConfig.ValueTypeEnum.STRING);
        generatorInsertConfig.setMinKeyLength(1);
        generatorInsertConfig.setMaxKeyLength(1);
        generatorInsertConfig.setStringEnumValues(List.of("s-val1","s-val2"));
        return generatorInsertConfig;
    }

    public static GeneratorInsertConfig staticKeyWithStaticStringEnumValues(){

        final GeneratorInsertConfig generatorInsertConfig = getGeneratorInsertConfig();
        generatorInsertConfig.setValueType(GeneratorInsertConfig.ValueTypeEnum.STRING);
        generatorInsertConfig.setKeyValue("staticKeyStaticStringEnumValues");
        generatorInsertConfig.setStringEnumValues(List.of("s-val1","s-val2"));
        return generatorInsertConfig;
    }

    public static GeneratorInsertConfig dynamicKeyWithUniformIntegerValue(){

        final GeneratorInsertConfig generatorInsertConfig = getGeneratorInsertConfig();
        generatorInsertConfig.setValueType(GeneratorInsertConfig.ValueTypeEnum.INTEGER);
        generatorInsertConfig.minKeyLength(2);
        generatorInsertConfig.maxKeyLength(5);
        generatorInsertConfig.setValueDistribution(GeneratorInsertConfig.ValueDistributionEnum.UNIFORM);
        generatorInsertConfig.setMinValue(BigDecimal.valueOf(0));
        generatorInsertConfig.setMaxValue(BigDecimal.valueOf(100));

        return generatorInsertConfig;
    }

    public static GeneratorInsertConfig staticKeyWithUniformIntegerValue(){

        final GeneratorInsertConfig generatorInsertConfig = getGeneratorInsertConfig();
        generatorInsertConfig.setValueType(GeneratorInsertConfig.ValueTypeEnum.INTEGER);
        generatorInsertConfig.setKeyValue("staticKeyUniformIntegerValue");
        generatorInsertConfig.setValueDistribution(GeneratorInsertConfig.ValueDistributionEnum.UNIFORM);
        generatorInsertConfig.setMinValue(BigDecimal.valueOf(0));
        generatorInsertConfig.setMaxValue(BigDecimal.valueOf(100));

        return generatorInsertConfig;
    }

    public static GeneratorInsertConfig staticKeyWithUniformDoubleValue(){

        final GeneratorInsertConfig generatorInsertConfig = getGeneratorInsertConfig();
        generatorInsertConfig.setValueType(GeneratorInsertConfig.ValueTypeEnum.DOUBLE);
        generatorInsertConfig.setKeyValue("staticKeyUniformDoubleValue");
        generatorInsertConfig.setValueDistribution(GeneratorInsertConfig.ValueDistributionEnum.UNIFORM);
        generatorInsertConfig.setMinValue(BigDecimal.valueOf(5.5d));
        generatorInsertConfig.setMaxValue(BigDecimal.valueOf(55.3d));

        return generatorInsertConfig;
    }

    public static GeneratorInsertConfig dynamicKeyWithTriangleIntegerValue(){

        final GeneratorInsertConfig generatorInsertConfig = getGeneratorInsertConfig();
        generatorInsertConfig.setValueType(GeneratorInsertConfig.ValueTypeEnum.INTEGER);
        generatorInsertConfig.minKeyLength(2);
        generatorInsertConfig.maxKeyLength(5);
        generatorInsertConfig.setValueDistribution(GeneratorInsertConfig.ValueDistributionEnum.TRIANGLE);
        generatorInsertConfig.setMinValue(BigDecimal.valueOf(0));
        generatorInsertConfig.setMaxValue(BigDecimal.valueOf(100));
        generatorInsertConfig.setTriangleSpike(BigDecimal.valueOf(50));

        return generatorInsertConfig;
    }

    public static GeneratorInsertConfig staticKeyWithTriangleIntegerValue(){

        final GeneratorInsertConfig generatorInsertConfig = getGeneratorInsertConfig();
        generatorInsertConfig.setValueType(GeneratorInsertConfig.ValueTypeEnum.INTEGER);
        generatorInsertConfig.setKeyValue("staticKeyTriangleIntegerValue");
        generatorInsertConfig.setValueDistribution(GeneratorInsertConfig.ValueDistributionEnum.TRIANGLE);
        generatorInsertConfig.setMinValue(BigDecimal.valueOf(0));
        generatorInsertConfig.setMaxValue(BigDecimal.valueOf(100));
        generatorInsertConfig.setTriangleSpike(BigDecimal.valueOf(50));


        return generatorInsertConfig;
    }

    public static GeneratorInsertConfig staticKeyWithTriangleDoubleValue(){

        final GeneratorInsertConfig generatorInsertConfig = getGeneratorInsertConfig();
        generatorInsertConfig.setValueType(GeneratorInsertConfig.ValueTypeEnum.DOUBLE);
        generatorInsertConfig.setKeyValue("staticKeyTriangleDoubleValue");
        generatorInsertConfig.setValueDistribution(GeneratorInsertConfig.ValueDistributionEnum.TRIANGLE);
        generatorInsertConfig.setMinValue(BigDecimal.valueOf(5.5d));
        generatorInsertConfig.setMaxValue(BigDecimal.valueOf(55.3d));
        generatorInsertConfig.setTriangleSpike(BigDecimal.valueOf(10));

        return generatorInsertConfig;
    }

    public static GeneratorInsertConfig dynamicKeyWithGaussIntegerValue(){

        final GeneratorInsertConfig generatorInsertConfig = getGeneratorInsertConfig();
        generatorInsertConfig.setValueType(GeneratorInsertConfig.ValueTypeEnum.INTEGER);
        generatorInsertConfig.minKeyLength(2);
        generatorInsertConfig.maxKeyLength(5);
        generatorInsertConfig.setValueDistribution(GeneratorInsertConfig.ValueDistributionEnum.GAUSS);
        generatorInsertConfig.setGaussMiddle(BigDecimal.valueOf(10));
        generatorInsertConfig.setGaussRange(BigDecimal.valueOf(10));


        return generatorInsertConfig;
    }

    public static GeneratorInsertConfig staticKeyWithGaussIntegerValue(){

        final GeneratorInsertConfig generatorInsertConfig = getGeneratorInsertConfig();
        generatorInsertConfig.setValueType(GeneratorInsertConfig.ValueTypeEnum.INTEGER);
        generatorInsertConfig.setKeyValue("staticKeyGaussIntegerValue");
        generatorInsertConfig.setValueDistribution(GeneratorInsertConfig.ValueDistributionEnum.GAUSS);
        generatorInsertConfig.setGaussMiddle(BigDecimal.valueOf(10));
        generatorInsertConfig.setGaussRange(BigDecimal.valueOf(10));


        return generatorInsertConfig;
    }

    public static GeneratorInsertConfig staticKeyWithGaussDoubleValue(){

        final GeneratorInsertConfig generatorInsertConfig = getGeneratorInsertConfig();

        generatorInsertConfig.setValueType(GeneratorInsertConfig.ValueTypeEnum.DOUBLE);
        generatorInsertConfig.setKeyValue("staticKeyGaussDoubleValue");
        generatorInsertConfig.setValueDistribution(GeneratorInsertConfig.ValueDistributionEnum.GAUSS);
        generatorInsertConfig.setGaussMiddle(BigDecimal.valueOf(10));
        generatorInsertConfig.setGaussRange(BigDecimal.valueOf(10));

        return generatorInsertConfig;
    }

    public static GeneratorGenerateRequest createGenerateRequest(final GeneratorQueryConfig queryConfig){

        final GeneratorGenerateRequest request = new GeneratorGenerateRequest();
        request.setQueryConfig(queryConfig);

        final List<GeneratorInsertConfig> generatorInsertConfigList = new ArrayList<>();

        generatorInsertConfigList.add(dynamicKeyWithDynamicStringValueIndividualKey());
        generatorInsertConfigList.add(dynamicKeyWithDynamicStringValueSharedKey());
        generatorInsertConfigList.add(staticKeyWithDynamicStringValue());
        generatorInsertConfigList.add(dynamicKeyWithDynamicStringEnumCountAndDynamicValue());
        generatorInsertConfigList.add(staticKeyWithDynamicStringEnumCountAndDynamicValue());
        generatorInsertConfigList.add(dynamicKeyWithStaticStringEnumValues());
        generatorInsertConfigList.add(staticKeyWithStaticStringEnumValues());
        generatorInsertConfigList.add(dynamicKeyWithUniformIntegerValue());
        generatorInsertConfigList.add(staticKeyWithUniformIntegerValue());
        generatorInsertConfigList.add(staticKeyWithUniformDoubleValue());
        generatorInsertConfigList.add(dynamicKeyWithTriangleIntegerValue());
        generatorInsertConfigList.add(staticKeyWithTriangleIntegerValue());
        generatorInsertConfigList.add(staticKeyWithTriangleDoubleValue());
        generatorInsertConfigList.add(dynamicKeyWithGaussIntegerValue());
        generatorInsertConfigList.add(staticKeyWithGaussIntegerValue());
        generatorInsertConfigList.add(staticKeyWithGaussDoubleValue());


        request.setInsertConfigs(generatorInsertConfigList);

        return request;

    }

    public static WorkerGeneralProperties getWorkerGeneralProperties(){
        final WorkerGeneralProperties workerGeneralProperties = new WorkerGeneralProperties();
        workerGeneralProperties.setCreateStorage(true);
        workerGeneralProperties.setCleanupStorage(true);

        return workerGeneralProperties;

    }

    public static WorkerGeneralProperties getInfluxGeneralProperties(){

        final WorkerGeneralProperties workerGeneralProperties = getWorkerGeneralProperties();

        workerGeneralProperties.setTsdbType(WorkerGeneralProperties.TsdbTypeEnum.INFLUX);
        workerGeneralProperties.setCustomProperties(new HashMap<>());
        workerGeneralProperties.getCustomProperties().put("bucketName","testbucket");
        workerGeneralProperties.getCustomProperties().put("organisationName","testorg");

        return workerGeneralProperties;

    }

    public static WorkerConnectionSettings createConnection(final WorkerGeneralProperties workerGeneralProperties, final WorkerConfiguration workerConfiguration){

        final WorkerConnectionSettings workerConnectionSettings = new WorkerConnectionSettings();
        workerConnectionSettings.setWorkerGeneralProperties(workerGeneralProperties);
        workerConnectionSettings.setWorkerConfigurations(List.of(workerConfiguration));

        return workerConnectionSettings;


    }

    public static WorkerGeneralProperties getVictoriaGeneralProperties(){

        final WorkerGeneralProperties workerGeneralProperties = getWorkerGeneralProperties();

        workerGeneralProperties.setTsdbType(WorkerGeneralProperties.TsdbTypeEnum.VICTORIA);

        return workerGeneralProperties;

    }

    public static WorkerConfiguration getInfluxWorkerConfiguration(){
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

    public static WorkerConfiguration getVictoriaWorkerConfiguration(){

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

    public static GeneratorInsertQueryConfig getInsertQueryConfig(final int insertCount,final int preloadCount, final String durationString) throws DurationException {

        final GeneratorInsertQueryConfig insertQueryConfig = new GeneratorInsertQueryConfig();
        insertQueryConfig.setInsertCount(insertCount);
        insertQueryConfig.setPreloadCount(preloadCount);

        insertQueryConfig.setStartOffsetDateTime(OffsetDateTime.now());
        insertQueryConfig.setInsertInterval(durationString);

        return insertQueryConfig;

    }

    public static GeneratorQueryConfig getQueryConfig(final GeneratorInsertQueryConfig insertQueryConfig, final GeneratorSelectQueryConfig selectQueryConfig) {

        GeneratorQueryConfig generatorQueryConfig = new GeneratorQueryConfig();
        generatorQueryConfig.setInsertQueryConfig(insertQueryConfig);
        generatorQueryConfig.setSelectQueryConfig(selectQueryConfig);
        return generatorQueryConfig;
    }

    public static GeneratorSelectQueryConfig getSelectQueryConfig(List<GeneratorSelectQuery> selectQueries) {

        GeneratorSelectQueryConfig selectQueryConfig = new GeneratorSelectQueryConfig();
        selectQueryConfig.setSelectQueries(selectQueries);
        return selectQueryConfig;

    }

    public static GeneratorSelectQuery getGeneratorSelectQuery(int selectCount, String minInterval, String maxInterval, GeneratorSelectQuery.AggregateTypeEnum type){
        final GeneratorSelectQuery generatorSelectQuery = new GeneratorSelectQuery();
        generatorSelectQuery.setSelectCount(selectCount);
        generatorSelectQuery.setMaxSelectInterval(maxInterval);
        generatorSelectQuery.setMinSelectInterval(minInterval);
        generatorSelectQuery.setAggregateType(type);
        return generatorSelectQuery;
    }

    public static GeneratorSelectQuery getRangeSelectQuery(int selectCount, String minInterval, String maxInterval) {

        return getGeneratorSelectQuery(selectCount,minInterval,maxInterval, GeneratorSelectQuery.AggregateTypeEnum.NONE);


    }

    public static GeneratorSelectQuery getAggregateQueryMin(int selectCount, String minInterval, String maxInterval) {

        return getGeneratorSelectQuery(selectCount,minInterval,maxInterval, GeneratorSelectQuery.AggregateTypeEnum.MIN);


    }

    public static GeneratorSelectQuery getAggregateQueryMax(int selectCount, String minInterval, String maxInterval) {

        return getGeneratorSelectQuery(selectCount,minInterval,maxInterval, GeneratorSelectQuery.AggregateTypeEnum.MAX);


    }

    public static GeneratorSelectQuery getAggregateQueryAvg(int selectCount, String minInterval, String maxInterval) {

        return getGeneratorSelectQuery(selectCount,minInterval,maxInterval, GeneratorSelectQuery.AggregateTypeEnum.AVG);


    }

    public static GeneratorSelectQuery getAggregateQuerySum(int selectCount, String minInterval, String maxInterval) {

        return getGeneratorSelectQuery(selectCount,minInterval,maxInterval, GeneratorSelectQuery.AggregateTypeEnum.SUM);


    }
}
