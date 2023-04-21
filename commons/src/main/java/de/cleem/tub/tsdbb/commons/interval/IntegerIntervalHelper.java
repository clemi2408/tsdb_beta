package de.cleem.tub.tsdbb.commons.interval;

import de.cleem.tub.tsdbb.api.model.WorkerConfiguration;
import de.cleem.tub.tsdbb.api.model.WorkerTsdbEndpoint;
import de.cleem.tub.tsdbb.commons.random.numbers.uniform.UniformGenerator;

import java.util.*;
import java.util.function.Function;

public class IntegerIntervalHelper {

    public static void main(String[] args) {

        final List<WorkerTsdbEndpoint> exampleEndpoints = getExampleEndpoints();
        final List<WorkerConfiguration> exampleWorkers = getExampleWorkerConfigs();

        final LinkedHashMap<Integer[],WorkerTsdbEndpoint> endpointLookup = createLookupIntervals(exampleEndpoints, WorkerTsdbEndpoint::getEndpointPercentage);
        final LinkedHashMap<Integer[],WorkerConfiguration> workerLookup = createLookupIntervals(exampleWorkers, WorkerConfiguration::getWorkerPercentage);

        final WorkerTsdbEndpoint endpoint = doRangeLookup(endpointLookup,WorkerTsdbEndpoint::getEndpointPercentage);
        System.out.println("Lookup: "+endpoint.getEndpointName()+" - "+endpoint.getEndpointPercentage());

        final WorkerConfiguration worker = doRangeLookup(workerLookup,WorkerConfiguration::getWorkerPercentage);
        System.out.println("Lookup: "+worker.getWorkerName()+" - "+ worker.getWorkerPercentage());

    }

    public static <T, U extends Comparable<? super U>> LinkedHashMap<Integer[],T> createLookupIntervals(
            final List<T> items,
            final Function<? super T, ? extends U> valueExtractor){

        final LinkedHashMap<Integer[], T> endpointLookupMap= new LinkedHashMap<>();

        items.sort(Comparator.comparing(valueExtractor));

        Integer previousBound=-1;
        Integer integer;

        for(T endpoint : items){

                previousBound+=1;
                integer=(Integer) valueExtractor.apply(endpoint);
                endpointLookupMap.put(new Integer[]{previousBound,integer},endpoint);
                previousBound=integer;

        }

        return endpointLookupMap;


    }

    public static <T,U> T doRangeLookup(final LinkedHashMap<Integer[],T> lookupMap, final Function<? super T, ? extends U> valueExtractor){

        if(lookupMap.size()==1){
            System.out.println("Returning single entry of LookupMap");
            return lookupMap.entrySet().iterator().next().getValue();
        }

        final Integer upperBound = getUpperBound(lookupMap,valueExtractor);
        final Integer randomNumber = UniformGenerator.getInteger(0,upperBound);

        System.out.println("Random range 0-"+upperBound);
        System.out.println("Random number "+randomNumber);


        return getEntryByValueInRange(lookupMap,randomNumber);

    }

    /////////

    private static <T> T getEntryByValueInRange(final LinkedHashMap<Integer[],T> lookupMap, final Integer value){

        Integer[] range;
        for(Map.Entry<Integer[], T> entry : lookupMap.entrySet()){

            range = entry.getKey();

            if(range.length==2){

                if(range[0]<=value && value<=range[1]){

                    System.out.println("Lookup-Range: "+range[0]+"-"+range[1]);

                    return entry.getValue();

                }

            }

        }

        return null;
    }

    private static <T,U> Integer getUpperBound(LinkedHashMap<Integer[],T> lookupMap, Function<? super T,? extends U> valueExtractor) {

        final Map.Entry result = (Map.Entry) lookupMap.entrySet().toArray()[lookupMap.size() - 1];

        return (Integer)valueExtractor.apply((T)result.getValue());

    }

    /////////

    private static List<WorkerTsdbEndpoint> getExampleEndpoints(){

        final List<WorkerTsdbEndpoint> endpoints = new ArrayList<>();

        endpoints.add(getEndpoint("E1",20));
        endpoints.add(getEndpoint("E2",30));
        endpoints.add(getEndpoint("E3",55));
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
        workers.add(getWorkerConfig("W2",35));
        workers.add(getWorkerConfig("W3",60));
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
