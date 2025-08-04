package com.myapp.climatscope.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.myapp.climatscope.ClimatScopeApplication
import com.myapp.climatscope.presentation.screens.*
import com.myapp.climatscope.presentation.viewmodels.CityViewModel
import com.myapp.climatscope.presentation.viewmodels.WeatherViewModel

sealed class Screen(val route: String) {
    object LocationPermission : Screen("location_permission")
    object Home : Screen("home")
    object CitySelection : Screen("city_selection")
    object Weather : Screen("weather/{cityName}") {
        fun createRoute(cityName: String) = "weather/$cityName"
    }
}

@Composable
fun ClimatScopeNavigation(
    navController: NavHostController = rememberNavController(),
    app: ClimatScopeApplication
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route // Démarrage direct sur l'écran principal
    ) {
        // Écran de demande de localisation (optionnel)
        composable(Screen.LocationPermission.route) {
            LocationPermissionScreen(
                onLocationGranted = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.LocationPermission.route) { inclusive = true }
                    }
                },
                onLocationDenied = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.LocationPermission.route) { inclusive = true }
                    }
                }
            )
        }

        // Écran principal simplifié
        composable(Screen.Home.route) {
            val weatherViewModel: WeatherViewModel = viewModel(
                factory = app.dependencyContainer.getWeatherViewModelFactory()
            )
            val cityViewModel: CityViewModel = viewModel(
                factory = app.dependencyContainer.getCityViewModelFactory()
            )

            HomeScreen(
                weatherViewModel = weatherViewModel,
                cityViewModel = cityViewModel
            )
        }

        // Écran de sélection des villes (optionnel)
        composable(Screen.CitySelection.route) {
            val cityViewModel: CityViewModel = viewModel(
                factory = app.dependencyContainer.getCityViewModelFactory()
            )
            val weatherViewModel: WeatherViewModel = viewModel(
                factory = app.dependencyContainer.getWeatherViewModelFactory()
            )

            CitySelectionScreen(
                cityViewModel = cityViewModel,
                onCitySelected = { city ->
                    weatherViewModel.loadWeatherForCity(city.name)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.CitySelection.route) { inclusive = true }
                    }
                },
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }

        // Écran détaillé d'une ville (optionnel)
        composable(Screen.Weather.route) { backStackEntry ->
            val cityName = backStackEntry.arguments?.getString("cityName") ?: ""
            val weatherViewModel: WeatherViewModel = viewModel(
                factory = app.dependencyContainer.getWeatherViewModelFactory()
            )

            WeatherScreen(
                cityName = cityName,
                onBackClick = {
                    navController.popBackStack()
                },
                viewModel = weatherViewModel
            )
        }
    }
}
