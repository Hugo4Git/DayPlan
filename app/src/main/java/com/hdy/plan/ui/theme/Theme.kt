package com.hdy.plan.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

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
        typography = Typography(),
        content = content
    )
}