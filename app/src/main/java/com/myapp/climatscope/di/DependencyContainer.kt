package com.myapp.climatscope.di

import android.content.Context
import com.myapp.climatscope.BuildConfig
import com.myapp.climatscope.data.local.CityLocalDataSource
import com.myapp.climatscope.data.location.DefaultLocationService
import com.myapp.climatscope.data.location.LocationService
import com.myapp.climatscope.data.remote.WeatherApiService
import com.myapp.climatscope.data.remote.WeatherRemoteDataSource
import com.myapp.climatscope.data.repositories.CityRepositoryImpl
import com.myapp.climatscope.data.repositories.WeatherRepositoryImpl
import com.myapp.climatscope.domain.repositories.CityRepository
import com.myapp.climatscope.domain.repositories.WeatherRepository
import com.myapp.climatscope.domain.usecases.*
import com.myapp.climatscope.presentation.viewmodels.CityViewModelFactory
import com.myapp.climatscope.presentation.viewmodels.WeatherViewModelFactory
import com.myapp.climatscope.presentation.viewmodels.CitySearchViewModelFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DependencyContainer(private val context: Context) {

    // Lecture sécurisée de la clé API depuis BuildConfig (qui lit local.properties)
    private val apiKey = BuildConfig.OPENWEATHER_API_KEY

    // Network
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    // Retrofit pour l'API météo (OpenWeatherMap unifié)
    private val retrofit = Retrofit.Builder()
        .client(httpClient)
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Services
    private val weatherApiService: WeatherApiService = retrofit.create(WeatherApiService::class.java)

    // Data Sources
    private val weatherRemoteDataSource = WeatherRemoteDataSource(weatherApiService)
    private val cityLocalDataSource = CityLocalDataSource(context)
    val locationService: LocationService = DefaultLocationService(context)

    // Repositories
    private val cityRepository: CityRepository = CityRepositoryImpl(cityLocalDataSource)
    private val weatherRepository: WeatherRepository = WeatherRepositoryImpl(weatherRemoteDataSource)

    // Use Cases - Mis à jour avec les nouvelles fonctionnalités
    private val getAllCitiesUseCase = GetAllCitiesUseCase(cityRepository)
    private val createCityUseCase = CreateCityUseCase(cityRepository)
    private val deleteCityUseCase = DeleteCityUseCase(cityRepository)
    private val getWeatherUseCase = GetWeatherUseCase(weatherRepository)
    private val getWeatherByLocationUseCase = DefaultGetWeatherByLocationUseCase(locationService, weatherRepository)
    private val searchCitiesUseCase = SearchCitiesUseCase(weatherRepository)
    private val getCityNameByLocationUseCase = GetCityNameByLocationUseCase(weatherRepository)

    // ViewModels Factories
    fun getCityViewModelFactory(): CityViewModelFactory {
        return CityViewModelFactory(getAllCitiesUseCase, createCityUseCase, deleteCityUseCase)
    }

    fun getWeatherViewModelFactory(): WeatherViewModelFactory {
        return WeatherViewModelFactory(getWeatherUseCase)
    }

    fun getCitySearchViewModelFactory(): CitySearchViewModelFactory {
        return CitySearchViewModelFactory(searchCitiesUseCase)
    }

    fun getWeatherByLocationUseCase(): DefaultGetWeatherByLocationUseCase {
        return getWeatherByLocationUseCase
    }

    fun getSearchCitiesUseCase(): SearchCitiesUseCase {
        return searchCitiesUseCase
    }

    fun getCityNameByLocationUseCase(): GetCityNameByLocationUseCase {
        return getCityNameByLocationUseCase
    }
}
