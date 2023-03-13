package de.cleem.tub.tsdbb.apps.worker.executor;


import de.cleem.tub.tsdbb.api.model.Record;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.UUID;

@Builder
@Data
@ToString
public class TaskResult {

    private Record record;

    private UUID recordId;

    private Date startDate;
    private Date endDate;
    private String taskName;

    private String threadName;

    private long durationInMs;


}
