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

// Chinese ink-wash palette: xuan-paper whitespace and five ink densities.
// Cinnabar is reserved only for destructive/error semantics.
internal val FocusBlue = Color(0xFF252521) // 焦墨
internal val FocusTeal = Color(0xFF454540) // 浓墨
internal val PageBackground = Color(0xFFF8F6F0) // 宣纸白
internal val CardBackground = Color(0xFFFFFDF8) // 净纸
internal val QuietText = Color(0xFF62625D) // 中墨
internal val LineColor = Color(0xFFD2CFC6) // 淡墨
internal val SuccessGreen = Color(0xFF3B3B37) // 墨黑语义成功
internal val WarningAmber = Color(0xFF696861) // 灰墨语义提醒
internal val ErrorRed = Color(0xFFA2372A) // 朱砂，仅错误/危险操作

internal val SchulteColorScheme: ColorScheme = lightColorScheme(
  primary = FocusBlue,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFE3E1DA),
  onPrimaryContainer = Color(0xFF252521),
  secondary = FocusTeal,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFE9E7E0),
  onSecondaryContainer = Color(0xFF30302C),
  tertiary = Color(0xFF696861),
  onTertiary = Color.White,
  tertiaryContainer = Color(0xFFEEEBE4),
  onTertiaryContainer = Color(0xFF32322E),
  error = ErrorRed,
  onError = Color.White,
  errorContainer = Color(0xFFF3DED9),
  onErrorContainer = Color(0xFF4C1712),
  background = PageBackground,
  onBackground = Color(0xFF252521),
  surface = CardBackground,
  onSurface = Color(0xFF252521),
  surfaceVariant = Color(0xFFE9E7E0),
  onSurfaceVariant = QuietText,
  outline = Color(0xFFAAA8A0),
  outlineVariant = Color(0xFFDCD9D1),
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
  successContainer = Color(0xFFE5E3DC),
  warning = WarningAmber,
  onWarning = Color.White,
  warningContainer = Color(0xFFECEAE3),
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
