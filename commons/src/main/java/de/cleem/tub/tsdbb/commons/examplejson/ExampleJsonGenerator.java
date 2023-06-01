package de.cleem.tub.tsdbb.commons.examplejson;

import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.commons.file.FileException;
import de.cleem.tub.tsdbb.commons.file.FileHelper;
import de.cleem.tub.tsdbb.commons.json.JsonException;
import de.cleem.tub.tsdbb.commons.json.JsonHelper;

import java.io.File;
import java.net.URI;

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

        final WorkerGeneralProperties workerGeneralPropertiesVictoria = ExampleDataGenerator.getVictoriaGeneralProperties();
        final WorkerConfiguration workerConfigurationVictoria = ExampleDataGenerator.getVictoriaWorkerConfiguration();
        final WorkerSetupRequest workerSetupRequestVictoria = ExampleDataGenerator.getWorkerSetupRequest(workerGeneralPropertiesVictoria, workerConfigurationVictoria,generatedWorkload,ExampleJsonGenerator.ORCHESTRATOR_URL);
        write(workerSetupRequestVictoria,1);


        final WorkerGeneralProperties workerGeneralPropertiesInflux = ExampleDataGenerator.getInfluxGeneralProperties();
        final WorkerConfiguration workerConfigurationInflux = ExampleDataGenerator.getInfluxWorkerConfiguration();
        final WorkerSetupRequest workerSetupRequestInflux = ExampleDataGenerator.getWorkerSetupRequest(workerGeneralPropertiesInflux,workerConfigurationInflux,generatedWorkload,ExampleJsonGenerator.ORCHESTRATOR_URL);
        write(workerSetupRequestInflux,2);


        write(workerGeneralPropertiesVictoria,1);
        write(workerGeneralPropertiesInflux,2);


        write(workerConfigurationVictoria,1);
        write(workerConfigurationInflux,2);

        write(ExampleDataGenerator.createGenerateRequest(),1);


        final OrchestratorSetupRequest orchestratorSetupRequestGeneratorInflux = ExampleDataGenerator.getOrchestratorSetupRequest(
                ExampleDataGenerator.createConnection(workerGeneralPropertiesInflux,workerConfigurationInflux),
                null,
                ExampleDataGenerator.createGenerateRequest(),
                GENERATOR_URL);

        write(orchestratorSetupRequestGeneratorInflux,1);


        final OrchestratorSetupRequest orchestratorSetupRequestGeneratorVictoria = ExampleDataGenerator.getOrchestratorSetupRequest(
                ExampleDataGenerator.createConnection(workerGeneralPropertiesVictoria,workerConfigurationVictoria),
                null,
                ExampleDataGenerator.createGenerateRequest(),
                GENERATOR_URL);

        write(orchestratorSetupRequestGeneratorVictoria,2);

        final OrchestratorSetupRequest orchestratorSetupRequestWorkloadInflux = ExampleDataGenerator.getOrchestratorSetupRequest(
                ExampleDataGenerator.createConnection(workerGeneralPropertiesInflux,workerConfigurationInflux),
                preparedWorkload,
                null,null);

        write(orchestratorSetupRequestWorkloadInflux,3);


        final OrchestratorSetupRequest orchestratorSetupRequestWorkloadVictoria = ExampleDataGenerator.getOrchestratorSetupRequest(
                ExampleDataGenerator.createConnection(workerGeneralPropertiesVictoria,workerConfigurationVictoria),
                preparedWorkload,
                null,null);


        write(orchestratorSetupRequestWorkloadVictoria,4);

        write(ExampleDataGenerator.getPingResponse(),1);

        write(ExampleDataGenerator.getSourceInformation(),1);

        write(ExampleDataGenerator.getResetResponse(false),1);

        write(ExampleDataGenerator.getResetResponse(true),2);

        write(ExampleDataGenerator.getTimeFrame(),1);

        write(ExampleDataGenerator.dynamicKeyWithDynamicStringValue(),1);
        write(ExampleDataGenerator.staticKeyWithDynamicStringValue(),2);
        write(ExampleDataGenerator.dynamicKeyWithDynamicStringEnumCountAndDynamicValue(),3);
        write(ExampleDataGenerator.staticKeyWithDynamicStringEnumCountAndDynamicValue(),4);
        write(ExampleDataGenerator.dynamicKeyWithStaticStringEnumValues(),5);
        write(ExampleDataGenerator.staticKeyWithStaticStringEnumValues(),6);
        write(ExampleDataGenerator.dynamicKeyWithUniformIntegerValue(),7);
        write(ExampleDataGenerator.staticKeyWithUniformIntegerValue(),8);
        write(ExampleDataGenerator.staticKeyWithUniformDoubleValue(),9);
        write(ExampleDataGenerator.dynamicKeyWithTriangleIntegerValue(),10);
        write(ExampleDataGenerator.staticKeyWithTriangleIntegerValue(),11);
        write(ExampleDataGenerator.staticKeyWithTriangleDoubleValue(),12);
        write(ExampleDataGenerator.dynamicKeyWithGaussIntegerValue(),13);
        write(ExampleDataGenerator.staticKeyWithGaussIntegerValue(),14);
        write(ExampleDataGenerator.staticKeyWithGaussDoubleValue(),15);

        write(ExampleDataGenerator.getKvPair(),1);

        write(ExampleDataGenerator.getRecord(),1);

        write(ExampleDataGenerator.getTaskResult(),1);

        write(ExampleDataGenerator.getBenchmarkResultRequest(),1);

        write(ExampleDataGenerator.getBenchmarkResultResponse(),1);

    }


}
