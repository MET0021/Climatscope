package com.myapp.climatscope.data.remote

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("weather") val weather: List<WeatherInfo>,
    @SerializedName("main") val main: MainInfo
)

data class WeatherInfo(
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)

data class MainInfo(
    @SerializedName("temp") val temperature: Double,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("pressure") val pressure: Int
)
