package de.cleem.tub.tsdbb.apps.worker.executor;

import de.cleem.tub.tsdbb.api.model.WorkerPreloadRequest;
import de.cleem.tub.tsdbb.apps.worker.adapters.BaseConnector;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterException;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterIF;
import de.cleem.tub.tsdbb.commons.exception.BaseException;
import de.cleem.tub.tsdbb.api.model.Record;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.Callable;

@Slf4j
public class TaskRequest extends BaseConnector implements Callable<TaskResult> {

    private String taskName;
    private Record record;


    public TaskRequest(final TSDBAdapterIF tsdbAdapterIF, final WorkerPreloadRequest workerPreloadRequest, final String taskName, final Record record) throws ExecutionException, TSDBAdapterException {

        this.record = record;
        this.taskName = taskName;
        this.workerPreloadRequest = workerPreloadRequest;
        this.tsdbInterface = tsdbAdapterIF;

    }


    @Override
    public TaskResult call() throws Exception {

        log.debug("Calling Task "+taskName);

        /////
        final Date startDate = new Date();
        tsdbInterface.write(record);
        final Date endDate = new Date();
        ////

        final long durationInMs = endDate.getTime() - startDate.getTime();

       // tsdbInterface.close();

        final String threadName = Thread.currentThread().getName();


        log.info(taskName+" wrote Record: "+record.getRecordId());

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
