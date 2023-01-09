package de.cleem.bm.tsdb.datagenerator;

import de.cleem.bm.tsdb.common.random.RandomHelper;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

@Slf4j
public class WorkloadGenerator {

    private WorkloadGeneratorConfig config;


    public WorkloadGenerator(){

        log.info("Constructing: "+this.getClass().getSimpleName());
        config = new WorkloadGeneratorConfig();

    }

    public static WorkloadGenerator builder(){

        return new WorkloadGenerator();
    }


    public WorkloadGenerator workloadGeneratorConfig(final WorkloadGeneratorConfig workloadGeneratorConfig){

        config = workloadGeneratorConfig;

        return this;

    }

    public WorkloadGenerator recordCount(final Integer recordCount){

        config.setRecordCount(recordCount);

        return this;

    }

    public WorkloadGenerator keyCountPerRecord(final Integer keyCountPerRecord){

        config.setKeyCountPerRecord(keyCountPerRecord);

        return this;

    }

    public WorkloadGenerator minKeyLength(final Integer minKeyLength){

        config.setMinKeyLength(minKeyLength);

        return this;

    }

    public WorkloadGenerator maxKeyLength(final Integer maxKeyLength){

       config.setMaxKeyLength(maxKeyLength);

        return this;

    }

    public WorkloadGenerator minValue(final Double minValue){

        config.setMinValue(minValue);

        return this;

    }

    public WorkloadGenerator maxValue(final Double maxValue){

       config.setMaxValue(maxValue);

       return this;

    }

    //////////////

    private void validate() throws DataGeneratorConfigurationException {

        log.info("Validating: "+config.getClass().getSimpleName());

        if(config==null){
            throw new DataGeneratorConfigurationException("Data generator config is NULL");
        }

        final ArrayList<String> nullFields = new ArrayList<>();
        final HashMap<String,Integer> invalidFields = new HashMap<>();

        Number currentFieldValue = null;
        String currentFieldName = null;
        boolean hasErrors = false;
        for(Field field : config.getClass().getDeclaredFields()){

            field.setAccessible(true);
            currentFieldName = field.getName();

            try {

            if(field.getType() == Integer.class){
                log.info("Validating Integer: "+config.getClass().getSimpleName() + " Field: "+currentFieldName);
                currentFieldValue = (Integer) field.get(config);

                if(currentFieldValue.intValue()<=0){

                    invalidFields.put(currentFieldName, currentFieldValue.intValue());
                    hasErrors = true;
                }

            }
            else if(field.getType() == Double.class){
                log.info("Validating Double: "+config.getClass().getSimpleName() + " Field: "+currentFieldName);
                currentFieldValue = (Double) field.get(config);
            }


            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

                if(currentFieldValue==null){

                   nullFields.add(currentFieldName);
                   hasErrors=true;

               }




            field.setAccessible(false);

        }


        if(hasErrors){

            throw new DataGeneratorConfigurationException(nullFields,invalidFields);

        }

        log.info(config.getClass().getSimpleName() +" is valid!");


    }

    private HashMap<String,Number> generateRecord(final String[] keyArray, final int index, final int total){

        final HashMap<String,Number> recordKv = new HashMap<>();

        log.info("Generating Record "+index+" of "+total);

        int randomValueStringLength;
        double randomValue;
        for(int i = 0; i < keyArray.length; i ++){

           // randomValueStringLength = RandomHelper.getRandomIntInRange(config.getMinValueLength(), config.getMaxValueLength());
            randomValue = RandomHelper.getRandomDoubleInRange(config.getMinValue(),config.getMaxValue());

            recordKv.put(keyArray[i], randomValue);

            log.info("\tGenerated: "+keyArray[i]+" --> "+randomValue);

        }

        return recordKv;
    }

    private String[] generateKeyArray(){

        log.info("Generating keyArray with "+config.getKeyCountPerRecord()+ " Keys per Record");

        final String[] keyArray = new String[config.getKeyCountPerRecord()];

        int randomKeyStringLength;
        String randomKeyString;
        for(int i = 0; i < config.getKeyCountPerRecord(); i++){

            randomKeyStringLength = RandomHelper.getRandomIntInRange(config.getMinKeyLength(),config.getMaxKeyLength());
            randomKeyString = RandomHelper.getRandomString(randomKeyStringLength);
            keyArray[i] = randomKeyString;

            log.info("\tGenerated Key "+(i+1)+": "+randomKeyString);

        }

        return keyArray;

    }

    private GeneratedWorkload generateRecords(){

        log.info("Generating: "+config.getRecordCount()+ " Records");

        final LinkedList<HashMap<String,Number>> data = new LinkedList<>();

        final String[] keyArray = generateKeyArray();

        for(int i = 1; i <=config.getRecordCount(); i++){

            data.add(generateRecord(keyArray,i,config.getRecordCount()));

        }

        final GeneratedWorkload generatedWorkload = new GeneratedWorkload();
        generatedWorkload.setData(data);

        return generatedWorkload;

    }

    public GeneratedWorkload build() throws DataGeneratorConfigurationException {

        validate();

        return generateRecords();

    }


}
