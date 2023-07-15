package de.cleem.tub.tsdbb.commons.insertsplit;

import de.cleem.tub.tsdbb.api.model.Insert;
import de.cleem.tub.tsdbb.api.model.WorkerConfiguration;
import de.cleem.tub.tsdbb.api.model.WorkerTsdbEndpoint;
import de.cleem.tub.tsdbb.commons.examplejson.ExampleDataGenerator;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
public class StartIT {

    public static void main(String[] args) {

        final List<Insert> inserts = ExampleDataGenerator.getInserts(1000);

        final List<WorkerTsdbEndpoint> exampleEndpoints = getExampleEndpoints();
        final LinkedHashMap<Integer[],WorkerTsdbEndpoint> endpointLookup = InsertListSplitter.createLookupIntervals(exampleEndpoints, WorkerTsdbEndpoint::getEndpointPercentage);
        final Integer upperBoundEndpoints = InsertListSplitter.getUpperBound(endpointLookup,WorkerTsdbEndpoint::getEndpointPercentage);
        final WorkerTsdbEndpoint endpoint = InsertListSplitter.doRangeLookup(endpointLookup,upperBoundEndpoints,WorkerTsdbEndpoint::getEndpointPercentage);
        //log.debug("Lookup: "+endpoint.getEndpointName()+" - "+endpoint.getEndpointPercentage());
        final List<List<Insert>> workerEndpointSplit = InsertListSplitter.splitWorkload(inserts,exampleEndpoints, WorkerTsdbEndpoint::getEndpointPercentage);
        displaySplit(workerEndpointSplit);

        final List<WorkerConfiguration> exampleWorkers = getExampleWorkerConfigs();
        final LinkedHashMap<Integer[],WorkerConfiguration> workerLookup = InsertListSplitter.createLookupIntervals(exampleWorkers, WorkerConfiguration::getWorkerPercentage);
        final Integer upperBoundWorkers = InsertListSplitter.getUpperBound(workerLookup,WorkerConfiguration::getWorkerPercentage);
        final WorkerConfiguration worker = InsertListSplitter.doRangeLookup(workerLookup,upperBoundWorkers,WorkerConfiguration::getWorkerPercentage);
        //log.debug("Lookup: "+worker.getWorkerName()+" - "+ worker.getWorkerPercentage());
        final List<List<Insert>> workerSplit = InsertListSplitter.splitWorkload(inserts,exampleWorkers, WorkerConfiguration::getWorkerPercentage);
        displaySplit(workerSplit);

    }

    private static void displaySplit(final List<List<Insert>> split){
        System.out.println("\n");
        System.out.println("Split into: "+split.size()+" parts");

        int totalInserts=0;
        int i = 0;
        for(List<Insert> splitPart : split){
            System.out.println("\tPart: "+i);
            i++;
            totalInserts+=splitPart.size();
            System.out.println("\t\tCount: "+splitPart.size());

        }

        System.out.println("Total Inserts: "+totalInserts);


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



