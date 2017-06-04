package ru.kest.trainswidget.ui

import android.content.Intent
import android.widget.RemoteViewsService

/**
 * List view service
 *
 * Created by Konstantin on 04.06.2017.
 */
class ListViewWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return ListViewRemoteViewsFactory(this.applicationContext, intent)
    }
}