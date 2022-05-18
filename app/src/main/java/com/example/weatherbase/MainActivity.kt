package com.example.weatherbase

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MyPermissionManager().withPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION, ::getUserLocation)

        findViewById<Button>(R.id.getWeatherButton).setOnClickListener {
            val postalCode = findViewById<EditText>(R.id.postalCodeEditText).text
            lifecycleScope.launch {
                viewModel.onPostalCodeInput(postalCode.toString())
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.state.collect(::updateWeatherUi)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        val fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProvider.lastLocation.addOnSuccessListener { location ->
            location?.let {
                lifecycleScope.launch {
                    viewModel.onGpsLocationOk(it.latitude, it.longitude)
                }
            } ?: Toast.makeText(this, "Location error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateWeatherUi(uiState: WeatherUiState) {
        findViewById<TextView>(R.id.skyTextView).text = uiState.sky
        findViewById<TextView>(R.id.temperatureTextView).text = uiState.temperature
        findViewById<TextView>(R.id.humidityTextView).text = uiState.humidity
    }
}
