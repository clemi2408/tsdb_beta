package de.cleem.tub.tsdbb.apps.worker.controller;

import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.api.worker.server.*;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterException;
import de.cleem.tub.tsdbb.apps.worker.executor.ExecutionException;
import de.cleem.tub.tsdbb.apps.worker.service.worker.WorkerService;
import de.cleem.tub.tsdbb.commons.api.ClientApiFacadeException;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import de.cleem.tub.tsdbb.commons.spring.pingresponder.PingResponderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class WorkerController extends BaseSpringComponent implements WorkerPingApi, WorkerPreloadApi, WorkerResetApi, WorkerStartApi, WorkerStopApi {

    @Autowired
    private WorkerService workerService;


    @Override
    public ResponseEntity<WorkerPreloadResponse> preload(final WorkerPreloadRequest workerPreloadRequest) throws ClientApiFacadeException, PingResponderException, TSDBAdapterException, ExecutionException {

        return ResponseEntity.ok(workerService.preload(workerPreloadRequest));

    }

    @Override
    public ResponseEntity<ResetResponse> reset() throws TSDBAdapterException {

        return ResponseEntity.ok(workerService.reset());

    }

    @Override
    public ResponseEntity<StartStopResponse> start() throws ExecutionException {

        return ResponseEntity.ok(workerService.start());

    }

    @Override
    public ResponseEntity<StartStopResponse> stop() {

        return ResponseEntity.ok(workerService.stop());
    }

    @Override
    public ResponseEntity<PingResponse> ping() {

        return workerService.ping();

    }
}
