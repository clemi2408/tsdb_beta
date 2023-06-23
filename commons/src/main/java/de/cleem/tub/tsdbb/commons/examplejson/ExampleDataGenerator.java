package de.cleem.tub.tsdbb.commons.examplejson;

import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.api.model.Record;
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

    public static List<Record> getRecords(final int count){

        final List<Record> records = new ArrayList<>();

        for(int i = 0; i < count; i++){

            records.add(getRecord());

        }


        return records;
    }

    private static List<KvPair> getKvPairs(){

        final List<KvPair> kvPairs = new ArrayList<>();
        kvPairs.add(getKvPair());
        kvPairs.add(getKvPair());
        return kvPairs;

    }

    static Record getRecord(){
        final Record record = new Record();
        record.setRecordId(UUID.randomUUID());
        record.setKvPairs(getKvPairs());

        return record;
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

    public static GeneratorRecordConfig dynamicKeyWithDynamicStringValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.STRING);
        generatorRecordConfig.setMinKeyLength(1);
        generatorRecordConfig.setMaxKeyLength(1);
        generatorRecordConfig.setMinStringValueLength(1);
        generatorRecordConfig.setMaxStringValueLength(3);

        return generatorRecordConfig;
    }

    public static GeneratorRecordConfig staticKeyWithDynamicStringValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setKeyValue("staticKeyDynStringValue");
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.STRING);
        generatorRecordConfig.setMinStringValueLength(3);
        generatorRecordConfig.setMaxStringValueLength(5);

        return generatorRecordConfig;
    }

    public static GeneratorRecordConfig dynamicKeyWithDynamicStringEnumCountAndDynamicValue(){

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

    public static GeneratorRecordConfig staticKeyWithDynamicStringEnumCountAndDynamicValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.STRING);
        generatorRecordConfig.setKeyValue("staticKeyDynStringEnumDynValue");
        generatorRecordConfig.setMinStringValueLength(1);
        generatorRecordConfig.setMaxStringValueLength(3);
        generatorRecordConfig.setMinStringEnumValues(1);
        generatorRecordConfig.setMaxStringEnumValues(5);

        return generatorRecordConfig;
    }

    public static GeneratorRecordConfig dynamicKeyWithStaticStringEnumValues(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.STRING);
        generatorRecordConfig.setMinKeyLength(1);
        generatorRecordConfig.setMaxKeyLength(1);
        generatorRecordConfig.setStringEnumValues(List.of("s-val1","s-val2"));
        return generatorRecordConfig;
    }

    public static GeneratorRecordConfig staticKeyWithStaticStringEnumValues(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.STRING);
        generatorRecordConfig.setKeyValue("staticKeyStaticStringEnumValues");
        generatorRecordConfig.setStringEnumValues(List.of("s-val1","s-val2"));
        return generatorRecordConfig;
    }

    public static GeneratorRecordConfig dynamicKeyWithUniformIntegerValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.INTEGER);
        generatorRecordConfig.minKeyLength(2);
        generatorRecordConfig.maxKeyLength(5);
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.UNIFORM);
        generatorRecordConfig.setMinValue(BigDecimal.valueOf(0));
        generatorRecordConfig.setMaxValue(BigDecimal.valueOf(100));

        return generatorRecordConfig;
    }

    public static GeneratorRecordConfig staticKeyWithUniformIntegerValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.INTEGER);
        generatorRecordConfig.setKeyValue("staticKeyUniformIntegerValue");
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.UNIFORM);
        generatorRecordConfig.setMinValue(BigDecimal.valueOf(0));
        generatorRecordConfig.setMaxValue(BigDecimal.valueOf(100));

        return generatorRecordConfig;
    }

    public static GeneratorRecordConfig staticKeyWithUniformDoubleValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.DOUBLE);
        generatorRecordConfig.setKeyValue("staticKeyUniformDoubleValue");
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.UNIFORM);
        generatorRecordConfig.setMinValue(BigDecimal.valueOf(5.5d));
        generatorRecordConfig.setMaxValue(BigDecimal.valueOf(55.3d));

        return generatorRecordConfig;
    }

    public static GeneratorRecordConfig dynamicKeyWithTriangleIntegerValue(){

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

    public static GeneratorRecordConfig staticKeyWithTriangleIntegerValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.INTEGER);
        generatorRecordConfig.setKeyValue("staticKeyTriangleIntegerValue");
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.TRIANGLE);
        generatorRecordConfig.setMinValue(BigDecimal.valueOf(0));
        generatorRecordConfig.setMaxValue(BigDecimal.valueOf(100));
        generatorRecordConfig.setTriangleSpike(BigDecimal.valueOf(50));


        return generatorRecordConfig;
    }

    public static GeneratorRecordConfig staticKeyWithTriangleDoubleValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.DOUBLE);
        generatorRecordConfig.setKeyValue("staticKeyTriangleDoubleValue");
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.TRIANGLE);
        generatorRecordConfig.setMinValue(BigDecimal.valueOf(5.5d));
        generatorRecordConfig.setMaxValue(BigDecimal.valueOf(55.3d));
        generatorRecordConfig.setTriangleSpike(BigDecimal.valueOf(10));

        return generatorRecordConfig;
    }

    public static GeneratorRecordConfig dynamicKeyWithGaussIntegerValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.INTEGER);
        generatorRecordConfig.minKeyLength(2);
        generatorRecordConfig.maxKeyLength(5);
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.GAUSS);
        generatorRecordConfig.setGaussMiddle(BigDecimal.valueOf(10));
        generatorRecordConfig.setGaussRange(BigDecimal.valueOf(10));


        return generatorRecordConfig;
    }

    public static GeneratorRecordConfig staticKeyWithGaussIntegerValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();
        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.INTEGER);
        generatorRecordConfig.setKeyValue("staticKeyGaussIntegerValue");
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.GAUSS);
        generatorRecordConfig.setGaussMiddle(BigDecimal.valueOf(10));
        generatorRecordConfig.setGaussRange(BigDecimal.valueOf(10));


        return generatorRecordConfig;
    }

    public static GeneratorRecordConfig staticKeyWithGaussDoubleValue(){

        final GeneratorRecordConfig generatorRecordConfig = new GeneratorRecordConfig();

        generatorRecordConfig.setValueType(GeneratorRecordConfig.ValueTypeEnum.DOUBLE);
        generatorRecordConfig.setKeyValue("staticKeyGaussDoubleValue");
        generatorRecordConfig.setValueDistribution(GeneratorRecordConfig.ValueDistributionEnum.GAUSS);
        generatorRecordConfig.setGaussMiddle(BigDecimal.valueOf(10));
        generatorRecordConfig.setGaussRange(BigDecimal.valueOf(10));

        return generatorRecordConfig;
    }

    public static GeneratorGenerateRequest createGenerateRequest(final GeneratorQueryConfig queryConfig){

        final GeneratorGenerateRequest request = new GeneratorGenerateRequest();
        request.setQueryConfig(queryConfig);

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
