package de.cleem.tub.tsdbb.apps.worker.service;


import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.api.orchestrator.client.OrchestratorPingApi;
import de.cleem.tub.tsdbb.commons.api.ClientApiFacadeException;
import de.cleem.tub.tsdbb.commons.spring.apiclient.ApiClientService;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import de.cleem.tub.tsdbb.commons.spring.objectcache.SingleObjectInstanceCache;
import de.cleem.tub.tsdbb.commons.spring.objectcache.SingleObjectInstanceCacheException;
import de.cleem.tub.tsdbb.commons.spring.ping.PingException;
import de.cleem.tub.tsdbb.commons.spring.ping.PingHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;


@Component
@Slf4j
public class WorkerService extends BaseSpringComponent {

    private final static Class[] CLASSES_TO_RESET = new Class[]{WorkerPreloadRequest.class};

    @Autowired
    private ApiClientService apiClientService;

    @Autowired
    private SingleObjectInstanceCache cache;


    public WorkerPreloadResponse preload(final WorkerPreloadRequest workerPreloadRequest) throws SingleObjectInstanceCacheException, ClientApiFacadeException, PingException {
        log.info("Received Workload: "+workerPreloadRequest.getBenchmarkWorkload().getRecords().size());

        final WorkerPreloadResponse workerPreloadResponse = new WorkerPreloadResponse();
        workerPreloadResponse.setStartTimestamp(OffsetDateTime.now());

        cache.add(workerPreloadRequest);

        final OrchestratorPingApi orchestratorPingApi = apiClientService.getApi(OrchestratorPingApi.class,workerPreloadRequest.getOrchestratorUrl());

        PingHelper.checkPingResponse(orchestratorPingApi.ping(),"orchestrator",workerPreloadRequest.getOrchestratorUrl());

        workerPreloadResponse.preloadRequest(workerPreloadRequest);
        workerPreloadResponse.setEndTimestamp(OffsetDateTime.now());

        return workerPreloadResponse;
    }

    public ResetResponse reset() {

        return cache.reset(CLASSES_TO_RESET);

    }
}
