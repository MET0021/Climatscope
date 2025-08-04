package com.myapp.climatscope.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.myapp.climatscope.data.remote.GeocodingResponse
import com.myapp.climatscope.domain.usecases.SearchCitiesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CitySearchViewModel(
    private val searchCitiesUseCase: SearchCitiesUseCase
) : ViewModel() {

    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    data class SearchState(
        val cities: List<GeocodingResponse> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    fun searchCities(query: String) {
        if (query.length < 2) {
            clearResults()
            return
        }

        viewModelScope.launch {
            _searchState.value = _searchState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val result = searchCitiesUseCase(query)
                result.fold(
                    onSuccess = { cities ->
                        _searchState.value = _searchState.value.copy(
                            cities = cities,
                            isLoading = false,
                            error = null
                        )
                    },
                    onFailure = { error ->
                        _searchState.value = _searchState.value.copy(
                            cities = emptyList(),
                            isLoading = false,
                            error = error.message ?: "Erreur de recherche"
                        )
                    }
                )
            } catch (e: Exception) {
                _searchState.value = _searchState.value.copy(
                    cities = emptyList(),
                    isLoading = false,
                    error = e.message ?: "Erreur inattendue"
                )
            }
        }
    }

    fun clearResults() {
        _searchState.value = SearchState()
    }
}

class CitySearchViewModelFactory(
    private val searchCitiesUseCase: SearchCitiesUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CitySearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CitySearchViewModel(searchCitiesUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
