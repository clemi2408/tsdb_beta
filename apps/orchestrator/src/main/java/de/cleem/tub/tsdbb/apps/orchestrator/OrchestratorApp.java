package de.cleem.tub.tsdbb.apps.orchestrator;

import de.cleem.tub.tsdbb.apps.orchestrator.controller.OrchestratorController;
import de.cleem.tub.tsdbb.apps.orchestrator.service.collector.WorkloadCollectorService;
import de.cleem.tub.tsdbb.apps.orchestrator.service.orchestrator.OrchestratorService;
import de.cleem.tub.tsdbb.apps.orchestrator.service.preparation.WorkloadPreparationService;
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
        ApiClientService.class,

        PingResponderService.class,
        RemoteControlService.class,
        WorkloadPreparationService.class,
        WorkloadCollectorService.class,

        OrchestratorService.class,
        OrchestratorController.class

})

public class OrchestratorApp extends BaseSpringApp {

    public static void main(String[] args) throws BaseAppException {

        startSpringApp(OrchestratorApp.class, args);

    }

}
