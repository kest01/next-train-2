package ru.kest.trainswidget.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Date utils
 * Created by Konstantin on 26.05.2017.
 */
@SuppressLint("SimpleDateFormat")
object DateUtil {

    private val timeFormatter = SimpleDateFormat("HH:mm")
    private val dayFormatter = SimpleDateFormat("yyyy-MM-dd")

    fun getTime(date: Date): String {
        return timeFormatter.format(date)
    }

    fun getDay(date: Date): String {
        return dayFormatter.format(date)
    }

    fun getTimeDiffInMinutes(time: Date): Int {
        val diffInMillis = System.currentTimeMillis() - time.time
        return TimeUnit.MINUTES.convert(Math.abs(diffInMillis), TimeUnit.MILLISECONDS).toInt()
    }
}
