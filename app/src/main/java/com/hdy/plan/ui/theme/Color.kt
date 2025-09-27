package com.hdy.plan.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

/**
 * Five curated palettes:
 * 1) Nature Calm       2) Vibrant & Playful
 * 3) Sleek Dark        4) Cool & Minimal
 * 5) Soft Pastel
 */

enum class ThemeFamily {
    NATURE_CALM,
    VIBRANT_PLAYFUL,
    SLEEK_DARK,
    COOL_MINIMAL,
    SOFT_PASTEL
}

/* ----------------------------- Nature Calm ----------------------------- */
// Light
private val NaturePrimaryLight = Color(0xFF4CAF50) // Fresh Green
private val NatureOnPrimaryLight = Color(0xFFFFFFFF)
private val NatureSecondaryLight = Color(0xFF8BC34A) // Soft Lime
private val NatureOnSecondaryLight = Color(0xFF0F1A00)
private val NatureBackgroundLight = Color(0xFFF1F8E9) // Light Sage
private val NatureSurfaceLight = Color(0xFFFFFFFF)
private val NatureOnSurfaceLight = Color(0xFF1A1C19)

// Dark
private val NaturePrimaryDark = Color(0xFF81C784)
private val NatureOnPrimaryDark = Color(0xFF003910)
private val NatureSecondaryDark = Color(0xFFAED581)
private val NatureOnSecondaryDark = Color(0xFF193000)
private val NatureBackgroundDark = Color(0xFF0F130E)
private val NatureSurfaceDark = Color(0xFF121712)
private val NatureOnSurfaceDark = Color(0xFFE2E6DF)

/* -------------------------- Vibrant & Playful -------------------------- */
// Light
private val VibrantPrimaryLight = Color(0xFFFF6F61) // Coral
private val VibrantOnPrimaryLight = Color(0xFFFFFFFF)
private val VibrantSecondaryLight = Color(0xFFFFC107) // Amber
private val VibrantOnSecondaryLight = Color(0xFF2A1B00)
private val VibrantBackgroundLight = Color(0xFFFFF8E1) // Warm Cream
private val VibrantSurfaceLight = Color(0xFFFFFFFF)
private val VibrantOnSurfaceLight = Color(0xFF201A18)

// Dark
private val VibrantPrimaryDark = Color(0xFFFF8A80)
private val VibrantOnPrimaryDark = Color(0xFF510004)
private val VibrantSecondaryDark = Color(0xFFFFD54F)
private val VibrantOnSecondaryDark = Color(0xFF2E2000)
private val VibrantBackgroundDark = Color(0xFF14100E)
private val VibrantSurfaceDark = Color(0xFF171311)
private val VibrantOnSurfaceDark = Color(0xFFEDE0DA)

/* ------------------------------ Sleek Dark ------------------------------ */
// Light (keeps dark accents but on light surface)
private val SleekPrimaryLight = Color(0xFFBB86FC) // Lavender
private val SleekOnPrimaryLight = Color(0xFF000000)
private val SleekSecondaryLight = Color(0xFF03DAC6) // Teal
private val SleekOnSecondaryLight = Color(0xFF00201C)
private val SleekBackgroundLight = Color(0xFFF7F7F7)
private val SleekSurfaceLight = Color(0xFFFFFFFF)
private val SleekOnSurfaceLight = Color(0xFF1C1B1F)

// Dark (true dark)
private val SleekPrimaryDark = Color(0xFFBB86FC)
private val SleekOnPrimaryDark = Color(0xFF000000)
private val SleekSecondaryDark = Color(0xFF03DAC6)
private val SleekOnSecondaryDark = Color(0xFF001A17)
private val SleekBackgroundDark = Color(0xFF121212)
private val SleekSurfaceDark = Color(0xFF1E1E1E)
private val SleekOnSurfaceDark = Color(0xFFE6E1E5)

/* ---------------------------- Cool & Minimal ---------------------------- */
// Light
private val CoolPrimaryLight = Color(0xFF1976D2) // Ocean Blue
private val CoolOnPrimaryLight = Color(0xFFFFFFFF)
private val CoolSecondaryLight = Color(0xFF29B6F6) // Sky Blue
private val CoolOnSecondaryLight = Color(0xFF001F2A)
private val CoolBackgroundLight = Color(0xFFF5F5F5) // Light Grey
private val CoolSurfaceLight = Color(0xFFFFFFFF)
private val CoolOnSurfaceLight = Color(0xFF1B1B1B)

// Dark
private val CoolPrimaryDark = Color(0xFF64B5F6)
private val CoolOnPrimaryDark = Color(0xFF002745)
private val CoolSecondaryDark = Color(0xFF4FC3F7)
private val CoolOnSecondaryDark = Color(0xFF00222E)
private val CoolBackgroundDark = Color(0xFF0F1113)
private val CoolSurfaceDark = Color(0xFF121417)
private val CoolOnSurfaceDark = Color(0xFFE2E2E2)

/* ----------------------------- Soft Pastel ------------------------------ */
// Light
private val PastelPrimaryLight = Color(0xFFF48FB1) // Blush
private val PastelOnPrimaryLight = Color(0xFF3B0017)
private val PastelSecondaryLight = Color(0xFFCE93D8) // Lilac
private val PastelOnSecondaryLight = Color(0xFF2D0032)
private val PastelBackgroundLight = Color(0xFFFFF3E0) // Peach tint
private val PastelSurfaceLight = Color(0xFFFFFFFF)
private val PastelOnSurfaceLight = Color(0xFF21191D)

// Dark
private val PastelPrimaryDark = Color(0xFFFFB1C8)
private val PastelOnPrimaryDark = Color(0xFF4A001F)
private val PastelSecondaryDark = Color(0xFFE1BEE7)
private val PastelOnSecondaryDark = Color(0xFF3E0044)
private val PastelBackgroundDark = Color(0xFF141013)
private val PastelSurfaceDark = Color(0xFF181419)
private val PastelOnSurfaceDark = Color(0xFFEDE3E7)

/* --------------------------- Public color schemes --------------------------- */

fun lightSchemeFor(theme: ThemeFamily) = when (theme) {
    ThemeFamily.NATURE_CALM -> lightColorScheme(
        primary = NaturePrimaryLight,
        onPrimary = NatureOnPrimaryLight,
        secondary = NatureSecondaryLight,
        onSecondary = NatureOnSecondaryLight,
        background = NatureBackgroundLight,
        surface = NatureSurfaceLight,
        onSurface = NatureOnSurfaceLight
    )
    ThemeFamily.VIBRANT_PLAYFUL -> lightColorScheme(
        primary = VibrantPrimaryLight,
        onPrimary = VibrantOnPrimaryLight,
        secondary = VibrantSecondaryLight,
        onSecondary = VibrantOnSecondaryLight,
        background = VibrantBackgroundLight,
        surface = VibrantSurfaceLight,
        onSurface = VibrantOnSurfaceLight
    )
    ThemeFamily.SLEEK_DARK -> lightColorScheme(
        primary = SleekPrimaryLight,
        onPrimary = SleekOnPrimaryLight,
        secondary = SleekSecondaryLight,
        onSecondary = SleekOnSecondaryLight,
        background = SleekBackgroundLight,
        surface = SleekSurfaceLight,
        onSurface = SleekOnSurfaceLight
    )
    ThemeFamily.COOL_MINIMAL -> lightColorScheme(
        primary = CoolPrimaryLight,
        onPrimary = CoolOnPrimaryLight,
        secondary = CoolSecondaryLight,
        onSecondary = CoolOnSecondaryLight,
        background = CoolBackgroundLight,
        surface = CoolSurfaceLight,
        onSurface = CoolOnSurfaceLight
    )
    ThemeFamily.SOFT_PASTEL -> lightColorScheme(
        primary = PastelPrimaryLight,
        onPrimary = PastelOnPrimaryLight,
        secondary = PastelSecondaryLight,
        onSecondary = PastelOnSecondaryLight,
        background = PastelBackgroundLight,
        surface = PastelSurfaceLight,
        onSurface = PastelOnSurfaceLight
    )
}

fun darkSchemeFor(theme: ThemeFamily) = when (theme) {
    ThemeFamily.NATURE_CALM -> darkColorScheme(
        primary = NaturePrimaryDark,
        onPrimary = NatureOnPrimaryDark,
        secondary = NatureSecondaryDark,
        onSecondary = NatureOnSecondaryDark,
        background = NatureBackgroundDark,
        surface = NatureSurfaceDark,
        onSurface = NatureOnSurfaceDark
    )
    ThemeFamily.VIBRANT_PLAYFUL -> darkColorScheme(
        primary = VibrantPrimaryDark,
        onPrimary = VibrantOnPrimaryDark,
        secondary = VibrantSecondaryDark,
        onSecondary = VibrantOnSecondaryDark,
        background = VibrantBackgroundDark,
        surface = VibrantSurfaceDark,
        onSurface = VibrantOnSurfaceDark
    )
    ThemeFamily.SLEEK_DARK -> darkColorScheme(
        primary = SleekPrimaryDark,
        onPrimary = SleekOnPrimaryDark,
        secondary = SleekSecondaryDark,
        onSecondary = SleekOnSecondaryDark,
        background = SleekBackgroundDark,
        surface = SleekSurfaceDark,
        onSurface = SleekOnSurfaceDark
    )
    ThemeFamily.COOL_MINIMAL -> darkColorScheme(
        primary = CoolPrimaryDark,
        onPrimary = CoolOnPrimaryDark,
        secondary = CoolSecondaryDark,
        onSecondary = CoolOnSecondaryDark,
        background = CoolBackgroundDark,
        surface = CoolSurfaceDark,
        onSurface = CoolOnSurfaceDark
    )
    ThemeFamily.SOFT_PASTEL -> darkColorScheme(
        primary = PastelPrimaryDark,
        onPrimary = PastelOnPrimaryDark,
        secondary = PastelSecondaryDark,
        onSecondary = PastelOnSecondaryDark,
        background = PastelBackgroundDark,
        surface = PastelSurfaceDark,
        onSurface = PastelOnSurfaceDark
    )
}
