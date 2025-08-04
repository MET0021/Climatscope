package com.myapp.climatscope.domain.usecases

import com.myapp.climatscope.domain.entities.City
import com.myapp.climatscope.domain.repositories.CityRepository

class DeleteCityUseCase(
    private val cityRepository: CityRepository
) {
    suspend operator fun invoke(city: City): Boolean {
        return cityRepository.deleteCity(city)
    }
}
