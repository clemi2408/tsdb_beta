package de.cleem.bm.tsdb.model.task;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.HashMap;

@Builder
@Data
@ToString
public class TaskResult {

    private HashMap<String,Number> record;

    private Date startDate;
    private Date endDate;
    private String taskName;

    private String threadName;

    private long durationInMs;


}
