package com.myapp.climatscope.openweathermap

import com.google.gson.annotations.SerializedName


data class WeatherWrapper(
    val weather: List<WeatherData>,
    val main: MainData)

data class WeatherData(
    val id: Int?,
    val main: String?,
    val description: String,
    val icon: String
)

data class MainData(
    @SerializedName("temp") val temperature: Double,
    val pressure: Int,
    val humidity: Int,
    val temp_min: Double?,
    val temp_max: Double?
)
