package ru.kest.trainswidget.model.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

/**
 * Train Model
 *
 * Created by KKharitonov on 07.01.2016.
 */
data class TrainThread
    @JsonCreator constructor(@JsonProperty("arrival") var arrival: Date,
                             @JsonProperty("departure") var departure: Date,
                             @JsonProperty("title") var title: String,

                             @JsonProperty("from") var from: String? = null,
                             @JsonProperty("to") var to: String? = null
)
