package ru.kest.trainswidget.data

import android.location.Location
import ru.kest.trainswidget.model.domain.TrainThread

/**
 * FieldDataStorage Entity
 *
 * Created by KKharitonov on 07.01.2016.
 */
data class FieldDataStorage(
        var trainsFromHomeToWork: List<TrainThread>,
        var trainsFromWorkToHome: List<TrainThread>,
        var lastLocation: Location?,
        var notificationTrain: TrainThread?
) {

}
