package com.myapp.climatscope.domain.usecases

import com.myapp.climatscope.data.remote.GeocodingResponse
import com.myapp.climatscope.domain.repositories.WeatherRepository

class SearchCitiesUseCase(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(query: String): Result<List<GeocodingResponse>> {
        if (query.isBlank()) {
            return Result.success(emptyList())
        }

        if (query.length < 2) {
            return Result.success(emptyList())
        }

        return weatherRepository.searchCities(query)
    }
}
