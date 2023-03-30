package de.cleem.tub.tsdbb.commons.json;


import de.cleem.tub.tsdbb.commons.base.exception.BaseException;

public class JsonException extends BaseException {


    public JsonException(String message) {

        super(message);

    }

    public JsonException(Exception e) {

        super(e);

    }


}
