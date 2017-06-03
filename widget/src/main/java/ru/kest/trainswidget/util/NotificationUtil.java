package ru.kest.trainswidget.util;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import lombok.Getter;
import lombok.Setter;
import ru.kest.trainswidget.R;
import ru.kest.trainswidget.TrainsWidget;
import ru.kest.trainswidget.data.DataService;
import ru.kest.trainswidget.data.TimeLimits;
import ru.kest.trainswidget.model.domain.TrainThread;
import ru.kest.trainswidget.ui.UIUpdater;

import static ru.kest.trainswidget.Constants.*;

/**
 * Created by Konstantin on 26.05.2017.
 */

public class NotificationUtil {

    public static void createOrUpdateNotification(Context context) {
        TrainThread thread = new DataService(context).getDataProvider().getNotificationTrain();
        Log.d(LOG_TAG, "createOrUpdateNotification: " + thread);
        if (thread == null) {
            return;
        }

        int remainMinutes = DateUtil.getTimeDiffInMinutes(thread.getDeparture());

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, createNotification(context, remainMinutes, thread));

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        SchedulerUtil.scheduleUpdateNotification(context, alarmManager);
    }

    private static Notification createNotification(Context context, int remainMinutes, TrainThread thread) {
        String remainText = UIUpdater.INSTANCE.getRemainText(remainMinutes);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Электричка через " + remainText)
                        .setTicker(remainText)
                        .setContentText(DateUtil.getTime(thread.getDeparture()) + " " + thread.getTitle());

        NotificationSoundAndVibro soundAndVibro = checkAndGetSount(context, remainMinutes);
        if (soundAndVibro != null) {
            builder.setSound(soundAndVibro.getSound());
            builder.setVibrate(soundAndVibro.getVibroPattern());
        }
        builder.setDeleteIntent(getDeletePendingIntent(context));
        return builder.build();
    }

    private static NotificationSoundAndVibro checkAndGetSount(Context context, int remainMinutes) {

        NotificationSoundAndVibro result = new NotificationSoundAndVibro();
        TimeLimits tl = new TimeLimits(new DataService(context).getDataProvider());

        if (tl.getTimeLimit(FIRST_CALL) == remainMinutes) {
            result.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            result.setVibroPattern(new long[] {0, 500, 200, 500});
            return result;
        } else if (tl.getTimeLimit(LAST_CALL) == remainMinutes) {
            result.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.train_horn));
            result.setVibroPattern(new long[] {0, 500, 200, 500, 200, 500});
            return result;
        }
        return null;
    }

    private static PendingIntent getDeletePendingIntent(Context context) {
        Intent deleteIntent = new Intent(context, TrainsWidget.class);
        deleteIntent.setAction(DELETED_NOTIFICATION);
        return PendingIntent.getBroadcast(context, 0, deleteIntent, 0);
    }

    @Getter
    @Setter
    private static class NotificationSoundAndVibro {
        Uri sound;
        long[] vibroPattern;
    }


}
