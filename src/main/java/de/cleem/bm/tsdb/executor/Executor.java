package de.cleem.bm.tsdb.executor;

import de.cleem.bm.tsdb.common.exception.TsdbBenchmarkException;
import de.cleem.bm.tsdb.model.config.TSDBConfig;
import de.cleem.bm.tsdb.model.result.BenchmarkResult;
import de.cleem.bm.tsdb.model.task.TaskRequest;
import de.cleem.bm.tsdb.model.task.TaskResult;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class Executor extends BaseConnector {

    private ExecutorService executor;

    private List<Future<TaskResult>> futureTaskResults;


    @Getter
    private BenchmarkResult benchmarkResult;



    public Executor(final TSDBConfig config) throws TsdbBenchmarkException {

        log.info("Constructing: "+this.getClass().getSimpleName());
        this.config = config;

        setStorageAdapter();

        tsdbInterface.createStorage();

    }



    public void execute() throws TsdbBenchmarkException {


        log.info("Creating ThreadPool using "+config.getThreadCount()+" Threads");

        executor = Executors.newFixedThreadPool(config.getThreadCount());

        ArrayList<TaskRequest> taskRequests = new ArrayList<>();

        int recordCount = 0;
        for(HashMap<String,Number> currentRecord: config.getWorkload().getData()){
            recordCount++;

            if(recordCount%10==0){
                log.info("Created Task: "+recordCount+"/"+config.getWorkload().getData().size());
            }

            taskRequests.add(new TaskRequest(config,"Record "+recordCount,currentRecord));

        }

        log.info("Created "+taskRequests.size()+" TaskRequests");

        try {

            log.info("Invoking "+taskRequests.size()+" TaskRequests");
            futureTaskResults = executor.invokeAll(taskRequests);

        } catch (InterruptedException e) {
            throw new ExecutionException(e);
        }

    }

    public void shutdown() throws TsdbBenchmarkException {

        log.info("Shutting down ThreadPool");

        executor.shutdown();

        collectResults();

        if(config.isCleanStorage()) {
            tsdbInterface.cleanup();
        }

        tsdbInterface.close();


    }

    private void collectResults() throws ExecutionException {

        log.info("Collecting results");

        benchmarkResult = BenchmarkResult.builder()
                .results(new ArrayList<>())
                .build();

        TaskResult taskResult;
        for(Future<TaskResult> taskResultFuture : futureTaskResults){

            try {
                taskResult = taskResultFuture.get();
                benchmarkResult.getResults().add(taskResult);

            } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
                throw new ExecutionException(e);
            }


        }

    }

    public void printResults(){

        log.info("Printing results");

        for(TaskResult taskResult : benchmarkResult.getResults()){

            log.info("\tResult: "+taskResult.getTaskName()+" --> "+taskResult.getDurationInMs()+" ms "+taskResult.getRecord().toString());

        }


    }
}
