package de.cleem.tub.tsdbb.commons.spring.base.component;


import de.cleem.tub.tsdbb.commons.base.clazz.BaseClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

@Slf4j
public class BaseSpringComponent extends BaseClass implements InitializingBean, DisposableBean {

    @Override
    public void destroy() throws Exception {

        log.info("Destroying: "+this.getClass().getSimpleName());

    }

    @Override
    public void afterPropertiesSet() throws Exception {

        log.info("Wired: "+this.getClass().getSimpleName());

    }
}
