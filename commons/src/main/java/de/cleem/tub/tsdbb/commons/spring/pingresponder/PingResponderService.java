package de.cleem.tub.tsdbb.commons.spring.pingresponder;

import de.cleem.tub.tsdbb.api.model.PingResponse;
import de.cleem.tub.tsdbb.api.model.PingResponse.StatusEnum;
import de.cleem.tub.tsdbb.commons.factories.sourceInformation.SourceInformationFactory;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class PingResponderService extends BaseSpringComponent {



    public ResponseEntity<PingResponse> pong(final boolean ok){

        final PingResponse pingResponse = new PingResponse().status(ok?StatusEnum.OK:StatusEnum.ERROR);
        pingResponse.setSourceInformation(SourceInformationFactory.getSourceInformation(getServerUrl()));
        pingResponse.setSourceInformation(SourceInformationFactory.getSourceInformation(getServerUrl()));

        if(ok){
            return ResponseEntity.ok(pingResponse);
        }
        else{
            return ResponseEntity.internalServerError().body(pingResponse);
        }

    }

    public void checkPingResponse(final PingResponse pingResponse, final String type, final String url) throws PingResponderException {

        if(!pingResponse.getStatus().equals(StatusEnum.OK)){

            throw new PingResponderException("Can not ping "+type+" at "+url);

        }

        log.info("URL "+url+" for "+type+" is available");

    }
}
