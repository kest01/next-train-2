package ru.kest.trainswidget.model.yandex

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*

/**
 * Yandex API DTO (for convert from json)
 *
 * Created by KKharitonov on 06.01.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ScheduleResponse(val threads: List<TrainThread>?) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class TrainThread(
            val arrival: Date?,
            val departure: Date?,
            val duration: Float = 0.toFloat(),
            val thread: Thread?
    ) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Thread(val shortTitle: String?)
    }
}
