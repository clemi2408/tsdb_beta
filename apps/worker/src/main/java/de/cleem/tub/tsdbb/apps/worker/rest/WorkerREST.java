package de.cleem.tub.tsdbb.apps.worker.rest;

import de.cleem.tub.tsdbb.api.model.PingResponse;
import de.cleem.tub.tsdbb.api.model.WorkerPreloadRequest;
import de.cleem.tub.tsdbb.api.model.WorkerPreloadResponse;
import de.cleem.tub.tsdbb.api.worker.server.WorkerPingApi;
import de.cleem.tub.tsdbb.api.worker.server.WorkerPreloadApi;
import de.cleem.tub.tsdbb.apps.worker.service.WorkerService;
import de.cleem.tub.tsdbb.commons.api.ClientApiFacadeException;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import de.cleem.tub.tsdbb.commons.spring.objectcache.SingleObjectInstanceCacheException;
import de.cleem.tub.tsdbb.commons.spring.ping.PingException;
import de.cleem.tub.tsdbb.commons.spring.ping.PingHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class WorkerREST extends BaseSpringComponent implements WorkerPingApi, WorkerPreloadApi {

    @Autowired
    private WorkerService workerService;

    @Override
    public ResponseEntity<PingResponse> ping() {

        return PingHelper.pong(true);

    }

    @Override
    public ResponseEntity<WorkerPreloadResponse> preload(final WorkerPreloadRequest workerPreloadRequest) throws SingleObjectInstanceCacheException, ClientApiFacadeException, PingException {

        return ResponseEntity.ok(workerService.preload(workerPreloadRequest));

    }
}
