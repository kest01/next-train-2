package ru.kest.trainswidget.ui;

import android.app.AlarmManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import ru.kest.trainswidget.R;
import ru.kest.trainswidget.data.DataProvider;
import ru.kest.trainswidget.data.DataService;
import ru.kest.trainswidget.model.domain.NearestStation;
import ru.kest.trainswidget.model.domain.TrainThread;
import ru.kest.trainswidget.util.SchedulerUtil;

import static ru.kest.trainswidget.Constants.ELEMENT_COUNT;
import static ru.kest.trainswidget.Constants.LOG_TAG;
import static ru.kest.trainswidget.Constants.PACKAGE_NAME;

/**
 * Created by Konstantin on 26.05.2017.
 */
public class WidgetUtil {

    public static void updateWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        AlarmManager alarmManager = SchedulerUtil.getAlarmManager(context);
        for (int id : appWidgetIds) {
            updateWidget(context, appWidgetManager, id);
        }

        if (!new DataService(context).getDataProvider().isSetTrainThreads()) {
            SchedulerUtil.sendTrainScheduleRequest(context, alarmManager);
        }
        SchedulerUtil.scheduleUpdateWidget(context, alarmManager);
//        Toast.makeText(context, "updateWidget", Toast.LENGTH_SHORT).show();
    }

    private static void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetID) {
        RemoteViews widgetView = new RemoteViews(PACKAGE_NAME, R.layout.widget);
        clearAllWidgetFields(context, widgetView);

        DataProvider dataProvider = new DataService(context).getDataProvider();

        if (dataProvider.isSetTrainThreads() && dataProvider.isSetLastLocation()) {
            List<TrainThread> trainThreads = WidgetUtil.getTrainsToDisplay(dataProvider);
            for (int i = 0; i < ELEMENT_COUNT && i < trainThreads.size(); i++) {
                UIUpdater.updateThread(context, widgetView, i, trainThreads.get(i));
            }
            // Обновляем виджет
            appWidgetManager.updateAppWidget(widgetID, widgetView);
            Log.d(LOG_TAG, "updateWidget: successful");
        } else {
            Log.d(LOG_TAG, "updateWidget: nothing to show");
        }
    }

    private static void clearAllWidgetFields(Context context, RemoteViews widgetView) {
        for (int i = 0; i < ELEMENT_COUNT; i++) {
            UIUpdater.clearThread(context, widgetView, i);
        }
    }


    public static int getElementId(Resources res, String label, int threadNum) {
        return res.getIdentifier(label + threadNum, "id", PACKAGE_NAME);
    }

    private static List<TrainThread> getTrainsToDisplay(DataProvider dataProvider) {
        List<TrainThread> trainThreads;
        if (dataProvider.getNearestStation() == NearestStation.HOME) {
            trainThreads = dataProvider.getTrainsFromHomeToWork();
        } else {
            trainThreads = dataProvider.getTrainsFromWorkToHome();
        }
        Integer indexOfNextTrains = indexOfNextTrains(trainThreads);
        if (indexOfNextTrains != null) {
            return trainThreads.subList(indexOfNextTrains, trainThreads.size());
        } else {
            return Collections.emptyList();
        }
    }

    @Nullable
    private static Integer indexOfNextTrains(List<TrainThread> trainThreads) {
        Date now = new Date();
        ListIterator<TrainThread> iterator = trainThreads.listIterator();
        while (iterator.hasNext()) {
            TrainThread thread = iterator.next();
            if (thread.getDeparture().compareTo(now) > 0) {
                return iterator.previousIndex();
            }
        }
        return null;
    }

}
