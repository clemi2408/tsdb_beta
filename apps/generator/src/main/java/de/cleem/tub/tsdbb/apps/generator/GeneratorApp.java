package de.cleem.tub.tsdbb.apps.generator;

import de.cleem.tub.tsdbb.apps.generator.rest.GeneratorREST;
import de.cleem.tub.tsdbb.apps.generator.service.GeneratorService;
import de.cleem.tub.tsdbb.commons.base.app.BaseAppException;
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

        GeneratorService.class,
        GeneratorREST.class

})

public class GeneratorApp extends BaseSpringApp {

    public static void main(String[] args) throws BaseAppException {

        startSpringApp(GeneratorApp.class, args);

    }

}
