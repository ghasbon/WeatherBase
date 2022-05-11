package com.example.weatherbase

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    val state: StateFlow<WeatherUiState>
        get() = mutableState

    private val mutableState = MutableStateFlow(WeatherUiState())

    suspend fun onPostalCodeInput(code: String) = withContext(Dispatchers.IO) {
        val testWeather = WeatherUiState("Rainy", "28", "30%")
        mutableState.value = testWeather
    }
}

data class WeatherUiState(val sky: String = "", val temperature: String = "", val humidity: String = "")
