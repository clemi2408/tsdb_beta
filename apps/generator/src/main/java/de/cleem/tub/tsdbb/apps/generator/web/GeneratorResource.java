package de.cleem.tub.tsdbb.apps.generator.web;

import de.cleem.tub.tsdbb.apps.generator.generators.key.KeyGeneratorException;
import de.cleem.tub.tsdbb.apps.generator.generators.value.ValueGeneratorException;
import de.cleem.tub.tsdbb.apps.generator.generators.workload.WorkloadGenerator;
import de.cleem.tub.tsdbb.apps.generator.generators.workload.WorkloadGeneratorException;
import de.cleem.tub.tsdbb.apps.generator.server.api.model.GeneratorConfig;
import de.cleem.tub.tsdbb.apps.generator.server.api.model.Workload;
import de.cleem.tub.tsdbb.apps.generator.server.api.service.GenerateApi;
import de.cleem.tub.tsdbb.commons.random.strings.StringGeneratorException;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class GeneratorResource extends BaseSpringComponent implements GenerateApi {
    @Override
    public ResponseEntity<Workload> generate(GeneratorConfig generatorConfig) throws ValueGeneratorException, StringGeneratorException, WorkloadGeneratorException, KeyGeneratorException {

            return ResponseEntity.ok(WorkloadGenerator.builder()
                    .config(generatorConfig)
                    .generate());

    }

}
