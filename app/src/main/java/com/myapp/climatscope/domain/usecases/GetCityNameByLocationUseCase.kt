package com.myapp.climatscope.domain.usecases

import com.myapp.climatscope.domain.repositories.WeatherRepository

class GetCityNameByLocationUseCase(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): Result<String> {
        return weatherRepository.getCityNameByCoordinates(latitude, longitude)
    }
}
