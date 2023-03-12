package de.cleem.tub.tsdbb.apps.orchestrator.rest;

import de.cleem.tub.tsdbb.api.model.GeneratorGenerateRequest;
import de.cleem.tub.tsdbb.api.model.OrchestratorPreloadRequest;
import de.cleem.tub.tsdbb.api.model.WorkerConfig;
import de.cleem.tub.tsdbb.api.model.WorkerTsdbConnection;
import de.cleem.tub.tsdbb.commons.file.FileException;
import de.cleem.tub.tsdbb.commons.file.FileHelper;
import de.cleem.tub.tsdbb.commons.json.JsonException;
import de.cleem.tub.tsdbb.commons.json.JsonHelper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

@Slf4j
public class TEST {

    public static void main(String[] args) throws FileException, JsonException {

        OrchestratorPreloadRequest preloadRequest = new OrchestratorPreloadRequest();

        GeneratorGenerateRequest generateRequest = JsonHelper.objectFromByteArray(FileHelper.read(new File("/Users/clemens/IdeaProjects/tsdb_beta/schema/src/main/resources/api/model/examples/generatorConfig_2.json")), GeneratorGenerateRequest.class);

        WorkerTsdbConnection tsdbConnection = new WorkerTsdbConnection();
        tsdbConnection.setHost("tst");
        tsdbConnection.setPort(9000);
        tsdbConnection.setTsdbType("abc");

        WorkerConfig workerConfig = new WorkerConfig();
        workerConfig.setWorkerUrl("http://localhost:8082");
        workerConfig.setCleanupStorage(true);
        workerConfig.setWorkerThreads(3);
        workerConfig.setTsdbConnection(tsdbConnection);

        preloadRequest.setGenerateRequest(generateRequest);
        preloadRequest.setGeneratorUrl("http://localhost:8080");
        preloadRequest.setWorkerConfigs(List.of(workerConfig));

        final String request = JsonHelper.toString(preloadRequest,true);

        log.info(request);

    }


}
