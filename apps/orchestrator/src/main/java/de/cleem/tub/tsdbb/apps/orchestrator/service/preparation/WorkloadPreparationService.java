package de.cleem.tub.tsdbb.apps.orchestrator.service.preparation;

import de.cleem.tub.tsdbb.api.model.Record;
import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.commons.list.ListHelper;
import de.cleem.tub.tsdbb.commons.random.numbers.uniform.UniformGenerator;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class WorkloadPreparationService extends BaseSpringComponent {

    public List<WorkerPreloadRequest> prepareWorkerPreloadRequests(final OrchestratorPreloadRequest orchestratorPreloadRequest, final Workload benchmarkWorkload) {

        final int workerCount = orchestratorPreloadRequest.getWorkerConfigs().size();

        final List<List<Record>> partitionedWorkload = ListHelper.splitListIntoParts(benchmarkWorkload.getRecords(),workerCount);

        final List<WorkerPreloadRequest> workerPreloadRequests = new ArrayList<>();

        WorkerPreloadRequest workerPreloadRequest;
        for(int i = 0; i < orchestratorPreloadRequest.getWorkerConfigs().size(); i++){

            workerPreloadRequest = new WorkerPreloadRequest();
            workerPreloadRequest.setWorkerConfig(orchestratorPreloadRequest.getWorkerConfigs().get(i));
            workerPreloadRequest.setOrchestratorUrl(getServerUrl());
            workerPreloadRequest.setBenchmarkWorkload(new Workload().records(partitionedWorkload.get(i)));
            workerPreloadRequests.add(workerPreloadRequest);

        }

        return workerPreloadRequests;

    }

    public Workload resolveMultiStringEnumValues(final GeneratorGenerateResponse generatorGenerateResponse){

        final Workload generatorWorkload = generatorGenerateResponse.getWorkload();

        final Workload benchmarkWorkload = new Workload().records(new ArrayList<>());

        Record benchmarkRecord;
        KvPair benchmarkWorkloadRecordKvPair;
        List<Object> benchmarkWorkloadRecordKvPairValues;

        for (Record inputRecord : generatorWorkload.getRecords()) {

            benchmarkRecord = new Record().recordId(inputRecord.getRecordId())
                    .kvPairs(new ArrayList<>());


            for (KvPair inputWorkloadRecordKvPair : inputRecord.getKvPairs()) {

                if (inputWorkloadRecordKvPair.getValue().size() == 1) {
                    benchmarkWorkloadRecordKvPairValues = inputWorkloadRecordKvPair.getValue();
                } else {

                    final int randomIndex = UniformGenerator.getInteger(0, inputWorkloadRecordKvPair.getValue().size() - 1);

                    benchmarkWorkloadRecordKvPairValues =  List.of(inputWorkloadRecordKvPair.getValue().get(randomIndex));
                }

                benchmarkWorkloadRecordKvPair = new KvPair()
                        .key(inputWorkloadRecordKvPair.getKey())
                        .value(benchmarkWorkloadRecordKvPairValues);

                benchmarkRecord.getKvPairs().add(benchmarkWorkloadRecordKvPair);

            }

            benchmarkWorkload.getRecords().add(benchmarkRecord);

        }

        return benchmarkWorkload;


    }



}
