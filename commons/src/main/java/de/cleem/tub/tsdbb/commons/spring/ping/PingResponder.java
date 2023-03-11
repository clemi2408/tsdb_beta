package de.cleem.tub.tsdbb.commons.spring.ping;

import de.cleem.tub.tsdbb.api.model.PingResponse;
import org.springframework.http.ResponseEntity;

public class PingResponder {

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
}
