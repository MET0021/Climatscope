package com.myapp.climatscope.domain.repositories

import com.myapp.climatscope.domain.entities.City

interface CityRepository {
    suspend fun getAllCities(): List<City>
    suspend fun createCity(city: City): Boolean
    suspend fun deleteCity(city: City): Boolean
}
