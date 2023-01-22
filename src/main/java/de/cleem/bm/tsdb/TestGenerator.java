package de.cleem.bm.tsdb;

import de.cleem.bm.tsdb.common.exception.TSDBBenchmarkException;
import de.cleem.bm.tsdb.common.json.JsonException;
import de.cleem.bm.tsdb.common.json.JsonHelper;
import de.cleem.bm.tsdb.datagenerator.WorkloadGenerator;
import de.cleem.bm.tsdb.model.datagenerator.Distribution;
import de.cleem.bm.tsdb.model.datagenerator.RecordConfig;
import de.cleem.bm.tsdb.model.datagenerator.WorkloadData;
import de.cleem.bm.tsdb.model.datagenerator.WorkloadGeneratorConfig;

import java.util.ArrayList;
import java.util.List;

public class TestGenerator {

    public static void main(String[] args) throws JsonException, TSDBBenchmarkException {

        final List<RecordConfig> recordConfig = new ArrayList<>();

        recordConfig.add(RecordConfig.builder()
                .valueDistribution(Distribution.UNIFORM)
                .maxKeyLength(5)
                .minKeyLength(2)
                .valueType(Integer.class)
                .minValue(0)
                .maxValue(100)
                .build());

        recordConfig.add(RecordConfig.builder()
                .valueDistribution(Distribution.UNIFORM)
                .keyValue("uniform-int")
                .valueType(Integer.class)
                .minValue(0)
                .maxValue(100)
                .build());

        recordConfig.add(RecordConfig.builder()
                .valueDistribution(Distribution.UNIFORM)
                .keyValue("uniform-double")
                .valueType(Double.class)
                .minValue(5.5d)
                .maxValue(55.3d)
                .build());

        recordConfig.add(RecordConfig.builder()
                .valueDistribution(Distribution.TRIANGLE)
                .keyValue("triangle-int")
                .valueType(Integer.class)
                .triangleSpike(50)
                .minValue(0)
                .maxValue(100)
                .build());

        recordConfig.add(RecordConfig.builder()
                .valueDistribution(Distribution.TRIANGLE)
                .keyValue("triangle-double")
                .valueType(Double.class)
                .triangleSpike(10d)
                .minValue(5.5d)
                .maxValue(55.3d)
                .build());

        recordConfig.add(RecordConfig.builder()
                .valueDistribution(Distribution.GAUSS)
                .keyValue("gaussian-int")
                .valueType(Integer.class)
                .gaussMiddle(10)
                .gaussRange(30)
                .build());

        recordConfig.add(RecordConfig.builder()
                .valueDistribution(Distribution.GAUSS)
                .keyValue("gaussian-double")
                .valueType(Double.class)
                .gaussMiddle(10d)
                .gaussRange(30d)
                .build());


        final WorkloadGeneratorConfig workloadGeneratorConfig = WorkloadGeneratorConfig.builder()
                .recordCount(10)
                .recordConfigList(recordConfig)
                .build();

        final WorkloadData workload = WorkloadGenerator.builder()
                .workloadGeneratorConfig(workloadGeneratorConfig)
                .build();

        final String configString = JsonHelper.toString(workloadGeneratorConfig,true);

        System.out.println(configString);

        final String workloadString = JsonHelper.toString(workload,true);

        System.out.println(workloadString);




    }
}
