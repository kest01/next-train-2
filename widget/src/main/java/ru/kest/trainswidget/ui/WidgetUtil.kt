package ru.kest.trainswidget.ui

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.widget.RemoteViews
import ru.kest.trainswidget.ELEMENT_COUNT
import ru.kest.trainswidget.LOG_TAG
import ru.kest.trainswidget.PACKAGE_NAME
import ru.kest.trainswidget.R
import ru.kest.trainswidget.data.DataProvider
import ru.kest.trainswidget.data.DataService
import ru.kest.trainswidget.model.domain.NearestStation.HOME
import ru.kest.trainswidget.model.domain.TrainThread
import ru.kest.trainswidget.util.SchedulerUtil
import java.util.*

/**
 * Widget utils
 *
 * Created by Konstantin on 26.05.2017.
 */
object WidgetUtil {

    fun updateWidgets(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val alarmManager = SchedulerUtil.getAlarmManager(context)
        for (id in appWidgetIds) {
            updateWidget(context, appWidgetManager, id)
        }

        if (!DataService(context).dataProvider.isSetTrainThreads) {
            SchedulerUtil.sendTrainScheduleRequest(context, alarmManager)
        }
        SchedulerUtil.scheduleUpdateWidget(context, alarmManager)
        //        Toast.makeText(context, "updateWidget", Toast.LENGTH_SHORT).show();
    }

    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, widgetID: Int) {
        val widgetView = RemoteViews(PACKAGE_NAME, R.layout.widget)
        clearAllWidgetFields(context, widgetView)

        val dataProvider = DataService(context).dataProvider

        if (dataProvider.isSetTrainThreads && dataProvider.isSetLastLocation) {
            val trainThreads = WidgetUtil.getTrainsToDisplay(dataProvider)
            var i = 0
            while (i < ELEMENT_COUNT && i < trainThreads.size) {
                UIUpdater.updateThread(context, widgetView, i, trainThreads[i])
                i++
            }
            // Обновляем виджет
            appWidgetManager.updateAppWidget(widgetID, widgetView)
            Log.d(LOG_TAG, "updateWidget: successful")
        } else {
            Log.d(LOG_TAG, "updateWidget: nothing to show")
        }
    }

    private fun clearAllWidgetFields(context: Context, widgetView: RemoteViews) {
        for (i in 0..ELEMENT_COUNT - 1) {
            UIUpdater.clearThread(context, widgetView, i)
        }
    }


    fun getElementId(res: Resources, label: String, threadNum: Int): Int {
        return res.getIdentifier(label + threadNum, "id", PACKAGE_NAME)
    }

    private fun getTrainsToDisplay(dataProvider: DataProvider): List<TrainThread> {
        val trainThreads: List<TrainThread>
                = if (dataProvider.nearestStation == HOME) dataProvider.trainsFromHomeToWork
                else dataProvider.trainsFromWorkToHome

        val indexOfNextTrains = indexOfNextTrains(trainThreads)

        return if (indexOfNextTrains != null) trainThreads.subList(indexOfNextTrains, trainThreads.size)
            else emptyList()
    }

    private fun indexOfNextTrains(trainThreads: List<TrainThread>): Int? {
        val now = Date()
        val iterator = trainThreads.listIterator()
        while (iterator.hasNext()) {
            val thread = iterator.next()
            if (thread.departure > now) {
                return iterator.previousIndex()
            }
        }
        return null
    }

}
