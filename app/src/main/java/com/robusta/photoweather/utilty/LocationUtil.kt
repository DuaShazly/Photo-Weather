package com.robusta.photoweather.utilty

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.robusta.photoweather.LOCATION_PERMISSION


object LocationUtil {

    private var mLocationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        context: AppCompatActivity,
        body: (newLocation: Location?) -> Unit
    ) {
        if (!PermissionUtil.isLocationPermissionGranted(context)) {
            PermissionUtil.requestLocationPermission(context, LOCATION_PERMISSION)
            return
        }
        val locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // todo these should be the same as what is used with location service.
            interval = 0
            fastestInterval = interval / 2
        }

        val builder = LocationSettingsRequest.Builder().apply {
            addLocationRequest(locationRequest)
        }

        val settingsClient = LocationServices.getSettingsClient(context)
        settingsClient.checkLocationSettings(builder.build())

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                body(locationResult.lastLocation)
            }

        }

        getFusedLocationProviderClient(context).requestLocationUpdates(
            locationRequest,
            mLocationCallback as LocationCallback,
            Looper.myLooper()
        )
    }

    fun stopCurrentLocationUpdates(context: AppCompatActivity) =
        mLocationCallback?.let {
            getFusedLocationProviderClient(context).removeLocationUpdates(it)
        }
}