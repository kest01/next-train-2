package ru.kest.trainswidget.util

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.util.Log
import ru.kest.trainswidget.*
import ru.kest.trainswidget.data.DataService
import ru.kest.trainswidget.data.TimeLimits
import ru.kest.trainswidget.model.domain.TrainThread
import ru.kest.trainswidget.ui.UIUpdater

/**
 * Notification Utils
 *
 * Created by Konstantin on 26.05.2017.
 */
object NotificationUtil {

    fun createOrUpdateNotification(context: Context) {
        val thread = DataService(context).dataProvider.notificationTrain
        Log.d(LOG_TAG, "createOrUpdateNotification: $thread")
        if (thread == null) {
            return
        }

        val remainMinutes = DateUtil.getTimeDiffInMinutes(thread.departure)

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, createNotification(context, remainMinutes, thread))

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        SchedulerUtil.scheduleUpdateNotification(context, alarmManager)
    }

    private fun createNotification(context: Context, remainMinutes: Int, thread: TrainThread): Notification {
        val remainText = UIUpdater.getRemainText(remainMinutes)

        val builder = NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Электричка через " + remainText)
                .setTicker(remainText)
                .setContentText(DateUtil.getTime(thread.departure) + " " + thread.title)

        val soundAndVibro = checkAndGetSount(context, remainMinutes)
        if (soundAndVibro != null) {
            builder.setSound(soundAndVibro.sound)
            builder.setVibrate(soundAndVibro.vibroPattern)
        }
        builder.setDeleteIntent(getDeletePendingIntent(context))
        return builder.build()
    }

    private fun checkAndGetSount(context: Context, remainMinutes: Int): NotificationSoundAndVibro? {
        val tl = TimeLimits(DataService(context).dataProvider)

        if (tl.getTimeLimit(FIRST_CALL) == remainMinutes) {
            return NotificationSoundAndVibro(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    longArrayOf(0, 500, 200, 500)
            )
        } else if (tl.getTimeLimit(LAST_CALL) == remainMinutes) {
            return NotificationSoundAndVibro(
                    Uri.parse("android.resource://" + context.packageName + "/" + R.raw.train_horn),
                    longArrayOf(0, 500, 200, 500, 200, 500)
            )
        }
        return null
    }

    private fun getDeletePendingIntent(context: Context): PendingIntent {
        val deleteIntent = Intent(context, TrainsWidget::class.java)
        deleteIntent.action = DELETED_NOTIFICATION
        return PendingIntent.getBroadcast(context, 0, deleteIntent, 0)
    }

    @Suppress("ArrayInDataClass")
    private data class NotificationSoundAndVibro(var sound: Uri?, var vibroPattern: LongArray?)


}
