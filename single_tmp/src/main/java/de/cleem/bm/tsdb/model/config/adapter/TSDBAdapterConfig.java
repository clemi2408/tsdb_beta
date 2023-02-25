package de.cleem.bm.tsdb.model.config.adapter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.cleem.bm.tsdb.adapter.influxdb.InfluxDbAdapterConfig;
import de.cleem.bm.tsdb.adapter.victoriametrics.VictoriaMetricsAdapterConfig;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(InfluxDbAdapterConfig.class),
        @JsonSubTypes.Type(VictoriaMetricsAdapterConfig.class)
})
public class TSDBAdapterConfig {


}
