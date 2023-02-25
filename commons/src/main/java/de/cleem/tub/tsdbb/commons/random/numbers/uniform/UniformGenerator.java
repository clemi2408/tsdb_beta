package de.cleem.tub.tsdbb.commons.random.numbers.uniform;

import java.util.Random;

public class UniformGenerator {

    final static Random RANDOM = new Random();

    public static Integer getInteger(final int min, final int max) {

        final int maxUsed = max + 1;
        return RANDOM.nextInt(maxUsed - min) + min;

    }

    public static Double getDouble(final Number min, final Number max) {

        final Double maxUsed = max.doubleValue() + 1d;
        return RANDOM.nextDouble(maxUsed - min.doubleValue()) + min.doubleValue();

    }

}
