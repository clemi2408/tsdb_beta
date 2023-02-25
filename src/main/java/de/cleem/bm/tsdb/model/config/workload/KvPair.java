package de.cleem.bm.tsdb.model.config.workload;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class KvPair<T> {

    private String key;

    private Object[] value;


}
