package com.myapp.climatscope.di

import android.content.Context
import com.myapp.climatscope.data.local.CityLocalDataSource
import com.myapp.climatscope.data.remote.WeatherApiService
import com.myapp.climatscope.data.remote.WeatherRemoteDataSource
import com.myapp.climatscope.data.repositories.CityRepositoryImpl
import com.myapp.climatscope.data.repositories.WeatherRepositoryImpl
import com.myapp.climatscope.domain.repositories.CityRepository
import com.myapp.climatscope.domain.repositories.WeatherRepository
import com.myapp.climatscope.domain.usecases.CreateCityUseCase
import com.myapp.climatscope.domain.usecases.DeleteCityUseCase
import com.myapp.climatscope.domain.usecases.GetAllCitiesUseCase
import com.myapp.climatscope.domain.usecases.GetWeatherUseCase
import com.myapp.climatscope.presentation.viewmodels.CityViewModelFactory
import com.myapp.climatscope.presentation.viewmodels.WeatherViewModelFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DependencyContainer(private val context: Context) {

    // Network
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    private val retrofit = Retrofit.Builder()
        .client(httpClient)
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Data Sources
    private val weatherApiService: WeatherApiService = retrofit.create(WeatherApiService::class.java)
    private val weatherRemoteDataSource = WeatherRemoteDataSource(weatherApiService)
    private val cityLocalDataSource = CityLocalDataSource(context)

    // Repositories
    private val cityRepository: CityRepository = CityRepositoryImpl(cityLocalDataSource)
    private val weatherRepository: WeatherRepository = WeatherRepositoryImpl(weatherRemoteDataSource)

    // Use Cases
    private val getAllCitiesUseCase = GetAllCitiesUseCase(cityRepository)
    private val createCityUseCase = CreateCityUseCase(cityRepository)
    private val deleteCityUseCase = DeleteCityUseCase(cityRepository)
    private val getWeatherUseCase = GetWeatherUseCase(weatherRepository)

    // ViewModels Factories
    val cityViewModelFactory = CityViewModelFactory(
        getAllCitiesUseCase,
        createCityUseCase,
        deleteCityUseCase
    )

    val weatherViewModelFactory = WeatherViewModelFactory(getWeatherUseCase)
}
