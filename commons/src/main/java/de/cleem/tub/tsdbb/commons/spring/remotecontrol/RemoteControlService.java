package de.cleem.tub.tsdbb.commons.spring.remotecontrol;

import de.cleem.tub.tsdbb.api.generator.client.GeneratorGenerateApi;
import de.cleem.tub.tsdbb.api.generator.client.GeneratorPingApi;
import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.api.orchestrator.client.OrchestratorCollectApi;
import de.cleem.tub.tsdbb.api.worker.client.*;
import de.cleem.tub.tsdbb.commons.api.ClientApiFacadeException;
import de.cleem.tub.tsdbb.commons.factories.sourceInformation.SourceInformationFactory;
import de.cleem.tub.tsdbb.commons.spring.apiclient.ApiClientService;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import de.cleem.tub.tsdbb.commons.spring.pingresponder.PingResponderException;
import de.cleem.tub.tsdbb.commons.spring.pingresponder.PingResponderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class RemoteControlService extends BaseSpringComponent {

    @Autowired
    private ApiClientService apiClientService;

    @Autowired
    private PingResponderService pingResponderService;

    // Generator and Ping Method
    public void pingPeers(final OrchestratorPreloadRequest preloadRequest) throws ClientApiFacadeException, PingResponderException {

        final GeneratorPingApi generatorPingApi = apiClientService.getApi(GeneratorPingApi.class,preloadRequest.getGeneratorUrl());

        log.info("Sending ping to generator: "+preloadRequest.getGeneratorUrl());
        pingResponderService.checkPingResponse(generatorPingApi.ping(),"generator",preloadRequest.getGeneratorUrl());

        WorkerPingApi workerPingApi;

        for(WorkerConfig workerConfig : preloadRequest.getWorkerConfigs()){

            workerPingApi = apiClientService.getApi(WorkerPingApi.class,workerConfig.getWorkerUrl());

            log.info("Sending ping to worker: "+workerConfig.getWorkerUrl());

            pingResponderService.checkPingResponse(workerPingApi.ping(),"worker",workerConfig.getWorkerUrl());

        }

    }

    // Generator Generate Method
    public GeneratorGenerateResponse loadWorkload(final OrchestratorPreloadRequest preloadRequest) throws ClientApiFacadeException {

        final GeneratorGenerateApi generatorGenerateApi = apiClientService.getApi(GeneratorGenerateApi.class,preloadRequest.getGeneratorUrl());

        log.info("Requesting workload from generator: "+preloadRequest.getGeneratorUrl());
        final GeneratorGenerateResponse generateResponse = generatorGenerateApi.generate(preloadRequest.getGenerateRequest());

        log.info("Got workload from generator with "+generateResponse.getWorkload().getRecords().size()+" records");

        return generateResponse;

    }

    // Worker Control Methods: PRELOAD/START/STOP/RESET
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

    public List<StartStopResponse> startWorkers(final OrchestratorPreloadRequest orchestratorPreloadRequest) throws ClientApiFacadeException {

        WorkerStartApi workerStartApi;
        StartStopResponse startStopResponse;

        final List<StartStopResponse> workerStartStopResponses = new ArrayList<>();

        for(WorkerConfig workerConfig : orchestratorPreloadRequest.getWorkerConfigs()){

            workerStartApi = apiClientService.getApi(WorkerStartApi.class,workerConfig.getWorkerUrl());
            log.info("Starting worker: "+workerConfig.getWorkerUrl());

            startStopResponse = workerStartApi.start();

            workerStartStopResponses.add(startStopResponse);

        }

        return workerStartStopResponses;

    }

    public List<StartStopResponse> stopWorkers(final OrchestratorPreloadRequest orchestratorPreloadRequest) throws ClientApiFacadeException {

        WorkerStopApi workerStopApi;
        StartStopResponse startStopResponse;

        final List<StartStopResponse> workerStartStopResponses = new ArrayList<>();

        for(WorkerConfig workerConfig : orchestratorPreloadRequest.getWorkerConfigs()){

            workerStopApi = apiClientService.getApi(WorkerStopApi.class,workerConfig.getWorkerUrl());
            log.info("Stopping worker: "+workerConfig.getWorkerUrl());

            startStopResponse = workerStopApi.stop();

            workerStartStopResponses.add(startStopResponse);

        }

        return workerStartStopResponses;

    }

    public void collect(final List<TaskResult> taskResults, final WorkerPreloadRequest workerPreloadRequest) throws ClientApiFacadeException {

        final BenchmarkResultRequest benchmarkResultRequest = new BenchmarkResultRequest();
        benchmarkResultRequest.setSourceInformation(SourceInformationFactory.getSourceInformation(getServerUrl()));
        benchmarkResultRequest.setTaskResults(taskResults);

        final String orchestratorBasePath = workerPreloadRequest.getOrchestratorUrl();

        final OrchestratorCollectApi orchestratorCollectApi = apiClientService.getApi(OrchestratorCollectApi.class,orchestratorBasePath);

        final BenchmarkResultResponse benchmarkResultResponse = orchestratorCollectApi.collect(benchmarkResultRequest);

        if(benchmarkResultResponse.getDeliveryCount().compareTo(BigDecimal.valueOf(taskResults.size()))==0){
            log.info("Delivered result to orchestrator "+orchestratorBasePath);
        }


    }

}
