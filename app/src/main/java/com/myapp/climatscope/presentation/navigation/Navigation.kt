package com.myapp.climatscope.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.myapp.climatscope.App
import com.myapp.climatscope.presentation.screens.CityScreen
import com.myapp.climatscope.presentation.screens.WeatherScreen
import com.myapp.climatscope.presentation.viewmodels.CityViewModel
import com.myapp.climatscope.presentation.viewmodels.WeatherViewModel

sealed class Screen(val route: String) {
    object Cities : Screen("cities")
    object Weather : Screen("weather/{cityName}") {
        fun createRoute(cityName: String) = "weather/$cityName"
    }
}

@Composable
fun ClimatScopeNavigation(
    navController: NavHostController = rememberNavController(),
    app: App
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Cities.route
    ) {
        composable(Screen.Cities.route) {
            val cityViewModel: CityViewModel = viewModel(
                factory = app.dependencyContainer.cityViewModelFactory
            )

            CityScreen(
                viewModel = cityViewModel,
                onCityClick = { city ->
                    navController.navigate(Screen.Weather.createRoute(city.name))
                }
            )
        }

        composable(Screen.Weather.route) { backStackEntry ->
            val cityName = backStackEntry.arguments?.getString("cityName") ?: ""
            val weatherViewModel: WeatherViewModel = viewModel(
                factory = app.dependencyContainer.weatherViewModelFactory
            )

            WeatherScreen(
                cityName = cityName,
                viewModel = weatherViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
