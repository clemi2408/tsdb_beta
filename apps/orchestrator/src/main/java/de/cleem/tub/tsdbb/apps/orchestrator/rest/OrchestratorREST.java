package de.cleem.tub.tsdbb.apps.orchestrator.rest;

import de.cleem.tub.tsdbb.api.model.OrchestratorPreloadRequest;
import de.cleem.tub.tsdbb.api.model.OrchestratorPreloadResponse;
import de.cleem.tub.tsdbb.api.model.PingResponse;
import de.cleem.tub.tsdbb.api.model.ResetResponse;
import de.cleem.tub.tsdbb.api.orchestrator.server.OrchestratorPingApi;
import de.cleem.tub.tsdbb.api.orchestrator.server.OrchestratorPreloadApi;
import de.cleem.tub.tsdbb.api.orchestrator.server.OrchestratorResetApi;
import de.cleem.tub.tsdbb.apps.orchestrator.service.OrchestratorService;
import de.cleem.tub.tsdbb.commons.api.ClientApiFacadeException;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import de.cleem.tub.tsdbb.commons.spring.objectcache.SingleObjectInstanceCacheException;
import de.cleem.tub.tsdbb.commons.spring.pingresponder.PingResponderException;
import de.cleem.tub.tsdbb.commons.spring.pingresponder.PingResponderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class OrchestratorREST extends BaseSpringComponent implements OrchestratorPreloadApi,OrchestratorPingApi, OrchestratorResetApi {

    @Autowired
    private OrchestratorService orchestratorService;

    @Autowired
    private PingResponderService pingResponderService;

    @Override
    public ResponseEntity<OrchestratorPreloadResponse> preload(final OrchestratorPreloadRequest orchestratorPreloadRequest) throws ClientApiFacadeException, PingResponderException, SingleObjectInstanceCacheException {

        return ResponseEntity.ok(orchestratorService.preload(orchestratorPreloadRequest));

    }

    @Override
    public ResponseEntity<PingResponse> ping() {

        return pingResponderService.pong(true);

    }

    @Override
    public ResponseEntity<ResetResponse> reset() throws SingleObjectInstanceCacheException, ClientApiFacadeException {
        return ResponseEntity.ok(orchestratorService.reset());
    }
}
