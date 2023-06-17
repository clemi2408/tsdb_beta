package de.cleem.tub.tsdbb.apps.generator.service;


import de.cleem.tub.tsdbb.api.model.GeneratorGenerateRequest;
import de.cleem.tub.tsdbb.api.model.GeneratorGenerateResponse;
import de.cleem.tub.tsdbb.api.model.PingResponse;
import de.cleem.tub.tsdbb.apps.generator.generators.key.KeyGeneratorException;
import de.cleem.tub.tsdbb.apps.generator.generators.value.ValueGeneratorException;
import de.cleem.tub.tsdbb.apps.generator.generators.workload.WorkloadGenerator;
import de.cleem.tub.tsdbb.apps.generator.generators.workload.WorkloadGeneratorException;
import de.cleem.tub.tsdbb.commons.duration.DurationException;
import de.cleem.tub.tsdbb.commons.factories.sourceInformation.SourceInformationFactory;
import de.cleem.tub.tsdbb.commons.factories.timeFrame.TimeFrameFactory;
import de.cleem.tub.tsdbb.commons.random.strings.StringGeneratorException;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import de.cleem.tub.tsdbb.commons.spring.pingresponder.PingResponderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class GeneratorService extends BaseSpringComponent {

    @Autowired
    private PingResponderService pingResponderService;

    public GeneratorGenerateResponse generate(final GeneratorGenerateRequest generateRequest) throws ValueGeneratorException, StringGeneratorException, WorkloadGeneratorException, KeyGeneratorException, DurationException {

        final GeneratorGenerateResponse generatorGenerateResponse = new GeneratorGenerateResponse();
        generatorGenerateResponse.setTimeFrame(TimeFrameFactory.getTimeFrame());
        generatorGenerateResponse.setSourceInformation(SourceInformationFactory.getSourceInformation(getServerUrl()));

        generatorGenerateResponse.setWorkload(WorkloadGenerator.builder()
                .generateRequest(generateRequest)
                .generate());

        generatorGenerateResponse.getTimeFrame().setEndTimestamp(OffsetDateTime.now());

        return generatorGenerateResponse;
    }

    public ResponseEntity<PingResponse> ping() {

        return pingResponderService.pong(true);

    }

}
