package de.cleem.tub.tsdbb.apps.orchestrator.service;


import de.cleem.tub.tsdbb.api.model.PreloadRequest;
import de.cleem.tub.tsdbb.api.model.PreloadResponse;
import de.cleem.tub.tsdbb.api.model.PingResponse;
import de.cleem.tub.tsdbb.api.model.WorkerConfig;
import de.cleem.tub.tsdbb.commons.spring.apiclient.ApiClientService;
import de.cleem.tub.tsdbb.commons.api.ClientApiFacadeException;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import de.cleem.tub.tsdbb.api.generator.client.GeneratorGenerateApi;
import de.cleem.tub.tsdbb.api.generator.client.GeneratorPingApi;
import de.cleem.tub.tsdbb.api.worker.client.WorkerPingApi;
import de.cleem.tub.tsdbb.api.model.Workload;
import de.cleem.tub.tsdbb.commons.spring.ping.PingException;
import de.cleem.tub.tsdbb.commons.spring.ping.PingResponder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
@Slf4j
public class OrchestratorService extends BaseSpringComponent {

    @Autowired
    private ApiClientService apiClientService;


    private void checkPingResponse(final PingResponse pingResponse, final String type, final String url) throws PingException {

        if(!pingResponse.getStatus().equals(PingResponder.OK)){

            throw new PingException("Can not ping "+type+" at "+url);

        }

    }

    public PreloadResponse preload(final PreloadRequest preloadRequest) throws ClientApiFacadeException, PingException {

        final String generatorUrl = preloadRequest.getGeneratorUrl();

        final GeneratorPingApi generatorPingApi = apiClientService.getApi(GeneratorPingApi.class,generatorUrl);

        log.info("Sending ping to generator: "+generatorUrl);
        checkPingResponse(generatorPingApi.ping(),"generator",generatorUrl);

        WorkerPingApi workerPingApi;
        String workerUrl;
        for(WorkerConfig workerConfig : preloadRequest.getWorkerConfigs()){

            workerUrl = workerConfig.getWorkerUrl();
            workerPingApi = apiClientService.getApi(WorkerPingApi.class,workerUrl);

            log.info("Sending ping to worker: "+workerUrl);
            checkPingResponse(workerPingApi.ping(),"worker",workerUrl);

        }

        final GeneratorGenerateApi generatorGenerateApi = apiClientService.getApi(GeneratorGenerateApi.class,generatorUrl);

        log.info("Requesting workload from generator: "+generatorUrl);
        final Workload workload = generatorGenerateApi.generate(preloadRequest.getGeneratorConfig());

        log.info("Got workload from generator with "+workload.getRecords().size()+" records");


        final PreloadResponse preloadResponse = new PreloadResponse();
        preloadResponse.setPreloadRequest(preloadRequest);
        preloadResponse.setWorkload(workload);


        return preloadResponse;

    }

}
