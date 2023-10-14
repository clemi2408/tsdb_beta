package de.cleem.tub.tsdbb.apps.orchestrator.service.preparation;

import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.commons.insertsplit.InsertListSplitter;
import de.cleem.tub.tsdbb.commons.random.numbers.uniform.UniformGenerator;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class WorkloadPreparationService extends BaseSpringComponent {

    public List<WorkerSetupRequest> prepareWorkerSetupRequests(final OrchestratorSetupRequest orchestratorSetupRequest, final Workload benchmarkWorkload) {



        final List<WorkerConfiguration> workerConfigurations = orchestratorSetupRequest.getWorkerConnectionSettings().getWorkerConfigurations();

        final int totalPreloadCount = orchestratorSetupRequest.getGenerateRequest().getQueryConfig().getInsertQueryConfig().getPreloadCount();
        final int workerCount = workerConfigurations.size();

        final int preloadCountPerWorker = totalPreloadCount/workerCount;

        final List<List<Insert>> partitionedWorkload = InsertListSplitter.splitWorkload(benchmarkWorkload.getInserts(),workerConfigurations, WorkerConfiguration::getWorkerPercentage);

        final List<WorkerSetupRequest> workerSetupRequests = new ArrayList<>();

        WorkerSetupRequest workerSetupRequest;
        for(int i = 0; i < workerConfigurations.size(); i++){

            workerSetupRequest = new WorkerSetupRequest();
            workerSetupRequest.setWorkerConfiguration(workerConfigurations.get(i));
            workerSetupRequest.setWorkerGeneralProperties(orchestratorSetupRequest.getWorkerConnectionSettings().getWorkerGeneralProperties());
            workerSetupRequest.setOrchestratorUrl(getServerUrl());
            workerSetupRequest.setBenchmarkWorkload(new Workload().inserts(partitionedWorkload.get(i)));
            workerSetupRequests.add(workerSetupRequest);
            workerSetupRequest.setWorkerLoadCount(preloadCountPerWorker);

        }

        return workerSetupRequests;

    }

    public Workload resolveMultiStringEnumValues(final GeneratorGenerateResponse generatorGenerateResponse){

        final Workload generatorWorkload = generatorGenerateResponse.getWorkload();

        final Workload benchmarkWorkload = new Workload().inserts(new ArrayList<>());

        Insert benchmarkInsert;
        KvPair benchmarkWorkloadInsertKvPair;
        List<Object> benchmarkWorkloadInsertKvPairValues;

        for (Insert inputInsert : generatorWorkload.getInserts()) {

            benchmarkInsert = new Insert()
                    .id(inputInsert.getId())
                    .timestamp(inputInsert.getTimestamp())
                    .kvPairs(new ArrayList<>());


            for (KvPair inputWorkloadInsertKvPair : inputInsert.getKvPairs()) {

                if (inputWorkloadInsertKvPair.getValue().size() == 1) {
                    benchmarkWorkloadInsertKvPairValues = inputWorkloadInsertKvPair.getValue();
                } else {

                    final int randomIndex = UniformGenerator.getInteger(0, inputWorkloadInsertKvPair.getValue().size() - 1);

                    benchmarkWorkloadInsertKvPairValues =  List.of(inputWorkloadInsertKvPair.getValue().get(randomIndex));
                }

                benchmarkWorkloadInsertKvPair = new KvPair()
                        .key(inputWorkloadInsertKvPair.getKey())
                        .value(benchmarkWorkloadInsertKvPairValues);

                benchmarkInsert.getKvPairs().add(benchmarkWorkloadInsertKvPair);

            }

            benchmarkWorkload.getInserts().add(benchmarkInsert);

        }

        return benchmarkWorkload;


    }



}
