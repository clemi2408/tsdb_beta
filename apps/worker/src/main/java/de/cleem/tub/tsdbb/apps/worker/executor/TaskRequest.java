package de.cleem.tub.tsdbb.apps.worker.executor;

import de.cleem.tub.tsdbb.api.model.*;
import de.cleem.tub.tsdbb.api.model.Record;
import de.cleem.tub.tsdbb.apps.worker.adapters.BaseConnector;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterIF;
import de.cleem.tub.tsdbb.commons.date.DateHelper;
import de.cleem.tub.tsdbb.commons.factories.sourceInformation.SourceInformationFactory;
import de.cleem.tub.tsdbb.commons.factories.timeFrame.TimeFrameFactory;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;

@Slf4j
public class TaskRequest extends BaseConnector implements Callable<TaskResult> {

    private final String taskName;
    private final Record record;
    private final WorkerTsdbEndpoint endpoint;

    public TaskRequest(final TSDBAdapterIF tsdbAdapterIF, final WorkerSetupRequest workerSetupRequest, final WorkerTsdbEndpoint endpoint, final String taskName, final Record record) {

        this.record = record;
        this.taskName = taskName;
        this.workerSetupRequest = workerSetupRequest;
        this.tsdbInterface = tsdbAdapterIF;
        this.endpoint=endpoint;

    }

    @Override
    public TaskResult call() throws Exception {

        log.debug("Calling Task "+taskName);

        /////
        final TimeFrame timeFrame = TimeFrameFactory.getTimeFrame();


        final int sizeInBytes = tsdbInterface.write(record,endpoint);

        timeFrame.setEndTimestamp(OffsetDateTime.now());

        final long duration = DateHelper.getDateDifference(timeFrame, ChronoUnit.MILLIS);

        log.info(taskName+" wrote Record: "+record.getRecordId());

        final TaskResult taskResult = new TaskResult();
        taskResult.setTaskName(taskName);
        taskResult.setSourceInformation(SourceInformationFactory.getSourceInformation(workerSetupRequest.getWorkerConfiguration().getWorkerUrl()));
        taskResult.setThreadName(Thread.currentThread().getName());
        taskResult.setTimeFrame(timeFrame);
        taskResult.setRequestSizeInBytes(BigDecimal.valueOf(sizeInBytes));
        taskResult.setDurationInMs(BigDecimal.valueOf(duration));

        return taskResult;
    }
}
