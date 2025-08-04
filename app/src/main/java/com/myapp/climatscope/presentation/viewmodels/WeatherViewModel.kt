package com.myapp.climatscope.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.myapp.climatscope.domain.entities.Weather
import com.myapp.climatscope.domain.usecases.GetWeatherUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WeatherUiState(
    val weather: Weather? = null,
    val cityName: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class WeatherViewModel(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    fun loadWeatherForCity(cityName: String) {
        if (cityName.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                cityName = cityName
            )

            try {
                val result = getWeatherUseCase(cityName)
                result.fold(
                    onSuccess = { weather ->
                        _uiState.value = _uiState.value.copy(
                            weather = weather,
                            isLoading = false
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Erreur lors du chargement de la météo"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erreur lors du chargement de la météo"
                )
            }
        }
    }

    fun refreshWeather() {
        val currentCityName = _uiState.value.cityName
        if (currentCityName.isNotBlank()) {
            loadWeatherForCity(currentCityName)
        }
    }

    fun clearWeather() {
        _uiState.value = WeatherUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

class WeatherViewModelFactory(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(getWeatherUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
