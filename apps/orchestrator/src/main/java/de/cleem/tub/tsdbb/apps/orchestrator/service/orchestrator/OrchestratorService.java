package de.cleem.tub.tsdbb.apps.orchestrator.service.orchestrator;

import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.apps.orchestrator.service.collector.WorkloadCollectorException;
import de.cleem.tub.tsdbb.apps.orchestrator.service.collector.WorkloadCollectorService;
import de.cleem.tub.tsdbb.apps.orchestrator.service.preparation.WorkloadPreparationService;
import de.cleem.tub.tsdbb.commons.api.ClientApiFacadeException;
import de.cleem.tub.tsdbb.commons.exceptions.SetupException;
import de.cleem.tub.tsdbb.commons.exceptions.StartStopException;
import de.cleem.tub.tsdbb.commons.factories.sourceInformation.SourceInformationFactory;
import de.cleem.tub.tsdbb.commons.factories.timeFrame.TimeFrameFactory;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import de.cleem.tub.tsdbb.commons.spring.pingresponder.PingResponderException;
import de.cleem.tub.tsdbb.commons.spring.pingresponder.PingResponderService;
import de.cleem.tub.tsdbb.commons.spring.remotecontrol.RemoteControlService;
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

    private OrchestratorSetupRequest orchestratorSetupRequest;
    private OrchestratorSetupResponse orchestratorSetupResponse;

    public OrchestratorSetupResponse setup(final OrchestratorSetupRequest orchestratorSetupRequest) throws ClientApiFacadeException, PingResponderException, SetupException {

        if(orchestratorSetupRequest.getGenerateRequest()!=null && orchestratorSetupRequest.getWorkload()!=null){
            throw new SetupException("Provide either GenerateRequest or Workload");
        }


        log.info("Setup Orchestrator");

        final OrchestratorSetupResponse orchestratorSetupResponse = new OrchestratorSetupResponse();
        orchestratorSetupResponse.setTimeFrame(TimeFrameFactory.getTimeFrame());
        orchestratorSetupResponse.setSourceInformation(SourceInformationFactory.getSourceInformation(getServerUrl()));
        orchestratorSetupResponse.setOrchestratorSetupRequest(orchestratorSetupRequest);

        remoteControlService.pingPeers(orchestratorSetupRequest);

        Workload benchmarkWorkload = null;
        if(orchestratorSetupRequest.getGenerateRequest()!=null) {

            if(orchestratorSetupRequest.getGeneratorUrl()==null){
                throw new SetupException("No Generator URL provided");
            }

            final GeneratorGenerateResponse generatorGenerateResponse = remoteControlService.loadWorkload(orchestratorSetupRequest);
            orchestratorSetupResponse.setGeneratorGenerateResponse(generatorGenerateResponse);
            benchmarkWorkload = workloadPreparationService.resolveMultiStringEnumValues(generatorGenerateResponse);

        }

        if(orchestratorSetupRequest.getWorkload()!=null){
            benchmarkWorkload=orchestratorSetupRequest.getWorkload();
        }

        orchestratorSetupResponse.setBenchmarkWorkload(benchmarkWorkload);

        // TODO: split generated Workload after certain distribution to send
        final List<WorkerSetupRequest> workerSetupRequests = workloadPreparationService.prepareWorkerSetupRequests(orchestratorSetupRequest,benchmarkWorkload);
        final List<WorkerSetupResponse> workerSetupResponses = remoteControlService.setupWorkers(workerSetupRequests);

        orchestratorSetupResponse.setWorkerSetupResponses(workerSetupResponses);
        orchestratorSetupResponse.getTimeFrame().setEndTimestamp(OffsetDateTime.now());

        this.orchestratorSetupRequest = orchestratorSetupRequest;
        this.orchestratorSetupResponse = orchestratorSetupResponse;

        return orchestratorSetupResponse;

    }

    public ResetResponse reset() throws ClientApiFacadeException, StartStopException {

        if(orchestratorSetupRequest ==null && orchestratorSetupResponse ==null){
            throw new StartStopException("Call setup before Reset");
        }

        workloadCollectorService.reset();

        log.info("Resetting Orchestrator");

        final ResetResponse resetResponse = new ResetResponse();
        resetResponse.setTimeFrame(TimeFrameFactory.getTimeFrame());
        resetResponse.setSourceInformation(SourceInformationFactory.getSourceInformation(getServerUrl()));

        final List<ResetResponse> workerResetResponses = remoteControlService.resetWorkers(orchestratorSetupRequest);

        resetResponse.setNestedResponses(workerResetResponses);

        orchestratorSetupRequest = null;
        orchestratorSetupResponse = null;

        resetResponse.setReset(true);

        resetResponse.getTimeFrame().setEndTimestamp(OffsetDateTime.now());

        return resetResponse;

    }

    public StartStopResponse start() throws ClientApiFacadeException, StartStopException {

        if(orchestratorSetupRequest ==null){
            throw new StartStopException("Call setup before Start");
        }

        if(workloadCollectorService.hasResults()){
            throw new StartStopException("Call reset to clean available results");
        }

        log.info("Starting Orchestrator");

        final StartStopResponse startStopResponse = new StartStopResponse();
        startStopResponse.setTimeFrame(TimeFrameFactory.getTimeFrame());
        startStopResponse.setSourceInformation(SourceInformationFactory.getSourceInformation(getServerUrl()));
        startStopResponse.setNestedResponses(remoteControlService.startWorkers(orchestratorSetupRequest));
        startStopResponse.getTimeFrame().setEndTimestamp(OffsetDateTime.now());
        startStopResponse.setStatus(StartStopResponse.StatusEnum.STARTED);

        return startStopResponse;

    }

    public StartStopResponse stop() throws ClientApiFacadeException, StartStopException {

        if(orchestratorSetupRequest ==null){
            throw new StartStopException("Call setup before Stop");
        }

        log.info("Stopping Orchestrator");

        final StartStopResponse startStopResponse = new StartStopResponse();
        startStopResponse.setTimeFrame(TimeFrameFactory.getTimeFrame());
        startStopResponse.setSourceInformation(SourceInformationFactory.getSourceInformation(getServerUrl()));
        startStopResponse.setNestedResponses(remoteControlService.stopWorkers(orchestratorSetupRequest));
        startStopResponse.getTimeFrame().setEndTimestamp(OffsetDateTime.now());
        startStopResponse.setStatus(StartStopResponse.StatusEnum.STOPPED);

        return startStopResponse;
    }

    public BenchmarkResultResponse collect(final BenchmarkResultRequest benchmarkResultRequest) throws WorkloadCollectorException {

        return workloadCollectorService.collect(benchmarkResultRequest);

    }

    public ResponseEntity<PingResponse> ping() {

        return pingResponderService.pong(true);

    }

    public OrchestratorResultResponse results() {

        return workloadCollectorService.results(orchestratorSetupRequest);

    }
}
