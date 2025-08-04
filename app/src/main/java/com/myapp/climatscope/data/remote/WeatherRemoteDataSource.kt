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
                // Suppression de ",fr" pour permettre la recherche mondiale
                val response = apiService.getWeather(cityName)
                if (response.isSuccessful) {
                    response.body()?.let { weatherResponse ->
                        val weather = mapToWeather(weatherResponse)
                        Result.success(weather)
                    } ?: Result.failure(Exception("Empty response body"))
                } else {
                    Log.e("WeatherAPI", "API Error: ${response.code()} - ${response.message()}")
                    Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e("WeatherAPI", "Exception: ${e.message}", e)
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
                    Log.e("WeatherAPI", "API Error: ${response.code()} - ${response.message()}")
                    Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e("WeatherAPI", "Exception: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    // Nouvelle fonction pour rechercher des villes dans le monde entier
    suspend fun searchCities(query: String): Result<List<GeocodingResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchCities(query)
                if (response.isSuccessful) {
                    response.body()?.let { cities ->
                        Result.success(cities)
                    } ?: Result.failure(Exception("Empty response body"))
                } else {
                    Log.e("WeatherAPI", "Search Cities Error: ${response.code()} - ${response.message()}")
                    Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e("WeatherAPI", "Search Cities Exception: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    // Nouvelle fonction pour obtenir le nom de la ville par coordonnées
    suspend fun getCityNameByCoordinates(latitude: Double, longitude: Double): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCityNameByCoordinates(latitude, longitude)
                if (response.isSuccessful) {
                    response.body()?.let { cities ->
                        if (cities.isNotEmpty()) {
                            val city = cities.first()
                            val cityName = if (city.state != null && city.country.isNotEmpty()) {
                                "${city.name}, ${city.state}, ${city.country}"
                            } else {
                                "${city.name}, ${city.country}"
                            }
                            Result.success(cityName)
                        } else {
                            Result.success("Position actuelle")
                        }
                    } ?: Result.failure(Exception("Empty response body"))
                } else {
                    Log.e("WeatherAPI", "Reverse Geocoding Error: ${response.code()} - ${response.message()}")
                    Result.success("Ma position") // Fallback en cas d'erreur
                }
            } catch (e: Exception) {
                Log.e("WeatherAPI", "Reverse Geocoding Exception: ${e.message}", e)
                Result.success("Ma position") // Fallback en cas d'erreur
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
