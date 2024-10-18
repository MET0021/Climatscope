package com.myapp.climatscope.weather

data class Weather (
    val description: String,
    val temperature: Double,
    val humidity: Int,
    val pressure: Int,
    val iconUrl: String
)

