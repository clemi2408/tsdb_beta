package de.cleem.bm.tsdb.datagenerator;

import de.cleem.bm.tsdb.common.exception.TSDBBenchmarkException;

import java.util.ArrayList;
import java.util.HashMap;

public class DataGeneratorConfigurationException extends TSDBBenchmarkException {

    private ArrayList<String> nullFields;
    private HashMap<String,Integer> invalidFields;

    private String message;

    public DataGeneratorConfigurationException(final ArrayList<String> nullFields, HashMap<String,Integer> invalidFields){

        this.nullFields = nullFields;
        this.invalidFields = invalidFields;

    }

    public DataGeneratorConfigurationException(final String message){

        this.message = message;

    }

    private String generateMessage(){

        final StringBuffer messageBuffer = new StringBuffer();

        messageBuffer.append("Invalid Generator Configuration\n");

        if(nullFields.size()>0) {

            for (String nullField : nullFields) {

                messageBuffer.append("Field: " + nullField + " is NULL\n");

            }
        }

        if(invalidFields.size()>0) {

            Integer invalidValue;
            for (String invalidField : invalidFields.keySet()) {
                invalidValue = invalidFields.get(invalidField);

                messageBuffer.append("Field: " + invalidField + " has invalid value: "+invalidValue+"\n");

            }
        }

        return messageBuffer.toString();
    }


    @Override
    public String getMessage(){

        if(message!=null){
            return message;
        }

        return generateMessage();

    }


}
