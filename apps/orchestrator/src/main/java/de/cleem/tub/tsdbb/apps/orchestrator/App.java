package de.cleem.tub.tsdbb.apps.orchestrator;

import de.cleem.tub.tsdbb.commons.spring.apiclient.ApiClientService;
import de.cleem.tub.tsdbb.apps.orchestrator.rest.OrchestratorREST;
import de.cleem.tub.tsdbb.apps.orchestrator.service.OrchestratorService;
import de.cleem.tub.tsdbb.commons.base.app.BaseAppException;
import de.cleem.tub.tsdbb.commons.spring.base.app.BaseSpringApp;
import de.cleem.tub.tsdbb.commons.spring.exceptionhandler.AppExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackageClasses = {

        AppExceptionHandler.class,
        ApiClientService.class,

        OrchestratorService.class,
        OrchestratorREST.class


})

public class App extends BaseSpringApp {

    public static void main(String[] args) throws BaseAppException {

        startSpringApp(App.class, args);

    }

}
