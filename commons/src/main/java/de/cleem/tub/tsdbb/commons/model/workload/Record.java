package de.cleem.tub.tsdbb.commons.model.workload;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Record {

    private List<KvPair> kvPairs;
    private UUID recordId;


}
