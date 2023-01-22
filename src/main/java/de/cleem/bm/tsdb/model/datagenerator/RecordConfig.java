package de.cleem.bm.tsdb.model.datagenerator;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RecordConfig {

    // Min Key Length Value / alternative to keyValue
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer minKeyLength;

    // Max Key Length Value / alternative to keyValue
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer maxKeyLength;

    // Key Value / alternative to minKeyLength-maxKeyLength generation
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String keyValue;

    // Distribution type, defaulted to uniform distribution
    private Distribution valueDistribution = Distribution.UNIFORM;

    // min value for uniform and triangle distribution
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Number minValue;

    // max value for uniform and triangle distribution
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Number maxValue;

    // value type, supported: Integer, Double and Float
    private Class valueType;

    // spike value for triangle distribution
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Number triangleSpike;

    // middle value for gauss distribution
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Number gaussMiddle;

    // range value for gauss distribution
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Number gaussRange;

}
