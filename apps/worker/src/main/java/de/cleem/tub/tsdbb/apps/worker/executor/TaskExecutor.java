package de.cleem.tub.tsdbb.apps.worker.executor;


import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.api.model.Record;
import de.cleem.tub.tsdbb.apps.worker.adapters.BaseConnector;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterException;
import de.cleem.tub.tsdbb.commons.api.ClientApiFacadeException;
import de.cleem.tub.tsdbb.commons.recordsplit.RecordListSplitter;
import de.cleem.tub.tsdbb.commons.spring.remotecontrol.RemoteControlService;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class TaskExecutor extends BaseConnector {

    private final URI workerUrl;
    private ExecutorService executor;

    private List<Future<TaskResult>> futureTaskResults;

    private List<TaskRequest> threads;

    final RemoteControlService remoteControlService;
    private WorkerTsdbEndpoint firstEndpoint;

    public TaskExecutor(final RemoteControlService remoteControlService, final URI workerUrl) {
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


        if (workerSetupRequest == null) {
            throw new ExecutionException("Call Setup before Start");
        }

        if (executor.isShutdown()) {
            initThreadRuntime();
        }

        log.info("Invoking " + threads.size() + " TaskRequests");

        try {

            futureTaskResults = executor.invokeAll(threads);
            remoteControlService.collect(collectResults(), workerSetupRequest);

        } catch (InterruptedException | ClientApiFacadeException | ExecutionException e) {
            try {
                throw new ExecutionException(e);
            } catch (ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        }

    }

    public void stop(){

        log.info("Shutting down ThreadPool");

        if(executor!=null) {

            executor.shutdown();

        }


    }

    public void cleanup() throws TSDBAdapterException {

        if(workerSetupRequest==null){
            throw new TSDBAdapterException("Run setup first");
        }

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

        final List<WorkerTsdbEndpoint> endpoints = workerSetupRequest.getWorkerConfiguration().getTsdbEndpoints();
        final List<Record> records = workerSetupRequest.getBenchmarkWorkload().getRecords();
        final LinkedHashMap<Integer[], WorkerTsdbEndpoint> lookupIntervals = RecordListSplitter.createLookupIntervals(endpoints,WorkerTsdbEndpoint::getEndpointPercentage);
        final Integer upperBoundEndpoints = RecordListSplitter.getUpperBound(lookupIntervals, WorkerTsdbEndpoint::getEndpointPercentage);

        int recordCount = 0;
        WorkerTsdbEndpoint endpoint;
        for (Record record : records) {
            recordCount++;

            if (recordCount % 10 == 0) {
                log.debug("Created Task: " + recordCount + "/" + records.size());
            }

            endpoint=RecordListSplitter.doRangeLookup(lookupIntervals,upperBoundEndpoints,WorkerTsdbEndpoint::getEndpointPercentage);

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
