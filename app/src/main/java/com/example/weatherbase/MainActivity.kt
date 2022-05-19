package com.example.weatherbase

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModels()

    private val locationManager = MyLocationManager()

    private var locationFlowJob: Job? = null

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)) {
            getUserLocation()
        } else Unit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isLocationPermissionGranted()) {
            getUserLocation()
        } else {
            locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
        }

        findViewById<Button>(R.id.getWeatherButton).setOnClickListener {
            val postalCode = findViewById<EditText>(R.id.postalCodeEditText).text
            lifecycleScope.launch {
                viewModel.onPostalCodeInput(postalCode.toString())
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.state.collect(::updateWeatherUi)
        }

        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, intent: Intent?) {
                if (intent?.action?.contains(LocationManager.PROVIDERS_CHANGED_ACTION) == true) {
                    getUserLocation()
                }
            }
        }, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
    }

    private fun getUserLocation() {
        locationFlowJob?.cancel()
        locationFlowJob = lifecycleScope.launch {
            locationManager.getLocation(this@MainActivity).collect {
                viewModel.onGpsLocationOk(it.latitude, it.longitude)
            }
        }
    }

    private fun updateWeatherUi(uiState: WeatherUiState) {
        findViewById<TextView>(R.id.skyTextView).text = uiState.sky
        findViewById<TextView>(R.id.temperatureTextView).text = uiState.temperature
        findViewById<TextView>(R.id.humidityTextView).text = uiState.humidity
    }

    private fun isLocationPermissionGranted(): Boolean {
        val locationPermission = Manifest.permission.ACCESS_COARSE_LOCATION
        val isGranted = ContextCompat.checkSelfPermission(this, locationPermission)
        return isGranted == PackageManager.PERMISSION_GRANTED
    }
}
