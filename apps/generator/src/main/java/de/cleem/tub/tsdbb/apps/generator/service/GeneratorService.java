package de.cleem.tub.tsdbb.apps.generator.service;


import de.cleem.tub.tsdbb.api.model.GeneratorGenerateRequest;
import de.cleem.tub.tsdbb.api.model.GeneratorGenerateResponse;
import de.cleem.tub.tsdbb.apps.generator.generators.key.KeyGeneratorException;
import de.cleem.tub.tsdbb.apps.generator.generators.value.ValueGeneratorException;
import de.cleem.tub.tsdbb.apps.generator.generators.workload.WorkloadGenerator;
import de.cleem.tub.tsdbb.apps.generator.generators.workload.WorkloadGeneratorException;
import de.cleem.tub.tsdbb.commons.random.strings.StringGeneratorException;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class GeneratorService extends BaseSpringComponent {

    public GeneratorGenerateResponse generate(final GeneratorGenerateRequest generateRequest) throws ValueGeneratorException, StringGeneratorException, WorkloadGeneratorException, KeyGeneratorException {

        final GeneratorGenerateResponse generatorGenerateResponse = new GeneratorGenerateResponse();

        generatorGenerateResponse.setStartTimestamp(OffsetDateTime.now());

        generatorGenerateResponse.setWorkload(WorkloadGenerator.builder()
                .generateRequest(generateRequest)
                .generate());

        generatorGenerateResponse.setEndTimestamp(OffsetDateTime.now());

        return generatorGenerateResponse;
    }

}
