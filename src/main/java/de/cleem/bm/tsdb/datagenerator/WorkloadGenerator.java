package de.cleem.bm.tsdb.datagenerator;

import de.cleem.bm.tsdb.common.exception.TSDBBenchmarkException;
import de.cleem.bm.tsdb.common.random.RandomHelper;
import de.cleem.bm.tsdb.common.random.RandomValueGenerationException;
import de.cleem.bm.tsdb.model.config.datagenerator.RecordConfig;
import de.cleem.bm.tsdb.model.config.datagenerator.WorkloadGeneratorConfig;
import de.cleem.bm.tsdb.model.config.workload.KvPair;
import de.cleem.bm.tsdb.model.config.workload.WorkloadData;
import de.cleem.bm.tsdb.model.config.workload.WorkloadRecord;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.UUID;

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

    //////////////

  /*  private HashMap<String,Number> generateRecord(final String[] keyArray, final int index, final int total){

        final HashMap<String,Number> recordKv = new HashMap<>();



        int randomValueStringLength;
        double randomValue;
        for(int i = 0; i < keyArray.length; i ++){

           // randomValueStringLength = RandomHelper.getRandomIntInRange(config.getMinValueLength(), config.getMaxValueLength());
           // randomValue = RandomHelper.getRandomDoubleInRange(config.getMinValue(),config.getMaxValue());

            // recordKv.put(keyArray[i], randomValue);

           // log.info("\tGenerated: "+keyArray[i]+" --> "+randomValue);

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

        return null;
    }
*/

    private Number generateValue(final RecordConfig recordConfig) throws RandomValueGenerationException {

        return RandomHelper.generateRandomValue(recordConfig);

    }

    private String generateKey(final RecordConfig recordConfig){

        if(recordConfig.getKeyValue()!=null){

            log.info("Using configured Key "+recordConfig.getKeyValue());

            return recordConfig.getKeyValue();
        }
        else{

            final String generatedString = RandomHelper.getRandomString(recordConfig.getMinKeyLength(),recordConfig.getMaxKeyLength());
            log.info("Generated random Key '"+generatedString+"' with length between: "+recordConfig.getMinKeyLength()+"-"+recordConfig.getMaxKeyLength());

            return generatedString;

        }

    }

    private KvPair generateKvPair(final RecordConfig recordConfig) throws RandomValueGenerationException {

        final KvPair pair = KvPair.builder()
                .key(generateKey(recordConfig))
                .value(generateValue(recordConfig))
                .build();

        return pair;

    }


    private WorkloadRecord generateRecord(final int index) throws RandomValueGenerationException {

        log.info("Generating Record "+index+" of "+config.getRecordCount());

        final LinkedList<KvPair> kvPairList = new LinkedList<>();

        for(RecordConfig recordConfig : config.getRecordConfigList()){

            kvPairList.add(generateKvPair(recordConfig));


        }


        final WorkloadRecord workloadRecord = WorkloadRecord.builder()
                .recordId(UUID.randomUUID())
                .kvPairs(kvPairList)
                .build();

        return workloadRecord;

    }

    private WorkloadData generateWorkload() throws RandomValueGenerationException {

        log.info("Generating Workload with "+config.getRecordCount()+ " Records");

        final LinkedList<WorkloadRecord> data = new LinkedList<>();

        for(int i = 1; i <=config.getRecordCount(); i++){

            data.add(generateRecord(i));

        }

        final WorkloadData workload = new WorkloadData();
        workload.setRecords(data);

        return workload;

    }

    public WorkloadData build() throws TSDBBenchmarkException {

        if(config==null){
            throw new DataGeneratorConfigurationException("Data generator config is NULL");
        }

        return generateWorkload();

    }


}
