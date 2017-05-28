package ru.kest.trainswidget.util;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import static ru.kest.trainswidget.Constants.LOG_TAG;

/**
 * Created by Konstantin on 26.05.2017.
 */

public class JsonUtil {

    private static ObjectMapper mapper = null;

    public static String objectToString(Object source) {
        try {
            return getJsonMapper().writeValueAsString(source);
        } catch (JsonProcessingException e) {
            Log.e(LOG_TAG, "JsonUtil.objectToString() error saving object: " + source, e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T stringToObject(String content, Class<T> clazz) {
        try {
            return getJsonMapper().readValue(content, clazz);
        } catch (IOException e) {
            Log.e(LOG_TAG, "JsonUtil.stringToObject() error parsing json: " + content, e);
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> stringToList(String content, Class<T> clazz) {
        try {
            return getJsonMapper().readValue(content, getJsonMapper().getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            Log.e(LOG_TAG, "JsonUtil.stringToList() error parsing json: " + content, e);
            throw new RuntimeException(e);
        }
    }

    private static ObjectMapper getJsonMapper() {
        if (mapper == null) {
            ObjectMapper localMapper = new ObjectMapper();
            localMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            localMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
            mapper = localMapper;
        }
        return mapper;
    }

}
