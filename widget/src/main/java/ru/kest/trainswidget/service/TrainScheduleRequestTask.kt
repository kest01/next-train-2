package ru.kest.trainswidget.service

import android.app.AlarmManager
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import ru.kest.trainswidget.LOG_TAG
import ru.kest.trainswidget.converters.YandexToDomainConverter
import ru.kest.trainswidget.data.DataService
import ru.kest.trainswidget.model.domain.TrainThread
import ru.kest.trainswidget.model.yandex.ScheduleResponse
import ru.kest.trainswidget.util.DateUtil
import ru.kest.trainswidget.util.JsonUtil
import ru.kest.trainswidget.util.SchedulerUtil
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Async task for retrieve train schedules from yandex.api
 *
 * Created by Konstantin on 26.05.2017.
 */

private val URL_TEMPLATE = "https://api.rasp.yandex.net/v1.0/search/?apikey=4616c13e-bcc2-49e3-b88a-5a1437ea7a40&format=json&from=%s&to=%s&lang=ru&date=%s"
private val HOME_STATION_CODE = "s9601770"
private val WORK_STATION_CODE = "s9601251"

private val SUCCESS_RESPONSE = "OK"

class TrainScheduleRequestTask(private val context: Context) : AsyncTask<Void, Void, String>() {

    override fun doInBackground(vararg params: Void): String {
        Log.d(LOG_TAG, "TrainScheduleRequestTask.doInBackground()")
        val dataProvider = DataService(context).dataProvider
        try {
            val fromHomeTrains = loadTrainSchedule(true)
            if (!fromHomeTrains.isEmpty()) {
                dataProvider.trainsFromHomeToWork = fromHomeTrains
                Log.d(LOG_TAG, "fromHomeTrains: " + fromHomeTrains)
            }
            val fromWorkTrains = loadTrainSchedule(false)
            if (!fromWorkTrains.isEmpty()) {
                dataProvider.trainsFromWorkToHome = fromWorkTrains
                Log.d(LOG_TAG, "fromWorkTrains: " + fromWorkTrains)
            }
            return SUCCESS_RESPONSE
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message, e)
            return e.message ?: e.javaClass.simpleName
        }

    }

    override fun onPostExecute(response: String) {
        Log.i(LOG_TAG, "onPostExecute()")
        val timeToNextExecute: Int
        if (SUCCESS_RESPONSE == response) {
            //            Log.i(LOG_TAG, "Train schedules successfully updated: " + FieldDataStorage.getTrainsFromHomeToWork().size() + "  " + FieldDataStorage.getTrainsFromWorkToHome().size());
            Log.i(LOG_TAG, "Train schedules successfully updated")
            SchedulerUtil.sendUpdateWidget(context)
            timeToNextExecute = 2 * 60 // 2 hours
        } else {
            timeToNextExecute = 5 // 5 min
            Log.w(LOG_TAG, "unsuccess result code: $response. reschedule retrieve data in 5 minute")
        }
        SchedulerUtil.scheduleTrainScheduleRequest(context, context.getSystemService(Context.ALARM_SERVICE) as AlarmManager, timeToNextExecute)
        executed.set(false)
    }

    @Throws(IOException::class)
    private fun loadTrainSchedule(fromHome: Boolean): List<TrainThread> {
        val content = getUrlContent(createURL(fromHome))
        //        Log.d(LOG_TAG, "service response: " + content);

        val response = JsonUtil.stringToObject(content, ScheduleResponse::class.java)

        return YandexToDomainConverter.scheduleResponseToDomain(response)
    }

    @Throws(IOException::class)
    private fun getUrlContent(url: URL): String {
        var urlConnection: HttpURLConnection? = null
        val result = StringBuilder()
        Log.i(LOG_TAG, "Getting content for url " + url)
        try {
            urlConnection = url.openConnection() as HttpURLConnection
            val reader = BufferedReader(InputStreamReader(BufferedInputStream(urlConnection.inputStream)))

            var line = reader.readLine()
            while (line != null) {
                result.append(line)
                line = reader.readLine()
            }
        } finally {
            urlConnection!!.disconnect()
        }

        return result.toString()
    }

    @Throws(MalformedURLException::class)
    private fun createURL(fromHome: Boolean): URL {
        val url = String.format(
                URL_TEMPLATE,
                if (fromHome) HOME_STATION_CODE else WORK_STATION_CODE,
                if (fromHome) WORK_STATION_CODE else HOME_STATION_CODE,
                DateUtil.getDay(Date())
        )
        return URL(url)
    }

    companion object {

        val executed = AtomicBoolean(false)

    }
}
