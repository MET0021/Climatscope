package com.myapp.climatscope.data.remote

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("weather") val weather: List<WeatherInfo>,
    @SerializedName("main") val main: MainInfo,
    @SerializedName("wind") val wind: WindInfo? = null,
    @SerializedName("name") val name: String,
    @SerializedName("sys") val sys: SysInfo,
    @SerializedName("coord") val coord: CoordInfo
)

data class WeatherInfo(
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)

data class MainInfo(
    @SerializedName("temp") val temperature: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("pressure") val pressure: Int,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double
)

data class WindInfo(
    @SerializedName("speed") val speed: Double,
    @SerializedName("deg") val deg: Int? = null
)

data class SysInfo(
    @SerializedName("country") val country: String
)

data class CoordInfo(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double
)

// Nouveau modèle pour la réponse de géocodage
data class GeocodingResponse(
    @SerializedName("name") val name: String,
    @SerializedName("local_names") val localNames: Map<String, String>? = null,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("country") val country: String,
    @SerializedName("state") val state: String? = null
)
