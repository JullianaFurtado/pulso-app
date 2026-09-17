package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Modos de tema suportados pelo aplicativo.
 */
enum class AppThemeMode(val title: String, val description: String) {
    SYSTEM("Sistema", "Acompanha a configuração do aparelho"),
    LIGHT("Claro", "Visual fresco e limpo"),
    DARK("Escuro", "Tons modernos em azul escuro e ardósia"),
    AMOLED("AMOLED", "Preto puro (#000000) e máxima economia de bateria"),
    SUNSET("Pôr do Sol", "Tons quentes e acolhedores em laranja e coral")
}

private val DarkColorScheme = darkColorScheme(
    primary = PulsoTealLight,
    onPrimary = Color(0xFF003730),
    primaryContainer = PulsoTealDark,
    onPrimaryContainer = Color(0xFFB2DFDB),
    secondary = PulsoCoralLight,
    onSecondary = Color(0xFF5A1410),
    tertiary = PulsoCyan,
    background = PulsoBackgroundDark,
    surface = PulsoSurfaceDark,
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF24334A),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = PulsoCardBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = PulsoTeal,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2F1),
    onPrimaryContainer = PulsoTealDark,
    secondary = PulsoCoral,
    onSecondary = Color.White,
    tertiary = PulsoCyan,
    background = PulsoBackgroundLight,
    surface = PulsoSurfaceLight,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = PulsoCardBorderLight
)

// AMOLED Theme Scheme (Preto absoluto #000000 + alto contraste e eficiência energética)
private val AmoledColorScheme = darkColorScheme(
    primary = PulsoAmoledPrimary,
    onPrimary = PulsoAmoledOnPrimary,
    primaryContainer = Color(0xFF004D54),
    onPrimaryContainer = Color(0xFFE0FBFC),
    secondary = PulsoCoralLight,
    onSecondary = Color(0xFF5A1410),
    tertiary = PulsoCyan,
    background = PulsoAmoledBackground,
    surface = PulsoAmoledSurface,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = PulsoAmoledSurfaceVariant,
    onSurfaceVariant = Color(0xFFE2E8F0),
    outline = PulsoAmoledOutline
)

// Sunset Warm Scheme (Tons solares e acolhedores)
private val SunsetColorScheme = lightColorScheme(
    primary = PulsoSunsetPrimary,
    onPrimary = Color.White,
    primaryContainer = PulsoSunsetSurfaceVariant,
    onPrimaryContainer = Color(0xFF7C2D12),
    secondary = PulsoSunsetSecondary,
    onSecondary = Color.White,
    tertiary = PulsoAmber,
    background = PulsoSunsetBackgroundLight,
    surface = PulsoSunsetSurfaceLight,
    onBackground = Color(0xFF1C1917),
    onSurface = Color(0xFF1C1917),
    surfaceVariant = PulsoSunsetSurfaceVariant,
    onSurfaceVariant = Color(0xFF57534E),
    outline = PulsoSunsetOutline
)

@Composable
fun PulsoTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val isEffectiveDark = when (themeMode) {
        AppThemeMode.SYSTEM -> darkTheme
        AppThemeMode.DARK, AppThemeMode.AMOLED -> true
        AppThemeMode.LIGHT, AppThemeMode.SUNSET -> false
    }

    val colorScheme: ColorScheme = when (themeMode) {
        AppThemeMode.AMOLED -> AmoledColorScheme
        AppThemeMode.SUNSET -> SunsetColorScheme
        AppThemeMode.LIGHT -> LightColorScheme
        AppThemeMode.DARK -> DarkColorScheme
        AppThemeMode.SYSTEM -> {
            if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (isEffectiveDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                if (isEffectiveDark) DarkColorScheme else LightColorScheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * Compatibilidade com testes existentes.
 */
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    PulsoTheme(
        themeMode = if (darkTheme) AppThemeMode.DARK else AppThemeMode.LIGHT,
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        content = content
    )
}
