package com.myapp.climatscope.data.repositories

import com.myapp.climatscope.data.local.CityLocalDataSource
import com.myapp.climatscope.domain.entities.City
import com.myapp.climatscope.domain.repositories.CityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CityRepositoryImpl(
    private val localDataSource: CityLocalDataSource
) : CityRepository {

    override suspend fun getAllCities(): List<City> {
        return withContext(Dispatchers.IO) {
            localDataSource.getAllCities()
        }
    }

    override suspend fun createCity(city: City): Boolean {
        return withContext(Dispatchers.IO) {
            localDataSource.createCity(city)
        }
    }

    override suspend fun deleteCity(city: City): Boolean {
        return withContext(Dispatchers.IO) {
            localDataSource.deleteCity(city)
        }
    }
}
