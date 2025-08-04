package com.myapp.climatscope.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.myapp.climatscope.domain.entities.City
import com.myapp.climatscope.domain.usecases.CreateCityUseCase
import com.myapp.climatscope.domain.usecases.DeleteCityUseCase
import com.myapp.climatscope.domain.usecases.GetAllCitiesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CityUiState(
    val cities: List<City> = emptyList(),
    val currentCity: City? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CityViewModel(
    private val getAllCitiesUseCase: GetAllCitiesUseCase,
    private val createCityUseCase: CreateCityUseCase,
    private val deleteCityUseCase: DeleteCityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CityUiState())
    val uiState: StateFlow<CityUiState> = _uiState.asStateFlow()

    init {
        loadCities()
    }

    fun loadCities() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val cities = getAllCitiesUseCase()
                _uiState.value = _uiState.value.copy(
                    cities = cities,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erreur lors du chargement des villes"
                )
            }
        }
    }

    fun createCity(cityName: String) {
        if (cityName.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val success = createCityUseCase(cityName.trim())
                if (success) {
                    loadCities() // Reload cities after creation
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Erreur lors de la création de la ville"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erreur lors de la création de la ville"
                )
            }
        }
    }

    fun deleteCity(city: City) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val success = deleteCityUseCase(city)
                if (success) {
                    loadCities() // Reload cities after deletion
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Erreur lors de la suppression de la ville"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erreur lors de la suppression de la ville"
                )
            }
        }
    }

    fun setCurrentCity(city: City) {
        _uiState.value = _uiState.value.copy(currentCity = city)
    }

    fun removeCity(city: City) {
        deleteCity(city)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

class CityViewModelFactory(
    private val getAllCitiesUseCase: GetAllCitiesUseCase,
    private val createCityUseCase: CreateCityUseCase,
    private val deleteCityUseCase: DeleteCityUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CityViewModel(getAllCitiesUseCase, createCityUseCase, deleteCityUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
