package de.cleem.bm.tsdb.model.config.workload;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class KvPair {

    private String key;

    private Number value;


}
