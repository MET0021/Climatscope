package com.myapp.climatscope.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.myapp.climatscope.domain.entities.Weather
import com.myapp.climatscope.presentation.theme.ClimatScopeTheme
import com.myapp.climatscope.presentation.viewmodels.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    cityName: String,
    onBackClick: () -> Unit,
    viewModel: WeatherViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Charger la météo au premier affichage
    LaunchedEffect(cityName) {
        viewModel.loadWeatherForCity(cityName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cityName) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshWeather() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualiser")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.weather != null -> {
                    WeatherContent(
                        weather = uiState.weather!!,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                uiState.errorMessage != null -> {
                    ErrorMessage(
                        message = uiState.errorMessage!!,
                        onRetry = { viewModel.loadWeatherForCity(cityName) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    // Gestion des erreurs avec SnackBar
    uiState.errorMessage?.let { message ->
        LaunchedEffect(message) {
            viewModel.clearError()
        }
    }
}

@Composable
private fun WeatherContent(
    weather: Weather,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Icône météo
        AsyncImage(
            model = weather.iconUrl,
            contentDescription = "Icône météo",
            modifier = Modifier.size(120.dp)
        )

        // Description
        Text(
            text = weather.description.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )

        // Température principale
        Text(
            text = "${weather.temperature.toInt()}°C",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        // Détails météo
        WeatherDetailsCard(weather = weather)
    }
}

@Composable
private fun WeatherDetailsCard(weather: Weather) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Détails",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            WeatherDetailRow(
                label = "Humidité",
                value = "${weather.humidity}%"
            )

            WeatherDetailRow(
                label = "Pression",
                value = "${weather.pressure} hPa"
            )
        }
    }
}

@Composable
private fun WeatherDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Erreur",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRetry) {
            Text("Réessayer")
        }
    }
}

// ========== PREVIEWS ==========

class WeatherPreviewParameterProvider : PreviewParameterProvider<Weather> {
    override val values = sequenceOf(
        // Temps ensoleillé
        Weather(
            description = "ensoleillé",
            temperature = 25.5,
            humidity = 60,
            pressure = 1013,
            iconUrl = "https://openweathermap.org/img/wn/01d.png"
        ),
        // Temps pluvieux
        Weather(
            description = "pluie légère",
            temperature = 15.2,
            humidity = 85,
            pressure = 1008,
            iconUrl = "https://openweathermap.org/img/wn/10d.png"
        ),
        // Temps neigeux
        Weather(
            description = "neige",
            temperature = -2.0,
            humidity = 95,
            pressure = 1020,
            iconUrl = "https://openweathermap.org/img/wn/13d.png"
        ),
        // Temps très chaud
        Weather(
            description = "ciel dégagé",
            temperature = 42.0,
            humidity = 25,
            pressure = 1005,
            iconUrl = "https://openweathermap.org/img/wn/01d.png"
        )
    )
}

@Preview(
    name = "Weather Screen - Light",
    showBackground = true
)
@Composable
private fun WeatherScreenPreview() {
    ClimatScopeTheme {
        WeatherScreenContent(
            cityName = "Paris",
            weather = Weather(
                description = "ensoleillé",
                temperature = 25.5,
                humidity = 60,
                pressure = 1013,
                iconUrl = "https://openweathermap.org/img/wn/01d.png"
            ),
            isLoading = false,
            errorMessage = null,
            onBackClick = {},
            onRefreshClick = {},
            onRetry = {}
        )
    }
}

@Preview(
    name = "Weather Screen - Dark",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun WeatherScreenDarkPreview() {
    ClimatScopeTheme(darkTheme = true) {
        WeatherScreenContent(
            cityName = "Londres",
            weather = Weather(
                description = "nuageux",
                temperature = 18.0,
                humidity = 75,
                pressure = 1010,
                iconUrl = "https://openweathermap.org/img/wn/04d.png"
            ),
            isLoading = false,
            errorMessage = null,
            onBackClick = {},
            onRefreshClick = {},
            onRetry = {}
        )
    }
}

@Preview(
    name = "Loading State",
    showBackground = true
)
@Composable
private fun WeatherLoadingPreview() {
    ClimatScopeTheme {
        WeatherScreenContent(
            cityName = "Tokyo",
            weather = null,
            isLoading = true,
            errorMessage = null,
            onBackClick = {},
            onRefreshClick = {},
            onRetry = {}
        )
    }
}

@Preview(
    name = "Error State",
    showBackground = true
)
@Composable
private fun WeatherErrorPreview() {
    ClimatScopeTheme {
        WeatherScreenContent(
            cityName = "InvalidCity",
            weather = null,
            isLoading = false,
            errorMessage = "Impossible de récupérer les données météo pour cette ville",
            onBackClick = {},
            onRefreshClick = {},
            onRetry = {}
        )
    }
}

@Preview(
    name = "Weather Content",
    showBackground = true
)
@Composable
private fun WeatherContentPreview(@PreviewParameter(WeatherPreviewParameterProvider::class) weather: Weather) {
    ClimatScopeTheme {
        WeatherContent(
            weather = weather,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(
    name = "Weather Details Card",
    showBackground = true
)
@Composable
private fun WeatherDetailsCardPreview() {
    ClimatScopeTheme {
        WeatherDetailsCard(
            weather = Weather(
                description = "ensoleillé",
                temperature = 25.5,
                humidity = 60,
                pressure = 1013,
                iconUrl = "https://openweathermap.org/img/wn/01d.png"
            )
        )
    }
}

@Preview(
    name = "Error Message",
    showBackground = true
)
@Composable
private fun ErrorMessagePreview() {
    ClimatScopeTheme {
        ErrorMessage(
            message = "Impossible de charger les données météo. Vérifiez votre connexion internet.",
            onRetry = {}
        )
    }
}

@Preview(
    name = "Weather Detail Row",
    showBackground = true
)
@Composable
private fun WeatherDetailRowPreview() {
    ClimatScopeTheme {
        Column {
            WeatherDetailRow(label = "Humidité", value = "65%")
            WeatherDetailRow(label = "Pression", value = "1013 hPa")
            WeatherDetailRow(label = "Température ressentie", value = "28°C")
        }
    }
}

// Composant séparé pour les previews (sans ViewModel)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeatherScreenContent(
    cityName: String,
    weather: Weather?,
    isLoading: Boolean,
    errorMessage: String?,
    onBackClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cityName) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(onClick = onRefreshClick) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualiser")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                weather != null -> {
                    WeatherContent(
                        weather = weather,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                errorMessage != null -> {
                    ErrorMessage(
                        message = errorMessage,
                        onRetry = onRetry,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
