package de.cleem.tub.tsdbb.commons.random.numbers.triangle;

import de.cleem.tub.tsdbb.commons.base.clazz.BaseClass;

import java.util.Random;

public class TriangleGenerator extends BaseClass {

    // https://stackoverflow.com/questions/33220176/triangular-distribution-in-java/33220276#33220276

    final static Random RANDOM = new Random();

    public static Integer getInteger(final Number min, final Number max, final Number spike) {

        int minInt = min.intValue();
        int maxInt = max.intValue();
        int spikeInt = spike.intValue();

        double F = (maxInt - minInt) / (spikeInt - minInt);
        double rand = RANDOM.nextDouble();
        if (rand < F) {
            return (int) (minInt + Math.sqrt(rand * (spikeInt - minInt) * (maxInt - minInt)));
        } else {
            return (int) (spikeInt - Math.sqrt((1 - rand) * (spikeInt - minInt) * (spikeInt - maxInt)));
        }

    }

    public static Double getDouble(final Number min, final Number max, final Number spike) {

        double minInt = min.doubleValue();
        double maxInt = max.doubleValue();
        double spikeInt = spike.doubleValue();

        double F = (maxInt - minInt) / (spikeInt - minInt);
        double rand = RANDOM.nextDouble();
        if (rand < F) {
            return (minInt + Math.sqrt(rand * (spikeInt - minInt) * (maxInt - minInt)));
        } else {
            return (spikeInt - Math.sqrt((1 - rand) * (spikeInt - minInt) * (spikeInt - maxInt)));
        }


    }


}
