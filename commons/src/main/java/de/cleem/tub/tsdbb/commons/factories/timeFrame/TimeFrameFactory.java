package de.cleem.tub.tsdbb.commons.factories.timeFrame;

import de.cleem.tub.tsdbb.api.model.TimeFrame;
import de.cleem.tub.tsdbb.commons.base.clazz.BaseClass;

import java.time.OffsetDateTime;

public class TimeFrameFactory extends BaseClass {

    public static TimeFrame getTimeFrame() {

        return getTimeFrame(OffsetDateTime.now());

    }
    public static TimeFrame getTimeFrame(final OffsetDateTime startDate){

        final TimeFrame timeFrame = new TimeFrame();
        timeFrame.setStartTimestamp(startDate);

        return timeFrame;
    }
}
