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

internal val FocusBlue = Color(0xFF2477D4)
internal val FocusTeal = Color(0xFF1C8C87)
internal val PageBackground = Color(0xFFF5F8FB)
internal val CardBackground = Color(0xFFFFFFFF)
internal val QuietText = Color(0xFF5E6A78)
internal val LineColor = Color(0xFFE2E8F0)
internal val SuccessGreen = Color(0xFF1C8E5A)
internal val WarningAmber = Color(0xFFB7791F)
internal val ErrorRed = Color(0xFFD14B4B)

internal val SchulteColorScheme: ColorScheme = lightColorScheme(
  primary = FocusBlue,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFD8E9FF),
  onPrimaryContainer = Color(0xFF00345E),
  secondary = FocusTeal,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFC8F2EE),
  onSecondaryContainer = Color(0xFF003C39),
  tertiary = Color(0xFF765A00),
  onTertiary = Color.White,
  tertiaryContainer = Color(0xFFFFE08A),
  onTertiaryContainer = Color(0xFF261900),
  error = ErrorRed,
  onError = Color.White,
  errorContainer = Color(0xFFFFDAD6),
  onErrorContainer = Color(0xFF410002),
  background = PageBackground,
  onBackground = Color(0xFF172033),
  surface = CardBackground,
  onSurface = Color(0xFF172033),
  surfaceVariant = Color(0xFFDDE3EA),
  onSurfaceVariant = QuietText,
  outline = LineColor,
  outlineVariant = Color(0xFFC2C8D0),
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
  successContainer = Color(0xFFB9F7D0),
  warning = WarningAmber,
  onWarning = Color.White,
  warningContainer = Color(0xFFFFE09A),
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
