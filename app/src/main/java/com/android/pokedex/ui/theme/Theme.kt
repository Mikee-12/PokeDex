package com.android.pokedex.ui.theme

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

// Pokemon-themed Light Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFDC0A2D), // Pokemon Red
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410002),

    secondary = Color(0xFFFFCB05), // Pokemon Yellow
    onSecondary = Color(0xFF3C3C3C),
    secondaryContainer = Color(0xFFFFF8E1),
    onSecondaryContainer = Color(0xFF1F1F1F),

    tertiary = Color(0xFF3B4CCA), // Pokemon Blue
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFDDE1FF),
    onTertiaryContainer = Color(0xFF001258),

    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),

    surfaceVariant = Color(0xFFE3F2FD),
    onSurfaceVariant = Color(0xFF49454F),

    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B)
)

// Pokemon-themed Dark Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF6B6B), // Lighter Pokemon Red
    onPrimary = Color(0xFF690005),
    primaryContainer = Color(0xFF93000A),
    onPrimaryContainer = Color(0xFFFFDAD6),

    secondary = Color(0xFFFFD54F), // Lighter Pokemon Yellow
    onSecondary = Color(0xFF3C3000),
    secondaryContainer = Color(0xFF554600),
    onSecondaryContainer = Color(0xFFFFF8E1),

    tertiary = Color(0xFF7B8CFF), // Lighter Pokemon Blue
    onTertiary = Color(0xFF002A77),
    tertiaryContainer = Color(0xFF1A3A9B),
    onTertiaryContainer = Color(0xFFDDE1FF),

    background = Color(0xFF121212),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),

    surfaceVariant = Color(0xFF2B2930),
    onSurfaceVariant = Color(0xFFCAC4D0),

    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC)
)

@Composable
fun PokedexTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic color untuk konsistensi tema Pokemon
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}