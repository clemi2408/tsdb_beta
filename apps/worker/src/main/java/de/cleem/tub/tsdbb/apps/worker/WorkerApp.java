package de.cleem.tub.tsdbb.apps.worker;

import de.cleem.tub.tsdbb.apps.worker.controller.WorkerController;
import de.cleem.tub.tsdbb.apps.worker.service.run.RunService;
import de.cleem.tub.tsdbb.apps.worker.service.worker.WorkerService;
import de.cleem.tub.tsdbb.commons.base.app.BaseAppException;
import de.cleem.tub.tsdbb.commons.spring.apiclient.ApiClientService;
import de.cleem.tub.tsdbb.commons.spring.base.app.BaseSpringApp;
import de.cleem.tub.tsdbb.commons.spring.exceptionhandler.AppExceptionHandler;
import de.cleem.tub.tsdbb.commons.spring.objectmapper.ObjectMapperConfig;
import de.cleem.tub.tsdbb.commons.spring.pingresponder.PingResponderService;
import de.cleem.tub.tsdbb.commons.spring.remotecontrol.RemoteControlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackageClasses = {

        ObjectMapperConfig.class,
        AppExceptionHandler.class,
        PingResponderService.class,
        ApiClientService.class,
        RemoteControlService.class,

        RunService.class,
        WorkerService.class,
        WorkerController.class

})
public class WorkerApp extends BaseSpringApp {

    public static void main(String[] args) throws BaseAppException {

        startSpringApp(WorkerApp.class, args);

    }

}
