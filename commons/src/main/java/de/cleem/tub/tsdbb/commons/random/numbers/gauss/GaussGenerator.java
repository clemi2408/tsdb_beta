package de.cleem.tub.tsdbb.commons.random.numbers.gauss;

import de.cleem.tub.tsdbb.commons.base.clazz.BaseClass;

import java.util.Random;

public class GaussGenerator extends BaseClass {

    final static Random RANDOM = new Random();


    public static Number getDouble(final Number gaussMiddle, final Number gaussRange) {

        final double gaussRangeDouble = gaussRange.doubleValue();
        final double gaussMiddleDouble = gaussMiddle.doubleValue();

        return ((gaussRangeDouble / 4) * RANDOM.nextGaussian()) + gaussMiddleDouble;

    }

    public static Number getInteger(final Number gaussMiddle, final Number gaussRange) {

        final int gaussRangeInt = gaussRange.intValue();
        final int gaussMiddleInt = gaussMiddle.intValue();

        return (int) ((gaussRangeInt / 4) * RANDOM.nextGaussian()) + gaussMiddleInt;

    }

}
