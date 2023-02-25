package de.cleem.tub.tsdbb.commons.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.cleem.tub.tsdbb.commons.date.DateException;
import de.cleem.tub.tsdbb.commons.date.DateHelper;
import de.cleem.tub.tsdbb.commons.file.FileException;
import de.cleem.tub.tsdbb.commons.file.FileHelper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@Slf4j
public class JsonHelper {

    public static <T> String toString(final T instance, final boolean indent) throws JsonException {

        return new String(toByteArray(instance, indent));

    }

    public static <T> byte[] toByteArray(final T instance, final boolean indent) throws JsonException {

        if (instance == null) {
            throw new JsonException("Instance to serialize is NULL");
        }

        try {

            final ObjectMapper objectMapper = new ObjectMapper();

            if (indent) {
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            }

            return objectMapper.writeValueAsBytes(instance);

        } catch (JsonProcessingException e) {
            throw new JsonException(e);
        }

    }

    public static <T> T objectFromString(final String jsonString, final Class<T> clazz) throws JsonException {

        return objectFromByteArray(jsonString.getBytes(), clazz);
    }

    public static <T> T objectFromByteArray(final byte[] jsonByteArray, final Class<T> clazz) throws JsonException {

        if (clazz == null) {
            throw new JsonException("Target class is NULL");
        } else if (jsonByteArray == null) {
            throw new JsonException("Json Data is NULL");

        } else if (jsonByteArray.length == 0) {
            throw new JsonException("Json Data length is 0");

        }

        final ObjectMapper objectMapper = new ObjectMapper();
        try {

            return objectMapper.readValue(jsonByteArray, clazz);

        } catch (IOException e) {
            throw new JsonException(e);
        }

    }

    public static <T> void writeToTimestampFile(final T instance, final String filePrefix, final String folderPath, final String suffix) throws JsonException, DateException, FileException {

        final byte[] jsonByteArray = toByteArray(instance, true);
        final String outputFileName = filePrefix + "-" + DateHelper.getCurrentFormattedDateString() + suffix;
        final File outputFile = new File(folderPath + "/" + outputFileName);

        log.info("Writing File: " + outputFile.getAbsolutePath());

        FileHelper.write(outputFile, jsonByteArray);

    }
}
