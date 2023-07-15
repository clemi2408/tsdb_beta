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
import java.net.URI;
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
    public void pingPeers(final OrchestratorSetupRequest orchestratorSetupRequest) throws ClientApiFacadeException, PingResponderException {

        if(orchestratorSetupRequest.getGenerateRequest()!=null) {
            final GeneratorPingApi generatorPingApi = apiClientService.getApi(GeneratorPingApi.class, orchestratorSetupRequest.getGeneratorUrl());

            log.info("Sending ping to generator: " + orchestratorSetupRequest.getGeneratorUrl());
            pingResponderService.checkPingResponse(generatorPingApi.ping(), "generator", orchestratorSetupRequest.getGeneratorUrl());
        }

        WorkerPingApi workerPingApi;


        for(WorkerConfiguration workerConfig : orchestratorSetupRequest.getWorkerConnectionSettings().getWorkerConfigurations()){

            workerPingApi = apiClientService.getApi(WorkerPingApi.class,workerConfig.getWorkerUrl());

            log.info("Sending ping to worker: "+workerConfig.getWorkerUrl());

            pingResponderService.checkPingResponse(workerPingApi.ping(),"worker",workerConfig.getWorkerUrl());

        }

    }

    // Generator Generate Method
    public GeneratorGenerateResponse loadWorkload(final OrchestratorSetupRequest orchestratorSetupRequest) throws ClientApiFacadeException {

        final GeneratorGenerateApi generatorGenerateApi = apiClientService.getApi(GeneratorGenerateApi.class,orchestratorSetupRequest.getGeneratorUrl());

        log.info("Requesting workload from generator: "+orchestratorSetupRequest.getGeneratorUrl());
        final GeneratorGenerateResponse generateResponse = generatorGenerateApi.generate(orchestratorSetupRequest.getGenerateRequest());

        log.info("Got workload from generator with "+generateResponse.getWorkload().getInserts().size()+" inserts");

        return generateResponse;

    }

    // Worker Control Methods: SETUP/START/STOP/RESET
    public List<WorkerSetupResponse> setupWorkers(final List<WorkerSetupRequest> workerSetupRequests) throws ClientApiFacadeException {

        WorkerSetupApi workerSetupApi;
        WorkerSetupResponse workerSetupResponse;

        final List<WorkerSetupResponse> workerSetupResponseList = new ArrayList<>();

        URI workerUri;
        for(WorkerSetupRequest workerSetupRequest : workerSetupRequests){
            workerUri=workerSetupRequest.getWorkerConfiguration().getWorkerUrl();

            workerSetupApi = apiClientService.getApi(WorkerSetupApi.class,workerUri);
            log.info("Setup benchmarkWorkload to worker: "+workerUri);

            workerSetupResponse = workerSetupApi.setup(workerSetupRequest);

            workerSetupResponseList.add(workerSetupResponse);

        }

        return workerSetupResponseList;

    }

    public List<ResetResponse> resetWorkers(final OrchestratorSetupRequest orchestratorSetupRequest) throws ClientApiFacadeException {

        WorkerResetApi workerResetApi;
        ResetResponse workerResetResponse;

        final List<ResetResponse> workerResetResponses = new ArrayList<>();

        URI workerUrl;
        for(WorkerConfiguration workerConfig : orchestratorSetupRequest.getWorkerConnectionSettings().getWorkerConfigurations()){
            workerUrl = workerConfig.getWorkerUrl();
            workerResetApi = apiClientService.getApi(WorkerResetApi.class,workerUrl);
            log.info("Resetting worker: "+workerUrl);

            workerResetResponse = workerResetApi.reset();

            workerResetResponses.add(workerResetResponse);

        }

        return workerResetResponses;

    }

    public List<StartStopResponse> startWorkers(final OrchestratorSetupRequest orchestratorSetupRequest) throws ClientApiFacadeException {

        WorkerStartApi workerStartApi;
        StartStopResponse startStopResponse;

        final List<StartStopResponse> workerStartStopResponses = new ArrayList<>();

        URI workerUrl;
        for(WorkerConfiguration workerConfig : orchestratorSetupRequest.getWorkerConnectionSettings().getWorkerConfigurations()){

            workerUrl = workerConfig.getWorkerUrl();

            workerStartApi = apiClientService.getApi(WorkerStartApi.class,workerUrl);
            log.info("Starting worker: "+workerUrl);

            startStopResponse = workerStartApi.start();

            workerStartStopResponses.add(startStopResponse);

        }

        return workerStartStopResponses;

    }

    public List<StartStopResponse> stopWorkers(final OrchestratorSetupRequest orchestratorSetupRequest) throws ClientApiFacadeException {

        WorkerStopApi workerStopApi;
        StartStopResponse startStopResponse;

        final List<StartStopResponse> workerStartStopResponses = new ArrayList<>();

        URI workerUrl;
        for(WorkerConfiguration workerConfig : orchestratorSetupRequest.getWorkerConnectionSettings().getWorkerConfigurations()){

            workerUrl=workerConfig.getWorkerUrl();
            workerStopApi = apiClientService.getApi(WorkerStopApi.class,workerUrl);
            log.info("Stopping worker: "+workerUrl);

            startStopResponse = workerStopApi.stop();

            workerStartStopResponses.add(startStopResponse);

        }

        return workerStartStopResponses;

    }

    public void collect(final List<TaskResult> taskResults, final WorkerSetupRequest workerSetupRequest) throws ClientApiFacadeException {

        final BenchmarkResultRequest benchmarkResultRequest = new BenchmarkResultRequest();
        benchmarkResultRequest.setSourceInformation(SourceInformationFactory.getSourceInformation(getServerUrl()));
        benchmarkResultRequest.setTaskResults(taskResults);

        final URI orchestratorBasePath = workerSetupRequest.getOrchestratorUrl();

        final OrchestratorCollectApi orchestratorCollectApi = apiClientService.getApi(OrchestratorCollectApi.class,orchestratorBasePath);

        final BenchmarkResultResponse benchmarkResultResponse = orchestratorCollectApi.collect(benchmarkResultRequest);

        if(benchmarkResultResponse.getDeliveryCount().compareTo(BigDecimal.valueOf(taskResults.size()))==0){
            log.info("Delivered result to orchestrator "+orchestratorBasePath);
        }


    }

}
