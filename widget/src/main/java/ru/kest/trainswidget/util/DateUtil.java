package ru.kest.trainswidget.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Konstantin on 26.05.2017.
 */

public class DateUtil {

    private static SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
    private static SimpleDateFormat timeWithSecondsFormatter = new SimpleDateFormat("HH:mm:ss");
    private static SimpleDateFormat dayFormatter = new SimpleDateFormat("yyyy-MM-dd");

    public static String getTime(Date date) {
        return timeFormatter.format(date);
    }

    public static String getTimeWithSeconds(Date date) {
        return timeWithSecondsFormatter.format(date);
    }

    public static String getDay(Date date) {
        return dayFormatter.format(date);
    }

    public static int getTimeDiffInMinutes(Date time) {
        long diffInMillies = System.currentTimeMillis() - time.getTime();
        return (int) TimeUnit.MINUTES.convert(Math.abs(diffInMillies), TimeUnit.MILLISECONDS);
    }
}
