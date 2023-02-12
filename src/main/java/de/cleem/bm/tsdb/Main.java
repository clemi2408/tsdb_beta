package de.cleem.bm.tsdb;


import de.cleem.bm.tsdb.common.exception.TSDBBenchmarkException;
import de.cleem.bm.tsdb.common.file.FileHelper;
import de.cleem.bm.tsdb.common.json.JsonHelper;
import de.cleem.bm.tsdb.datagenerator.WorkloadGenerator;
import de.cleem.bm.tsdb.executor.Executor;
import de.cleem.bm.tsdb.model.config.TSDBConfig;
import de.cleem.bm.tsdb.model.config.datagenerator.Distribution;
import de.cleem.bm.tsdb.model.config.datagenerator.RecordConfig;
import de.cleem.bm.tsdb.model.config.datagenerator.WorkloadGeneratorConfig;
import de.cleem.bm.tsdb.model.result.BenchmarkResult;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Main {

    public static void main(String[] args) throws TSDBBenchmarkException {

        final File tsdbBenchmarkConfigFile = new File("src/main/resources/input/benchmarkConfig/influx-data-benchmark.json");
        //final File tsdbBenchmarkConfigFile = new File("src/main/resources/input/benchmarkConfig/influx-config-benchmark.json");
        //final File tsdbBenchmarkConfigFile = new File("src/main/resources/input/benchmarkConfig/influx-demo-benchmark.json");

        //final File tsdbBenchmarkConfigFile = new File("src/main/resources/input/benchmarkConfig/victoria-data-benchmark.json");
        //final File tsdbBenchmarkConfigFile = new File("src/main/resources/input/benchmarkConfig/victoria-config-benchmark.json");
        //final File tsdbBenchmarkConfigFile = new File("src/main/resources/input/benchmarkConfig/victoria-demo-benchmark.json");



        final TSDBConfig config = JsonHelper.objectFromByteArray(FileHelper.read(tsdbBenchmarkConfigFile),TSDBConfig.class);

        switch(config.getMode()){

             case "data":

                 log.info("Using workload data from file: "+tsdbBenchmarkConfigFile.getAbsolutePath());

                 break;

            case "config":


                log.info("Using workload generation config from file: "+tsdbBenchmarkConfigFile.getAbsolutePath());

                config.setInputWorkload(WorkloadGenerator.builder()
                        .workloadGeneratorConfig(config.getWorkloadGeneratorConfig())
                        .generate());

                break;

            case "demo":

                log.info("Loading demo workload generation workloadGeneratorConfig");


                final List<RecordConfig> recordConfig = new ArrayList<>();

                recordConfig.add(RecordConfig.builder()
                        .valueDistribution(Distribution.UNIFORM)
                        .maxKeyLength(5)
                        .minKeyLength(2)
                        .valueType("integer")
                        .minValue(0)
                        .maxValue(100)
                        .build());

                recordConfig.add(RecordConfig.builder()
                        .valueDistribution(Distribution.UNIFORM)
                        .keyValue("uniformDouble")
                        .valueType("double")
                        .minValue(5.5d)
                        .maxValue(55.3d)
                        .build());

                recordConfig.add(RecordConfig.builder()
                        .valueDistribution(Distribution.TRIANGLE)
                        .keyValue("triangleInteger")
                        .valueType("integer")
                        .triangleSpike(50)
                        .minValue(0)
                        .maxValue(100)
                        .build());

                recordConfig.add(RecordConfig.builder()
                        .valueDistribution(Distribution.GAUSS)
                        .keyValue("gaussianDouble")
                        .valueType("double")
                        .gaussMiddle(10d)
                        .gaussRange(30d)
                        .build());


                final WorkloadGeneratorConfig workloadGeneratorConfig = WorkloadGeneratorConfig.builder()
                        .recordCount(10)
                        .recordConfigList(recordConfig)
                        .build();


               config.setWorkloadGeneratorConfig(workloadGeneratorConfig);

                config.setInputWorkload(WorkloadGenerator.builder()
                        .workloadGeneratorConfig(config.getWorkloadGeneratorConfig())
                        .generate());

                break;

            default:

                log.error("Invalid mode: "+config.getMode());

                throw new TSDBBenchmarkException("Invalid mode: "+config.getMode());

        }


           final Executor parallelExecutor = new Executor(config);

            parallelExecutor.execute();

            parallelExecutor.shutdown();

         //   parallelExecutor.printResults();



        JsonHelper.writeToTimestampFile(parallelExecutor.getTsdbConfig(), config.getConfigPrefix(), config.getConfigOutputFolder(), ".json");


        JsonHelper.writeToTimestampFile(parallelExecutor.getBenchmarkResult(), config.getResultPrefix(), config.getResultOutputFolder(), ".json");


    }

}