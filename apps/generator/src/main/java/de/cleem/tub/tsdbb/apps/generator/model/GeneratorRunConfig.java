package de.cleem.tub.tsdbb.apps.generator.model;

import de.cleem.tub.tsdbb.commons.model.generator.GeneratorConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneratorRunConfig {

    private GeneratorConfig generatorConfig;
    private String outputFolder;
    private String outputFilePrefix;

}
