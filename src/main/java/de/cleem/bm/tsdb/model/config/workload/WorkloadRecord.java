package de.cleem.bm.tsdb.model.config.workload;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WorkloadRecord {

    private List<KvPair> kvPairs;
    private UUID recordId;


}
