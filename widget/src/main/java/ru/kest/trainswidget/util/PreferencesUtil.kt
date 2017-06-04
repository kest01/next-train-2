package ru.kest.trainswidget.util

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.util.Log
import ru.kest.trainswidget.LOG_TAG
import ru.kest.trainswidget.data.FieldDataStorage
import ru.kest.trainswidget.model.domain.TrainThread

/**
 * Utils for working with shared properties
 *
 * Created by Konstantin on 26.05.2017.
 */
object PreferencesUtil {

    private val PREF_NAME = "NextTrainPref"

    private val TRAINS_FROM_HOME = "TRAINS_FROM_HOME"
    private val TRAINS_FROM_WORK = "TRAINS_FROM_WORK"
    private val NOTIFICATION_TRAIN = "NOTIFICATION_TRAIN"
    private val LOCATION_LATITUDE = "LOCATION_LATITUDE"
    private val LOCATION_LONGITUDE = "LOCATION_LONGITUDE"

    fun createDataStorage(context: Context): FieldDataStorage {
        var trainsFromHomeToWork: List<TrainThread> = emptyList()
        var trainsFromWorkToHome: List<TrainThread> = emptyList()
        var lastLocation: Location? = null
        var notificationTrain: TrainThread? = null

        val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        var json = preferences.getString(TRAINS_FROM_HOME, null)
        Log.d(LOG_TAG, "JSON: $json")
        if (json != null) {
            trainsFromHomeToWork = JsonUtil.stringToList(json, TrainThread::class.java)
        }

        json = preferences.getString(TRAINS_FROM_WORK, null)
        Log.d(LOG_TAG, "JSON: $json")
        if (json != null) {
            trainsFromWorkToHome = JsonUtil.stringToList(json, TrainThread::class.java)
        }

        json = preferences.getString(NOTIFICATION_TRAIN, null)
        Log.d(LOG_TAG, "JSON: $json")
        if (json != null) {
            notificationTrain = JsonUtil.stringToObject(json, TrainThread::class.java)
        }

        val locationLatitude = getDouble(preferences, LOCATION_LATITUDE, null)
        val locationLongitude = getDouble(preferences, LOCATION_LONGITUDE, null)
        Log.d(LOG_TAG, "location: {$locationLatitude,$locationLongitude}")
        if (locationLatitude != null && locationLongitude != null) {
            lastLocation = Location("")
            lastLocation.latitude = locationLatitude
            lastLocation.longitude = locationLongitude
        }

        return FieldDataStorage(trainsFromHomeToWork, trainsFromWorkToHome, lastLocation, notificationTrain)
    }

    fun saveLastLocation(context: Context, location: Location) {
        val editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
        putDouble(editor, LOCATION_LATITUDE, location.latitude)
        putDouble(editor, LOCATION_LONGITUDE, location.longitude)
        editor.apply()
    }

    fun saveTrainsFromHomeToWork(context: Context, trains: List<TrainThread>) {
        saveTrains(context, TRAINS_FROM_HOME, trains)
    }

    fun saveTrainsFromWorkToHome(context: Context, trains: List<TrainThread>) {
        saveTrains(context, TRAINS_FROM_WORK, trains)
    }


    fun saveNotificationTrain(context: Context, train: TrainThread) {
        val editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
        val json = JsonUtil.objectToString(train)
        Log.d(LOG_TAG, "saveTrains:" + json)
        editor.putString(NOTIFICATION_TRAIN, json)
        editor.apply()
    }

    private fun saveTrains(context: Context, key: String, trains: List<TrainThread>) {
        val editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
        val json = JsonUtil.objectToString(trains)
        Log.d(LOG_TAG, "saveTrains:($key) $json")
        editor.putString(key, json)
        editor.apply()
    }

    private fun putDouble(edit: SharedPreferences.Editor, key: String, value: Double): SharedPreferences.Editor {
        return edit.putLong(key, java.lang.Double.doubleToRawLongBits(value))
    }

    private fun getDouble(prefs: SharedPreferences, key: String, defaultValue: Double?): Double? {
        if (!prefs.contains(key)) {
            return defaultValue
        }
        return java.lang.Double.longBitsToDouble(prefs.getLong(key, 0))
    }

}
