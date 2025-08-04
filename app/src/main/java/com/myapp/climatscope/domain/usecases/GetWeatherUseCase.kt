package com.myapp.climatscope.domain.usecases

import com.myapp.climatscope.domain.entities.Weather
import com.myapp.climatscope.domain.repositories.WeatherRepository

class GetWeatherUseCase(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(cityName: String): Result<Weather> {
        return weatherRepository.getWeatherForCity(cityName)
    }
}
