package com.myapp.climatscope.domain.repositories

import com.myapp.climatscope.domain.entities.Weather

interface WeatherRepository {
    suspend fun getWeatherForCity(cityName: String): Result<Weather>
    suspend fun getWeatherByCoordinates(latitude: Double, longitude: Double): Result<Weather>
}
