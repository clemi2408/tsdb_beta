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

    public List<WorkerSetupRequest> prepareWorkerSetupRequests(final OrchestratorSetupRequest orchestratorSetupRequest, final Workload benchmarkWorkload) {

        final int workerCount = orchestratorSetupRequest.getWorkerConfigs().size();

        final List<List<Record>> partitionedWorkload = ListHelper.splitListIntoParts(benchmarkWorkload.getRecords(),workerCount);

        final List<WorkerSetupRequest> workerSetupRequests = new ArrayList<>();

        WorkerSetupRequest workerSetupRequest;
        for(int i = 0; i < orchestratorSetupRequest.getWorkerConfigs().size(); i++){

            workerSetupRequest = new WorkerSetupRequest();
            workerSetupRequest.setWorkerConfig(orchestratorSetupRequest.getWorkerConfigs().get(i));
            workerSetupRequest.setOrchestratorUrl(getServerUrl());
            workerSetupRequest.setBenchmarkWorkload(new Workload().records(partitionedWorkload.get(i)));
            workerSetupRequests.add(workerSetupRequest);

        }

        return workerSetupRequests;

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
