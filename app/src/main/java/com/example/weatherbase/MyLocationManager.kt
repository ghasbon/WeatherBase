package com.example.weatherbase

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.os.Looper
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

class MyLocationManager {

    @OptIn(ExperimentalCoroutinesApi::class)
    @SuppressLint("MissingPermission")
    fun getLocation(activity: Activity): Flow<Location> = callbackFlow {

        val fusedLocationProvider = LocationServices.getFusedLocationProviderClient(activity)

        val callback = object : LocationCallback() {

            override fun onLocationResult(result: LocationResult) {
                try {
                    sendBlocking(result.lastLocation)
                } catch (e: Exception) {
                    fusedLocationProvider.removeLocationUpdates(this)
                }
            }

            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
            }
        }

        fusedLocationProvider.requestLocationUpdates(
            getLocationRequest(),
            callback,
            Looper.getMainLooper()
        )

        awaitClose {
            fusedLocationProvider.removeLocationUpdates(callback)
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
