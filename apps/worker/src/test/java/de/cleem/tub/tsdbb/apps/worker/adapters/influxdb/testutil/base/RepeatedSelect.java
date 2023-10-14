package de.cleem.tub.tsdbb.apps.worker.adapters.influxdb.testutil.base;

import de.cleem.tub.tsdbb.apps.worker.adapters.SelectResponse;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterException;
import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
public class RepeatedSelect {

    private final int timeoutInSeconds;
    private final int frequencyInSeconds;
    private final String targetString;

    public RepeatedSelect( final int timeoutInSeconds, final int frequencyInSeconds, final String targetString){
        this.timeoutInSeconds=timeoutInSeconds;
        this.frequencyInSeconds=frequencyInSeconds;
        this.targetString=targetString;
    }

    public void execute(SelectCall a) throws TSDBAdapterException {

        final long start = System.currentTimeMillis();
        final long end = start + (timeoutInSeconds * 1000L);

        long current;
        SelectResponse response;
        int count = 0;
        boolean isAvailable = false;
        String logString;
        while ((current=System.currentTimeMillis()) < end && !isAvailable) {

            if(current%(frequencyInSeconds* 1000L)==0){
                count++;
                log.info("Attempt: "+count);
                response = a.select();

                if(response.getResponseBody().contains(targetString)){

                    logString="Result available after "+(current-start)+" ms";
                    log.info(logString);
                    assertTrue(true,logString);
                    isAvailable=true;
                }
            }

        }

        if(!isAvailable){
            logString="Timeout "+timeoutInSeconds+" s reached";
            log.info(logString);
            fail(logString);
        }

    }

    public abstract static class SelectCall {
        public abstract SelectResponse select() throws TSDBAdapterException;
    }
}
