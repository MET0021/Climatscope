package com.myapp.climatscope.data.remote

import com.myapp.climatscope.domain.entities.Weather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRemoteDataSource(
    private val apiService: WeatherApiService
) {
    suspend fun getWeatherForCity(cityName: String): Result<Weather> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getWeather("$cityName,fr")
                if (response.isSuccessful) {
                    response.body()?.let { weatherResponse ->
                        val weather = mapToWeather(weatherResponse)
                        Result.success(weather)
                    } ?: Result.failure(Exception("Empty response body"))
                } else {
                    Result.failure(Exception("API Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun mapToWeather(response: WeatherResponse): Weather {
        val weatherFirst = response.weather.first()
        return Weather(
            description = weatherFirst.description,
            temperature = response.main.temperature,
            humidity = response.main.humidity,
            pressure = response.main.pressure,
            iconUrl = "https://openweathermap.org/img/wn/${weatherFirst.icon}.png"
        )
    }
}
