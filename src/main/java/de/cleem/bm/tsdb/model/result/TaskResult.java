package de.cleem.bm.tsdb.model.result;

import de.cleem.bm.tsdb.model.config.workload.WorkloadRecord;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.UUID;

@Builder
@Data
@ToString
public class TaskResult {

    private WorkloadRecord record;

    private UUID recordId;

    private Date startDate;
    private Date endDate;
    private String taskName;

    private String threadName;

    private long durationInMs;


}
