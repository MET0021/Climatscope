package com.myapp.climatscope.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.myapp.climatscope.domain.entities.City
import com.myapp.climatscope.presentation.theme.ClimatScopeTheme
import com.myapp.climatscope.presentation.viewmodels.CityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityScreen(
    onCityClick: (City) -> Unit,
    viewModel: CityViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mes Villes") },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Ajouter une ville")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.cities.isEmpty() -> {
                    EmptyStateMessage()
                }
                else -> {
                    CityList(
                        cities = uiState.cities,
                        onCityClick = onCityClick,
                        onDeleteCity = { city -> viewModel.deleteCity(city) }
                    )
                }
            }
        }

        // Gestion des erreurs
        uiState.errorMessage?.let { message ->
            LaunchedEffect(message) {
                // Afficher le message d'erreur
                viewModel.clearError()
            }
        }
    }

    // Dialog d'ajout de ville
    if (showAddDialog) {
        AddCityDialog(
            onDismiss = { showAddDialog = false },
            onAddCity = { cityName ->
                viewModel.createCity(cityName)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun EmptyStateMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Aucune ville ajoutée",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Appuyez sur + pour ajouter votre première ville",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CityList(
    cities: List<City>,
    onCityClick: (City) -> Unit,
    onDeleteCity: (City) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cities) { city ->
            CityItem(
                city = city,
                onClick = { onCityClick(city) },
                onDelete = { onDeleteCity(city) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CityItem(
    city: City,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = city.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Supprimer ${city.name}",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun AddCityDialog(
    onDismiss: () -> Unit,
    onAddCity: (String) -> Unit
) {
    var cityName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter une ville") },
        text = {
            OutlinedTextField(
                value = cityName,
                onValueChange = { cityName = it },
                label = { Text("Nom de la ville") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (cityName.isNotBlank()) {
                        onAddCity(cityName.trim())
                    }
                }
            ) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

// ========== PREVIEWS ==========

class CityPreviewParameterProvider : PreviewParameterProvider<List<City>> {
    override val values = sequenceOf(
        // Liste vide
        emptyList(),
        // Une ville
        listOf(City(1, "Paris")),
        // Plusieurs villes
        listOf(
            City(1, "Paris"),
            City(2, "London"),
            City(3, "New York"),
            City(4, "Tokyo"),
            City(5, "Berlin")
        )
    )
}

@Preview(
    name = "City Screen - Light",
    showBackground = true
)
@Composable
private fun CityScreenPreview() {
    ClimatScopeTheme {
        CityScreenContent(
            cities = listOf(
                City(1, "Paris"),
                City(2, "London"),
                City(3, "New York")
            ),
            isLoading = false,
            onCityClick = {},
            onDeleteCity = {},
            onAddCityClick = {}
        )
    }
}

@Preview(
    name = "City Screen - Dark",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun CityScreenDarkPreview() {
    ClimatScopeTheme(darkTheme = true) {
        CityScreenContent(
            cities = listOf(
                City(1, "Paris"),
                City(2, "London"),
                City(3, "New York")
            ),
            isLoading = false,
            onCityClick = {},
            onDeleteCity = {},
            onAddCityClick = {}
        )
    }
}

@Preview(
    name = "Empty State",
    showBackground = true
)
@Composable
private fun EmptyStatePreview() {
    ClimatScopeTheme {
        CityScreenContent(
            cities = emptyList(),
            isLoading = false,
            onCityClick = {},
            onDeleteCity = {},
            onAddCityClick = {}
        )
    }
}

@Preview(
    name = "Loading State",
    showBackground = true
)
@Composable
private fun LoadingStatePreview() {
    ClimatScopeTheme {
        CityScreenContent(
            cities = emptyList(),
            isLoading = true,
            onCityClick = {},
            onDeleteCity = {},
            onAddCityClick = {}
        )
    }
}

@Preview(
    name = "City Item",
    showBackground = true
)
@Composable
private fun CityItemPreview() {
    ClimatScopeTheme {
        CityItem(
            city = City(1, "Paris"),
            onClick = {},
            onDelete = {}
        )
    }
}

@Preview(
    name = "City List",
    showBackground = true,
    heightDp = 400
)
@Composable
private fun CityListPreview(@PreviewParameter(CityPreviewParameterProvider::class) cities: List<City>) {
    ClimatScopeTheme {
        CityList(
            cities = cities,
            onCityClick = {},
            onDeleteCity = {}
        )
    }
}

@Preview(
    name = "Add City Dialog",
    showBackground = true
)
@Composable
private fun AddCityDialogPreview() {
    ClimatScopeTheme {
        AddCityDialog(
            onDismiss = {},
            onAddCity = {}
        )
    }
}

// Composant séparé pour les previews (sans ViewModel)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CityScreenContent(
    cities: List<City>,
    isLoading: Boolean,
    onCityClick: (City) -> Unit,
    onDeleteCity: (City) -> Unit,
    onAddCityClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mes Villes") },
                actions = {
                    IconButton(onClick = onAddCityClick) {
                        Icon(Icons.Default.Add, contentDescription = "Ajouter une ville")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                cities.isEmpty() -> {
                    EmptyStateMessage()
                }
                else -> {
                    CityList(
                        cities = cities,
                        onCityClick = onCityClick,
                        onDeleteCity = onDeleteCity
                    )
                }
            }
        }
    }
}
