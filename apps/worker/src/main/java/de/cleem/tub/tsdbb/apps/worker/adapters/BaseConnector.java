package de.cleem.tub.tsdbb.apps.worker.adapters;

import de.cleem.tub.tsdbb.api.model.WorkerPreloadRequest;
import de.cleem.tub.tsdbb.api.model.WorkerTsdbConnection;
import de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.InfluxDbAdapter;
import de.cleem.tub.tsdbb.apps.worker.adapters.victoriametrics.VictoriaMetricsAdapter;
import de.cleem.tub.tsdbb.apps.worker.executor.ExecutionException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseConnector {

    @Getter
    protected WorkerPreloadRequest workerPreloadRequest;

    protected TSDBAdapterIF tsdbInterface;

    protected void setStorageAdapter() throws ExecutionException, TSDBAdapterException {

        final WorkerTsdbConnection workerTsdbConnection = workerPreloadRequest.getWorkerConfig().getTsdbConnection();

        final WorkerTsdbConnection.TsdbTypeEnum tsdbType = workerTsdbConnection.getTsdbType();


        log.info("Setting up "+tsdbType.getValue()+" TSDB Connection");

        if (tsdbType.equals(WorkerTsdbConnection.TsdbTypeEnum.INFLUX)) {


            tsdbInterface = new InfluxDbAdapter();
            tsdbInterface.setup(workerTsdbConnection);

        } else if (tsdbType.equals(WorkerTsdbConnection.TsdbTypeEnum.VICTORIA)) {

            tsdbInterface = new VictoriaMetricsAdapter();
            tsdbInterface.setup(workerTsdbConnection);

        } else {
            throw new ExecutionException("Invalid WorkerTsdbConnection: " + tsdbType);

        }

    }


}
