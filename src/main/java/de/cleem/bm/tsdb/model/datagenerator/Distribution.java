package de.cleem.bm.tsdb.model.datagenerator;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Distribution {

        @JsonProperty("uniform") UNIFORM,
        @JsonProperty("gaussian") GAUSS,
        @JsonProperty("triangle") TRIANGLE;

}
