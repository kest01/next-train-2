package ru.kest.trainswidget.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import ru.kest.trainswidget.LOG_TAG
import ru.kest.trainswidget.data.DataProvider
import ru.kest.trainswidget.data.DataService
import ru.kest.trainswidget.util.SchedulerUtil
import java.lang.RuntimeException

/**
 * Клиент Google Api для определения координат
 *
 * Created by Konstantin on 26.05.2017.
 */
class LocationClient(context: Context) : GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private var googleApiClient: GoogleApiClient

    init {
        Log.d(LOG_TAG, "LocationClient init")
        this.googleApiClient = GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
    }

    fun connect() {
        googleApiClient.connect()
    }

    override fun onConnected(bundle: Bundle?) {
        Log.d(LOG_TAG, "onConnected: $googleApiClient")
        if (updateLastLocation(DataService(googleApiClient.context).dataProvider)) {
            SchedulerUtil.sendUpdateWidget(googleApiClient.context)
        }
        Log.d(LOG_TAG, "googleApiClient.disconnect()")
        googleApiClient.disconnect()
    }

    override fun onConnectionSuspended(i: Int) {
        Log.d(LOG_TAG, "onConnectionSuspended: $i")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d(LOG_TAG, "onConnectionFailed: $connectionResult")
    }

    private fun updateLastLocation(dataProvider: DataProvider): Boolean {
        if (ActivityCompat.checkSelfPermission(googleApiClient.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw RuntimeException("Permissions required!")
        }
        val location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
        Log.d(LOG_TAG, "Location: $location")
        if (location != null) {
            if (dataProvider.isSetLastLocation) {
                if (location.distanceTo(dataProvider.lastLocation) > 500) { // difference more then 500 meters
                    dataProvider.lastLocation = location
                    return true
                }
            } else {
                dataProvider.lastLocation = location
                return true
            }
        }
        Log.d(LOG_TAG, "lastLocation has not changed")
        return false
    }
}
