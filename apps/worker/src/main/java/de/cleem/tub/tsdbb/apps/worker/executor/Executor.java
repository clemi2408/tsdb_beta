package de.cleem.tub.tsdbb.apps.worker.executor;


import de.cleem.tub.tsdbb.api.model.Record;
import de.cleem.tub.tsdbb.api.model.TaskResult;
import de.cleem.tub.tsdbb.api.model.WorkerSetupRequest;
import de.cleem.tub.tsdbb.api.model.WorkerTsdbEndpoint;
import de.cleem.tub.tsdbb.apps.worker.adapters.BaseConnector;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterException;
import de.cleem.tub.tsdbb.commons.api.ClientApiFacadeException;
import de.cleem.tub.tsdbb.commons.spring.remotecontrol.RemoteControlService;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class Executor extends BaseConnector {

    private final URI workerUrl;
    private ExecutorService executor;

    private List<Future<TaskResult>> futureTaskResults;

    private List<TaskRequest> threads;

    final RemoteControlService remoteControlService;
    private WorkerTsdbEndpoint firstEndpoint;

    public Executor(final RemoteControlService remoteControlService, final URI workerUrl) {
        this.remoteControlService = remoteControlService;
        this.workerUrl = workerUrl;
        log.info("Constructing: " + this.getClass().getSimpleName());

    }

    public void setup(final WorkerSetupRequest workerSetupRequest) throws ExecutionException, TSDBAdapterException {

        this.workerSetupRequest = workerSetupRequest;
        this.firstEndpoint = workerSetupRequest.getWorkerConfiguration().getTsdbEndpoints().get(0);
        setStorageAdapter();

        if (workerSetupRequest.getWorkerGeneralProperties().getCreateStorage()) {
            log.info("Creating storage");
            tsdbInterface.createStorage(firstEndpoint);
        }


        initThreadRuntime();


    }

    public void start() throws ExecutionException {

        try {

            log.info("Invoking " + threads.size() + " TaskRequests");

            if(this.workerSetupRequest ==null){
                throw new ExecutionException("Call Setup before Start");
            }

            if(executor.isShutdown()){
                initThreadRuntime();
            }

            futureTaskResults = executor.invokeAll(threads);

            remoteControlService.collect(collectResults(), workerSetupRequest);

        } catch (InterruptedException | ClientApiFacadeException e) {
            throw new ExecutionException(e);
        }

    }

    public void stop(){

        log.info("Shutting down ThreadPool");

        executor.shutdown();


    }

    public void cleanup() throws TSDBAdapterException {

        if (workerSetupRequest.getWorkerGeneralProperties().getCleanupStorage()) {
            log.info("Cleaning up storage");
            tsdbInterface.cleanup(firstEndpoint);
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

        final Integer threads = workerSetupRequest.getWorkerConfiguration().getWorkerThreads();

        log.info("Creating ThreadPool using " + threads + " Threads");

        return Executors.newFixedThreadPool(threads);


    }

    private List<TaskRequest> createTasks() {

        log.info("Creating Tasks");

        final List<TaskRequest> threads = new ArrayList<>();

        int recordCount = 0;
        WorkerTsdbEndpoint endpoint;
        for (Record record : workerSetupRequest.getBenchmarkWorkload().getRecords()) {
            recordCount++;

            if (recordCount % 10 == 0) {
                log.debug("Created Task: " + recordCount + "/" + workerSetupRequest.getBenchmarkWorkload().getRecords().size());
            }

            /// SELECT ENDPOINT:
            endpoint=workerSetupRequest.getWorkerConfiguration().getTsdbEndpoints().get(0);


            threads.add(new TaskRequest(tsdbInterface, workerSetupRequest,endpoint,"Task: " + recordCount, record));

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
