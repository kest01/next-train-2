package ru.kest.trainswidget.ui

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.widget.RemoteViews
import ru.kest.trainswidget.ELEMENT_COUNT
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

/*    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the app widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; ++i) {
            // Set up the intent that starts the ListViewService, which will
            // provide the views for this collection.
            Intent intent = new Intent(context, ListViewWidgetService.class);
            // Add the app widget ID to the intent extras.
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            // Instantiate the RemoteViews object for the app widget layout.
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            // Set up the RemoteViews object to use a RemoteViews adapter.
            // This adapter connects
            // to a RemoteViewsService  through the specified intent.
            // This is how you populate the data.
            rv.setRemoteAdapter(appWidgetIds[i], R.id.list_view, intent);
            // Trigger listview item click
            Intent startActivityIntent = new Intent(context,MainActivity.class);
            PendingIntent startActivityPendingIntent = PendingIntent.getActivity(context, 0, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.list_view, startActivityPendingIntent);

            // The empty view is displayed when the collection has no items.
            // It should be in the same layout used to instantiate the RemoteViews  object above.
            rv.setEmptyView(R.id.list_view, R.id.empty_view);
            // Do additional processing specific to this app widget...
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }*/

    fun updateWidgets(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val alarmManager = SchedulerUtil.getAlarmManager(context)
        for (id in appWidgetIds) {
            updateWidget(context, appWidgetManager, id)
        }

        if (!DataService(context).dataProvider.isSetTrainThreads) {
            SchedulerUtil.sendTrainScheduleRequest(context, alarmManager)
        }
        SchedulerUtil.scheduleUpdateWidget(context, alarmManager)
    }

    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, widgetID: Int) {
        val remoteViews = RemoteViews(PACKAGE_NAME, R.layout.widget)

        //RemoteViews Service needed to provide adapter for ListView
        val svcIntent = Intent(context, ListViewWidgetService::class.java)
        //passing app widget id to that RemoteViews Service
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
        //setting a unique Uri to the intent
        //don't know its purpose to me right now
        svcIntent.data = Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME))
        //setting adapter to listview of the widget
        remoteViews.setRemoteAdapter(R.id.listViewWidget, svcIntent)
        //setting an empty view in case of no data
        remoteViews.setEmptyView(R.id.listViewWidget, R.id.empty_view)

        appWidgetManager.updateAppWidget(widgetID, remoteViews)
    }
/*
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
*/

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
