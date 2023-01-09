package de.cleem.bm.tsdb;


import de.cleem.bm.tsdb.common.exception.TsdbBenchmarkException;
import de.cleem.bm.tsdb.common.file.FileHelper;
import de.cleem.bm.tsdb.common.json.JsonHelper;
import de.cleem.bm.tsdb.datagenerator.WorkloadGenerator;
import de.cleem.bm.tsdb.datagenerator.WorkloadGeneratorConfig;
import de.cleem.bm.tsdb.executor.Executor;
import de.cleem.bm.tsdb.model.config.TSDBConfig;
import de.cleem.bm.tsdb.model.result.BenchmarkResult;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class Main {

    public static void main(String[] args) throws TsdbBenchmarkException {

        //final File tsdbBenchmarkConfigFile = new File("src/main/resources/input/benchmarkConfig/influx-data-benchmark.json");
        //final File tsdbBenchmarkConfigFile = new File("src/main/resources/input/benchmarkConfig/influx-config-benchmark.json");
        //final File tsdbBenchmarkConfigFile = new File("src/main/resources/input/benchmarkConfig/influx-demo-benchmark.json");

        final File tsdbBenchmarkConfigFile = new File("src/main/resources/input/benchmarkConfig/victoria-config-benchmark.json");
        //final File tsdbBenchmarkConfigFile = new File("src/main/resources/input/benchmarkConfig/victoria-data-benchmark.json");

        final TSDBConfig config = JsonHelper.objectFromByteArray(FileHelper.read(tsdbBenchmarkConfigFile),TSDBConfig.class);

        switch(config.getMode()){

             case "data":

                 log.info("Using workload data from file: "+tsdbBenchmarkConfigFile.getAbsolutePath());

                 break;

            case "config":


                log.info("Using workload generation config from file: "+tsdbBenchmarkConfigFile.getAbsolutePath());

                config.setWorkload(WorkloadGenerator.builder()
                        .workloadGeneratorConfig(config.getWorkloadGeneratorConfig())
                        .build());

                break;

            case "demo":

                log.info("Loading demo workload generation workloadGeneratorConfig");

                config.setWorkloadGeneratorConfig(WorkloadGeneratorConfig.builder()
                        .recordCount(100)
                        .keyCountPerRecord(2)
                        .minKeyLength(3)
                        .maxKeyLength(3)
                        .minValue(1d)
                        .maxValue(10d)
                        .build());

                config.setWorkload(WorkloadGenerator.builder()
                        .workloadGeneratorConfig(config.getWorkloadGeneratorConfig())
                        .build());

                break;

            default:

                log.error("Invalid mode: "+config.getMode());

                throw new TsdbBenchmarkException("Invalid mode: "+config.getMode());

        }

           JsonHelper.writeToTimestampFile(config, config.getConfigPrefix(), config.getConfigOutputFolder(), ".json");

           final Executor parallelExecutor = new Executor(config);

            parallelExecutor.execute();

            parallelExecutor.shutdown();

         //   parallelExecutor.printResults();

            final BenchmarkResult benchmarkResult = parallelExecutor.getBenchmarkResult();

            JsonHelper.writeToTimestampFile(benchmarkResult, config.getResultPrefix(), config.getResultOutputFolder(), ".json");


    }

}