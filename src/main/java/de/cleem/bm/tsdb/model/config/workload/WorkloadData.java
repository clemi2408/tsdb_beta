package de.cleem.bm.tsdb.model.config.workload;

import lombok.Data;

import java.util.LinkedList;

@Data
public class WorkloadData {

   private LinkedList<WorkloadRecord> records;

}
