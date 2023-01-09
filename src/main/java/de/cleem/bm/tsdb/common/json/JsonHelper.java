package de.cleem.bm.tsdb.common.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.cleem.bm.tsdb.common.date.DateHelper;
import de.cleem.bm.tsdb.common.exception.TsdbBenchmarkException;
import de.cleem.bm.tsdb.common.file.FileHelper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@Slf4j
public class JsonHelper {

     public static <T> String toString(final T instance, final boolean indent) throws JsonException {

        return new String(toByteArray(instance,indent));

    }

    public static <T> byte[] toByteArray(final T instance, final boolean indent) throws JsonException {

        if(instance==null){
            throw new JsonException("Instance to serialize is NULL");
        }

        try {

            final ObjectMapper objectMapper = new ObjectMapper();

            if(indent){
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            }

            return objectMapper.writeValueAsBytes(instance);

        } catch (JsonProcessingException e) {
            throw new JsonException(e);
        }

    }

    public static <T> T objectFromString(final String jsonString, Class<T> clazz) throws JsonException {

         return objectFromByteArray(jsonString.getBytes(), clazz);
    }

    public static <T> T objectFromByteArray(final byte[] jsonByteArray, Class<T> clazz) throws JsonException {

        if(clazz==null){
            throw new JsonException("Target class is NULL");
        }
        else if(jsonByteArray==null){
            throw new JsonException("Json Data is NULL");

        }
        else if(jsonByteArray.length==0){
            throw new JsonException("Json Data length is 0");

        }

        final ObjectMapper objectMapper = new ObjectMapper();
        try {

            return objectMapper.readValue(jsonByteArray,clazz);

        } catch (IOException e) {
            throw new JsonException(e);
        }

    }

    public static <T> void writeToTimestampFile(T instance, String filePrefix, String folderPath, String suffix) throws TsdbBenchmarkException {

        final byte[] jsonByteArray = toByteArray(instance,true);
        final String outputFileName = filePrefix+"-"+ DateHelper.getTimestamp()+suffix;
        final File outputFile = new File(folderPath+"/"+outputFileName);

        log.info("Writing File: "+outputFile.getAbsolutePath());

        FileHelper.write(outputFile, jsonByteArray);

    }
}
