package de.cleem.bm.tsdb.executor;

import de.cleem.bm.tsdb.adapter.common.TSDBAdapterIF;
import de.cleem.bm.tsdb.adapter.influxdb.InfluxDbAdapter;
import de.cleem.bm.tsdb.adapter.influxdb.InfluxDbAdapterConfig;
import de.cleem.bm.tsdb.adapter.victoriametrics.VictoriaMetricsAdapter;
import de.cleem.bm.tsdb.adapter.victoriametrics.VictoriaMetricsAdapterConfig;
import de.cleem.bm.tsdb.common.exception.TsdbBenchmarkException;
import de.cleem.bm.tsdb.model.config.TSDBConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseConnector {

    protected TSDBConfig config;

    protected TSDBAdapterIF tsdbInterface;

    protected void setStorageAdapter() throws TsdbBenchmarkException {

        if(config.getTsdbAdapterConfig() instanceof InfluxDbAdapterConfig){
            tsdbInterface = new InfluxDbAdapter();
            tsdbInterface.setup(config.getTsdbAdapterConfig());

        }
        else if(config.getTsdbAdapterConfig() instanceof VictoriaMetricsAdapterConfig){

            tsdbInterface = new VictoriaMetricsAdapter();
            tsdbInterface.setup(config.getTsdbAdapterConfig());

        }


        else{
            throw new TsdbBenchmarkException("Invalid config: " + config.getTsdbAdapterConfig().getClass().getSimpleName());

        }

    }




}
