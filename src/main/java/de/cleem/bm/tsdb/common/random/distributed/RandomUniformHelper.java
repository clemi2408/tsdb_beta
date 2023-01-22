package de.cleem.bm.tsdb.common.random.distributed;

import java.util.Random;

public class RandomUniformHelper {

    final static Random RANDOM = new Random();
    public static Integer getInteger(final Number min, final Number max) {

        final Integer maxUsed = max.intValue()+1;
        return RANDOM.nextInt(maxUsed - min.intValue()) + min.intValue();

    }

    public static Double getDouble(final Number min, final Number max) {

        final Double maxUsed = max.doubleValue()+1d;
        return RANDOM.nextDouble(maxUsed - min.doubleValue()) + min.doubleValue();

    }

}
