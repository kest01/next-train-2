package ru.kest.trainswidget.model.domain

import java.util.*

/**
 * Train Model
 *
 * Created by KKharitonov on 07.01.2016.
 */
data class TrainThread(
        var arrival: Date,
        var departure: Date,
        var title: String,

        var from: String? = null,
        var to: String? = null
)
