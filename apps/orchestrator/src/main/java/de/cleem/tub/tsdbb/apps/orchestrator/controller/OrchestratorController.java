package de.cleem.tub.tsdbb.apps.orchestrator.controller;

import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.api.orchestrator.server.*;
import de.cleem.tub.tsdbb.apps.orchestrator.service.collector.WorkloadCollectorException;
import de.cleem.tub.tsdbb.apps.orchestrator.service.orchestrator.OrchestratorService;
import de.cleem.tub.tsdbb.apps.orchestrator.service.orchestrator.ResultException;
import de.cleem.tub.tsdbb.commons.api.ClientApiFacadeException;
import de.cleem.tub.tsdbb.commons.exceptions.SetupException;
import de.cleem.tub.tsdbb.commons.exceptions.StartStopException;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import de.cleem.tub.tsdbb.commons.spring.pingresponder.PingResponderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class OrchestratorController extends BaseSpringComponent implements OrchestratorSetupApi, OrchestratorCollectApi,OrchestratorPingApi, OrchestratorResetApi, OrchestratorStartApi, OrchestratorStopApi, OrchestratorResultsApi {

    @Autowired
    private OrchestratorService orchestratorService;

    @Override
    public ResponseEntity<OrchestratorSetupResponse> setup(final OrchestratorSetupRequest orchestratorSetupRequest) throws ClientApiFacadeException, PingResponderException, SetupException, StartStopException {

        return ResponseEntity.ok(orchestratorService.setup(orchestratorSetupRequest));

    }

    @Override
    public ResponseEntity<PingResponse> ping() {

        return orchestratorService.ping();

    }

    @Override
    public ResponseEntity<ResetResponse> reset() throws ClientApiFacadeException, StartStopException {
        return ResponseEntity.ok(orchestratorService.reset());
    }

    @Override
    public ResponseEntity<StartStopResponse> start() throws StartStopException, ClientApiFacadeException {

        return ResponseEntity.ok(orchestratorService.start());

    }

    @Override
    public ResponseEntity<StartStopResponse> stop() throws ClientApiFacadeException, StartStopException {
        return ResponseEntity.ok(orchestratorService.stop());
    }

    @Override
    public ResponseEntity<BenchmarkResultResponse> collect(BenchmarkResultRequest benchmarkResultRequest) throws WorkloadCollectorException {
        return ResponseEntity.ok(orchestratorService.collect(benchmarkResultRequest));
    }

    @Override
    public ResponseEntity<OrchestratorResultResponse> results() throws ResultException {
        return ResponseEntity.ok(orchestratorService.results());
    }
}
