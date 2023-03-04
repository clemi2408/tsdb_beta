package de.cleem.bm.tsdb.adapter.exception;


import de.cleem.tub.tsdbb.commons.exception.BaseException;

public class TSDBAdapterException extends BaseException {


    public TSDBAdapterException(String message) {

        super(message);

    }

    public TSDBAdapterException(Exception e) {

        super(e);

    }


}
