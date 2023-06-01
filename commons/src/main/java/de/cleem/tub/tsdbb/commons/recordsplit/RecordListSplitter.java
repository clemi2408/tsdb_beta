package de.cleem.tub.tsdbb.commons.recordsplit;

import de.cleem.tub.tsdbb.api.model.Record;
import de.cleem.tub.tsdbb.commons.random.numbers.uniform.UniformGenerator;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;

@Slf4j
public class RecordListSplitter {

    //////// Split Worker based on Items Percentage (e.g. to Workers or Endpoints)
    public static <T, U extends Comparable<? super U>> List<List<Record>> splitWorkload(final List<Record> records, final List<T> items, final Function<? super T, ? extends U> valueExtractor) {

        // Create Output List<List>
        final List<List<Record>> itemsWorkloadList = new ArrayList<>();

        // Create LookupMap
        final Map<T, List<Record>> itemToRecordListMap = new HashMap<>();

        //Prepare Output List<List> and LookupMap
        List<Record> itemRecordList;
        for(T item : items){

            itemRecordList = new ArrayList<Record>();
            // Add Reference to WorkerWorkload List to Output List<List>
            itemsWorkloadList.add(itemRecordList);

            // Add Reference to WorkerWorkload List to LookupMap
            itemToRecordListMap.put(item, itemRecordList);

        }

        // Create Distribution Intervals
        final LinkedHashMap<Integer[], T> lookupIntervals = createLookupIntervals(items, valueExtractor);

        final Integer upperBound = getUpperBound(lookupIntervals,valueExtractor);

        //Map Records to Items
        T currentItem;
        for(Record currentRecord : records){

            currentItem= doRangeLookup(lookupIntervals, upperBound,valueExtractor);
            itemToRecordListMap.get(currentItem).add(currentRecord);

        }

        return itemsWorkloadList;

    }

    /////// Create Lookup Intervals based on Integer percentage values of Items (e.g. Worker or Endpoints)
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

    /////// Lookup Item (e.g Worker or Endpoint) based on Random value and Item Interval
    public static <T,U> T doRangeLookup(final LinkedHashMap<Integer[],T> lookupMap, final Integer upperBound, final Function<? super T, ? extends U> valueExtractor){

        if(lookupMap.size()==1){
            //If only one candidate is available, return this without random selection
            return lookupMap.entrySet().iterator().next().getValue();
        }

        final Integer randomNumber = UniformGenerator.getInteger(0,upperBound);

        //log.debug("Random range 0-"+upperBound);
        //log.debug("Random number "+randomNumber);


        return getEntryByValueInRange(lookupMap,randomNumber);

    }

    ////// Return Item where value is in Range of the Item
    private static <T> T getEntryByValueInRange(final LinkedHashMap<Integer[],T> lookupMap, final Integer value){

        Integer[] range;
        for(Map.Entry<Integer[], T> entry : lookupMap.entrySet()){

            range = entry.getKey();

            if(range.length==2){

                if(range[0]<=value && value<=range[1]){

                   // log.debug("Lookup-Range: "+range[0]+"-"+range[1]);

                    return entry.getValue();

                }

            }

        }

        return null;
    }

    // Get the Upper bound of the Interval
    public static <T,U> Integer getUpperBound(LinkedHashMap<Integer[],T> lookupMap, Function<? super T,? extends U> valueExtractor) {

        final Map.Entry result = (Map.Entry) lookupMap.entrySet().toArray()[lookupMap.size() - 1];

        return (Integer)valueExtractor.apply((T)result.getValue());

    }

}
