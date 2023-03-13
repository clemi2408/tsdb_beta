package de.cleem.tub.tsdbb.apps.worker.service;


import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.api.orchestrator.client.OrchestratorPingApi;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterException;
import de.cleem.tub.tsdbb.apps.worker.executor.ExecutionException;
import de.cleem.tub.tsdbb.commons.api.ClientApiFacadeException;
import de.cleem.tub.tsdbb.commons.spring.apiclient.ApiClientService;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import de.cleem.tub.tsdbb.commons.spring.objectcache.SingleObjectInstanceCache;
import de.cleem.tub.tsdbb.commons.spring.objectcache.SingleObjectInstanceCacheException;
import de.cleem.tub.tsdbb.commons.spring.pingresponder.PingResponderException;
import de.cleem.tub.tsdbb.commons.spring.pingresponder.PingResponderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;


@Component
@Slf4j
public class WorkerService extends BaseSpringComponent {

    private static final String STARTED_STRING = "STARTED";
    private static final String STOPPED_STRING = "STOPPED";

    private final static Class[] CLASSES_TO_RESET = new Class[]{WorkerPreloadRequest.class};

    @Autowired
    private ApiClientService apiClientService;

    @Autowired
    private PingResponderService pingResponderService;

    @Autowired
    private SingleObjectInstanceCache cache;

    @Autowired
    private RunService runService;


    public WorkerPreloadResponse preload(final WorkerPreloadRequest workerPreloadRequest) throws SingleObjectInstanceCacheException, ClientApiFacadeException, PingResponderException, TSDBAdapterException, ExecutionException {
        log.info("Received Workload: "+workerPreloadRequest.getBenchmarkWorkload().getRecords().size());

        final WorkerPreloadResponse workerPreloadResponse = new WorkerPreloadResponse();
        workerPreloadResponse.setStartTimestamp(OffsetDateTime.now());

        cache.add(workerPreloadRequest);

        final OrchestratorPingApi orchestratorPingApi = apiClientService.getApi(OrchestratorPingApi.class,workerPreloadRequest.getOrchestratorUrl());

        pingResponderService.checkPingResponse(orchestratorPingApi.ping(),"orchestrator",workerPreloadRequest.getOrchestratorUrl());

        runService.setup(workerPreloadRequest);

        workerPreloadResponse.preloadRequest(workerPreloadRequest);
        workerPreloadResponse.setEndTimestamp(OffsetDateTime.now());

        return workerPreloadResponse;
    }

    public ResetResponse reset() {

        return cache.reset(CLASSES_TO_RESET);

    }

    public StartStopResponse start() throws TSDBAdapterException, ExecutionException {

        final StartStopResponse startStopResponse = new StartStopResponse();
        startStopResponse.setStartTimestamp(OffsetDateTime.now());
        runService.start();
        startStopResponse.setEndTimestamp(OffsetDateTime.now());

        startStopResponse.setStatus(STARTED_STRING);

        return startStopResponse;
    }

    public StartStopResponse stop() throws TSDBAdapterException, ExecutionException {

        final StartStopResponse startStopResponse = new StartStopResponse();
        startStopResponse.setStartTimestamp(OffsetDateTime.now());
        runService.stop();
        startStopResponse.setEndTimestamp(OffsetDateTime.now());

        startStopResponse.setStatus(STOPPED_STRING);

        reset();

        return startStopResponse;
    }
}
