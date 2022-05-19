package com.example.weatherbase

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.lang.Exception

class MyLocationManager() {

    private var fusedLocationProvider: FusedLocationProviderClient? = null

    private var locationCallback: LocationCallback? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    @SuppressLint("MissingPermission")
    fun getLocation(activity: Activity): Flow<Location> = callbackFlow {

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(activity)

        locationCallback?.let {
            fusedLocationProvider?.removeLocationUpdates(it)
        }

        locationCallback = object : LocationCallback() {

            override fun onLocationResult(result: LocationResult) {
                try {
                    sendBlocking(result.lastLocation)
                } catch (e: Exception) {
                    fusedLocationProvider?.removeLocationUpdates(this)
                }
            }

            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
            }
        }.apply {
            fusedLocationProvider?.requestLocationUpdates(getLocationRequest(), this, Looper.getMainLooper())
        }

        awaitClose {
            locationCallback?.let {
                fusedLocationProvider?.removeLocationUpdates(it)
            }
        }
    }

    private fun getLocationRequest(): LocationRequest {
        val request = LocationRequest.create()
        request.isWaitForAccurateLocation = true
        request.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        request.interval = 500
        request.fastestInterval = 200
        request.numUpdates = 10
        request.maxWaitTime = 5000
        return request
    }
}
