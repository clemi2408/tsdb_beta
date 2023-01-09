package de.cleem.bm.tsdb.model.config;

import de.cleem.bm.tsdb.adapter.common.TSDBAdapterConfig;
import de.cleem.bm.tsdb.datagenerator.GeneratedWorkload;
import de.cleem.bm.tsdb.datagenerator.WorkloadGeneratorConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TSDBConfig {

    private String mode;

    private String configOutputFolder;

    private String configPrefix;

    private String resultOutputFolder;

    private String resultPrefix;

    private WorkloadGeneratorConfig workloadGeneratorConfig;

    private GeneratedWorkload workload;

    private TSDBAdapterConfig tsdbAdapterConfig;

    int threadCount;

    boolean cleanStorage;


}
