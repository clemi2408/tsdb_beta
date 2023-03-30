package de.cleem.tub.tsdbb.commons.base.clazz;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseClass {

    public BaseClass(){
        log.info("Creating: " + this.getClass().getSimpleName());
    }
}
