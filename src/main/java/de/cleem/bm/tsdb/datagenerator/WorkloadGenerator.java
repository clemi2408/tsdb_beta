package de.cleem.bm.tsdb.datagenerator;

import de.cleem.bm.tsdb.common.exception.TSDBBenchmarkException;
import de.cleem.bm.tsdb.common.random.ValueGenerationHelper;
import de.cleem.bm.tsdb.common.random.RandomValueGenerationException;
import de.cleem.bm.tsdb.common.random.keys.KeyGenerationHelper;
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
    private KvPair generateKvPair(final RecordConfig recordConfig) throws RandomValueGenerationException {

        final KvPair pair = KvPair.builder()
                .key(KeyGenerationHelper.generate(recordConfig))
                .value(ValueGenerationHelper.generate(recordConfig))
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

        final WorkloadData workload = WorkloadData.builder().build();
        workload.setRecords(data);

        return workload;

    }
    public WorkloadData generate() throws TSDBBenchmarkException {

        if(config==null){
            throw new DataGeneratorConfigurationException("Data generator config is NULL");
        }

        return generateWorkload();

    }

}
