package de.cleem.bm.tsdb.model.request;

import de.cleem.bm.tsdb.common.exception.TSDBBenchmarkException;
import de.cleem.bm.tsdb.executor.BaseConnector;
import de.cleem.bm.tsdb.model.config.TSDBConfig;
import de.cleem.bm.tsdb.model.config.workload.WorkloadRecord;
import de.cleem.bm.tsdb.model.result.TaskResult;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.Callable;

@Slf4j
public class TaskRequest extends BaseConnector implements Callable<TaskResult> {

    private String taskName;
    private WorkloadRecord record;



    public TaskRequest(final TSDBConfig config, final String taskName, final WorkloadRecord record) throws TSDBBenchmarkException {

         this.record = record;
         this.taskName=taskName;
         this.config=config;
         setStorageAdapter();

    }


    @Override
    public TaskResult call() throws Exception {

        /////
        final Date startDate = new Date();
        tsdbInterface.write(record);
        final Date endDate = new Date();
        ////

        final long durationInMs = endDate.getTime()-startDate.getTime();

        tsdbInterface.close();

        final String threadName = Thread.currentThread().getName();

        return TaskResult.builder()
                .taskName(taskName)
                .threadName(threadName)
                .record(record)
                .recordId(record.getRecordId())
                .startDate(startDate)
                .endDate(endDate)
                .durationInMs(durationInMs)
                .build();
    }
}
