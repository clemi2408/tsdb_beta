package de.cleem.tub.tsdbb.apps.worker.executor;


import de.cleem.tub.tsdbb.commons.exception.BaseException;

public class ExecutionException extends BaseException {


    public ExecutionException(String message) {

        super(message);

    }

    public ExecutionException(Exception e) {

        super(e);

    }


}
