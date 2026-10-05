package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = RoyalDarkPrimary,
    onPrimary = RoyalDarkOnPrimary,
    primaryContainer = RoyalDarkPrimaryContainer,
    onPrimaryContainer = RoyalDarkOnPrimaryContainer,
    secondary = RoyalDarkSecondary,
    onSecondary = RoyalDarkOnSecondary,
    secondaryContainer = RoyalDarkSecondaryContainer,
    onSecondaryContainer = RoyalDarkOnSecondaryContainer,
    tertiary = RoyalDarkTertiary,
    onTertiary = RoyalDarkOnTertiary,
    tertiaryContainer = RoyalDarkTertiaryContainer,
    onTertiaryContainer = RoyalDarkOnTertiaryContainer,
    background = RoyalDarkBackground,
    onBackground = RoyalDarkOnBackground,
    surface = RoyalDarkSurface,
    onSurface = RoyalDarkOnSurface,
    surfaceVariant = RoyalDarkSurfaceVariant,
    onSurfaceVariant = RoyalDarkOnSurfaceVariant
)

private val LightColorScheme = lightColorScheme(
    primary = RoyalLightPrimary,
    onPrimary = RoyalLightOnPrimary,
    primaryContainer = RoyalLightPrimaryContainer,
    onPrimaryContainer = RoyalLightOnPrimaryContainer,
    secondary = RoyalLightSecondary,
    onSecondary = RoyalLightOnSecondary,
    secondaryContainer = RoyalLightSecondaryContainer,
    onSecondaryContainer = RoyalLightOnSecondaryContainer,
    tertiary = RoyalLightTertiary,
    onTertiary = RoyalLightOnTertiary,
    tertiaryContainer = RoyalLightTertiaryContainer,
    onTertiaryContainer = RoyalLightOnTertiaryContainer,
    background = RoyalLightBackground,
    onBackground = RoyalLightOnBackground,
    surface = RoyalLightSurface,
    onSurface = RoyalLightOnSurface,
    surfaceVariant = RoyalLightSurfaceVariant,
    onSurfaceVariant = RoyalLightOnSurfaceVariant
)

@Composable
fun RoyalRouteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep signature royal brand theme consistent
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
