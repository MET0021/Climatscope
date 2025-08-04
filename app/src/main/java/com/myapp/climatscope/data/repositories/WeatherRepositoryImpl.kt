package com.myapp.climatscope.data.repositories

import com.myapp.climatscope.data.remote.WeatherRemoteDataSource
import com.myapp.climatscope.domain.entities.Weather
import com.myapp.climatscope.domain.repositories.WeatherRepository

class WeatherRepositoryImpl(
    private val remoteDataSource: WeatherRemoteDataSource
) : WeatherRepository {

    override suspend fun getWeatherForCity(cityName: String): Result<Weather> {
        return remoteDataSource.getWeatherForCity(cityName)
    }
}
