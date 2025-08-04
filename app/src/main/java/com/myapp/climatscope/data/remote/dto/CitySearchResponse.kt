package com.myapp.climatscope.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CitySearchResponse(
    @SerializedName("name")
    val name: String,
    @SerializedName("lat")
    val latitude: Double,
    @SerializedName("lon")
    val longitude: Double,
    @SerializedName("country")
    val country: String,
    @SerializedName("state")
    val state: String? = null
) {
    fun getDisplayName(): String {
        return when {
            !state.isNullOrEmpty() -> "$name, $state, $country"
            else -> "$name, $country"
        }
    }
}
