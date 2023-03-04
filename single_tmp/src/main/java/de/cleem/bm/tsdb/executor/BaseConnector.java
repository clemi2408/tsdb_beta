package de.cleem.bm.tsdb.executor;

import de.cleem.bm.tsdb.adapter.TSDBAdapterIF;
import de.cleem.bm.tsdb.adapter.influxdb.InfluxDbAdapter;
import de.cleem.bm.tsdb.adapter.influxdb.InfluxDbAdapterConfig;
import de.cleem.bm.tsdb.adapter.victoriametrics.VictoriaMetricsAdapter;
import de.cleem.bm.tsdb.adapter.victoriametrics.VictoriaMetricsAdapterConfig;
import de.cleem.bm.tsdb.model.config.TSDBConfig;
import de.cleem.tub.tsdbb.commons.exception.BaseException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseConnector {

    @Getter
    protected TSDBConfig tsdbConfig;

    protected TSDBAdapterIF tsdbInterface;

    protected void setStorageAdapter() throws BaseException {

        if (tsdbConfig.getTsdbAdapterConfig() instanceof InfluxDbAdapterConfig) {

            tsdbInterface = new InfluxDbAdapter();
            tsdbInterface.setup(tsdbConfig.getTsdbAdapterConfig());

        } else if (tsdbConfig.getTsdbAdapterConfig() instanceof VictoriaMetricsAdapterConfig) {

            tsdbInterface = new VictoriaMetricsAdapter();
            tsdbInterface.setup(tsdbConfig.getTsdbAdapterConfig());

        } else {
            throw new BaseException("Invalid config: " + tsdbConfig.getTsdbAdapterConfig().getClass().getSimpleName());

        }

    }


}
