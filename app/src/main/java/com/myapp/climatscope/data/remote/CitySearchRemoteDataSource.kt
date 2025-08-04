package com.myapp.climatscope.data.remote

import com.myapp.climatscope.data.remote.dto.CitySearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CitySearchApi {
    @GET("geo/1.0/direct")
    suspend fun searchCities(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String
    ): Response<List<CitySearchResponse>>
}

interface CitySearchRemoteDataSource {
    suspend fun searchCities(query: String): Result<List<CitySearchResponse>>
}

class DefaultCitySearchRemoteDataSource(
    private val api: CitySearchApi,
    private val apiKey: String
) : CitySearchRemoteDataSource {

    override suspend fun searchCities(query: String): Result<List<CitySearchResponse>> {
        return try {
            if (query.isBlank() || query.length < 2) {
                return Result.success(emptyList())
            }

            val response = api.searchCities(
                query = query.trim(),
                limit = 5,
                apiKey = apiKey
            )

            if (response.isSuccessful) {
                val cities = response.body() ?: emptyList()
                Result.success(cities)
            } else {
                Result.failure(Exception("Erreur de recherche: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
