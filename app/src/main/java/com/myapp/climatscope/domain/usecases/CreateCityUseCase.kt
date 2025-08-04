package com.myapp.climatscope.domain.usecases

import com.myapp.climatscope.domain.entities.City
import com.myapp.climatscope.domain.repositories.CityRepository

class CreateCityUseCase(
    private val cityRepository: CityRepository
) {
    suspend operator fun invoke(cityName: String): Boolean {
        val city = City(name = cityName)
        return cityRepository.createCity(city)
    }
}
