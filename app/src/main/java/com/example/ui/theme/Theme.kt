package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val ManifestationDarkColorScheme = darkColorScheme(
    primary = GoldSolar,
    onPrimary = Color(0xFF261800),
    primaryContainer = Color(0xFF422C00),
    onPrimaryContainer = GoldLight,
    secondary = CelestialIndigo,
    onSecondary = Color(0xFF1E0E57),
    secondaryContainer = Color(0xFF3B2E7C),
    onSecondaryContainer = Color(0xFFE5DFFF),
    tertiary = SolfeggioRose,
    onTertiary = Color(0xFF49001A),
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = CosmicBorder
)

private val ManifestationLightColorScheme = lightColorScheme(
    primary = GoldDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE8A3),
    onPrimaryContainer = Color(0xFF291800),
    secondary = CelestialDeep,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE5DFFF),
    onSecondaryContainer = Color(0xFF1B0E57),
    tertiary = SolfeggioRose,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = Color(0xFFCCC5DB)
)

@Composable
fun Affirmation369Theme(
    darkTheme: Boolean = true, // Celestial dark ambiance feels most transcendent for 369 rituals
    dynamicColor: Boolean = false, // Keep intentional golden/celestial styling
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> ManifestationDarkColorScheme
        else -> ManifestationLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
