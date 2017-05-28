package ru.kest.trainswidget.ui;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.RemoteViews;

import ru.kest.trainswidget.PopUpActivity;
import ru.kest.trainswidget.data.DataService;
import ru.kest.trainswidget.data.TimeLimits;
import ru.kest.trainswidget.model.domain.TrainThread;
import ru.kest.trainswidget.util.DateUtil;

import static ru.kest.trainswidget.Constants.CREATE_NOTIFICATION;
import static ru.kest.trainswidget.Constants.DETAILS;
import static ru.kest.trainswidget.Constants.GREEN_STATUS;
import static ru.kest.trainswidget.Constants.LOG_TAG;
import static ru.kest.trainswidget.Constants.RECORD_HASH;
import static ru.kest.trainswidget.Constants.RED_STATUS;
import static ru.kest.trainswidget.Constants.YELLOW_STATUS;
import static ru.kest.trainswidget.ui.WidgetUtil.getElementId;

/**
 * Created by Konstantin on 26.05.2017.
 */

public class UIUpdater {

    public static void clearThread(Context context, RemoteViews widgetView, int threadNum) {
        Resources res = context.getResources();

        widgetView.setTextViewText(getElementId(res, "time", threadNum), "");
        widgetView.setTextViewText(getElementId(res, "from", threadNum), "");
        widgetView.setTextViewText(getElementId(res, "to",   threadNum), "");
        widgetView.setTextViewText(getElementId(res, "remain",threadNum), "");
    }

    public static void updateThread(Context context, RemoteViews widgetView, int threadNum, TrainThread thread) {
        Resources res = context.getResources();
        TimeLimits tl = new TimeLimits(DataService.getDataProvider(context));

        widgetView.setTextViewText(getElementId(res, "time", threadNum), DateUtil.getTime(thread.getDeparture()) + " - " + DateUtil.getTime(thread.getArrival()));
        widgetView.setTextViewText(getElementId(res, "from", threadNum), thread.getFrom());
        widgetView.setTextViewText(getElementId(res, "to",   threadNum), thread.getTo());

        int remainId = getElementId(res, "remain", threadNum);
        int remainMinutes = TimeLimits.getTimeDiffInMinutes(thread.getDeparture());

        widgetView.setTextViewText(remainId, getFormattedRemainText(remainMinutes, remainMinutes < tl.getTimeLimit(GREEN_STATUS)));
        widgetView.setTextColor(remainId, getRemainColor(remainMinutes, tl));


        widgetView.setOnClickPendingIntent(getElementId(res, "ll", threadNum), createPopUpDialogPendingIntent(context, thread));
    }

    private static int getRemainColor(int remainMinutes, TimeLimits tl) {
        if (remainMinutes < tl.getTimeLimit(RED_STATUS)) {
            return Color.RED;
        } else if (remainMinutes < tl.getTimeLimit(YELLOW_STATUS)) {
            return Color.YELLOW;
        } else if (remainMinutes < tl.getTimeLimit(GREEN_STATUS)) {
            return Color.GREEN;
        } else {
//            return android.R.color.primary_text_dark;
            return Color.LTGRAY;
        }
    }

    private static SpannableString getFormattedRemainText(int remainMinutes, boolean isBold) {
        SpannableString s = new SpannableString(getRemainText(remainMinutes));
        if (isBold) {
            s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), 0);
        }
        Log.d(LOG_TAG, "getFormattedRemainText(" + remainMinutes + ", " + isBold + "): " + s);
        return s;
    }

    @NonNull
    public static String getRemainText(int remainTime) {
        StringBuilder sb = new StringBuilder();
        if (remainTime > 59) {
            int remainHours = remainTime / 60;
            remainTime = remainTime - remainHours * 60;
            sb.append(remainHours).append(" ч ");
        }
        sb.append(remainTime).append(" мин");
        return sb.toString();
    }

    private static PendingIntent createPopUpDialogPendingIntent(Context context, TrainThread thread) {
        Intent popUpIntent = new Intent(context, PopUpActivity.class);

        popUpIntent.setAction(CREATE_NOTIFICATION);
        popUpIntent.putExtra(DETAILS, DateUtil.getTime(thread.getDeparture()) + " " + thread.getTitle());
        popUpIntent.putExtra(RECORD_HASH, thread.hashCode());
        popUpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return PendingIntent.getActivity(context, thread.hashCode(), popUpIntent, 0);
    }

}
