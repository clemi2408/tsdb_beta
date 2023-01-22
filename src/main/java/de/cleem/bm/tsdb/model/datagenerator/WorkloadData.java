package de.cleem.bm.tsdb.model.datagenerator;

import lombok.Data;

import java.util.LinkedList;

@Data
public class WorkloadData {

   private LinkedList<WorkloadRecord> records;

}
