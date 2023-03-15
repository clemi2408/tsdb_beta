package de.cleem.tub.tsdbb.commons.api;

import de.cleem.tub.tsdbb.commons.base.clazz.BaseClass;
import lombok.Builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Builder
public class OpenAPIClientFacade extends BaseClass {

    private String basePath;


    public <A,C> A getApi(final Class<A> apiClass) throws ClientApiFacadeException{

        try {

            final A api = newInstance(apiClass);

            final Method getApiClientMethod = api.getClass().getDeclaredMethod("getApiClient");
            getApiClientMethod.setAccessible(true);

            @SuppressWarnings("unchecked") final C apiClient = (C)getApiClientMethod.invoke(api);

            getApiClientMethod.setAccessible(false);

            final Method setBasePathMethod = apiClient.getClass().getDeclaredMethod("setBasePath",String.class);
            setBasePathMethod.setAccessible(true);
            setBasePathMethod.invoke(apiClient,basePath);
            setBasePathMethod.setAccessible(false);

            return api;

        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new ClientApiFacadeException(e);
        }

    }


    private <T> T newInstance(final Class<T> clazz) throws ClientApiFacadeException{

        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new ClientApiFacadeException(e);
        }
    }

}
