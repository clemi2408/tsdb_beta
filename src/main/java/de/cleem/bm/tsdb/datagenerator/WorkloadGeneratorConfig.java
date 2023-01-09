package de.cleem.bm.tsdb.datagenerator;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WorkloadGeneratorConfig {

    private Integer recordCount;
    private Integer keyCountPerRecord;
    private Integer minKeyLength;
    private Integer maxKeyLength;
    private Double minValue;
    private Double maxValue;

}
