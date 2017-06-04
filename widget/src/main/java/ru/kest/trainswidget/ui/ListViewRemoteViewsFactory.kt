package ru.kest.trainswidget.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import ru.kest.trainswidget.LOG_TAG
import ru.kest.trainswidget.PACKAGE_NAME
import ru.kest.trainswidget.R

/**
 *
 * Created by Konstantin on 04.06.2017.
 */
class ListViewRemoteViewsFactory(context : Context, intent : Intent?) : RemoteViewsService.RemoteViewsFactory {

    override fun getViewAt(position: Int): RemoteViews {
        Log.d(LOG_TAG, "ListViewRemoteViewsFactory.getViewAt($position)")
        val remoteView = RemoteViews(PACKAGE_NAME, R.layout.trains_line)
        for(i in 1..4) {
            remoteView.addView(R.id.line_ll, getNestedView(i))
        }
        return remoteView
    }

    private fun getNestedView(num : Int) : RemoteViews {
        val remoteView = RemoteViews(PACKAGE_NAME, R.layout.train_item)
        remoteView.setTextViewText(R.id.from, "from $num")
        remoteView.setTextViewText(R.id.to, "to $num")
        return remoteView

    }

    override fun getCount(): Int {
        return 2
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun onDestroy() {
        Log.d(LOG_TAG, "ListViewRemoteViewsFactory.onDestroy()")
    }

    override fun onCreate() {
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onDataSetChanged() {
        Log.d(LOG_TAG, "ListViewRemoteViewsFactory.onDataSetChanged()")
    }

    override fun hasStableIds(): Boolean {
        return true
    }


}