package cz.ukh.fim.kumte.cryptotracker.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CryptoPrimaryDark,
    onPrimary = CryptoTextPrimaryDark,
    background = CryptoBackgroundDark,
    onBackground = CryptoTextPrimaryDark,
    tertiary = CryptoTopAppBarDark,
    surface = CryptoSurfaceDark,
    onSurface = CryptoTextPrimaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = CryptoPrimaryLight,
    onPrimary = CryptoTextPrimaryLight,
    background = CryptoBackgroundLight,
    onBackground = CryptoTextPrimaryLight,
    tertiary = CryptoTopAppBarLight,
    surface = CryptoSurfaceLight,
    onSurface = CryptoTextPrimaryLight
)

@Composable
fun KUMTE_CryptoTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Nepoužíváme, vlastní barvy mají přednost
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
