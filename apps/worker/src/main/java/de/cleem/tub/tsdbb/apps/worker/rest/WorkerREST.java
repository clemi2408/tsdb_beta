package de.cleem.tub.tsdbb.apps.worker.rest;

import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.api.worker.server.*;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterException;
import de.cleem.tub.tsdbb.apps.worker.executor.ExecutionException;
import de.cleem.tub.tsdbb.apps.worker.service.WorkerService;
import de.cleem.tub.tsdbb.commons.api.ClientApiFacadeException;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import de.cleem.tub.tsdbb.commons.spring.objectcache.SingleObjectInstanceCacheException;
import de.cleem.tub.tsdbb.commons.spring.pingresponder.PingResponderException;
import de.cleem.tub.tsdbb.commons.spring.pingresponder.PingResponderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class WorkerREST extends BaseSpringComponent implements WorkerPingApi, WorkerPreloadApi, WorkerResetApi, WorkerStartApi, WorkerStopApi {

    @Autowired
    private WorkerService workerService;

    @Autowired
    private PingResponderService pingResponderService;

    @Override
    public ResponseEntity<PingResponse> ping() {

        return pingResponderService.pong(true);

    }

    @Override
    public ResponseEntity<WorkerPreloadResponse> preload(final WorkerPreloadRequest workerPreloadRequest) throws SingleObjectInstanceCacheException, ClientApiFacadeException, PingResponderException, TSDBAdapterException, ExecutionException {

        return ResponseEntity.ok(workerService.preload(workerPreloadRequest));

    }

    @Override
    public ResponseEntity<ResetResponse> reset() {

        return ResponseEntity.ok(workerService.reset());

    }

    @Override
    public ResponseEntity<StartStopResponse> start() throws TSDBAdapterException, ExecutionException {

        return ResponseEntity.ok(workerService.start());

    }

    @Override
    public ResponseEntity<StartStopResponse> stop() throws TSDBAdapterException, ExecutionException {

        return ResponseEntity.ok(workerService.stop());
    }
}
