package de.cleem.bm.tsdb.common.random.keys;

import de.cleem.bm.tsdb.common.random.values.strings.RandomStringHelper;
import de.cleem.bm.tsdb.model.config.datagenerator.RecordConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeyGenerationHelper {

    public static String generate(final RecordConfig recordConfig){

        if(recordConfig.getKeyValue()!=null){

            log.info("Using configured Key "+recordConfig.getKeyValue());

            return recordConfig.getKeyValue();
        }
        else{

            final String generatedString = RandomStringHelper.getRandomString(recordConfig.getMinKeyLength(),recordConfig.getMaxKeyLength());
            log.info("Generated random Key '"+generatedString+"' with length between: "+recordConfig.getMinKeyLength()+"-"+recordConfig.getMaxKeyLength());

            return generatedString;

        }

    }
}
