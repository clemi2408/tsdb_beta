package de.cleem.bm.tsdb;


import de.cleem.bm.tsdb.executor.Executor;
import de.cleem.bm.tsdb.model.config.TSDBConfig;
import de.cleem.tub.tsdbb.commons.exception.TSDBBException;
import de.cleem.tub.tsdbb.commons.json.JsonHelper;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Main {

    public static void main(String[] args) throws TSDBBException {


        TSDBConfig config = TSDBConfig.builder().build();

        final Executor parallelExecutor = new Executor(config);

        parallelExecutor.execute();

        parallelExecutor.shutdown();

        //   parallelExecutor.printResults();


        JsonHelper.writeToTimestampFile(parallelExecutor.getTsdbConfig(), config.getConfigPrefix(), config.getConfigOutputFolder(), ".json");


        JsonHelper.writeToTimestampFile(parallelExecutor.getBenchmarkResult(), config.getResultPrefix(), config.getResultOutputFolder(), ".json");


    }

}