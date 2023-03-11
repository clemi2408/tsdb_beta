package de.cleem.tub.tsdbb.apps.generator.rest;

import de.cleem.tub.tsdbb.api.generator.server.GeneratorGenerateApi;
import de.cleem.tub.tsdbb.api.generator.server.GeneratorPingApi;
import de.cleem.tub.tsdbb.api.model.GeneratorConfig;
import de.cleem.tub.tsdbb.api.model.PingResponse;
import de.cleem.tub.tsdbb.api.model.Workload;
import de.cleem.tub.tsdbb.apps.generator.generators.key.KeyGeneratorException;
import de.cleem.tub.tsdbb.apps.generator.generators.value.ValueGeneratorException;
import de.cleem.tub.tsdbb.apps.generator.generators.workload.WorkloadGeneratorException;
import de.cleem.tub.tsdbb.apps.generator.service.GeneratorService;
import de.cleem.tub.tsdbb.commons.random.strings.StringGeneratorException;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import de.cleem.tub.tsdbb.commons.spring.ping.PingResponder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class GeneratorREST extends BaseSpringComponent implements GeneratorGenerateApi, GeneratorPingApi {

    @Autowired
    private GeneratorService generatorService;

    @Override
    public ResponseEntity<Workload> generate(GeneratorConfig generatorConfig) throws ValueGeneratorException, StringGeneratorException, WorkloadGeneratorException, KeyGeneratorException {

            return ResponseEntity.ok(generatorService.generate(generatorConfig));

    }

    @Override
    public ResponseEntity<PingResponse> ping() {

        return PingResponder.pong(true);

    }

}
