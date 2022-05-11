package com.example.weatherbase

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    private fun updateWeatherUi(uiState: WeatherUiState) {
        findViewById<TextView>(R.id.skyTextView).text = uiState.sky
        findViewById<TextView>(R.id.temperatureTextView).text = uiState.temperature
        findViewById<TextView>(R.id.humidityTextView).text = uiState.humidity
    }
}
