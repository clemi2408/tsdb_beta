package de.cleem.tub.tsdbb.commons.random.numbers.uniform;

import de.cleem.tub.tsdbb.commons.base.clazz.BaseClass;

import java.util.Random;

public class UniformGenerator extends BaseClass {

    final static Random RANDOM = new Random();

    public static Integer getInteger(final int min, final int max) {

        final int maxUsed = max + 1;
        return RANDOM.nextInt(maxUsed - min) + min;

    }

    public static Double getDouble(final Number min, final Number max) {

        final double maxUsed = max.doubleValue() + 1d;
        return RANDOM.nextDouble(maxUsed - min.doubleValue()) + min.doubleValue();

    }

}
