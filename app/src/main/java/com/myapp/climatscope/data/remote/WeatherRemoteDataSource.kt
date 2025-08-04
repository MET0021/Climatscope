package com.myapp.climatscope.data.remote

import android.util.Log
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
                Log.e("TAG", "TEST getWeatherForCity ----- $response", )
                Log.e("TAG", "TEST getWeatherForCity isSuccessful ****** ${response.isSuccessful}", )
                if (response.isSuccessful) {
                    response.body()?.let { weatherResponse ->
                        val weather = mapToWeather(weatherResponse)
                        Log.i("TAG", "TEST weatherResponse ><<<<<<< $weatherResponse")
                        Log.i("TAG", "TEST ><<<<<<< $weather")
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

    suspend fun getWeatherByCoordinates(latitude: Double, longitude: Double): Result<Weather> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getWeatherByCoordinates(latitude, longitude)
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
            feelsLike = response.main.feelsLike,
            humidity = response.main.humidity,
            pressure = response.main.pressure,
            windSpeed = response.wind?.speed ?: 0.0,
            iconUrl = "https://openweathermap.org/img/wn/${weatherFirst.icon}.png"
        )
    }
}
