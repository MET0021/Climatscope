package com.myapp.climatscope.presentation.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.myapp.climatscope.ClimatScopeApplication
import com.myapp.climatscope.domain.entities.City
import com.myapp.climatscope.domain.entities.Weather
import com.myapp.climatscope.presentation.components.*
import com.myapp.climatscope.presentation.theme.ClimatScopeTheme
import com.myapp.climatscope.presentation.viewmodels.CityViewModel
import com.myapp.climatscope.presentation.viewmodels.WeatherViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    weatherViewModel: WeatherViewModel,
    cityViewModel: CityViewModel = viewModel()
) {
    val cityUiState by cityViewModel.uiState.collectAsState()
    val weatherUiState by weatherViewModel.uiState.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val app = context.applicationContext as ClimatScopeApplication
    val scope = rememberCoroutineScope()

    // Gestionnaire de permissions de géolocalisation
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            scope.launch {
                try {
                    val result = app.dependencyContainer.getWeatherByLocationUseCase().invoke()
                    result.fold(
                        onSuccess = { (weather, _) ->
                            weatherViewModel.setLocationWeather(
                                weather,
                                "Ma position"
                            )
                        },
                        onFailure = { error ->
                            println("Erreur de géolocalisation: ${error.message}")
                            weatherViewModel.loadWeatherForCity("Paris")
                        }
                    )
                } catch (e: Exception) {
                    println("Erreur lors de la récupération de la localisation: ${e.message}")
                    weatherViewModel.loadWeatherForCity("Paris")
                }
            }
        }
    }

    // Chargement initial des données
    LaunchedEffect(Unit) {
        cityViewModel.loadCities()

        // Charger une ville par défaut si aucune donnée
        if (weatherUiState.weather == null && !weatherUiState.isLoading) {
            weatherViewModel.loadWeatherForCity("Paris")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        // Top Bar
        TopBar(
            onLocationClick = {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            onCitiesClick = { showBottomSheet = true }
        )

        // Contenu principal
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Carte météo principale
            item {
                when {
                    weatherUiState.isLoading -> {
                        LoadingCard()
                    }

                    weatherUiState.errorMessage != null -> {
                        val errorMessage = weatherUiState.errorMessage!! // Force unwrap car on vérifie != null
                        ErrorCard(
                            errorMessage = errorMessage,
                            onRetry = { weatherViewModel.loadWeatherForCity("Paris") }
                        )
                    }

                    weatherUiState.weather != null -> {
                        val weather = weatherUiState.weather!! // Force unwrap car on vérifie != null
                        WeatherCard(
                            weather = weather,
                            cityName = weatherUiState.cityName.ifEmpty { "Ma position" },
                            onRefresh = {
                                if (weatherUiState.cityName.isNotEmpty()) {
                                    weatherViewModel.loadWeatherForCity(weatherUiState.cityName)
                                }
                            }
                        )
                    }

                    else -> {
                        EmptyStateCard()
                    }
                }
            }

            // Indicateurs météo détaillés
            weatherUiState.weather?.let { weather ->
                if (!weatherUiState.isLoading) {
                    item {
                        WeatherIndicators(weather = weather)
                    }
                }
            }

            // Liste des villes favorites
            item {
                CitiesSection(
                    cities = cityUiState.cities,
                    onCityClick = { city ->
                        weatherViewModel.loadWeatherForCity(city.name)
                    },
                    onAddCityClick = { showBottomSheet = true }
                )
            }
        }
    }

    // Bottom Sheet pour la gestion des villes
    if (showBottomSheet) {
        CityBottomSheet(
            cities = cityUiState.cities,
            isLoading = cityUiState.isLoading,
            onCitySelected = { city ->
                weatherViewModel.loadWeatherForCity(city.name)
                showBottomSheet = false
            },
            onAddCity = { cityName ->
                cityViewModel.createCity(cityName)
            },
            onDeleteCity = { city ->
                cityViewModel.deleteCity(city)
            },
            onDismiss = { showBottomSheet = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onLocationClick: () -> Unit,
    onCitiesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = "ClimatScope",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(onClick = onLocationClick) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Ma position",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onCitiesClick) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "Mes villes"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        modifier = modifier
    )
}

@Composable
private fun LoadingCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Chargement de la météo...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorCard(
    errorMessage: String?,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Erreur de chargement",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = errorMessage ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Réessayer")
            }
        }
    }
}

@Composable
private fun WeatherCard(
    weather: Weather,
    cityName: String,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = cityName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = weather.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Actualiser"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "${weather.temperature.toInt()}°",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Ressenti ${weather.feelsLike.toInt()}°",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WeatherIndicators(
    weather: Weather,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        WeatherIndicatorCard(
            title = "Humidité",
            value = "${weather.humidity}%",
            icon = Icons.Default.Opacity,
            modifier = Modifier.weight(1f)
        )

        WeatherIndicatorCard(
            title = "Pression",
            value = "${weather.pressure} hPa",
            icon = Icons.Default.BarChart,
            modifier = Modifier.weight(1f)
        )

        WeatherIndicatorCard(
            title = "Vent",
            value = "${weather.windSpeed.toInt()} km/h",
            icon = Icons.Default.Air,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun WeatherIndicatorCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CitiesSection(
    cities: List<City>,
    onCityClick: (City) -> Unit,
    onAddCityClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mes villes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            TextButton(onClick = onAddCityClick) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ajouter")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (cities.isEmpty()) {
            EmptyStateCard()
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cities) { city ->
                    CityCard(
                        city = city,
                        onClick = { onCityClick(city) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Cloud,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Aucune ville ajoutée",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Ajoutez vos villes favorites pour suivre leur météo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CityCard(
    city: City,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.width(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.LocationCity,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = city.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun CityBottomSheet(
    cities: List<City>,
    isLoading: Boolean,
    onCitySelected: (City) -> Unit,
    onAddCity: (String) -> Unit,
    onDeleteCity: (City) -> Unit,
    onDismiss: () -> Unit
) {
    // Utilisation du composant EnhancedCityBottomSheet existant
    EnhancedCityBottomSheet(
        cities = cities,
        isLoading = isLoading,
        onCitySelected = onCitySelected,
        onAddCity = onAddCity,
        onDeleteCity = onDeleteCity,
        onDismiss = onDismiss
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ClimatScopeTheme {
        // Preview avec des données factices
    }
}
