package com.myapp.climatscope.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.myapp.climatscope.domain.entities.Weather
import com.myapp.climatscope.presentation.theme.ClimatScopeTheme
import com.myapp.climatscope.presentation.viewmodels.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CleanWeatherScreen(
    weatherViewModel: WeatherViewModel = viewModel(),
    onChangeLocationClick: () -> Unit = {}
) {
    val weatherUiState by weatherViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp)) // Laisser place à la barre de statut système

        // Title
        Text(
            text = "ClimaSense",
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(60.dp))

        weatherUiState.weather?.let { weather ->
            // Weather Icon (placeholder for now - we'll use emoji or drawable)
            WeatherIcon(
                condition = weather.description,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Weather Description
            Text(
                text = weather.description.replaceFirstChar { it.uppercase() },
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Temperature
            Text(
                text = "${weather.temperature.toInt()}°C",
                fontSize = 64.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            // Feels like temperature
            Text(
                text = "ressenti ${weather.feelsLike.toInt()}°C",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Weather details row
            WeatherDetailsRow(
                windSpeed = weather.windSpeed,
                humidity = weather.humidity,
                uvIndex = "Modéré", // This would come from weather data
                pressure = weather.pressure
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Forecast section
            ForecastSection()

            Spacer(modifier = Modifier.weight(1f))

            // Change location button
            Card(
                onClick = onChangeLocationClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                border = null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Changer de ville",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } ?: run {
            // Loading state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun WeatherIcon(
    condition: String,
    modifier: Modifier = Modifier
) {
    // Weather icon based on condition - using emoji for now
    val icon = when {
        condition.contains("sunny", ignoreCase = true) ||
        condition.contains("clear", ignoreCase = true) ||
        condition.contains("ensoleillé", ignoreCase = true) -> "☀️"
        condition.contains("cloud", ignoreCase = true) ||
        condition.contains("nuageux", ignoreCase = true) -> "⛅"
        condition.contains("rain", ignoreCase = true) ||
        condition.contains("pluie", ignoreCase = true) -> "🌧️"
        condition.contains("storm", ignoreCase = true) ||
        condition.contains("orage", ignoreCase = true) -> "⛈️"
        condition.contains("snow", ignoreCase = true) ||
        condition.contains("neige", ignoreCase = true) -> "❄️"
        else -> "⛅"
    }

    Text(
        text = icon,
        fontSize = 80.sp,
        modifier = modifier,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun WeatherDetailsRow(
    windSpeed: Double,
    humidity: Int,
    uvIndex: String,
    pressure: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        WeatherDetailItem(
            label = "Vent:",
            value = "${windSpeed.toInt()} km/h"
        )
        WeatherDetailItem(
            label = "Humidité:",
            value = "$humidity %"
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        WeatherDetailItem(
            label = "UV:",
            value = uvIndex
        )
        WeatherDetailItem(
            label = "",
            value = pressure.toString()
        )
    }
}

@Composable
private fun WeatherDetailItem(
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun ForecastSection() {
    Column {
        Text(
            text = "Prévisions",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ForecastDay("Lun", "☀️", "25°C")
            ForecastDay("Mar", "☀️", "24°C")
            ForecastDay("Mer", "🌧️", "20°C")
            ForecastDay("Jeu", "⛈️", "22°C")
        }
    }
}

@Composable
private fun ForecastDay(
    day: String,
    icon: String,
    temperature: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = day,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = icon,
            fontSize = 24.sp
        )
        Text(
            text = temperature,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CleanWeatherScreenPreview() {
    ClimatScopeTheme {
        CleanWeatherScreen()
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CleanWeatherScreenDarkPreview() {
    ClimatScopeTheme {
        CleanWeatherScreen()
    }
}
