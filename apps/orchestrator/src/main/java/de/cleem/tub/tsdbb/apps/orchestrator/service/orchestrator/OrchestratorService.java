package de.cleem.tub.tsdbb.apps.orchestrator.service.orchestrator;

import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.apps.orchestrator.service.collector.WorkloadCollectorException;
import de.cleem.tub.tsdbb.apps.orchestrator.service.collector.WorkloadCollectorService;
import de.cleem.tub.tsdbb.apps.orchestrator.service.preparation.WorkloadPreparationService;
import de.cleem.tub.tsdbb.commons.api.ClientApiFacadeException;
import de.cleem.tub.tsdbb.commons.exception.StartStopException;
import de.cleem.tub.tsdbb.commons.factories.sourceInformation.SourceInformationFactory;
import de.cleem.tub.tsdbb.commons.factories.timeFrame.TimeFrameFactory;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import de.cleem.tub.tsdbb.commons.spring.pingresponder.PingResponderException;
import de.cleem.tub.tsdbb.commons.spring.pingresponder.PingResponderService;
import de.cleem.tub.tsdbb.commons.spring.remotecontrol.RemoteControlService;
import de.cleem.tub.tsdbb.commons.status.StatusItems;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.time.OffsetDateTime;
import java.util.List;


@Component
@ApplicationScope
@Slf4j
public class OrchestratorService extends BaseSpringComponent {

    @Autowired
    private PingResponderService pingResponderService;

    @Autowired
    private RemoteControlService remoteControlService;

    @Autowired
    private WorkloadPreparationService workloadPreparationService;

    @Autowired
    private WorkloadCollectorService workloadCollectorService;

    private OrchestratorPreloadRequest orchestratorPreloadRequest;
    private OrchestratorPreloadResponse orchestratorPreloadResponse;

    public OrchestratorPreloadResponse preload(final OrchestratorPreloadRequest orchestratorPreloadRequest) throws ClientApiFacadeException, PingResponderException {

        log.info("Preloading Orchestrator");

        final OrchestratorPreloadResponse orchestratorPreloadResponse = new OrchestratorPreloadResponse();
        orchestratorPreloadResponse.setTimeFrame(TimeFrameFactory.getTimeFrame());
        orchestratorPreloadResponse.setSourceInformation(SourceInformationFactory.getSourceInformation(getServerUrl()));

        remoteControlService.pingPeers(orchestratorPreloadRequest);

        final GeneratorGenerateResponse generatorGenerateResponse = remoteControlService.loadWorkload(orchestratorPreloadRequest);
        final Workload benchmarkWorkload = workloadPreparationService.resolveMultiStringEnumValues(generatorGenerateResponse);

        // TODO: split generated Workload after certain distribution to send
        final List<WorkerPreloadRequest> workerPreloadRequests = workloadPreparationService.prepareWorkerPreloadRequests(orchestratorPreloadRequest,benchmarkWorkload);

        final List<WorkerPreloadResponse> workerPreloadResponses = remoteControlService.preloadWorkers(workerPreloadRequests);

        orchestratorPreloadResponse.setOrchestratorPreloadRequest(orchestratorPreloadRequest);
        orchestratorPreloadResponse.setGeneratorGenerateResponse(generatorGenerateResponse);
        orchestratorPreloadResponse.setBenchmarkWorkload(benchmarkWorkload);
        orchestratorPreloadResponse.setWorkerPreloadResponses(workerPreloadResponses);
        orchestratorPreloadResponse.getTimeFrame().setEndTimestamp(OffsetDateTime.now());

        this.orchestratorPreloadRequest = orchestratorPreloadRequest;
        this.orchestratorPreloadResponse = orchestratorPreloadResponse;

        return orchestratorPreloadResponse;

    }

    public ResetResponse reset() throws ClientApiFacadeException, StartStopException {

        if(orchestratorPreloadRequest==null && orchestratorPreloadResponse==null){
            throw new StartStopException("Call preload before Reset");
        }

        log.info("Resetting Orchestrator");

        final ResetResponse resetResponse = new ResetResponse();
        resetResponse.setTimeFrame(TimeFrameFactory.getTimeFrame());
        resetResponse.setSourceInformation(SourceInformationFactory.getSourceInformation(getServerUrl()));

        final List<ResetResponse> workerResetResponses = remoteControlService.resetWorkers(orchestratorPreloadRequest);

        resetResponse.setNestedResponses(workerResetResponses);

        orchestratorPreloadRequest = null;
        orchestratorPreloadResponse = null;

        resetResponse.setReset(true);

        resetResponse.getTimeFrame().setEndTimestamp(OffsetDateTime.now());

        return resetResponse;

    }

    public StartStopResponse start() throws ClientApiFacadeException, StartStopException {

        if(orchestratorPreloadRequest==null){
            throw new StartStopException("Call preload before Start");
        }

        log.info("Starting Orchestrator");

        final StartStopResponse startStopResponse = new StartStopResponse();
        startStopResponse.setTimeFrame(TimeFrameFactory.getTimeFrame());
        startStopResponse.setSourceInformation(SourceInformationFactory.getSourceInformation(getServerUrl()));
        startStopResponse.setNestedResponses(remoteControlService.startWorkers(orchestratorPreloadRequest));
        startStopResponse.getTimeFrame().setEndTimestamp(OffsetDateTime.now());
        startStopResponse.setStatus(StatusItems.STARTED_STRING);

        return startStopResponse;

    }

    public StartStopResponse stop() throws ClientApiFacadeException, StartStopException {

        if(orchestratorPreloadRequest==null){
            throw new StartStopException("Call preload before Stop");
        }

        log.info("Stopping Orchestrator");

        final StartStopResponse startStopResponse = new StartStopResponse();
        startStopResponse.setTimeFrame(TimeFrameFactory.getTimeFrame());
        startStopResponse.setSourceInformation(SourceInformationFactory.getSourceInformation(getServerUrl()));
        startStopResponse.setNestedResponses(remoteControlService.stopWorkers(orchestratorPreloadRequest));
        startStopResponse.getTimeFrame().setEndTimestamp(OffsetDateTime.now());
        startStopResponse.setStatus(StatusItems.STOPPED_STRING);

        return startStopResponse;
    }

    public BenchmarkResultResponse collect(final BenchmarkResultRequest benchmarkResultRequest) throws WorkloadCollectorException {

        return workloadCollectorService.collect(benchmarkResultRequest);

    }

    public ResponseEntity<PingResponse> ping() {

        return pingResponderService.pong(true);

    }

    public OrchestratorResultResponse results() {

        return workloadCollectorService.results(orchestratorPreloadRequest);

    }
}
