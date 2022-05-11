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
        "https://dark-sky.p.rapidapi.com/40.461564,-3.636349"
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
