package ru.kest.trainswidget.data

import android.location.Location

import ru.kest.trainswidget.model.domain.NearestStation
import ru.kest.trainswidget.model.domain.TrainThread

/**
 * DataProvider interface
 *
 * Created by KKharitonov on 14.02.2016.
 */
interface DataProvider {

    var trainsFromHomeToWork: List<TrainThread>

    var trainsFromWorkToHome: List<TrainThread>

    val isSetTrainThreads: Boolean

    var lastLocation: Location? // TODO Проверить нужно ли тут ?
    val isSetLastLocation: Boolean

    var notificationTrain: TrainThread?  // TODO Проверить нужно ли тут ?
    val isSetNotificationTrain: Boolean

    val nearestStation: NearestStation?  // TODO Проверить нужно ли тут ?

    fun getThreadByHash(hash: Int): TrainThread?

}
