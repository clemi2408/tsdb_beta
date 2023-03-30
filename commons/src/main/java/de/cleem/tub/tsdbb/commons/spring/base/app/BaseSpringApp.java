package de.cleem.tub.tsdbb.commons.spring.base.app;

import de.cleem.tub.tsdbb.commons.base.app.BaseApp;
import de.cleem.tub.tsdbb.commons.base.app.BaseAppException;
import de.cleem.tub.tsdbb.commons.spring.banner.DynamicBanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;


@Slf4j
public class BaseSpringApp extends BaseApp {

    public static void startSpringApp(final Class<?> appClass, final String[] args) throws BaseAppException {

        final SpringApplication app = new SpringApplication(appClass);
        app.setBanner(DynamicBanner.builder().build());
        app.run(args);

        startBaseApp();

    }

}
