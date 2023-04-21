package de.cleem.tub.tsdbb.commons.spring.apiclient;

import de.cleem.tub.tsdbb.commons.api.ClientApiFacadeException;
import de.cleem.tub.tsdbb.commons.api.OpenAPIClientFacade;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@Slf4j
public class ApiClientService extends BaseSpringComponent {


    public <A> A getApi(final Class<A> apiClass, final URI basePath) throws ClientApiFacadeException {

       log.info("Providing Client Api for "+apiClass.getSimpleName()+" with basePath "+basePath);

       return OpenAPIClientFacade.builder()
                .basePath(basePath.toString())
                .build()
                .getApi(apiClass);

    }

}
