package com.myapp.climatscope.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Couleurs modernes et vibrants
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6366F1), // Indigo moderne
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF312E81),
    secondary = Color(0xFF10B981), // Emerald
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1FAE5),
    onSecondaryContainer = Color(0xFF064E3B),
    tertiary = Color(0xFFF59E0B), // Amber
    onTertiary = Color.White,
    surface = Color(0xFFFAFAFA),
    onSurface = Color(0xFF1F2937),
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF6B7280),
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1F2937),
    error = Color(0xFFEF4444),
    onError = Color.White,
    outline = Color(0xFFD1D5DB)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF818CF8), // Indigo clair
    onPrimary = Color(0xFF1E1B4B),
    primaryContainer = Color(0xFF3730A3),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = Color(0xFF34D399), // Emerald clair
    onSecondary = Color(0xFF022C22),
    secondaryContainer = Color(0xFF047857),
    onSecondaryContainer = Color(0xFFD1FAE5),
    tertiary = Color(0xFFFBBF24), // Amber clair
    onTertiary = Color(0xFF92400E),
    surface = Color(0xFF111827),
    onSurface = Color(0xFFE5E7EB),
    surfaceVariant = Color(0xFF1F2937),
    onSurfaceVariant = Color(0xFF9CA3AF),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFE5E7EB),
    error = Color(0xFFF87171),
    onError = Color(0xFF7F1D1D),
    outline = Color(0xFF374151)
)

@Composable
fun ClimatScopeTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// ========== THEME PREVIEWS ==========

@Preview(
    name = "Colors - Light Theme",
    showBackground = true,
    widthDp = 360,
    heightDp = 400
)
@Composable
private fun LightThemeColorsPreview() {
    ClimatScopeTheme(darkTheme = false) {
        ThemeColorsPalette()
    }
}

@Preview(
    name = "Colors - Dark Theme",
    showBackground = true,
    widthDp = 360,
    heightDp = 400,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun DarkThemeColorsPreview() {
    ClimatScopeTheme(darkTheme = true) {
        ThemeColorsPalette()
    }
}

@Preview(
    name = "Typography",
    showBackground = true
)
@Composable
private fun TypographyPreview() {
    ClimatScopeTheme {
        TypographyShowcase()
    }
}

@Preview(
    name = "Material Components",
    showBackground = true
)
@Composable
private fun MaterialComponentsPreview() {
    ClimatScopeTheme {
        MaterialComponentsShowcase()
    }
}

@Composable
private fun ThemeColorsPalette() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Palette de Couleurs",
            style = MaterialTheme.typography.headlineSmall
        )

        ColorCard("Primary", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
        ColorCard("Secondary", MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary)
        ColorCard("Surface", MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.onSurface)
        ColorCard("Background", MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.onBackground)
        ColorCard("Error", MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.onError)
    }
}

@Composable
private fun ColorCard(name: String, backgroundColor: Color, contentColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth().height(60.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = androidx.compose.ui.Alignment.CenterStart
        ) {
            Text(
                text = name,
                color = contentColor,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun TypographyShowcase() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Display Large", style = MaterialTheme.typography.displayLarge)
        Text("Headline Large", style = MaterialTheme.typography.headlineLarge)
        Text("Title Large", style = MaterialTheme.typography.titleLarge)
        Text("Body Large", style = MaterialTheme.typography.bodyLarge)
        Text("Body Medium", style = MaterialTheme.typography.bodyMedium)
        Text("Label Small", style = MaterialTheme.typography.labelSmall)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MaterialComponentsShowcase() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Composants Material3",
            style = MaterialTheme.typography.headlineSmall
        )

        Button(onClick = {}) {
            Text("Button Primary")
        }

        OutlinedButton(onClick = {}) {
            Text("Outlined Button")
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Ceci est une Card Material3",
                modifier = Modifier.padding(16.dp)
            )
        }

        OutlinedTextField(
            value = "Exemple de texte",
            onValueChange = {},
            label = { Text("TextField") }
        )
    }
}
