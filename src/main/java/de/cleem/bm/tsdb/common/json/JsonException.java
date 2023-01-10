package de.cleem.bm.tsdb.common.json;

import de.cleem.bm.tsdb.common.exception.TSDBBenchmarkException;

public class JsonException extends TSDBBenchmarkException {


    public JsonException(String message){

        super(message);

    }
    public JsonException(Exception e){

        super(e);

    }


}
