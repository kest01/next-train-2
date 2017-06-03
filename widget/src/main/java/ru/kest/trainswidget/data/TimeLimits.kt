package ru.kest.trainswidget.data

import ru.kest.trainswidget.Constants.*
import ru.kest.trainswidget.model.domain.NearestStation

/**
 * Calculate time limits according to location
 *
 * Created by KKharitonov on 14.02.2016.
 */
class TimeLimits(dataProvider: DataProvider) {

    private val nearestStation = dataProvider.nearestStation
    private val timeLimitsMap = mapOf(
            NearestStation.HOME to mapOf(
                    RED_STATUS to HOME_RED_STATUS,
                    YELLOW_STATUS to HOME_YELLOW_STATUS,
                    GREEN_STATUS to HOME_GREEN_STATUS,
                    FIRST_CALL to HOME_FIRST_CALL,
                    LAST_CALL to HOME_LAST_CALL),
            NearestStation.WORK to mapOf(
                    RED_STATUS to WORK_RED_STATUS,
                    YELLOW_STATUS to WORK_YELLOW_STATUS,
                    GREEN_STATUS to WORK_GREEN_STATUS,
                    FIRST_CALL to WORK_FIRST_CALL,
                    LAST_CALL to WORK_LAST_CALL)
    )

    fun getTimeLimit(key: String): Int {
        return timeLimitsMap[nearestStation]!!.get(key)!!
    }

}
