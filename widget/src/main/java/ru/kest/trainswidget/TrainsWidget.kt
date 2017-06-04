package ru.kest.trainswidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import ru.kest.trainswidget.data.DataService
import ru.kest.trainswidget.service.LocationClient
import ru.kest.trainswidget.service.TrainScheduleRequestTask
import ru.kest.trainswidget.ui.WidgetUtil
import ru.kest.trainswidget.util.NotificationUtil
import ru.kest.trainswidget.util.SchedulerUtil
import java.util.*

/**
 * Основной класс виджета

 * Created by Konstantin on 26.05.2017.
 */
class TrainsWidget : AppWidgetProvider() {

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Log.d(LOG_TAG, "onEnabled")
        val alarmManager = SchedulerUtil.getAlarmManager(context)

        SchedulerUtil.sendUpdateLocation(context)
        SchedulerUtil.sendTrainScheduleRequest(context, alarmManager)
        SchedulerUtil.scheduleUpdateWidget(context, alarmManager)
        SchedulerUtil.scheduleUpdateLocation(context, alarmManager)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager,
                          appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Log.d(LOG_TAG, "onUpdate " + Arrays.toString(appWidgetIds))

        WidgetUtil.updateWidgets(context, appWidgetManager, appWidgetIds)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        Log.d(LOG_TAG, "onDeleted " + Arrays.toString(appWidgetIds))

        val alarmManager = SchedulerUtil.getAlarmManager(context)
        SchedulerUtil.cancelScheduleUpdateWidget(context, alarmManager)
        SchedulerUtil.cancelScheduleUpdateLocation(context, alarmManager)
        SchedulerUtil.cancelScheduleTrainScheduleRequest(context, alarmManager)
        SchedulerUtil.cancelScheduleUpdateNotification(context, alarmManager)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        Log.d(LOG_TAG, "onDisabled")
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val dataProvider = DataService(context).dataProvider

        Log.d(LOG_TAG, "onReceive: $intent - $this")
        when (intent.action) {
            UPDATE_ALL_WIDGETS -> {
                val thisAppWidget = ComponentName(context.packageName, javaClass.name)
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val ids = appWidgetManager.getAppWidgetIds(thisAppWidget)
                WidgetUtil.updateWidgets(context, appWidgetManager, ids)
            }
            UPDATE_LOCATION -> LocationClient(context).connect()
            TRAIN_SCHEDULE_REQUEST -> if (TrainScheduleRequestTask.executed.compareAndSet(false, true)) {
                TrainScheduleRequestTask(context).execute()
            }
            CREATE_NOTIFICATION -> if (dataProvider.isSetTrainThreads) {
                val threadHash = intent.getIntExtra(RECORD_HASH, 0)
                val thread = dataProvider.getThreadByHash(threadHash)
                if (thread != null) {
                    dataProvider.notificationTrain = thread
                    NotificationUtil.createOrUpdateNotification(context)
                }
            }
            UPDATE_NOTIFICATION -> NotificationUtil.createOrUpdateNotification(context)
            DELETED_NOTIFICATION -> {
                Toast.makeText(context, "Notification has been deleted", Toast.LENGTH_LONG).show()
                dataProvider.notificationTrain = null
                SchedulerUtil.cancelScheduleUpdateNotification(context, SchedulerUtil.getAlarmManager(context))
            }
        }
    }

}
