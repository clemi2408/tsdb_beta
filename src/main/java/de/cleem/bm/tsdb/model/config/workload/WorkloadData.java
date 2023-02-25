package de.cleem.bm.tsdb.model.config.workload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkloadData {

   private LinkedList<WorkloadRecord> records;

}
