package com.myapp.climatscope.domain.entities

data class Weather(
    val description: String,
    val temperature: Double,
    val humidity: Int,
    val pressure: Int,
    val iconUrl: String,
    val windSpeed: Double = 0.0,
    val feelsLike: Double = temperature
)
