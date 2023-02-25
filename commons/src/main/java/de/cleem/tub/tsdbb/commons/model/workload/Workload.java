package de.cleem.tub.tsdbb.commons.model.workload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Workload {

    private LinkedList<Record> records;

}
