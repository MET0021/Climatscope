package com.myapp.climatscope.data.remote

import com.myapp.climatscope.BuildConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/2.5/weather?units=metric&lang=fr")
    suspend fun getWeather(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String = BuildConfig.API_KEY,
    ): Response<WeatherResponse>
}
