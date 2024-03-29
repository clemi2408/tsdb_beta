package de.cleem.tub.tsdbb.apps.generator.controller;

import de.cleem.tub.tsdbb.api.generator.server.GeneratorGenerateApi;
import de.cleem.tub.tsdbb.api.generator.server.GeneratorPingApi;
import de.cleem.tub.tsdbb.api.model.GeneratorGenerateRequest;
import de.cleem.tub.tsdbb.api.model.GeneratorGenerateResponse;
import de.cleem.tub.tsdbb.api.model.PingResponse;
import de.cleem.tub.tsdbb.apps.generator.generators.key.KeyGeneratorException;
import de.cleem.tub.tsdbb.apps.generator.generators.value.ValueGeneratorException;
import de.cleem.tub.tsdbb.apps.generator.generators.workload.WorkloadGeneratorException;
import de.cleem.tub.tsdbb.apps.generator.service.GeneratorService;
import de.cleem.tub.tsdbb.commons.duration.DurationException;
import de.cleem.tub.tsdbb.commons.random.strings.StringGeneratorException;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class GeneratorController extends BaseSpringComponent implements GeneratorGenerateApi, GeneratorPingApi {

    @Autowired
    private GeneratorService generatorService;

    @Override
    public ResponseEntity<GeneratorGenerateResponse> generate(final GeneratorGenerateRequest generatorGenerateRequest) throws ValueGeneratorException, StringGeneratorException, WorkloadGeneratorException, KeyGeneratorException, DurationException {

        return ResponseEntity.ok(generatorService.generate(generatorGenerateRequest));

    }

    @Override
    public ResponseEntity<PingResponse> ping() {

        return generatorService.ping();

    }

}
