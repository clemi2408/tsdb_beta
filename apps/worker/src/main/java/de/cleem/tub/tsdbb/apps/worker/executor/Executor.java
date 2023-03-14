package de.cleem.tub.tsdbb.apps.worker.executor;


import de.cleem.tub.tsdbb.api.model.Record;
import de.cleem.tub.tsdbb.api.model.TaskResult;
import de.cleem.tub.tsdbb.api.model.WorkerPreloadRequest;
import de.cleem.tub.tsdbb.apps.worker.adapters.BaseConnector;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterException;
import de.cleem.tub.tsdbb.commons.api.ClientApiFacadeException;
import de.cleem.tub.tsdbb.commons.spring.remotecontrol.RemoteControlService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class Executor extends BaseConnector {

    private final String workerUrl;
    private ExecutorService executor;

    private List<Future<TaskResult>> futureTaskResults;

    private List<TaskRequest> threads;

    final RemoteControlService remoteControlService;

    public Executor(final RemoteControlService remoteControlService, final String workerUrl) {
        this.remoteControlService = remoteControlService;
        this.workerUrl = workerUrl;
        log.info("Constructing: " + this.getClass().getSimpleName());

    }

    public void setup(final WorkerPreloadRequest workerPreloadRequest) throws ExecutionException, TSDBAdapterException {

        this.workerPreloadRequest = workerPreloadRequest;

        setStorageAdapter();

        if (workerPreloadRequest.getWorkerConfig().getCreateStorage()) {
            log.info("Creating storage");
            tsdbInterface.createStorage();
        }


        initThreadRuntime();


    }

    public void start() throws ExecutionException {

        try {

            log.info("Invoking " + threads.size() + " TaskRequests");

            if(this.workerPreloadRequest==null){
                throw new ExecutionException("Call preload before Start");
            }

            if(executor.isShutdown()){
                initThreadRuntime();
            }

            futureTaskResults = executor.invokeAll(threads);

            remoteControlService.collect(collectResults(),workerPreloadRequest);

        } catch (InterruptedException | ClientApiFacadeException e) {
            throw new ExecutionException(e);
        }

    }

    public void stop(){

        log.info("Shutting down ThreadPool");

        executor.shutdown();


    }

    public void cleanup() throws TSDBAdapterException {

        if (workerPreloadRequest.getWorkerConfig().getCleanupStorage()) {
            log.info("Cleaning up storage");
            tsdbInterface.cleanup();
        }

    }

    public void close(){

        tsdbInterface.close();

    }


    ////

    private void initThreadRuntime() {
        executor = getExecutorService();
        threads = createTasks();
    }

    private ExecutorService getExecutorService(){
        log.info("Creating ThreadPool using " + workerPreloadRequest.getWorkerConfig().getWorkerThreads() + " Threads");

        return Executors.newFixedThreadPool(workerPreloadRequest.getWorkerConfig().getWorkerThreads());


    }

    private List<TaskRequest> createTasks() {

        log.info("Creating Tasks");

        final List<TaskRequest> threads = new ArrayList<>();

        int recordCount = 0;
        for (Record record : workerPreloadRequest.getBenchmarkWorkload().getRecords()) {
            recordCount++;

            if (recordCount % 10 == 0) {
                log.debug("Created Task: " + recordCount + "/" + workerPreloadRequest.getBenchmarkWorkload().getRecords().size());
            }

            threads.add(new TaskRequest(workerUrl,tsdbInterface, workerPreloadRequest,"Task: " + recordCount, record));

        }

        log.info("Created " + threads.size() + " Tasks");

        return threads;

    }

    private List<TaskResult> collectResults() throws ExecutionException {

        log.info("Collecting results");

       final List<TaskResult> taskResults = new ArrayList<>();

        TaskResult taskResult;
        for (Future<TaskResult> taskResultFuture : futureTaskResults) {

            try {
                taskResult = taskResultFuture.get();
                taskResults.add(taskResult);

            } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
                throw new ExecutionException(e);
            }


        }

        return taskResults;

    }


}
