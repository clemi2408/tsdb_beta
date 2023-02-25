package de.cleem.bm.tsdb.model.config;

import de.cleem.bm.tsdb.model.config.adapter.TSDBAdapterConfig;
import de.cleem.tub.tsdbb.commons.model.generator.GeneratorConfig;
import de.cleem.tub.tsdbb.commons.model.workload.Workload;
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

    private GeneratorConfig generatorConfig;

    private Workload inputWorkload;

    private Workload benchmarkWorkload;

    private TSDBAdapterConfig tsdbAdapterConfig;

    private int threadCount;

    private boolean cleanStorage = false;


}
