package ru.kest.trainswidget.model.domain

import android.location.Location

/**
 * Enum with nearest station
 *
 * Created by KKharitonov on 14.02.2016.
 */
enum class NearestStation {
    HOME,
    WORK;

    companion object {

        fun getNearestStation(lastLocation: Location): NearestStation {
            if (getDistanceToHome(lastLocation) <= getDistanceToWork(lastLocation)) {
                return HOME
            } else {
                return WORK
            }
        }

        private val homeLocation = Location("").apply {
            latitude = 55.8300989
            longitude = 37.2187062
        }
        private val workLocation = Location("").apply {
            latitude = 55.802753
            longitude = 37.491259
        }

        private fun getDistanceToWork(lastLocation: Location): Int {
            return Math.round(workLocation.distanceTo(lastLocation))
        }

        private fun getDistanceToHome(lastLocation: Location): Int {
            return Math.round(homeLocation.distanceTo(lastLocation))
        }
    }

}
