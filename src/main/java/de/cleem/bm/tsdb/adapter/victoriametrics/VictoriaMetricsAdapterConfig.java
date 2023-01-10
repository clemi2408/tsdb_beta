package de.cleem.bm.tsdb.adapter.victoriametrics;

import de.cleem.bm.tsdb.model.config.TSDBAdapterConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VictoriaMetricsAdapterConfig extends TSDBAdapterConfig {

    private String victoriaMetricsUrl;



}
