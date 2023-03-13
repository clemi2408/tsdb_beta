package de.cleem.tub.tsdbb.apps.worker.executor;


import de.cleem.tub.tsdbb.api.model.WorkerPreloadRequest;
import de.cleem.tub.tsdbb.apps.worker.adapters.BaseConnector;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterException;
import de.cleem.tub.tsdbb.apps.worker.adapters.model.BenchmarkResult;
import de.cleem.tub.tsdbb.api.model.Record;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class Executor extends BaseConnector {

    private ExecutorService executor;

    private List<Future<TaskResult>> futureTaskResults;

    private List<TaskRequest> threads;

    @Getter
    private BenchmarkResult benchmarkResult;

    public Executor() {

        log.info("Constructing: " + this.getClass().getSimpleName());

    }

    public void setup(final WorkerPreloadRequest workerPreloadRequest) throws ExecutionException, TSDBAdapterException {

        this.workerPreloadRequest = workerPreloadRequest;

        setStorageAdapter();

        if (workerPreloadRequest.getWorkerConfig().getCreateStorage()) {
            log.info("Creating storage");
            tsdbInterface.createStorage();
        }

        log.info("Creating ThreadPool using " + workerPreloadRequest.getWorkerConfig().getWorkerThreads() + " Threads");

        executor = Executors.newFixedThreadPool(workerPreloadRequest.getWorkerConfig().getWorkerThreads());

        log.info("Creating Tasks");

        threads = createTasks();

    }

    public void start() throws ExecutionException {

        try {

            log.info("Invoking " + threads.size() + " TaskRequests");
            futureTaskResults = executor.invokeAll(threads);

        } catch (InterruptedException e) {
            throw new ExecutionException(e);
        }

    }

    public void stop() throws ExecutionException, TSDBAdapterException {

        log.info("Shutting down ThreadPool");

        executor.shutdown();

        collectResults();

        if (workerPreloadRequest.getWorkerConfig().getCleanupStorage()) {
            log.info("Cleaning up storage");
            tsdbInterface.cleanup();
        }

        tsdbInterface.close();


    }

    ////
    private List<TaskRequest> createTasks() throws TSDBAdapterException, ExecutionException {

        final List<TaskRequest> threads = new ArrayList<>();

        int recordCount = 0;
        for (Record record : workerPreloadRequest.getBenchmarkWorkload().getRecords()) {
            recordCount++;

            if (recordCount % 10 == 0) {
                log.debug("Created Task: " + recordCount + "/" + workerPreloadRequest.getBenchmarkWorkload().getRecords().size());
            }

            threads.add(new TaskRequest(tsdbInterface, workerPreloadRequest,"Task: " + recordCount, record));

        }

        log.info("Created " + threads.size() + " Tasks");

        return threads;

    }

    private void collectResults() throws ExecutionException {

        log.info("Collecting results");

        benchmarkResult = BenchmarkResult.builder()
                .results(new ArrayList<>())
                .build();

        TaskResult taskResult;
        for (Future<TaskResult> taskResultFuture : futureTaskResults) {

            try {
                taskResult = taskResultFuture.get();
                benchmarkResult.getResults().add(taskResult);

            } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
                throw new ExecutionException(e);
            }


        }

    }

    public void printResults() {

        log.info("Printing results");

        for (TaskResult taskResult : benchmarkResult.getResults()) {

            log.info("\tResult: " + taskResult.getTaskName() + " --> " + taskResult.getDurationInMs() + " ms " + taskResult.getRecord().toString());

        }


    }
}
