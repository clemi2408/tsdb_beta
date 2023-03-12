package de.cleem.tub.tsdbb.apps.orchestrator.service;


import de.cleem.tub.tsdbb.api.generator.client.GeneratorGenerateApi;
import de.cleem.tub.tsdbb.api.generator.client.GeneratorPingApi;
import de.cleem.tub.tsdbb.api.model.Record;
import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.api.worker.client.WorkerPingApi;
import de.cleem.tub.tsdbb.api.worker.client.WorkerPreloadApi;
import de.cleem.tub.tsdbb.commons.api.ClientApiFacadeException;
import de.cleem.tub.tsdbb.commons.list.ListHelper;
import de.cleem.tub.tsdbb.commons.random.numbers.uniform.UniformGenerator;
import de.cleem.tub.tsdbb.commons.spring.apiclient.ApiClientService;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import de.cleem.tub.tsdbb.commons.spring.ping.PingException;
import de.cleem.tub.tsdbb.commons.spring.ping.PingHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j
public class OrchestratorService extends BaseSpringComponent {

    @Autowired
    private ApiClientService apiClientService;

    public OrchestratorPreloadResponse preload(final OrchestratorPreloadRequest orchestratorPreloadRequest) throws ClientApiFacadeException, PingException {

        final OrchestratorPreloadResponse preloadResponse = new OrchestratorPreloadResponse();
        preloadResponse.setStartTimestamp(OffsetDateTime.now());

        pingPeers(orchestratorPreloadRequest);

        final Workload generatorWorkload = fetchGeneratorWorkload(orchestratorPreloadRequest);
        final Workload benchmarkWorkload = resolveMultiStringEnumValues(generatorWorkload);

        // TODO: split generated Workload after certain distribution to send
        final List<WorkerPreloadRequest> workerPreloadRequests = prepareWorkerPreloadRequests(orchestratorPreloadRequest,benchmarkWorkload);

        final List<WorkerPreloadResponse> workerPreloadResponses = preloadWorkers(workerPreloadRequests);

        preloadResponse.setOrchestratorPreloadRequest(orchestratorPreloadRequest);
        preloadResponse.setGeneratorWorkload(generatorWorkload);
        preloadResponse.setBenchmarkWorkload(benchmarkWorkload);
        preloadResponse.setWorkerPreloadResponses(workerPreloadResponses);
        preloadResponse.setEndTimestamp(OffsetDateTime.now());

        return preloadResponse;

    }


    // PING METHODS
    private void pingPeers(final OrchestratorPreloadRequest preloadRequest) throws ClientApiFacadeException, PingException {

        final GeneratorPingApi generatorPingApi = apiClientService.getApi(GeneratorPingApi.class,preloadRequest.getGeneratorUrl());

        log.info("Sending ping to generator: "+preloadRequest.getGeneratorUrl());
        PingHelper.checkPingResponse(generatorPingApi.ping(),"generator",preloadRequest.getGeneratorUrl());

        WorkerPingApi workerPingApi;

        for(WorkerConfig workerConfig : preloadRequest.getWorkerConfigs()){

            workerPingApi = apiClientService.getApi(WorkerPingApi.class,workerConfig.getWorkerUrl());

            log.info("Sending ping to worker: "+workerConfig.getWorkerUrl());

            PingHelper.checkPingResponse(workerPingApi.ping(),"worker",workerConfig.getWorkerUrl());

        }

    }



    // WORKLOAD METHODS
    public Workload fetchGeneratorWorkload(final OrchestratorPreloadRequest preloadRequest) throws ClientApiFacadeException {

        final GeneratorGenerateApi generatorGenerateApi = apiClientService.getApi(GeneratorGenerateApi.class,preloadRequest.getGeneratorUrl());

        log.info("Requesting workload from generator: "+preloadRequest.getGeneratorUrl());
        final GeneratorGenerateResponse generateResponse = generatorGenerateApi.generate(preloadRequest.getGenerateRequest());
        final Workload generatorWorkload = generateResponse.getWorkload();

        log.info("Got workload from generator with "+generatorWorkload.getRecords().size()+" records");

        return generatorWorkload;

    }

    private List<WorkerPreloadRequest> prepareWorkerPreloadRequests(final OrchestratorPreloadRequest orchestratorPreloadRequest, final Workload benchmarkWorkload) {

        final String orchestratorUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        final int workerCount = orchestratorPreloadRequest.getWorkerConfigs().size();

        final List<List<Record>> partitionedWorkload = ListHelper.splitListIntoParts(benchmarkWorkload.getRecords(),workerCount);

        final List<WorkerPreloadRequest> workerPreloadRequests = new ArrayList<>();

        WorkerPreloadRequest workerPreloadRequest;
        for(int i = 0; i < orchestratorPreloadRequest.getWorkerConfigs().size(); i++){

            workerPreloadRequest = new WorkerPreloadRequest();
            workerPreloadRequest.setWorkerConfig(orchestratorPreloadRequest.getWorkerConfigs().get(i));
            workerPreloadRequest.setOrchestratorUrl(orchestratorUrl);
            workerPreloadRequest.setBenchmarkWorkload(new Workload().records(partitionedWorkload.get(i)));
            workerPreloadRequests.add(workerPreloadRequest);

        }

        return workerPreloadRequests;

    }

    private Workload resolveMultiStringEnumValues(final Workload generatorWorkload){

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

    // WORKER METHODS
    public List<WorkerPreloadResponse> preloadWorkers(final List<WorkerPreloadRequest> workerPreloadRequests) throws ClientApiFacadeException {

        WorkerPreloadApi workerPreloadApi;
        WorkerPreloadResponse workerPreloadResponse;

        final List<WorkerPreloadResponse> workerPreloadResponseList = new ArrayList<>();


        for(WorkerPreloadRequest workerPreloadRequest : workerPreloadRequests){

            workerPreloadApi = apiClientService.getApi(WorkerPreloadApi.class,workerPreloadRequest.getWorkerConfig().getWorkerUrl());
            log.info("Preloading benchmarkWorkload to worker: "+workerPreloadRequest.getWorkerConfig().getWorkerUrl());

            workerPreloadResponse = workerPreloadApi.preload(workerPreloadRequest);

            workerPreloadResponseList.add(workerPreloadResponse);

        }

        return workerPreloadResponseList;

    }




}
