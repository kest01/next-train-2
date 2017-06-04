package ru.kest.trainswidget.model.yandex

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*

/**
 * Yandex API DTO (for convert from json)
 *
 * Created by KKharitonov on 06.01.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ScheduleResponse(val threads: List<ApiTrainThread> = emptyList()) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ApiTrainThread(
            val arrival: Date? = null,
            val departure: Date? = null,
            val duration: Float = 0.toFloat(),
            val thread: Thread? = null
    ) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Thread(val shortTitle: String = "")
    }
}
