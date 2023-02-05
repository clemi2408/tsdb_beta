package de.cleem.bm.tsdb.common.lineprotocolformat;

import de.cleem.bm.tsdb.adapter.exception.TSDBAdapterException;
import de.cleem.bm.tsdb.model.config.datagenerator.ValueTypes;
import de.cleem.bm.tsdb.model.config.workload.KvPair;
import de.cleem.bm.tsdb.model.config.workload.WorkloadRecord;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Builder
@Slf4j
public class LineProtocolFormat {

    private String measurementName;
    private String labelKey;
    private String labelValue;

    public String getLine(final WorkloadRecord record, final Instant time) throws TSDBAdapterException {

        if (record == null) {
            throw new TSDBAdapterException("Can not write to Storage - record is NULL");
        }

        if (record.getKvPairs().size() == 0) {
            throw new TSDBAdapterException("Can not write to Storage - record is Empty");
        }

        // <measurement>[,<tag_key>=<tag_value>[,<tag_key>=<tag_value>]] <field_key>=<field_value>[,<field_key>=<field_value>] [<timestamp>]

        final StringBuffer lineStringBuilder = new StringBuffer();
        lineStringBuilder.append(measurementName);
        lineStringBuilder.append(",");
        lineStringBuilder.append(labelKey);
        lineStringBuilder.append("=");
        lineStringBuilder.append(labelValue);
        lineStringBuilder.append(" ");


        int counter = 0;
        for(KvPair kvPair : record.getKvPairs()){

            if(counter!=0){
                lineStringBuilder.append(",");
            }

            lineStringBuilder.append(kvPair.getKey());
            lineStringBuilder.append("=");
            lineStringBuilder.append(extractValue(kvPair));
            counter++;

        }

        if(time!=null){
            lineStringBuilder.append(" ");
            lineStringBuilder.append(time.toEpochMilli());
        }

        final String line = lineStringBuilder.toString();

        log.info("Created line: "+line);
        return line;
    }

    private String extractValue(final KvPair kvPair) throws TSDBAdapterException {

        final Object[] values = kvPair.getValue();

        final Class elementClass = values[0].getClass();
        final ValueTypes valueType = ValueTypes.get(elementClass);

        if(valueType==ValueTypes.STRING){

            final StringBuffer buffer = new StringBuffer();

            buffer.append("\"");
            Object value;
            for(int i=0;i<values.length;i++){

                if(i!=0){
                    buffer.append(",");
                }
                value=values[i];

                buffer.append((String)value);


            }
            buffer.append("\"");

            return buffer.toString();

        }

        else if(valueType==ValueTypes.INTEGER || valueType==ValueTypes.DOUBLE || valueType==ValueTypes.NUMBER){

            if(values.length>1){

                throw new TSDBAdapterException("Multiple values not supported for type "+valueType);

            }

            return values[0].toString();

        }
        else{
                throw new TSDBAdapterException("Value Type not supported: "+valueType);
        }

    }

}
