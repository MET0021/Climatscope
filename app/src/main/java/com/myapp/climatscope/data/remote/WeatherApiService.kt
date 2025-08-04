package com.myapp.climatscope.data.remote

import com.myapp.climatscope.BuildConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/2.5/weather?units=metric")
    suspend fun getWeather(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String = BuildConfig.OPENWEATHER_API_KEY,
        @Query("lang") language: String = "en"
    ): Response<WeatherResponse>

    @GET("data/2.5/weather?units=metric")
    suspend fun getWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = BuildConfig.OPENWEATHER_API_KEY,
        @Query("lang") language: String = "en"
    ): Response<WeatherResponse>

    // API pour rechercher des villes dans le monde entier
    @GET("geo/1.0/direct")
    suspend fun searchCities(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String = BuildConfig.OPENWEATHER_API_KEY
    ): Response<List<GeocodingResponse>>

    // API pour obtenir le nom de la ville par coordonnées (géolocalisation inverse)
    @GET("geo/1.0/reverse")
    suspend fun getCityNameByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String = BuildConfig.OPENWEATHER_API_KEY
    ): Response<List<GeocodingResponse>>
}
