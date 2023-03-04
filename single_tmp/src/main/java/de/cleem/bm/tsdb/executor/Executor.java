package de.cleem.bm.tsdb.executor;


import de.cleem.bm.tsdb.common.random.values.numbers.RandomUniformHelper;
import de.cleem.bm.tsdb.model.config.TSDBConfig;
import de.cleem.bm.tsdb.model.request.TaskRequest;
import de.cleem.bm.tsdb.model.result.BenchmarkResult;
import de.cleem.bm.tsdb.model.result.TaskResult;
import de.cleem.tub.tsdbb.commons.exception.BaseException;
import de.cleem.tub.tsdbb.commons.model.workload.KvPair;
import de.cleem.tub.tsdbb.commons.model.workload.Record;
import de.cleem.tub.tsdbb.commons.model.workload.Workload;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedList;
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


    public Executor(final TSDBConfig tsdbConfig) throws BaseException {

        log.info("Constructing: " + this.getClass().getSimpleName());
        this.tsdbConfig = tsdbConfig;
        this.tsdbConfig.setBenchmarkWorkload(getBenchmarkWorkload());

        setStorageAdapter();

        tsdbInterface.createStorage();

    }

    private Workload getBenchmarkWorkload() {

        final Workload benchmarkWorkload = Workload.builder().records(new LinkedList<>()).build();

        Record benchmarkRecord;
        KvPair benchmarkWorkloadRecordKvPair;
        Object[] benchmarkWorkloadRecordKvPairValues;

        for (Record inputRecord : tsdbConfig.getInputWorkload().getRecords()) {

            benchmarkRecord = Record.builder()
                    .recordId(inputRecord.getRecordId())
                    .kvPairs(new ArrayList<>())
                    .build();

            for (KvPair inputWorkloadRecordKvPair : inputRecord.getKvPairs()) {

                if (inputWorkloadRecordKvPair.getValue().length == 1) {
                    benchmarkWorkloadRecordKvPairValues = inputWorkloadRecordKvPair.getValue();
                } else {
                    benchmarkWorkloadRecordKvPairValues = pickSingleValue(inputWorkloadRecordKvPair.getValue());
                }

                benchmarkWorkloadRecordKvPair = KvPair.builder()
                        .key(inputWorkloadRecordKvPair.getKey())
                        .value(benchmarkWorkloadRecordKvPairValues)
                        .build();

                benchmarkRecord.getKvPairs().add(benchmarkWorkloadRecordKvPair);

            }

            benchmarkWorkload.getRecords().add(benchmarkRecord);

        }

        return benchmarkWorkload;


    }

    private Object[] pickSingleValue(final Object[] multiValueArray) {

        final int randomIndex = RandomUniformHelper.getInteger(0, multiValueArray.length - 1);

        return new Object[]{multiValueArray[randomIndex]};

    }


    public void execute() throws BaseException {


        log.info("Creating ThreadPool using " + tsdbConfig.getThreadCount() + " Threads");

        executor = Executors.newFixedThreadPool(tsdbConfig.getThreadCount());

        ArrayList<TaskRequest> taskRequests = new ArrayList<>();

        int recordCount = 0;
        for (Record record : tsdbConfig.getBenchmarkWorkload().getRecords()) {
            recordCount++;

            if (recordCount % 10 == 0) {
                log.info("Created Task: " + recordCount + "/" + tsdbConfig.getInputWorkload().getRecords().size());
            }

            taskRequests.add(new TaskRequest(tsdbConfig, "Record " + recordCount, record));

        }

        log.info("Created " + taskRequests.size() + " TaskRequests");

        try {

            log.info("Invoking " + taskRequests.size() + " TaskRequests");
            futureTaskResults = executor.invokeAll(taskRequests);

        } catch (InterruptedException e) {
            throw new ExecutionException(e);
        }

    }

    public void shutdown() throws BaseException {

        log.info("Shutting down ThreadPool");

        executor.shutdown();

        collectResults();

        if (tsdbConfig.isCleanStorage()) {
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
