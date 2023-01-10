package de.cleem.bm.tsdb.adapter.influxdb;

import de.cleem.bm.tsdb.model.config.TSDBAdapterConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfluxDbAdapterConfig extends TSDBAdapterConfig {

    private String influxDbUrl;
    private String token;
    private String organisation;
    private String bucket;

    ////
    private String organisationId;
    private String bucketId;



}
