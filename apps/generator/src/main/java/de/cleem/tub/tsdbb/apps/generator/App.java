package de.cleem.tub.tsdbb.apps.generator;

import de.cleem.tub.tsdbb.apps.generator.generators.workload.WorkloadGenerator;
import de.cleem.tub.tsdbb.apps.generator.model.GeneratorRunConfig;
import de.cleem.tub.tsdbb.commons.app.BaseApp;
import de.cleem.tub.tsdbb.commons.exception.TSDBBException;
import de.cleem.tub.tsdbb.commons.json.JsonHelper;
import de.cleem.tub.tsdbb.commons.model.workload.Workload;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App extends BaseApp {

    private static final String FALLBACK_CONFIG = "apps/generator/src/main/resources/input/config/runConfig1.json";
    // "apps/generator/src/main/resources/input/config/runConfig1.json";

    public static void main(String[] args) throws TSDBBException {

        initialize();

        final GeneratorRunConfig generatorRunConfig = handleArgs(args,GeneratorRunConfig.class,FALLBACK_CONFIG);

        run(generatorRunConfig);

    }

    private static void run(final GeneratorRunConfig generatorRunConfig) throws TSDBBException {

        final Workload workload = WorkloadGenerator.builder()
                .config(generatorRunConfig.getGeneratorConfig())
                .generate();

        JsonHelper.writeToTimestampFile(workload, generatorRunConfig.getOutputFilePrefix(), generatorRunConfig.getOutputFolder(), ".json");

    }

}
