package de.cleem.bm.tsdb.common.random;

import de.cleem.bm.tsdb.common.exception.TSDBBenchmarkException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class RandomValueGenerationException extends TSDBBenchmarkException {
    public RandomValueGenerationException(String message){

        super(message);

    }
    public RandomValueGenerationException(IOException e){

        super(e);

    }


}
