package ru.kest.trainswidget.data

import android.content.Context

/**
 * Singleton factory for DataProvider
 *
 * Created by KKharitonov on 14.02.2016.
 */
class DataService(context: Context) {

    val dataProvider: DataProvider by lazy { DataProviderImpl(context) }

}
