package de.cleem.tub.tsdbb.commons.model.workload;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class KvPair {

    private String key;

    private Object[] value;

}
