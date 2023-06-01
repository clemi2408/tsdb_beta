package de.cleem.tub.tsdbb.apps.worker.adapters;

import de.cleem.tub.tsdbb.api.model.WorkerGeneralProperties;
import de.cleem.tub.tsdbb.api.model.WorkerSetupRequest;
import de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.InfluxDbAdapter;
import de.cleem.tub.tsdbb.apps.worker.adapters.victoriametrics.VictoriaMetricsAdapter;
import de.cleem.tub.tsdbb.apps.worker.executor.ExecutionException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseConnector {

    @Getter
    protected WorkerSetupRequest workerSetupRequest;

    protected TSDBAdapterIF tsdbInterface;

    protected void setStorageAdapter() throws ExecutionException, TSDBAdapterException {

        final WorkerGeneralProperties.TsdbTypeEnum tsdbType = workerSetupRequest.getWorkerGeneralProperties().getTsdbType();

        log.info("Setting up "+tsdbType.getValue()+" TSDB Connection");

        if (tsdbType.equals(WorkerGeneralProperties.TsdbTypeEnum.INFLUX)) {


            tsdbInterface = new InfluxDbAdapter();
            tsdbInterface.setup(workerSetupRequest);

        } else if (tsdbType.equals(WorkerGeneralProperties.TsdbTypeEnum.VICTORIA)) {

            tsdbInterface = new VictoriaMetricsAdapter();
            tsdbInterface.setup(workerSetupRequest);

        } else {
            throw new ExecutionException("Invalid WorkerTsdbConnection: " + tsdbType);

        }

    }


}
