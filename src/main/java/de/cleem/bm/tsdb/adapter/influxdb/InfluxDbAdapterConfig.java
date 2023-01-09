package de.cleem.bm.tsdb.adapter.influxdb;

import de.cleem.bm.tsdb.adapter.common.TSDBAdapterConfig;
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
    private String username;
    private String password;

    private String organisation;

    private String bucket;

    private String bucketId;

}
