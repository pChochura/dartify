package com.pointlessapps.dartify.compose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pointlessapps.dartify.R

private val fontFamily = FontFamily(
    Font(
        resId = R.font.montserrat_light,
        weight = FontWeight.Light,
    ),
    Font(
        resId = R.font.montserrat_normal,
        weight = FontWeight.Normal,
    ),
    Font(
        resId = R.font.montserrat_medium,
        weight = FontWeight.Medium,
    ),
    Font(
        resId = R.font.montserrat_semi_bold,
        weight = FontWeight.SemiBold,
    ),
    Font(
        resId = R.font.montserrat_bold,
        weight = FontWeight.Bold,
    ),
)

@Composable
private fun typography() = Typography(
    defaultFontFamily = fontFamily,
    h1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
    ),
    h2 = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
    ),
    h3 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    ),
    button = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    ),
    body1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    ),
    subtitle1 = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
    ),
)

@Composable
private fun shapes() = Shapes(
    small = RoundedCornerShape(dimensionResource(id = R.dimen.small_rounded_corners)),
    medium = RoundedCornerShape(dimensionResource(id = R.dimen.medium_rounded_corners)),
)

@Composable
private fun lightColorPalette() = lightColors(
    primary = colorResource(id = R.color.gray_3),
    onPrimary = colorResource(id = R.color.gray_8),
    secondary = colorResource(id = R.color.gray_4),
    onSecondary = colorResource(id = R.color.gray_8),
    background = colorResource(id = R.color.gray_2),
    onBackground = colorResource(id = R.color.gray_8),
)

@Composable
private fun darkColorPalette() = darkColors(
    primary = colorResource(id = R.color.gray_5),
    onPrimary = colorResource(id = R.color.gray_1),
    secondary = colorResource(id = R.color.gray_6),
    onSecondary = colorResource(id = R.color.gray_1),
    background = colorResource(id = R.color.gray_7),
    onBackground = colorResource(id = R.color.gray_1),
)

@Composable
internal fun ProjectTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (isDarkTheme) darkColorPalette() else lightColorPalette()
    val systemUiController = rememberSystemUiController()
    LaunchedEffect(systemUiController, isDarkTheme) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = !isDarkTheme,
        )
    }

    MaterialTheme(
        colors = colors,
        typography = typography(),
        shapes = shapes(),
        content = content,
    )
}
