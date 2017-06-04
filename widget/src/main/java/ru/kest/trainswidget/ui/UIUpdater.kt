package ru.kest.trainswidget.ui

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.widget.RemoteViews
import ru.kest.trainswidget.*
import ru.kest.trainswidget.data.DataService
import ru.kest.trainswidget.data.TimeLimits
import ru.kest.trainswidget.model.domain.TrainThread
import ru.kest.trainswidget.ui.WidgetUtil.getElementId
import ru.kest.trainswidget.util.DateUtil

/**
 * Update UI in separate thread
 *
 * Created by Konstantin on 26.05.2017.
 */
object UIUpdater {

    fun clearThread(context: Context, widgetView: RemoteViews, threadNum: Int) {
        val res = context.resources

        widgetView.setTextViewText(getElementId(res, "time", threadNum), "")
        widgetView.setTextViewText(getElementId(res, "from", threadNum), "")
        widgetView.setTextViewText(getElementId(res, "to", threadNum), "")
        widgetView.setTextViewText(getElementId(res, "remain", threadNum), "")
    }

    fun updateThread(context: Context, widgetView: RemoteViews, threadNum: Int, thread: TrainThread) {
        val res = context.resources
        val tl = TimeLimits(DataService(context).dataProvider)

        widgetView.setTextViewText(getElementId(res, "time", threadNum), DateUtil.getTime(thread.departure) + " - " + DateUtil.getTime(thread.arrival))
        widgetView.setTextViewText(getElementId(res, "from", threadNum), thread.from)
        widgetView.setTextViewText(getElementId(res, "to", threadNum), thread.to)

        val remainId = getElementId(res, "remain", threadNum)
        val remainMinutes = DateUtil.getTimeDiffInMinutes(thread.departure)

        widgetView.setTextViewText(remainId, getFormattedRemainText(remainMinutes, remainMinutes < tl.getTimeLimit(GREEN_STATUS)))
        widgetView.setTextColor(remainId, getRemainColor(remainMinutes, tl))


        widgetView.setOnClickPendingIntent(getElementId(res, "ll", threadNum), createPopUpDialogPendingIntent(context, thread))
    }

    private fun getRemainColor(remainMinutes: Int, tl: TimeLimits): Int
            = when {
        remainMinutes < tl.getTimeLimit(RED_STATUS) -> Color.RED
        remainMinutes < tl.getTimeLimit(YELLOW_STATUS) -> Color.YELLOW
        remainMinutes < tl.getTimeLimit(GREEN_STATUS) -> Color.GREEN
        else -> Color.LTGRAY
    }

    private fun getFormattedRemainText(remainMinutes: Int, isBold: Boolean): SpannableString {
        val s = SpannableString(getRemainText(remainMinutes))
        if (isBold) {
            s.setSpan(StyleSpan(Typeface.BOLD), 0, s.length, 0)
        }
        Log.d(LOG_TAG, "getFormattedRemainText($remainMinutes, $isBold): $s")
        return s
    }

    fun getRemainText(remainTime: Int): String {
        var time = remainTime
        val sb = StringBuilder()
        if (time > 59) {
            val remainHours = time / 60
            time -= remainHours * 60
            sb.append(remainHours).append(" ч ")
        }
        sb.append(time).append(" мин")
        return sb.toString()
    }

    private fun createPopUpDialogPendingIntent(context: Context, thread: TrainThread): PendingIntent {
        val popUpIntent = Intent(context, PopUpActivity::class.java)

        popUpIntent.action = CREATE_NOTIFICATION
        popUpIntent.putExtra(DETAILS, DateUtil.getTime(thread.departure) + " " + thread.title)
        popUpIntent.putExtra(RECORD_HASH, thread.hashCode())
        popUpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        return PendingIntent.getActivity(context, thread.hashCode(), popUpIntent, 0)
    }

}
