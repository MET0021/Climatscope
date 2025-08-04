package com.myapp.climatscope.domain.usecases

import com.myapp.climatscope.data.location.LocationService
import com.myapp.climatscope.domain.repositories.WeatherRepository
import com.myapp.climatscope.domain.entities.Weather
import android.location.Location

interface GetWeatherByLocationUseCase {
    suspend operator fun invoke(): Result<Pair<Weather, Location>>
}

class DefaultGetWeatherByLocationUseCase(
    private val locationService: LocationService,
    private val weatherRepository: WeatherRepository
) : GetWeatherByLocationUseCase {

    override suspend fun invoke(): Result<Pair<Weather, Location>> {
        return try {
            // Obtenir la position actuelle
            val locationResult = locationService.getCurrentLocation()

            locationResult.fold(
                onSuccess = { location ->
                    // Obtenir la météo pour cette position
                    val weatherResult = weatherRepository.getWeatherByCoordinates(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )

                    weatherResult.fold(
                        onSuccess = { weather ->
                            Result.success(Pair(weather, location))
                        },
                        onFailure = { error ->
                            Result.failure(error)
                        }
                    )
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
