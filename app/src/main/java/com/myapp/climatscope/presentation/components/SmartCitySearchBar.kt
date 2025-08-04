package com.myapp.climatscope.presentation.components

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.myapp.climatscope.data.remote.dto.CitySearchResponse
import com.myapp.climatscope.presentation.viewmodels.CitySearchViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartCitySearchBar(
    onCitySelected: (CitySearchResponse) -> Unit,
    onDismiss: () -> Unit,
    searchViewModel: CitySearchViewModel,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val searchState by searchViewModel.searchState.collectAsState()

    // Déclencher la recherche avec un délai pour éviter trop d'appels API
    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 2) {
            delay(300) // Délai de 300ms
            searchViewModel.searchCities(searchQuery)
        } else {
            searchViewModel.clearResults()
        }
    }

    // Auto-focus quand le composant s'affiche
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(modifier = modifier) {
        // Barre de recherche principale
        DockedSearchBar(
            query = searchQuery,
            onQueryChange = {
                searchQuery = it
                isExpanded = it.isNotEmpty()
            },
            onSearch = { query ->
                if (query.isNotEmpty()) {
                    searchViewModel.searchCities(query)
                }
                keyboardController?.hide()
            },
            active = isExpanded,
            onActiveChange = { isExpanded = it },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            placeholder = {
                Text(
                    text = "Rechercher une ville...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Rechercher",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                Row {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                searchQuery = ""
                                searchViewModel.clearResults()
                            }
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Effacer",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                dividerColor = Color.Transparent
            )
        ) {
            SearchResultsContent(
                searchState = searchState,
                searchQuery = searchQuery,
                onCitySelected = { city ->
                    onCitySelected(city)
                    searchQuery = ""
                    isExpanded = false
                    keyboardController?.hide()
                },
                onRetry = {
                    searchViewModel.searchCities(searchQuery)
                }
            )
        }
    }
}

@Composable
private fun SearchResultsContent(
    searchState: CitySearchViewModel.SearchState,
    searchQuery: String,
    onCitySelected: (CitySearchResponse) -> Unit,
    onRetry: () -> Unit
) {
    Log.e("TAG", "TEST SearchResultsContent ----- $searchState ", )
    when {
        searchState.isLoading -> {
            LoadingSearchResults()
        }

        searchState.error != null -> {
            ErrorSearchResults(
                error = searchState.error,
                onRetry = onRetry
            )
        }

        searchState.cities.isEmpty() && searchQuery.length >= 2 -> {
            NoResultsFound(searchQuery = searchQuery)
        }

        searchState.cities.isNotEmpty() -> {
            CitySearchResults(
                cities = searchState.cities,
                onCitySelected = onCitySelected
            )
        }

        else -> {
            SearchInstructions()
        }
    }
}

@Composable
private fun LoadingSearchResults() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Recherche en cours...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorSearchResults(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Erreur de recherche",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = error,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onRetry) {
            Text("Réessayer")
        }
    }
}

@Composable
private fun NoResultsFound(searchQuery: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.SearchOff,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Aucun résultat trouvé",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "Aucune ville trouvée pour \"$searchQuery\"",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Essayez avec un autre nom de ville",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CitySearchResults(
    cities: List<CitySearchResponse>,
    onCitySelected: (CitySearchResponse) -> Unit
) {
    LazyColumn(
        modifier = Modifier.heightIn(max = 300.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(cities) { city ->
            CitySearchResultItem(
                city = city,
                onClick = { onCitySelected(city) }
            )
        }
    }
}

@Composable
private fun CitySearchResultItem(
    city: CitySearchResponse,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = city.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = city.getDisplayName(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            Icons.Default.ArrowForward,
            contentDescription = "Sélectionner",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun SearchInstructions() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.TravelExplore,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Recherchez une ville",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "Tapez au moins 2 caractères pour commencer la recherche",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
