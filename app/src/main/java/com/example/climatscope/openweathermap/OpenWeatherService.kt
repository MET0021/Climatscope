package com.example.climatscope.openweathermap

import com.example.climatscope.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface OpenWeatherService {


    @GET("data/2.5/weather?units=metric&lang=fr")
    fun getWeather(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String = BuildConfig.API_KEY,
    ) : Call<WeatherWrapper>
}