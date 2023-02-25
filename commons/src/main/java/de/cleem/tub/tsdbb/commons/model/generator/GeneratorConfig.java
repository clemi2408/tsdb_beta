package de.cleem.tub.tsdbb.commons.model.generator;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GeneratorConfig {

    private Integer recordCount;
    private List<RecordConfig> recordConfigs;

}
