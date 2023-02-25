package de.cleem.tub.tsdbb.commons.model.generator;

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

    // Distribution type
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Distribution valueDistribution;

    // min value for uniform and triangle distribution
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Number minValue;

    // max value for uniform and triangle distribution
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Number maxValue;

    // spike value for triangle distribution
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Number triangleSpike;

    // middle value for gauss distribution
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Number gaussMiddle;

    // range value for gauss distribution
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Number gaussRange;

    // value type, supported: Integer, Double and String
    private String valueType;


    /// STRING

    // Min String Value Length Value / alternative to pre-defined stringEnumValues
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer minStringValueLength;

    // Max String Value Length Value / alternative to pre-defined stringEnumValues
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer maxStringValueLength;

    // Min String Enum Value count / alternative to pre-defined stringEnumValues
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer minStringEnumValues;

    // Max String Enum Value count / alternative to pre-defined stringEnumValues
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer maxStringEnumValues;

    // Pre-defined stringEnumValues / alternative to Max/Min String Enum Value count with Min/Max String Value Length
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String[] stringEnumValues;


}
