package de.cleem.tub.tsdbb.commons.spring.ping;

import de.cleem.tub.tsdbb.api.model.PingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

@Slf4j
public class PingHelper {

    public static final String OK = "OK";
    public static final String ERROR = "ERROR";

    public static ResponseEntity<PingResponse> pong(final boolean ok){

        final PingResponse pingResponse = new PingResponse().status(ok?OK:ERROR);

        if(ok){
            return ResponseEntity.ok(pingResponse);
        }
        else{
            return ResponseEntity.internalServerError().body(pingResponse);
        }

    }

    public static void checkPingResponse(final PingResponse pingResponse, final String type, final String url) throws PingException {

        if(!pingResponse.getStatus().equals(PingHelper.OK)){

            throw new PingException("Can not ping "+type+" at "+url);

        }

        log.info("URL "+url+" for "+type+" is available");

    }

}
