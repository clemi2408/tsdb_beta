package de.cleem.tub.tsdbb.apps.generator.service;


import de.cleem.tub.tsdbb.api.model.GeneratorConfig;
import de.cleem.tub.tsdbb.api.model.Workload;
import de.cleem.tub.tsdbb.apps.generator.generators.key.KeyGeneratorException;
import de.cleem.tub.tsdbb.apps.generator.generators.value.ValueGeneratorException;
import de.cleem.tub.tsdbb.apps.generator.generators.workload.WorkloadGenerator;
import de.cleem.tub.tsdbb.apps.generator.generators.workload.WorkloadGeneratorException;
import de.cleem.tub.tsdbb.commons.random.strings.StringGeneratorException;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import org.springframework.stereotype.Component;

@Component
public class GeneratorService extends BaseSpringComponent {

    public Workload generate(final GeneratorConfig generatorConfig) throws ValueGeneratorException, StringGeneratorException, WorkloadGeneratorException, KeyGeneratorException {

        return WorkloadGenerator.builder()
                .config(generatorConfig)
                .generate();

    }

}
