package de.cleem.bm.tsdb.datagenerator;

import lombok.Data;

import java.util.HashMap;
import java.util.LinkedList;

@Data
public class GeneratedWorkload {

   private LinkedList<HashMap<String,Number>> data;

}
