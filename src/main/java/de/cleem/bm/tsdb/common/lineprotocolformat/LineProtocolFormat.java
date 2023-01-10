package de.cleem.bm.tsdb.common.lineprotocolformat;

import de.cleem.bm.tsdb.adapter.exception.TSDBAdapterException;
import lombok.Builder;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Builder
public class LineProtocolFormat {

    private String measurementName;
    private String labelKey;
    private String labelValue;

    public String getLine(HashMap<String, Number> record, Instant time) throws TSDBAdapterException {

        if (record == null) {
            throw new TSDBAdapterException("Can not write to Storage - record is NULL");
        }

        if (record.size() == 0) {
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
        for(Map.Entry<String, Number> entry : record.entrySet()){

            if(counter!=0){
                lineStringBuilder.append(",");
            }

            lineStringBuilder.append(entry.getKey());
            lineStringBuilder.append("=");
            lineStringBuilder.append(entry.getValue());
            counter++;

        }

        if(time!=null){
            lineStringBuilder.append(" ");
            lineStringBuilder.append(time.toEpochMilli());
        }

        return lineStringBuilder.toString();

    }


}
