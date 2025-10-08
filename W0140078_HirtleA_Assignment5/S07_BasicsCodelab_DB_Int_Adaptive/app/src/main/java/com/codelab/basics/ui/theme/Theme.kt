package com.codelab.basics.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat

// DARK: Futuristic default (recommended)
private val DarkColorScheme = darkColorScheme(
    primary        = PokeBlue,        // main action color (logo blue)
    onPrimary      = PokeWhite,
    primaryContainer = DarkSurfaceVar, // for large buttons / cards
    onPrimaryContainer = PokeWhite,

    secondary      = PikaYellow,      // fun accent (sparks / highlights)
    onSecondary    = PokeBlack,
    secondaryContainer = DarkSurfaceHi,
    onSecondaryContainer = PikaYellow,

    tertiary       = NeonCyan,        // neon effect (toggles, chips)
    onTertiary     = PokeBlack,
    tertiaryContainer = DarkSurfaceVar,
    onTertiaryContainer = NeonCyan,

    background     = DarkSurface,
    onBackground   = DarkOn,
    surface        = DarkSurfaceHi,
    onSurface      = DarkOn,
    surfaceVariant = DarkSurfaceVar,
    onSurfaceVariant = DarkOn,

    outline        = HoloEdge,
    inversePrimary = NeonPurple       // for inverse surfaces (e.g., snackbars)
)

// LIGHT: Still brand-true but clean
private val LightColorScheme = lightColorScheme(
    primary        = PokeRed,
    onPrimary      = PokeWhite,
    primaryContainer = PokeRed.copy(alpha = 0.12f),
    onPrimaryContainer = PokeRed,

    secondary      = PokeBlue,
    onSecondary    = PokeWhite,
    secondaryContainer = PokeBlue.copy(alpha = 0.12f),
    onSecondaryContainer = PokeBlue,

    tertiary       = NeonPink,
    onTertiary     = PokeWhite,
    tertiaryContainer = NeonPink.copy(alpha = 0.12f),
    onTertiaryContainer = NeonPink,

    background     = PokeWhite,
    onBackground   = LightOn,
    surface        = LightSurface,
    onSurface      = LightOn,
    surfaceVariant = PokeWhite.copy(alpha = 0.7f),
    onSurfaceVariant = LightOn,

    outline        = HoloEdge,
    inversePrimary = NeonCyan
)

@Composable
fun BasicsCodelabTheme(
    // Futuristic apps tend to look best dark; still follow system by default.
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Keep off so branding stays Pokémon, not Material You.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme =
        if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        } else {
            if (darkTheme) DarkColorScheme else LightColorScheme
        }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            // Dark mode -> light icons, Light mode -> dark icons
            ViewCompat.getWindowInsetsController(view)
                ?.isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
