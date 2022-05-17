package com.example.weatherbase

import androidx.lifecycle.ViewModel
import com.github.kittinunf.fuel.httpGet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    val state: StateFlow<WeatherUiState>
        get() = mutableState

    private val mutableState = MutableStateFlow(WeatherUiState())

    suspend fun onPostalCodeInput(code: String) = withContext(Dispatchers.IO) {
        "https://forward-reverse-geocoding.p.rapidapi.com/v1/search"
            .httpGet(listOf("q" to code))
            .header(
                "X-RapidAPI-Host" to "forward-reverse-geocoding.p.rapidapi.com",
                "X-RapidAPI-Key" to "718390cf80mshbaae151ab7aa8acp1df702jsnac8cc8f5308b"
            )
            .responseString { _, _, result ->
                mutableState.value = WeatherUiState(result.get(), "", "")
            }
    }

    suspend fun onGpsLocationOk(latitude: Double, longitude: Double) {
        "https://dark-sky.p.rapidapi.com/${latitude},${longitude}"
            .httpGet()
            .header(
                "X-RapidAPI-Host" to "dark-sky.p.rapidapi.com",
                "X-RapidAPI-Key" to "718390cf80mshbaae151ab7aa8acp1df702jsnac8cc8f5308b"
            )
            .responseString {_, _, result ->
                mutableState.value = WeatherUiState(result.get(), "", "")
            }
    }
}

data class WeatherUiState(val sky: String = "", val temperature: String = "", val humidity: String = "")
