package ru.kest.trainswidget.model.domain

/**
 * Уровень комфортности поезда
 *
 * Created by Konstantin on 04.06.2017.
 */
enum class TrainComfortLevel {

    REGULAR,
    COMFORT;

    companion object {

        private val comfortTrains = setOf("6302/6301", "6304/6303", "6306/6305", "6314/6313", "6694", "6316/6315",
                "6318/6317", "6320/6319/6235", "6322/6321", "6106", "6610", "6108", "6614",
                "6332/6331", "6616", "6334/6333", "6336/6335", "6340/6339", "6434/6433", "6698",
                "6622", "6010", "6624", "6344/6343", "6348/6347", "6628", "6012",
                "6512", "6222", "6516", "6226")

        fun getComfortLevel(trainNumber: String) : TrainComfortLevel {
            return if (trainNumber in comfortTrains) COMFORT else REGULAR
        }
    }
}