package de.cleem.tub.tsdbb.commons.json;


import de.cleem.tub.tsdbb.commons.exception.TSDBBException;

public class JsonException extends TSDBBException {


    public JsonException(String message) {

        super(message);

    }

    public JsonException(Exception e) {

        super(e);

    }


}
