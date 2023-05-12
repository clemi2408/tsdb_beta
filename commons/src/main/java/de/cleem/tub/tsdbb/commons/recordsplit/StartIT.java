package de.cleem.tub.tsdbb.commons.recordsplit;

import de.cleem.tub.tsdbb.api.model.Record;
import de.cleem.tub.tsdbb.api.model.WorkerConfiguration;
import de.cleem.tub.tsdbb.api.model.WorkerTsdbEndpoint;
import de.cleem.tub.tsdbb.commons.examplejson.ExampleDataGenerator;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
public class StartIT {

    public static void main(String[] args) {

        final List<Record> records = ExampleDataGenerator.getRecords(1000);

        final List<WorkerTsdbEndpoint> exampleEndpoints = getExampleEndpoints();
        final LinkedHashMap<Integer[],WorkerTsdbEndpoint> endpointLookup = RecordListSplitter.createLookupIntervals(exampleEndpoints, WorkerTsdbEndpoint::getEndpointPercentage);
        final Integer upperBoundEndpoints = RecordListSplitter.getUpperBound(endpointLookup,WorkerTsdbEndpoint::getEndpointPercentage);
        final WorkerTsdbEndpoint endpoint = RecordListSplitter.doRangeLookup(endpointLookup,upperBoundEndpoints,WorkerTsdbEndpoint::getEndpointPercentage);
        //log.debug("Lookup: "+endpoint.getEndpointName()+" - "+endpoint.getEndpointPercentage());
        final List<List<Record>> workerEndpointSplit = RecordListSplitter.splitWorkload(records,exampleEndpoints, WorkerTsdbEndpoint::getEndpointPercentage);
        displaySplit(workerEndpointSplit);

        final List<WorkerConfiguration> exampleWorkers = getExampleWorkerConfigs();
        final LinkedHashMap<Integer[],WorkerConfiguration> workerLookup = RecordListSplitter.createLookupIntervals(exampleWorkers, WorkerConfiguration::getWorkerPercentage);
        final Integer upperBoundWorkers = RecordListSplitter.getUpperBound(workerLookup,WorkerConfiguration::getWorkerPercentage);
        final WorkerConfiguration worker = RecordListSplitter.doRangeLookup(workerLookup,upperBoundWorkers,WorkerConfiguration::getWorkerPercentage);
        //log.debug("Lookup: "+worker.getWorkerName()+" - "+ worker.getWorkerPercentage());
        final List<List<Record>> workerSplit = RecordListSplitter.splitWorkload(records,exampleWorkers, WorkerConfiguration::getWorkerPercentage);
        displaySplit(workerSplit);

    }

    private static void displaySplit(final List<List<Record>> split){
        System.out.println("\n");
        System.out.println("Split into: "+split.size()+" parts");

        int totalRecords=0;
        int i = 0;
        for(List<Record> splitPart : split){
            System.out.println("\tPart: "+i);
            i++;
            totalRecords+=splitPart.size();
            System.out.println("\t\tCount: "+splitPart.size());

        }

        System.out.println("Total Records: "+totalRecords);


    }

    private static List<WorkerTsdbEndpoint> getExampleEndpoints(){

        final List<WorkerTsdbEndpoint> endpoints = new ArrayList<>();

        endpoints.add(getEndpoint("E1",25));
        endpoints.add(getEndpoint("E2",50));
        endpoints.add(getEndpoint("E3",75));
        endpoints.add(getEndpoint("E4",100));

        return endpoints;
    }

    private static WorkerTsdbEndpoint getEndpoint(final String name, final Integer percentage){

        final WorkerTsdbEndpoint endpoint = new WorkerTsdbEndpoint();
        endpoint.setEndpointName(name);
        endpoint.setEndpointPercentage(percentage);

        return endpoint;

    }

    private static List<WorkerConfiguration> getExampleWorkerConfigs(){

        final List<WorkerConfiguration> workers = new ArrayList<>();

        workers.add(getWorkerConfig("W1",25));
        workers.add(getWorkerConfig("W2",50));
        workers.add(getWorkerConfig("W3",75));
        workers.add(getWorkerConfig("W4",100));

        return workers;
    }

    private static WorkerConfiguration getWorkerConfig(final String name, final Integer percentage){

        final WorkerConfiguration worker = new WorkerConfiguration();
        worker.setWorkerName(name);
        worker.setWorkerPercentage(percentage);

        return worker;

    }





}



