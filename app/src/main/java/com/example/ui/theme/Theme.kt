package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DannyDarkColorScheme =
  darkColorScheme(
    primary = ElectricYellow,
    onPrimary = MidnightBlueDark,
    primaryContainer = MidnightBlueCard,
    onPrimaryContainer = PureWhite,
    secondary = ElectricYellowBright,
    onSecondary = MidnightBlueDark,
    secondaryContainer = MidnightBlueSurface,
    onSecondaryContainer = PureWhite,
    tertiary = InfoCyan,
    onTertiary = MidnightBlueDark,
    background = MidnightBlue,
    onBackground = PureWhite,
    surface = MidnightBlueDark,
    onSurface = PureWhite,
    surfaceVariant = MidnightBlueSurface,
    onSurfaceVariant = TextSecondary,
    outline = MidnightBlueBorder,
    error = DangerRed,
    onError = PureWhite,
  )

private val DannyLightColorScheme =
  lightColorScheme(
    primary = MidnightBlue,
    onPrimary = PureWhite,
    primaryContainer = MidnightBlueSurface,
    onPrimaryContainer = PureWhite,
    secondary = MidnightBlueCard,
    onSecondary = PureWhite,
    secondaryContainer = OffWhite,
    onSecondaryContainer = MidnightBlueDark,
    tertiary = WarningOrange,
    onTertiary = PureWhite,
    background = MidnightBlue,
    onBackground = PureWhite,
    surface = MidnightBlueSurface,
    onSurface = PureWhite,
    surfaceVariant = MidnightBlueCard,
    onSurfaceVariant = PureWhite,
    outline = MidnightBlueBorder,
    error = DangerRed,
    onError = PureWhite,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DannyDarkColorScheme else DannyLightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content,
  )
}

