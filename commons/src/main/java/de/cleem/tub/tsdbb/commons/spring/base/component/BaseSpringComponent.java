package de.cleem.tub.tsdbb.commons.spring.base.component;


import de.cleem.tub.tsdbb.commons.base.clazz.BaseClass;
import jakarta.servlet.ServletContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Slf4j
@Component
public class BaseSpringComponent extends BaseClass implements InitializingBean, DisposableBean {

    @Autowired
    private ServletContext servletContext;

    @Override
    public void destroy() {

        log.info("Destroying: "+this.getClass().getSimpleName());

    }

    @Override
    public void afterPropertiesSet() throws Exception {

        log.info("Wired: "+this.getClass().getSimpleName());

    }

    public URI getServerUrl(){

        return ServletUriComponentsBuilder.fromCurrentServletMapping().build().toUri();

    }



}
