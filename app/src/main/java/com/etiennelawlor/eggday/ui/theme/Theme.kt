package com.etiennelawlor.eggday.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Built around the launcher icon's tan (#E5C2A3), the color of a brown egg shell.
val EggDayLightColors = lightColorScheme(
    primary = Color(0xFF7A5638),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE5C2A3),
    onPrimaryContainer = Color(0xFF2E1A08),
    secondary = Color(0xFF6E5B4C),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF8DECB),
    onSecondaryContainer = Color(0xFF27190E),
    background = Color(0xFFFFF8F4),
    onBackground = Color(0xFF211A15),
    surface = Color(0xFFFFF8F4),
    onSurface = Color(0xFF211A15),
    surfaceVariant = Color(0xFFF2DFD3),
    onSurfaceVariant = Color(0xFF51443B),
    surfaceContainer = Color(0xFFF7ECE5),
    surfaceContainerHigh = Color(0xFFF1E6DF),
    outline = Color(0xFF84746A),
)

val EggDayDarkColors = darkColorScheme(
    primary = Color(0xFFEBBD97),
    onPrimary = Color(0xFF46290F),
    primaryContainer = Color(0xFF603F23),
    onPrimaryContainer = Color(0xFFFFDCC2),
    secondary = Color(0xFFDBC2AF),
    onSecondary = Color(0xFF3D2D21),
    secondaryContainer = Color(0xFF554336),
    onSecondaryContainer = Color(0xFFF8DECB),
    background = Color(0xFF19120D),
    onBackground = Color(0xFFEFE0D7),
    surface = Color(0xFF19120D),
    onSurface = Color(0xFFEFE0D7),
    surfaceVariant = Color(0xFF51443B),
    onSurfaceVariant = Color(0xFFD6C3B7),
    surfaceContainer = Color(0xFF261E19),
    surfaceContainerHigh = Color(0xFF312823),
    outline = Color(0xFF9E8E83),
)

@Composable
fun EggDayTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) EggDayDarkColors else EggDayLightColors,
        content = content,
    )
}
