package de.cleem.bm.tsdb.model.datagenerator;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WorkloadGeneratorConfig {

    private Integer recordCount;
    private List<RecordConfig> recordConfigList;

}
