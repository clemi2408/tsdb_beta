package de.cleem.tub.tsdbb.commons.duration;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

public class DurationHelper {

    public static Duration parseDuration(final String durationString) throws DurationException {

        // https://stackoverflow.com/questions/32044846/regex-for-iso-8601-durations
        // Duration Regex for json Schema:
        // ^(\d{4}(-\d{2}(-\d{2})?(?!:))?(T\d{2}(:\d{2}(:\d{2})?(\.\d+)?)?)?(Z|([+,-]\d{2}(:\d{2})?))?)?P(([0-9]+([.,][0-9]*)?Y)?([0-9]+([.,][0-9]*)?M)?([0-9]+([.,][0-9]*)?D)?T?([0-9]+([.,][0-9]*)?H)?([0-9]+([.,][0-9]*)?M)?([0-9]+([.,][0-9]*)?S)?)|\d{4}-?(0[1-9]|11|12)-?(?:[0-2]\d|30|31)T((?:[0-1][0-9]|[2][0-3]):?(?:[0-5][0-9]):?(?:[0-5][0-9]|60)|2400|24:00)$

        if(durationString==null){

            throw new DurationException("Duration String to parse is NULL");

        }

        try{

            return Duration.parse(durationString);

        }
        catch (DateTimeParseException dateTimeParseException){

            throw new DurationException(dateTimeParseException);

        }

    }

    public static OffsetDateTime addDuration(final OffsetDateTime baseDateTime, final Duration duration) throws DurationException {

        if(baseDateTime==null){
            throw new DurationException("Can not add duration to baseDateTime - baseDateTime is NULL");
        }
        if(duration==null){
            throw new DurationException("Can not add duration to baseDateTime - duration is NULL");

        }

        return baseDateTime.toInstant().plus(duration).atOffset(baseDateTime.getOffset());

    }


}
