package de.cleem.tub.tsdbb.apps.worker.rest;

import de.cleem.tub.tsdbb.api.model.PingResponse;
import de.cleem.tub.tsdbb.api.worker.server.WorkerPingApi;
import de.cleem.tub.tsdbb.apps.worker.service.WorkerService;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import de.cleem.tub.tsdbb.commons.spring.ping.PingResponder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class WorkerREST extends BaseSpringComponent implements WorkerPingApi  {

    @Autowired
    private WorkerService workerService;

    @Override
    public ResponseEntity<PingResponse> ping() {

        return PingResponder.pong(true);

    }

}
