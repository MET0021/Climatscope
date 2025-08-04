package com.myapp.climatscope.data.repositories

import com.myapp.climatscope.data.remote.WeatherRemoteDataSource
import com.myapp.climatscope.data.remote.GeocodingResponse
import com.myapp.climatscope.domain.entities.Weather
import com.myapp.climatscope.domain.repositories.WeatherRepository

class WeatherRepositoryImpl(
    private val remoteDataSource: WeatherRemoteDataSource
) : WeatherRepository {

    override suspend fun getWeatherForCity(cityName: String): Result<Weather> {
        return remoteDataSource.getWeatherForCity(cityName)
    }

    override suspend fun getWeatherByCoordinates(latitude: Double, longitude: Double): Result<Weather> {
        return remoteDataSource.getWeatherByCoordinates(latitude, longitude)
    }

    override suspend fun searchCities(query: String): Result<List<GeocodingResponse>> {
        return remoteDataSource.searchCities(query)
    }

    override suspend fun getCityNameByCoordinates(latitude: Double, longitude: Double): Result<String> {
        return remoteDataSource.getCityNameByCoordinates(latitude, longitude)
    }
}
