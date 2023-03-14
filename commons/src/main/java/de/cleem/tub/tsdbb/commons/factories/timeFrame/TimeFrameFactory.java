package de.cleem.tub.tsdbb.commons.factories.timeFrame;

import de.cleem.tub.tsdbb.api.model.TimeFrame;

import java.time.OffsetDateTime;

public class TimeFrameFactory {

    public static TimeFrame getTimeFrame() {

        return getTimeFrame(OffsetDateTime.now());

    }
    public static TimeFrame getTimeFrame(final OffsetDateTime startDate){

        final TimeFrame timeFrame = new TimeFrame();
        timeFrame.setStartTimestamp(startDate);

        return timeFrame;
    }
}
