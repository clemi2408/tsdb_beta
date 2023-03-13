package de.cleem.tub.tsdbb.commons.spring.pingresponder;

import de.cleem.tub.tsdbb.api.model.PingResponse;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class PingResponderService extends BaseSpringComponent {

    public static final String OK = "OK";
    public static final String ERROR = "ERROR";

    public ResponseEntity<PingResponse> pong(final boolean ok){

        final PingResponse pingResponse = new PingResponse().status(ok?OK:ERROR);

        if(ok){
            return ResponseEntity.ok(pingResponse);
        }
        else{
            return ResponseEntity.internalServerError().body(pingResponse);
        }

    }

    public static void checkPingResponse(final PingResponse pingResponse, final String type, final String url) throws PingResponderException {

        if(!pingResponse.getStatus().equals(OK)){

            throw new PingResponderException("Can not ping "+type+" at "+url);

        }

        log.info("URL "+url+" for "+type+" is available");

    }
}
