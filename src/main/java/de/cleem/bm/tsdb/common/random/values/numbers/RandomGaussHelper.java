package de.cleem.bm.tsdb.common.random.values.numbers;

import java.util.Random;

public class RandomGaussHelper {

    final static Random RANDOM = new Random();


    public static Number getDouble(final Number gaussMiddle, final Number gaussRange) {

        final double gaussRangeDouble = gaussRange.doubleValue();
        final double gaussMiddleDouble = gaussMiddle.doubleValue();

        return ((gaussRangeDouble/4) * RANDOM.nextGaussian()) + gaussMiddleDouble;

    }

    public static Number getInteger(final Number gaussMiddle, final Number gaussRange) {

        final int gaussRangeInt = gaussRange.intValue();
        final int gaussMiddleInt = gaussMiddle.intValue();

        return (int)((gaussRangeInt/4) * RANDOM.nextGaussian()) + gaussMiddleInt;

    }

}
