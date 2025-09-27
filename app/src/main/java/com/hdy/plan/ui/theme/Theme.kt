package com.hdy.plan.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * PlanTheme
 * - theme: which palette family to use (see ThemeFamily)
 * - useDynamicColor: if true and Android 12+, use system (Monet) dynamic colors
 * - darkTheme: null -> follow system; otherwise force light/dark
 */
@Composable
fun PlanTheme(
    theme: ThemeFamily = ThemeFamily.NATURE_CALM,
    useDynamicColor: Boolean = true,
    darkTheme: Boolean? = null,
    content: @Composable () -> Unit
) {
    val isDark = darkTheme ?: isSystemInDarkTheme()
    val context = LocalContext.current

    val colorScheme = when {
        useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> darkSchemeFor(theme)
        else -> lightSchemeFor(theme)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(), // Replace with your custom Typography if needed
        content = content
    )
}


//import android.app.Activity
//import android.os.Build
//import androidx.compose.foundation.isSystemInDarkTheme
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.darkColorScheme
//import androidx.compose.material3.dynamicDarkColorScheme
//import androidx.compose.material3.dynamicLightColorScheme
//import androidx.compose.material3.lightColorScheme
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.platform.LocalContext
//
//private val LightColorScheme = lightColorScheme(
//    primary = LagoonPrimary,
//    onPrimary = LagoonOnPrimary,
//    secondary = LagoonSecondary,
//    onSecondary = LagoonOnSecondary,
//    tertiary = LagoonTertiary,
//    onTertiary = LagoonOnTertiary,
//    background = LagoonBgLight,
//    onBackground = LagoonOnBgLight,
//    surface = LagoonSurfaceLight,
//    onSurface = LagoonOnSurfLight,
//    error = LagoonError,
//    onError = LagoonOnError
//)
//
//private val DarkColorScheme = darkColorScheme(
//    primary = LagoonPrimary,
//    onPrimary = LagoonOnPrimary,
//    secondary = LagoonSecondary,
//    onSecondary = LagoonOnSecondary,
//    tertiary = LagoonTertiary,
//    onTertiary = LagoonOnTertiary,
//    background = LagoonBgDark,
//    onBackground = LagoonOnBgDark,
//    surface = LagoonSurfaceDark,
//    onSurface = LagoonOnSurfDark,
//    error = LagoonError,
//    onError = LagoonOnError
//)
//
//@Composable
//fun PlanTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
//    // Dynamic color is available on Android 12+
//    dynamicColor: Boolean = false,
//    content: @Composable () -> Unit
//) {
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }
//
//    MaterialTheme(
//        colorScheme = colorScheme,
//        typography = Typography,
//        content = content
//    )
//}