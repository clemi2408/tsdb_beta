package de.cleem.bm.tsdb.model.config;

import de.cleem.bm.tsdb.model.datagenerator.WorkloadData;
import de.cleem.bm.tsdb.model.datagenerator.WorkloadGeneratorConfig;
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

    private WorkloadData workload;

    private TSDBAdapterConfig tsdbAdapterConfig;

    private int threadCount;

    private boolean cleanStorage = false;


}
