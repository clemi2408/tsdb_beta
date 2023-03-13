package de.cleem.tub.tsdbb.commons.spring.objectcache;

import de.cleem.tub.tsdbb.api.model.ResetResponse;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Component
@ApplicationScope
@Slf4j
public class SingleObjectInstanceCache extends BaseSpringComponent {

    private static final HashMap<Class<?>,Object> CACHE = new HashMap<>();

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

    public <T> void remove(final Class<T> clazz) throws SingleObjectInstanceCacheException {

        if(clazz==null){
            throw new SingleObjectInstanceCacheException("Can not remove NULL instance from cache");
        }

        if(!CACHE.containsKey(clazz)){
            throw new SingleObjectInstanceCacheException("Nothing cached for class "+clazz.getSimpleName());
        }

        log.info("Removing instance of: "+clazz.getSimpleName()+" from cache");
        CACHE.remove(clazz);

    }


    public ResetResponse reset(final Class<?>[] classesToReset) {

        return reset(classesToReset,null);

    }

        public ResetResponse reset(final Class<?>[] classesToReset, final List<ResetResponse> nestedResponses) {

        final ResetResponse resetResponse = new ResetResponse();
        resetResponse.setStartTimestamp(OffsetDateTime.now());

        for(Class<?> clazz : classesToReset){

            try {

                remove(clazz);

                if(resetResponse.getCleared()==null){
                    resetResponse.setCleared(new ArrayList<>());
                }

                resetResponse.getCleared().add(clazz.getSimpleName());


            } catch (SingleObjectInstanceCacheException e) {

                if(resetResponse.getError()==null){
                    resetResponse.setError(new ArrayList<>());
                }

                resetResponse.getError().add(clazz.getSimpleName());

            }

        }

        if(nestedResponses!=null && nestedResponses.size()>0) {
            resetResponse.setNestedResponses(nestedResponses);
        }

        if(resetResponse.getError()!=null&& resetResponse.getError().size()>0){

            resetResponse.setStatus("Cleared cache with errors");

        }
        else{

            resetResponse.setStatus("Cache cleared");

        }

        resetResponse.setEndTimestamp(OffsetDateTime.now());

        return resetResponse;


    }


}
