package com.myapp.climatscope.domain.usecases

import android.util.Log
import com.myapp.climatscope.data.remote.CitySearchRemoteDataSource
import com.myapp.climatscope.data.remote.dto.CitySearchResponse

interface SearchCitiesUseCase {
    suspend operator fun invoke(query: String): Result<List<CitySearchResponse>>
}

class DefaultSearchCitiesUseCase(
    private val citySearchRemoteDataSource: CitySearchRemoteDataSource
) : SearchCitiesUseCase {

    override suspend fun invoke(query: String): Result<List<CitySearchResponse>> {
        val result =  citySearchRemoteDataSource.searchCities(query)
        Log.i("TAG", "TEST invoke: $result")
        return result
    }
}
