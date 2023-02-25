package de.cleem.tub.tsdbb.commons.model.generator;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Distribution {

    @JsonProperty("uniform")
    UNIFORM,
    @JsonProperty("gaussian")
    GAUSS,
    @JsonProperty("triangle")
    TRIANGLE;

}
