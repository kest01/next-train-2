package ru.kest.trainswidget.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import ru.kest.trainswidget.data.DataProvider;
import ru.kest.trainswidget.data.DataService;
import ru.kest.trainswidget.util.SchedulerUtil;

import static ru.kest.trainswidget.Constants.LOG_TAG;

/**
 * Created by Konstantin on 26.05.2017.
 */

public class LocationClient implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private GoogleApiClient googleApiClient;

    public LocationClient(Context context) {
        Log.d(LOG_TAG, "getLocationApi: ");
        this.googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void connect() {
        googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(LOG_TAG, "onConnected: " + googleApiClient);
        if (updateLastLocation(DataService.getDataProvider(googleApiClient.getContext()))) {
            SchedulerUtil.sendUpdateWidget(googleApiClient.getContext());
        }
        Log.d(LOG_TAG, "googleApiClient.disconnect()");
        googleApiClient.disconnect();
        googleApiClient = null;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "onConnectionSuspended: " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "onConnectionFailed: " + connectionResult);
    }

    private boolean updateLastLocation(DataProvider dataProvider) {
        if (ActivityCompat.checkSelfPermission(googleApiClient.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // return TODO;
            throw new RuntimeException("Permission failed!");
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(location != null){
            if (dataProvider.isSetLastLocation()) {
                if (location.distanceTo(dataProvider.getLastLocation()) > 500) { // difference more then 500 meters
                    dataProvider.setLastLocation(location);
                    return true;
                }
            } else {
                dataProvider.setLastLocation(location);
                return true;
            }
        }
        Log.d(LOG_TAG, "lastLocation has not changed");
        return false;
    }
}
