package com.myapp.climatscope.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.myapp.climatscope.domain.entities.City
import com.myapp.climatscope.domain.entities.Weather
import com.myapp.climatscope.presentation.theme.ClimatScopeTheme
import com.myapp.climatscope.data.remote.GeocodingResponse
import com.myapp.climatscope.presentation.viewmodels.CitySearchViewModel
import com.myapp.climatscope.ClimatScopeApplication

@Composable
fun WeatherAdviceCard(
    weather: Weather,
    modifier: Modifier = Modifier
) {
    val advice = getWeatherAdvice(weather)
    val adviceColor = getAdviceColor(weather)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = adviceColor.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(adviceColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getAdviceIcon(weather),
                    contentDescription = null,
                    tint = adviceColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Conseil mét��o",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = advice,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedCityBottomSheet(
    cities: List<City>,
    isLoading: Boolean,
    onCitySelected: (City) -> Unit,
    onAddCity: (String) -> Unit,
    onDeleteCity: (City) -> Unit,
    onDismiss: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        dragHandle = {
            Surface(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    Modifier
                        .size(width = 32.dp, height = 4.dp)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            // En-tête avec titre et bouton d'ajout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Mes villes",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${cities.size} ville${if (cities.size > 1) "s" else ""} ajoutée${if (cities.size > 1) "s" else ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                FilledIconButton(
                    onClick = { showAddDialog = true },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Ajouter une ville",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Contenu de la liste
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Chargement des villes...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                else -> {

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.heightIn(max = 400.dp)
                    ) {
                        items(
                            items = cities,
                            key = { it.id }
                        ) { city ->
                            EnhancedCityItemBottomSheet(
                                city = city,
                                onClick = { onCitySelected(city) },
                                onDelete = { onDeleteCity(city) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog d'ajout de ville
    if (showAddDialog) {
        AddCityDialog(
            onAddCity = { cityName ->
                onAddCity(cityName)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnhancedCityItem(
    city: City,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = city.name.first().uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column {
                    Text(
                        text = city.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Appuyez pour voir la météo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnhancedCityItemBottomSheet(
    city: City,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = city.name.first().uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column {
                    Text(
                        text = city.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Touchez pour voir la météo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onDelete,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Supprimer ${city.name}",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddCityDialog(
    onAddCity: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // Interface de recherche unifiée directement
    SmartCitySearchDialog(
        onCitySelected = { cityResponse ->
            onAddCity(cityResponse.name)
        },
        onDismiss = onDismiss
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SmartCitySearchDialog(
    onCitySelected: (GeocodingResponse) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as ClimatScopeApplication

    // Créer le ViewModel avec la factory du DependencyContainer
    val searchViewModel: CitySearchViewModel = viewModel(
        factory = app.dependencyContainer.getCitySearchViewModelFactory()
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // En-tête
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Rechercher une ville",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Trouvez n'importe quelle ville dans le monde",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Barre de recherche intelligente
                SmartCitySearchBar(
                    onCitySelected = onCitySelected,
                    onDismiss = onDismiss,
                    searchViewModel = searchViewModel,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private fun getWeatherAdvice(weather: Weather): String {
    return when {
        weather.temperature < 0 -> "Il fait très froid ! Couvrez-vous bien."
        weather.temperature < 10 -> "Température fraîche, pensez à prendre une veste."
        weather.temperature > 30 -> "Il fait chaud ! Restez hydraté et cherchez l'ombre."
        weather.humidity > 80 -> "Forte humidité, l'air peut sembler lourd."
        weather.windSpeed > 20 -> "Vent fort, attention aux objets qui peuvent voler."
        else -> "Conditions météo agréables !"
    }
}

private fun getAdviceColor(weather: Weather) = when {
    weather.temperature < 0 -> Color(0xFF4FC3F7) // Bleu clair pour le froid
    weather.temperature < 10 -> Color(0xFF81C784) // Vert pour frais
    weather.temperature > 30 -> Color(0xFFFF8A65) // Orange pour chaud
    weather.humidity > 80 -> Color(0xFF64B5F6) // Bleu pour humidité
    weather.windSpeed > 20 -> Color(0xFFAED581) // Vert clair pour vent
    else -> Color(0xFF4CAF50) // Vert pour normal
}

private fun getAdviceIcon(weather: Weather) = when {
    weather.temperature < 0 -> Icons.Default.AcUnit
    weather.temperature < 10 -> Icons.Default.DeviceThermostat
    weather.temperature > 30 -> Icons.Default.WbSunny
    weather.humidity > 80 -> Icons.Default.Opacity
    weather.windSpeed > 20 -> Icons.Default.Air
    else -> Icons.Default.CheckCircle
}

@Preview(showBackground = true)
@Composable
fun WeatherAdviceCardPreview() {
    ClimatScopeTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WeatherAdviceCard(
                weather = Weather(
                    temperature = 35.0,
                    description = "Ensoleillé",
                    humidity = 45,
                    pressure = 1013,
                    windSpeed = 10.0,
                    feelsLike = 38.0,
                    iconUrl = ""
                )
            )
        }
    }
}
