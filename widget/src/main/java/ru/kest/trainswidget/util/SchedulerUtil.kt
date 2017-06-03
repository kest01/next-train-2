package ru.kest.trainswidget.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import ru.kest.trainswidget.Constants.*
import ru.kest.trainswidget.TrainsWidget
import java.util.*

/**
 * Utils for schedule operations
 *
 * Created by Konstantin on 26.05.2017.
 */
object SchedulerUtil {

    fun scheduleUpdateWidget(context: Context, alarmManager: AlarmManager) {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.MINUTE, 1)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        alarmManager.set(
                AlarmManager.RTC,
                calendar.timeInMillis,
                createBroadcastPI(
                        context, createIntent(context, UPDATE_ALL_WIDGETS)
                )
        )
    }

    fun scheduleUpdateNotification(context: Context, alarmManager: AlarmManager) {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.MINUTE, 1)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                createBroadcastPI(
                        context, createIntent(context, UPDATE_NOTIFICATION)
                )
        )
    }

    fun scheduleUpdateLocation(context: Context, alarmManager: AlarmManager) {
        alarmManager.setRepeating(
                AlarmManager.RTC,
                System.currentTimeMillis(),
                (60 * 60 * 1000).toLong(), // 1 hour
                createBroadcastPI(
                        context, createIntent(context, UPDATE_LOCATION)
                )
        )
    }

    fun scheduleTrainScheduleRequest(context: Context, alarmManager: AlarmManager, munitesToNextExecute: Int) {
        alarmManager.set(
                AlarmManager.RTC,
                System.currentTimeMillis() + munitesToNextExecute * 60 * 1000,
                createBroadcastPI(
                        context, createIntent(context, TRAIN_SCHEDULE_REQUEST)
                )
        )
    }

    fun sendUpdateWidget(context: Context) {
        val intent = createIntent(context, UPDATE_ALL_WIDGETS)
        context.sendBroadcast(intent)
    }

    fun sendUpdateLocation(context: Context) {
        val intent = createIntent(context, UPDATE_LOCATION)
        context.sendBroadcast(intent)
    }

    fun sendTrainScheduleRequest(context: Context, alarmManager: AlarmManager) {
        scheduleTrainScheduleRequest(context, alarmManager, 0)
    }

    fun cancelScheduleUpdateWidget(context: Context, alarmManager: AlarmManager) {
        alarmManager.cancel(
                createBroadcastPI(
                        context, createIntent(context, UPDATE_ALL_WIDGETS)
                )
        )
    }

    fun cancelScheduleUpdateLocation(context: Context, alarmManager: AlarmManager) {
        alarmManager.cancel(
                createBroadcastPI(
                        context, createIntent(context, UPDATE_LOCATION)
                )
        )
    }

    fun cancelScheduleTrainScheduleRequest(context: Context, alarmManager: AlarmManager) {
        alarmManager.cancel(
                createBroadcastPI(
                        context, createIntent(context, TRAIN_SCHEDULE_REQUEST)
                )
        )
    }

    fun cancelScheduleUpdateNotification(context: Context, alarmManager: AlarmManager) {
        alarmManager.cancel(
                createBroadcastPI(
                        context, createIntent(context, UPDATE_NOTIFICATION)
                )
        )
    }

    private fun createIntent(context: Context, action: String): Intent {
        return Intent(context, TrainsWidget::class.java).setAction(action)
    }

    private fun createBroadcastPI(context: Context, intent: Intent): PendingIntent {
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }


    fun getAlarmManager(context: Context): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

}
