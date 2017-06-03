package ru.kest.trainswidget.util

import android.util.Log
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import ru.kest.trainswidget.Constants.LOG_TAG
import java.io.IOException
import java.text.SimpleDateFormat

/**
 * Json Util
 *
 * Created by Konstantin on 26.05.2017.
 */
object JsonUtil {

    private val jsonMapper: ObjectMapper by lazy {
        ObjectMapper().apply {
            dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
        }
    }

    fun objectToString(source: Any): String {
        try {
            return jsonMapper.writeValueAsString(source)
        } catch (e: JsonProcessingException) {
            Log.e(LOG_TAG, "JsonUtil.objectToString() error saving object: " + source, e)
            throw RuntimeException(e)
        }

    }

    fun <T> stringToObject(content: String, clazz: Class<T>): T {
        try {
            return jsonMapper.readValue(content, clazz)
        } catch (e: IOException) {
            Log.e(LOG_TAG, "JsonUtil.stringToObject() error parsing json: " + content, e)
            throw RuntimeException(e)
        }

    }

    fun <T> stringToList(content: String, clazz: Class<T>): List<T> {
        try {
            return jsonMapper.readValue<List<T>>(content, jsonMapper.typeFactory.constructCollectionType(List::class.java, clazz))
        } catch (e: IOException) {
            Log.e(LOG_TAG, "JsonUtil.stringToList() error parsing json: " + content, e)
            throw RuntimeException(e)
        }

    }

}
