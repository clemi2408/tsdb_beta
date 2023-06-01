package de.cleem.tub.tsdbb.apps.worker.formats;

import de.cleem.tub.tsdbb.api.model.KvPair;
import de.cleem.tub.tsdbb.api.model.Record;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterException;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;

@Builder
@Slf4j
public class LineProtocolFormat {

    private static final String STRING_OUTPUT_PATTERN = "\"%s\"";
    private String measurementName;
    private String labelKey;
    private String labelValue;

    public String getLine(final Record record) throws TSDBAdapterException {

        if (record == null) {
            throw new TSDBAdapterException("Can not write to Storage - record is NULL");
        }

        if (record.getKvPairs().size() == 0) {
            throw new TSDBAdapterException("Can not write to Storage - record is Empty");
        }

        // <measurement>[,<tag_key>=<tag_value>[,<tag_key>=<tag_value>]] <field_key>=<field_value>[,<field_key>=<field_value>] [<timestamp>]

        final StringBuilder lineStringBuilder = new StringBuilder();
        lineStringBuilder.append(measurementName);
        lineStringBuilder.append(",");
        lineStringBuilder.append(labelKey);
        lineStringBuilder.append("=");
        lineStringBuilder.append(labelValue);
        lineStringBuilder.append(" ");


        int counter = 0;
        for (KvPair kvPair : record.getKvPairs()) {

            if (counter != 0) {
                lineStringBuilder.append(",");
            }

            lineStringBuilder.append(kvPair.getKey());
            lineStringBuilder.append("=");
            lineStringBuilder.append(extractValue(kvPair));
            counter++;

        }

        lineStringBuilder.append(" ");

        Instant instant;
        if (record.getTimestamp() != null) {
            instant = record.getTimestamp().toInstant();
        }
        else{
            instant = Instant.now();
        }

        lineStringBuilder.append(instant.toEpochMilli());

        final String line = lineStringBuilder.toString();

        log.debug("Created line: " + line);
        return line;
    }

    private String extractValue(final KvPair kvPair) throws TSDBAdapterException {

        final List<Object> values = kvPair.getValue();
        final Object value = values.get(0);

        if (value instanceof String stringValue) {

            return String.format(STRING_OUTPUT_PATTERN, stringValue);

        } else if (value instanceof Integer integerValue) {

            return integerValue.toString();

        } else if (value instanceof Double doubleValue) {

            return doubleValue.toString();

        } else if (value instanceof Number numberValue) {

            return numberValue.toString();

        } else {

            throw new TSDBAdapterException("Value Type not supported: " + value.getClass());

        }

    }

}
