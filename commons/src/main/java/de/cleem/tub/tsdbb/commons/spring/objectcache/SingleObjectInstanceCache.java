package de.cleem.tub.tsdbb.commons.spring.objectcache;

import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.HashMap;


@Component
@ApplicationScope
@Slf4j
public class SingleObjectInstanceCache extends BaseSpringComponent {

    private static final HashMap<Class,Object> CACHE = new HashMap<>();

    public <T> T get(final Class<T> clazz) throws SingleObjectInstanceCacheException {

        if(clazz==null){
            throw new SingleObjectInstanceCacheException("Can not resolve NULL class");
        }

        if(!CACHE.containsKey(clazz)){
            throw new SingleObjectInstanceCacheException("Nothing cached for class "+clazz.getSimpleName());
        }

        log.info("Reading instance of: "+clazz+" from cache");


        return (T)CACHE.get(clazz);

    }

    public <T> void add(final T instance) throws SingleObjectInstanceCacheException {

        if(instance==null){
            throw new SingleObjectInstanceCacheException("Can not cache NULL instance");
        }

        log.info("Caching instance of: "+instance.getClass());
        CACHE.put(instance.getClass(),instance);

    }

}
