package de.cleem.bm.tsdb.model.datagenerator;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class KvPair {

    private String key;

    private Number value;


}
