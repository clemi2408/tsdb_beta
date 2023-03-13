package de.cleem.tub.tsdbb.commons.spring.ping;

import de.cleem.tub.tsdbb.api.model.PingResponse;
import de.cleem.tub.tsdbb.api.model.ResetResponse;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import de.cleem.tub.tsdbb.commons.spring.objectcache.SingleObjectInstanceCacheException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Component
@Slf4j
public class PingService extends BaseSpringComponent {
    
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

        if(!pingResponse.getStatus().equals(OK)){

            throw new PingException("Can not ping "+type+" at "+url);

        }

        log.info("URL "+url+" for "+type+" is available");

    }
}
