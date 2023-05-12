package de.cleem.tub.tsdbb.apps.orchestrator.service.collector;


import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.api.model.OrchestratorResultResponse.StatusEnum;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@ApplicationScope
public class WorkloadCollectorService extends BaseSpringComponent {

    private final Map<URI,List<TaskResult>> resultMap = new ConcurrentHashMap<>();

    public BenchmarkResultResponse collect(final BenchmarkResultRequest benchmarkResultRequest) throws WorkloadCollectorException {


        final URI workerUrl = benchmarkResultRequest.getSourceInformation().getUrl();

        if(resultMap.containsKey(workerUrl)){
            throw new WorkloadCollectorException("Worker "+workerUrl+" already collected");
        }

        log.info("Received result from worker "+workerUrl+" with "+benchmarkResultRequest.getTaskResults().size()+" results");

        resultMap.put(workerUrl,benchmarkResultRequest.getTaskResults());

        final BenchmarkResultResponse benchmarkResultResponse = new BenchmarkResultResponse();
        benchmarkResultResponse.setDeliveryCount(BigDecimal.valueOf(benchmarkResultRequest.getTaskResults().size()));

        return benchmarkResultResponse;
    }

    public OrchestratorResultResponse results(final OrchestratorSetupRequest orchestratorSetupRequest) {

        final int totalResultParts = orchestratorSetupRequest.getWorkerConnectionSettings().getWorkerConfigurations().size();
        final int completedParts = resultMap.size();

        final StatusEnum status = totalResultParts==completedParts?StatusEnum.COMPLETE:StatusEnum.INCOMPLETE;

        final OrchestratorResultResponse orchestratorResultResponse = new OrchestratorResultResponse();
        orchestratorResultResponse.setStatus(status);
        orchestratorResultResponse.setTotalResultParts(totalResultParts);
        orchestratorResultResponse.setCompletedParts(completedParts);
        orchestratorResultResponse.setTaskResults(aggregateResult());

        return orchestratorResultResponse;

    }

    private List<TaskResult> aggregateResult(){

        final List<TaskResult> aggregatedResult = new ArrayList<>();

        for(Map.Entry<URI, List<TaskResult>> entry : resultMap.entrySet()){

            aggregatedResult.addAll(entry.getValue());

        }

        return aggregatedResult;

    }

    public boolean hasResults(){
        return !resultMap.isEmpty();
    }

    public void reset(){

        if(hasResults()){
            resultMap.clear();
        }

    }

}
