package org.easy.schulte.core.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Chinese ink-wash palette: xuan-paper surfaces, ink-blue hierarchy, celadon accents,
// ochre warnings, and cinnabar destructive states.
internal val FocusBlue = Color(0xFF2F4F4F)
internal val FocusTeal = Color(0xFF52796F)
internal val PageBackground = Color(0xFFF6F1E6)
internal val CardBackground = Color(0xFFFFFCF5)
internal val QuietText = Color(0xFF5D625D)
internal val LineColor = Color(0xFFD5CCBC)
internal val SuccessGreen = Color(0xFF52796F)
internal val WarningAmber = Color(0xFF9B6D2A)
internal val ErrorRed = Color(0xFFB54036)

internal val SchulteColorScheme: ColorScheme = lightColorScheme(
  primary = FocusBlue,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFDCE8E0),
  onPrimaryContainer = Color(0xFF17352E),
  secondary = FocusTeal,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFD4E6DD),
  onSecondaryContainer = Color(0xFF1B4035),
  tertiary = Color(0xFF8B682F),
  onTertiary = Color.White,
  tertiaryContainer = Color(0xFFF5E6BF),
  onTertiaryContainer = Color(0xFF3A2600),
  error = ErrorRed,
  onError = Color.White,
  errorContainer = Color(0xFFF8DCD6),
  onErrorContainer = Color(0xFF531C18),
  background = PageBackground,
  onBackground = Color(0xFF242A27),
  surface = CardBackground,
  onSurface = Color(0xFF242A27),
  surfaceVariant = Color(0xFFE7E0D2),
  onSurfaceVariant = QuietText,
  outline = Color(0xFFB7AE9E),
  outlineVariant = Color(0xFFD6CEBF),
)

internal val SchulteTypography = Typography(
  displaySmall = TextStyle(fontSize = 36.sp, lineHeight = 44.sp, fontWeight = FontWeight.Bold),
  headlineSmall = TextStyle(fontSize = 24.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold),
  titleLarge = TextStyle(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold),
  titleMedium = TextStyle(fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold),
  titleSmall = TextStyle(fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold),
  bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
  bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
  bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 18.sp),
  labelLarge = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
  labelMedium = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium),
)

internal val SchulteShapes = Shapes(
  extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
  small = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
  medium = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
  large = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
  extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(32.dp),
)

internal data class SchulteStatusColors(
  val success: Color,
  val onSuccess: Color,
  val successContainer: Color,
  val warning: Color,
  val onWarning: Color,
  val warningContainer: Color,
)

private val DefaultSchulteStatusColors = SchulteStatusColors(
  success = SuccessGreen,
  onSuccess = Color.White,
  successContainer = Color(0xFFD5E7DC),
  warning = WarningAmber,
  onWarning = Color.White,
  warningContainer = Color(0xFFF3E3BD),
)

private val LocalSchulteStatusColors = staticCompositionLocalOf { DefaultSchulteStatusColors }

internal val MaterialTheme.schulteStatusColors: SchulteStatusColors
  @Composable
  @ReadOnlyComposable
  get() = LocalSchulteStatusColors.current

@Composable
internal fun SchulteTheme(content: @Composable () -> Unit) {
  androidx.compose.material3.MaterialTheme(
    colorScheme = SchulteColorScheme,
    typography = SchulteTypography,
    shapes = SchulteShapes,
  ) {
    androidx.compose.runtime.CompositionLocalProvider(
      LocalSchulteStatusColors provides DefaultSchulteStatusColors,
      content = content,
    )
  }
}
