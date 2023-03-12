package de.cleem.tub.tsdbb.apps.worker;

import de.cleem.tub.tsdbb.apps.worker.rest.WorkerREST;
import de.cleem.tub.tsdbb.apps.worker.service.WorkerService;
import de.cleem.tub.tsdbb.commons.spring.objectcache.SingleObjectInstanceCache;
import de.cleem.tub.tsdbb.commons.base.app.BaseAppException;
import de.cleem.tub.tsdbb.commons.spring.apiclient.ApiClientService;
import de.cleem.tub.tsdbb.commons.spring.base.app.BaseSpringApp;
import de.cleem.tub.tsdbb.commons.spring.exceptionhandler.AppExceptionHandler;
import de.cleem.tub.tsdbb.commons.spring.objectmapper.ObjectMapperConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackageClasses = {

        ObjectMapperConfig.class,
        AppExceptionHandler.class,
        ApiClientService.class,

        SingleObjectInstanceCache.class,

        WorkerService.class,
        WorkerREST.class

})
public class WorkerApp extends BaseSpringApp {

    public static void main(String[] args) throws BaseAppException {

        startSpringApp(WorkerApp.class, args);

    }

}
