package de.cleem.tub.tsdbb.apps.orchestrator.rest;

import de.cleem.tub.tsdbb.api.model.TsdbConnection;
import de.cleem.tub.tsdbb.commons.file.FileException;
import de.cleem.tub.tsdbb.commons.file.FileHelper;
import de.cleem.tub.tsdbb.commons.json.JsonException;
import de.cleem.tub.tsdbb.commons.json.JsonHelper;
import de.cleem.tub.tsdbb.api.model.GeneratorConfig;
import de.cleem.tub.tsdbb.api.model.PreloadRequest;
import de.cleem.tub.tsdbb.api.model.WorkerConfig;
import  de.cleem.tub.tsdbb.api.model.TsdbConnection;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

@Slf4j
public class TEST {

    public static void main(String[] args) throws FileException, JsonException {

        PreloadRequest preloadRequest = new de.cleem.tub.tsdbb.api.model.PreloadRequest();

        GeneratorConfig generatorConfig = JsonHelper.objectFromByteArray(FileHelper.read(new File("/Users/clemens/IdeaProjects/tsdb_beta/schema/src/main/resources/api/model/examples/generatorConfig_1.json")),GeneratorConfig.class);

        TsdbConnection tsdbConnection = new TsdbConnection();
        tsdbConnection.setHost("tst");
        tsdbConnection.setPort(9000);
        tsdbConnection.setTsdbType("abc");

        WorkerConfig workerConfig = new WorkerConfig();
        workerConfig.setWorkerUrl("http://localhost:8082");
        workerConfig.setTsdbConnection(tsdbConnection);

        preloadRequest.setGeneratorConfig(generatorConfig);
        preloadRequest.setGeneratorUrl("http://localhost:8080");
        preloadRequest.setWorkerConfigs(List.of(workerConfig));

        final String request = JsonHelper.toString(preloadRequest,true);

        log.info(request);

    }


}
