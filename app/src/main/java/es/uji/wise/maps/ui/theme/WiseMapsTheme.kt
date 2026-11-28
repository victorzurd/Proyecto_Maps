package es.uji.wise.maps.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Paleta corporativa oficial de Wise Maps (Identidad Visual Equipo Wise).
 */
val WisePlum = Color(0xFFB80D48)
val WisePlumDark = Color(0xFF8B0835)
val WisePlumContainer = Color(0xFFFDE8EF)
val WiseOnPlumContainer = Color(0xFF3E0018)

val WiseOrange = Color(0xFFF29724)
val WiseOrangeDark = Color(0xFFC4730A)
val WiseOrangeContainer = Color(0xFFFFF3E0)
val WiseOnOrangeContainer = Color(0xFF442500)

val WiseTeal = Color(0xFF2B6A6C)
val WiseTealContainer = Color(0xFFDFF1F1)
val WiseOnTealContainer = Color(0xFF0A3234)

val WiseDarkGrey = Color(0xFF404040)
val WiseMutedGrey = Color(0xFF6B7280)
val WiseCanvas = Color(0xFFF7F8FA)
val WiseSurface = Color(0xFFFFFFFF)
val WiseSurfaceVariant = Color(0xFFEEF1F5)
val WiseOutline = Color(0xFFD5DCE4)

val WiseError = Color(0xFFBA1A1A)
val WiseErrorContainer = Color(0xFFFFDAD6)
val WiseSuccess = Color(0xFF1E7E4E)
val WiseSuccessContainer = Color(0xFFE3F7EC)

private val WiseLightColorScheme = lightColorScheme(
    primary = WisePlum,
    onPrimary = Color.White,
    primaryContainer = WisePlumContainer,
    onPrimaryContainer = WiseOnPlumContainer,
    secondary = WiseOrange,
    onSecondary = Color.White,
    secondaryContainer = WiseOrangeContainer,
    onSecondaryContainer = WiseOnOrangeContainer,
    tertiary = WiseTeal,
    onTertiary = Color.White,
    tertiaryContainer = WiseTealContainer,
    onTertiaryContainer = WiseOnTealContainer,
    background = WiseCanvas,
    onBackground = WiseDarkGrey,
    surface = WiseSurface,
    onSurface = WiseDarkGrey,
    surfaceVariant = WiseSurfaceVariant,
    onSurfaceVariant = WiseMutedGrey,
    outline = WiseOutline,
    error = WiseError,
    onError = Color.White,
    errorContainer = WiseErrorContainer,
    onErrorContainer = Color(0xFF410002)
)

private val WiseDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF7DA1),
    onPrimary = Color(0xFF5D0024),
    primaryContainer = WisePlumDark,
    onPrimaryContainer = WisePlumContainer,
    secondary = Color(0xFFFFBA66),
    onSecondary = Color(0xFF482900),
    secondaryContainer = Color(0xFF6A3D00),
    onSecondaryContainer = WiseOrangeContainer,
    tertiary = Color(0xFF7CD0D2),
    onTertiary = Color(0xFF003739),
    tertiaryContainer = Color(0xFF144E50),
    onTertiaryContainer = WiseTealContainer,
    background = Color(0xFF121418),
    onBackground = Color(0xFFE6E9EF),
    surface = Color(0xFF1A1D23),
    onSurface = Color(0xFFE6E9EF),
    surfaceVariant = Color(0xFF252A33),
    onSurfaceVariant = Color(0xFFB4BDC9),
    outline = Color(0xFF3E4654)
)

private val WiseTypography = Typography(
    headlineSmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.2).sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp
    )
)

private val WiseShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp)
)

@Composable
fun WiseMapsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) WiseDarkColorScheme else WiseLightColorScheme,
        typography = WiseTypography,
        shapes = WiseShapes,
        content = content
    )
}
