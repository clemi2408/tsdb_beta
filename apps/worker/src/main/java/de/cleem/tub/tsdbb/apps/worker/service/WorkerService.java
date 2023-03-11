package de.cleem.tub.tsdbb.apps.worker.service;


import de.cleem.tub.tsdbb.commons.spring.apiclient.ApiClientService;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class WorkerService extends BaseSpringComponent {

    @Autowired
    private ApiClientService apiClientService;

}
