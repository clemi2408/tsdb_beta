package de.cleem.tub.tsdbb.apps.orchestrator.service;


import de.cleem.tub.tsdbb.api.generator.client.GeneratorGenerateApi;
import de.cleem.tub.tsdbb.api.generator.client.GeneratorPingApi;
import de.cleem.tub.tsdbb.api.model.Record;
import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.api.worker.client.WorkerPingApi;
import de.cleem.tub.tsdbb.api.worker.client.WorkerPreloadApi;
import de.cleem.tub.tsdbb.api.worker.client.WorkerResetApi;
import de.cleem.tub.tsdbb.commons.api.ClientApiFacadeException;
import de.cleem.tub.tsdbb.commons.list.ListHelper;
import de.cleem.tub.tsdbb.commons.random.numbers.uniform.UniformGenerator;
import de.cleem.tub.tsdbb.commons.spring.apiclient.ApiClientService;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import de.cleem.tub.tsdbb.commons.spring.objectcache.SingleObjectInstanceCache;
import de.cleem.tub.tsdbb.commons.spring.objectcache.SingleObjectInstanceCacheException;
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


    private final static Class[] CLASSES_TO_RESET = new Class[]{OrchestratorPreloadRequest.class,OrchestratorPreloadResponse.class};

    @Autowired
    private ApiClientService apiClientService;

    @Autowired
    private SingleObjectInstanceCache cache;

    public OrchestratorPreloadResponse preload(final OrchestratorPreloadRequest orchestratorPreloadRequest) throws ClientApiFacadeException, PingException, SingleObjectInstanceCacheException {

        final OrchestratorPreloadResponse orchestratorPreloadResponse = new OrchestratorPreloadResponse();
        orchestratorPreloadResponse.setStartTimestamp(OffsetDateTime.now());

        pingPeers(orchestratorPreloadRequest);

        final Workload generatorWorkload = fetchGeneratorWorkload(orchestratorPreloadRequest);
        final Workload benchmarkWorkload = resolveMultiStringEnumValues(generatorWorkload);

        // TODO: split generated Workload after certain distribution to send
        final List<WorkerPreloadRequest> workerPreloadRequests = prepareWorkerPreloadRequests(orchestratorPreloadRequest,benchmarkWorkload);

        final List<WorkerPreloadResponse> workerPreloadResponses = preloadWorkers(workerPreloadRequests);

        orchestratorPreloadResponse.setOrchestratorPreloadRequest(orchestratorPreloadRequest);
        orchestratorPreloadResponse.setGeneratorWorkload(generatorWorkload);
        orchestratorPreloadResponse.setBenchmarkWorkload(benchmarkWorkload);
        orchestratorPreloadResponse.setWorkerPreloadResponses(workerPreloadResponses);
        orchestratorPreloadResponse.setEndTimestamp(OffsetDateTime.now());

        cache.add(orchestratorPreloadRequest);
        cache.add(orchestratorPreloadResponse);

        return orchestratorPreloadResponse;

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

    public List<ResetResponse> resetWorkers(final OrchestratorPreloadRequest orchestratorPreloadRequest) throws ClientApiFacadeException {

        WorkerResetApi workerResetApi;
        ResetResponse workerResetResponse;

        final List<ResetResponse> workerResetResponses = new ArrayList<>();

        for(WorkerConfig workerConfig : orchestratorPreloadRequest.getWorkerConfigs()){

            workerResetApi = apiClientService.getApi(WorkerResetApi.class,workerConfig.getWorkerUrl());
            log.info("Resetting worker: "+workerConfig.getWorkerUrl());

            workerResetResponse = workerResetApi.reset();

            workerResetResponses.add(workerResetResponse);

        }

        return workerResetResponses;

    }


    public ResetResponse reset() throws SingleObjectInstanceCacheException, ClientApiFacadeException {

        final OrchestratorPreloadRequest orchestratorPreloadRequest = cache.get(OrchestratorPreloadRequest.class);

        final List<ResetResponse> workerResetResponses = resetWorkers(orchestratorPreloadRequest);

        return cache.reset(CLASSES_TO_RESET,workerResetResponses);

    }
}
