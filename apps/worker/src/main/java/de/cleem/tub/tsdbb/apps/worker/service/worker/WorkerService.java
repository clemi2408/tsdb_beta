package de.cleem.tub.tsdbb.apps.worker.service.worker;


import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.api.orchestrator.client.OrchestratorPingApi;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterException;
import de.cleem.tub.tsdbb.apps.worker.executor.ExecutionException;
import de.cleem.tub.tsdbb.apps.worker.service.run.RunService;
import de.cleem.tub.tsdbb.commons.api.ClientApiFacadeException;
import de.cleem.tub.tsdbb.commons.factories.sourceInformation.SourceInformationFactory;
import de.cleem.tub.tsdbb.commons.factories.timeFrame.TimeFrameFactory;
import de.cleem.tub.tsdbb.commons.spring.apiclient.ApiClientService;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import de.cleem.tub.tsdbb.commons.spring.pingresponder.PingResponderException;
import de.cleem.tub.tsdbb.commons.spring.pingresponder.PingResponderService;
import de.cleem.tub.tsdbb.commons.status.StatusItems;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.time.OffsetDateTime;


@Component
@Slf4j
@ApplicationScope
public class WorkerService extends BaseSpringComponent {

    @Autowired
    private ApiClientService apiClientService;

    @Autowired
    private PingResponderService pingResponderService;

    @Autowired
    private RunService runService;
    private WorkerPreloadRequest workerPreloadRequest;


    public WorkerPreloadResponse preload(final WorkerPreloadRequest workerPreloadRequest) throws ClientApiFacadeException, PingResponderException, TSDBAdapterException, ExecutionException {


        log.info("Preloading Worker");

        log.info("Received Workload: "+workerPreloadRequest.getBenchmarkWorkload().getRecords().size());

        final WorkerPreloadResponse workerPreloadResponse = new WorkerPreloadResponse();
        workerPreloadResponse.setTimeFrame(TimeFrameFactory.getTimeFrame());
        workerPreloadResponse.setSourceInformation(SourceInformationFactory.getSourceInformation(getServerUrl()));

        this.workerPreloadRequest = workerPreloadRequest;

        final OrchestratorPingApi orchestratorPingApi = apiClientService.getApi(OrchestratorPingApi.class,workerPreloadRequest.getOrchestratorUrl());

        pingResponderService.checkPingResponse(orchestratorPingApi.ping(),"orchestrator",workerPreloadRequest.getOrchestratorUrl());

        runService.setup(workerPreloadRequest);

        workerPreloadResponse.preloadRequest(workerPreloadRequest);
        workerPreloadResponse.getTimeFrame().setEndTimestamp(OffsetDateTime.now());

        return workerPreloadResponse;
    }

    public ResetResponse reset() throws TSDBAdapterException {

        log.info("Resetting Worker");

        stop();
        cleanup();
        close();

        final ResetResponse resetResponse = new ResetResponse();
        resetResponse.setTimeFrame(TimeFrameFactory.getTimeFrame());
        resetResponse.setSourceInformation(SourceInformationFactory.getSourceInformation(getServerUrl()));
        resetResponse.setReset(false);

        if(workerPreloadRequest!=null){

            this.workerPreloadRequest = null;

            resetResponse.setReset(true);

        }

        resetResponse.getTimeFrame().setEndTimestamp(OffsetDateTime.now());
        return resetResponse;

    }

    public StartStopResponse start() throws ExecutionException {

        log.info("Starting Worker");

        final StartStopResponse startStopResponse = new StartStopResponse();
        startStopResponse.setTimeFrame(TimeFrameFactory.getTimeFrame());
        startStopResponse.setSourceInformation(SourceInformationFactory.getSourceInformation(getServerUrl()));

        runService.start();

        startStopResponse.getTimeFrame().setEndTimestamp(OffsetDateTime.now());

        startStopResponse.setStatus(StatusItems.STARTED_STRING);

        return startStopResponse;
    }

    public StartStopResponse stop() {

        log.info("Stopping Worker");

        final StartStopResponse startStopResponse = new StartStopResponse();
        startStopResponse.setTimeFrame(TimeFrameFactory.getTimeFrame());
        startStopResponse.setSourceInformation(SourceInformationFactory.getSourceInformation(getServerUrl()));

        runService.stop();

        startStopResponse.setStatus(StatusItems.STOPPED_STRING);

        startStopResponse.getTimeFrame().setEndTimestamp(OffsetDateTime.now());
        return startStopResponse;
    }

    public void cleanup() throws TSDBAdapterException {
        runService.cleanup();
    }

    public void close(){
        runService.close();
    }

    public ResponseEntity<PingResponse> ping() {

        return pingResponderService.pong(true);

    }
}
