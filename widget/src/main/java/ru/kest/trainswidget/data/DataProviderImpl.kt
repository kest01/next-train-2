package ru.kest.trainswidget.data

import android.content.Context
import android.location.Location

import ru.kest.trainswidget.model.domain.NearestStation
import ru.kest.trainswidget.model.domain.TrainThread
import ru.kest.trainswidget.util.PreferencesUtil

/**
 * Имплементация хранилища данных на основе SharedPreferences
 *
 * Created by KKharitonov on 14.02.2016.
 */
class DataProviderImpl(private val context: Context) : DataProvider {

    private val dataStorage: FieldDataStorage = PreferencesUtil.createDataStorage(context)

    override var trainsFromHomeToWork: List<TrainThread>
        get() = dataStorage.trainsFromHomeToWork
        set(value) {
            PreferencesUtil.saveTrainsFromHomeToWork(context, value)
            dataStorage.trainsFromHomeToWork = value
        }

    override var trainsFromWorkToHome: List<TrainThread>
        get() = dataStorage.trainsFromWorkToHome
        set(value) {
            PreferencesUtil.saveTrainsFromWorkToHome(context, value)
            dataStorage.trainsFromWorkToHome = value
        }

    override val isSetTrainThreads: Boolean
        get() = trainsFromHomeToWork.isNotEmpty() && trainsFromWorkToHome.isNotEmpty()

    override var lastLocation: Location?
        get() = dataStorage.lastLocation
        set(value) {
            if (value != null) {
                PreferencesUtil.saveLastLocation(context, value)
            }
            dataStorage.lastLocation = value
        }

    override val isSetLastLocation: Boolean
        get() = lastLocation != null

    override var notificationTrain: TrainThread?
        get() = dataStorage.notificationTrain
        set(value) {
            if (value != null) {
                PreferencesUtil.saveNotificationTrain(context, value)
            }
            dataStorage.notificationTrain = value
        }

    override val isSetNotificationTrain: Boolean
        get() = notificationTrain != null

    override val nearestStation: NearestStation?
        get() = if (isSetLastLocation) NearestStation.getNearestStation(lastLocation) else null


    override fun getThreadByHash(hash: Int): TrainThread? {
        var result = getThreadByHash(trainsFromHomeToWork, hash)
        if (result == null) {
            result = getThreadByHash(trainsFromWorkToHome, hash)
        }
        return result
    }

    private fun getThreadByHash(threads: List<TrainThread>?, hash: Int): TrainThread? {
        if (threads != null) {
            for (thread in threads) {
                if (thread.hashCode() == hash) {
                    return thread
                }
            }
        }
        return null
    }
}
