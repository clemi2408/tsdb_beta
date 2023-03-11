package de.cleem.tub.tsdbb.apps.worker;

import de.cleem.tub.tsdbb.apps.worker.rest.WorkerREST;
import de.cleem.tub.tsdbb.apps.worker.service.WorkerService;
import de.cleem.tub.tsdbb.commons.base.app.BaseAppException;
import de.cleem.tub.tsdbb.commons.spring.apiclient.ApiClientService;
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

        WorkerService.class,
        WorkerREST.class

})
public class App extends BaseSpringApp {

    public static void main(String[] args) throws BaseAppException {

        startSpringApp(App.class, args);

    }

}
