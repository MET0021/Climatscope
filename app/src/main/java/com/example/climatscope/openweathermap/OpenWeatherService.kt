package com.example.climatscope.openweathermap

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "d09396fffae65d3e4f27d85e12d19e29"

interface OpenWeatherService {


    @GET("data/2.5/weather?units=metric&lang=fr")
    fun getWeather(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String = API_KEY,
    ) : Call<WeatherWrapper>
}