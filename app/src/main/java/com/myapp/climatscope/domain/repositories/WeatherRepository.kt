package com.myapp.climatscope.domain.repositories

import com.myapp.climatscope.domain.entities.Weather
import com.myapp.climatscope.data.remote.GeocodingResponse

interface WeatherRepository {
    suspend fun getWeatherForCity(cityName: String): Result<Weather>
    suspend fun getWeatherByCoordinates(latitude: Double, longitude: Double): Result<Weather>

    // Nouvelles fonctions pour la recherche de villes mondiale
    suspend fun searchCities(query: String): Result<List<GeocodingResponse>>
    suspend fun getCityNameByCoordinates(latitude: Double, longitude: Double): Result<String>
}
