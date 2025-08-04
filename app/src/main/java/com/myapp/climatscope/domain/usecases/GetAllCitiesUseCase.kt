package com.myapp.climatscope.domain.usecases
import com.myapp.climatscope.domain.entities.City
import com.myapp.climatscope.domain.repositories.CityRepository

class GetAllCitiesUseCase(
    private val cityRepository: CityRepository
) {
    suspend operator fun invoke(): List<City> {
        return cityRepository.getAllCities()
    }
}

