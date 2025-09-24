package com.nexoft.phonebook.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Green500,
    onPrimary = White,
    primaryContainer = Green50,
    onPrimaryContainer = Green600,

    secondary = Gray700,
    onSecondary = White,
    secondaryContainer = Gray200,
    onSecondaryContainer = Gray900,

    tertiary = Green400,
    onTertiary = White,
    tertiaryContainer = Green50,
    onTertiaryContainer = Green600,

    error = RedDelete,
    onError = White,
    errorContainer = RedLight,
    onErrorContainer = RedDark,

    background = White,
    onBackground = Gray900,

    surface = White,
    onSurface = Gray900,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray700,

    outline = Gray300,
    outlineVariant = Gray200,

    scrim = Black.copy(alpha = 0.32f)
)

val LocalPhoneBookColors = staticCompositionLocalOf {
    PhoneBookColors()
}

data class PhoneBookColors(
    val searchBarBackground: Color = SearchBarGray,
    val divider: Color = Gray300,
    val textSecondary: Color = Gray600,
    val textHint: Color = Gray500,
    val toastBackground: Color = ToastGreen,
    val swipeEditBackground: Color = Green500,
    val swipeDeleteBackground: Color = RedDelete,
    val contactGroupHeader: Color = Gray700,
    val deviceContactIndicator: Color = Green500
)

@Composable
fun PhoneBookTheme(
    darkTheme: Boolean = false, // Only light theme as per requirement
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val phoneBookColors = PhoneBookColors()

    CompositionLocalProvider(
        LocalPhoneBookColors provides phoneBookColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}