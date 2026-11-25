package com.saku.app.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// SAKU Brand Colors (from FigCraft Design)
// ==========================================

// Primary & Forest Tones
val SakuDarkGreen = Color(0xFF114232)
val SakuDarkSurface = Color(0xFF142921)
val SakuDarkGreenBorder = Color(0xFF1A5A45)
val SakuForestMedium = Color(0xFF226B54)
val SakuForestDivider = Color(0xFF2B775E)

// Emerald & Accents
val SakuEmerald = Color(0xFF10B981)
val SakuEmeraldLight = Color(0xFF34D399)
val SakuMint = Color(0xFFEAF3EF)
val SakuMintLight = Color(0xFFD1FAE5)

// Neutral & Backgrounds
val SakuBackground = Color(0xFFF7F5EE)
val SakuSurface = Color(0xFFFFFFFF)
val SakuBorder = Color(0xFFEAE7DC)
val SakuBorderLight = Color(0xFFE5E7EB)
val SakuTrack = Color(0xFFF1EFE6)

// Text Colors
val SakuTextPrimary = Color(0xFF114232)
val SakuTextDark = Color(0xFF142921)
val SakuTextSecondary = Color(0xFF6B7280)
val SakuTextMuted = Color(0xFF9CA3AF)
val SakuTextWhite = Color(0xFFFFFFFF)

// Functional & Flow
val SakuExpenseRed = Color(0xFFDC2626)
val SakuExpenseBg = Color(0xFF522932)
val SakuExpenseTag = Color(0xFFFFE4E6)
val SakuIncomeGreen = Color(0xFF10B981)
val SakuIncomeLight = Color(0xFF34D399)

// Category Colors
val SakuAmber = Color(0xFFD97706)
val SakuAmberBg = Color(0xFFFEF3C7)
val SakuBlue = Color(0xFF2563EB)
val SakuBlueBg = Color(0xFFEFF6FF)
val SakuPurple = Color(0xFF7C3AED)
val SakuPurpleBg = Color(0xFFF5F3FF)

// Material 3 Compatible Color Definitions
val primaryLight = SakuDarkGreen
val onPrimaryLight = Color.White
val primaryContainerLight = SakuMint
val onPrimaryContainerLight = SakuDarkGreen

val secondaryLight = SakuEmerald
val tertiaryLight = SakuAmber

val backgroundLight = SakuBackground
val onBackgroundLight = SakuTextDark
val surfaceLight = SakuSurface
val onSurfaceLight = SakuTextDark

val primaryDark = SakuEmeraldLight
val primaryContainerDark = SakuDarkGreen
val secondaryDark = SakuMintLight
val tertiaryDark = SakuAmber

// ==========================================
// Semantic SAKU Color Scheme (Light & Dark)
// ==========================================
data class SakuColorScheme(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val border: Color,
    val borderLight: Color,
    val divider: Color,
    val textPrimary: Color,
    val textDark: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val title: Color,
    val iconBoxBg: Color,
    val iconBoxTint: Color,
    val pillBg: Color,
    val pillText: Color,
    val bottomNavBg: Color,
    val bottomNavBorder: Color,
    val fabBorder: Color,
    val cardShadow: Color,
    val logoutBg: Color,
    val logoutBorder: Color,
    val logoutIconBg: Color,
    val logoutText: Color,
    val chipActiveBg: Color = if (isDark) SakuEmerald else SakuDarkGreen,
    val chipActiveText: Color = if (isDark) Color(0xFF0F241C) else Color.White,
    val chipInactiveBg: Color = if (isDark) Color(0xFF1E4536) else SakuMint,
    val chipInactiveText: Color = if (isDark) Color(0xFF9EBAAE) else SakuDarkGreen,
    val inputBg: Color = if (isDark) Color(0xFF1B3F32) else Color.White,
    val inputBorder: Color = if (isDark) Color(0xFF2B624E) else SakuBorder,
    val trackBg: Color = if (isDark) Color(0xFF1D4537) else SakuTrack
) {
    val cardBg: Color get() = surface
    val cardBorder: Color get() = border
    val primary: Color get() = if (isDark) SakuEmeraldLight else SakuDarkGreen
    val secondary: Color get() = SakuEmerald
    val expenseRed: Color get() = SakuExpenseRed
    val incomeGreen: Color get() = SakuIncomeGreen
}

val LightSakuColorScheme = SakuColorScheme(
    isDark = false,
    background = SakuBackground,
    surface = Color.White,
    surfaceVariant = Color(0xFFF9F8F5),
    border = SakuBorder,
    borderLight = SakuBorderLight,
    divider = Color(0xFFF1EFE6),
    textPrimary = SakuDarkGreen,
    textDark = SakuTextDark,
    textSecondary = SakuTextSecondary,
    textMuted = SakuTextMuted,
    title = SakuDarkGreen,
    iconBoxBg = SakuMint,
    iconBoxTint = SakuDarkGreen,
    pillBg = SakuMint,
    pillText = SakuDarkGreen,
    bottomNavBg = Color.White,
    bottomNavBorder = SakuBorder,
    fabBorder = Color.White,
    cardShadow = Color(0x0A000000),
    logoutBg = Color(0xFFFFF5F5),
    logoutBorder = Color(0xFFFED7D7),
    logoutIconBg = Color(0xFFFEE2E2),
    logoutText = SakuExpenseRed
)

val DarkSakuColorScheme = SakuColorScheme(
    isDark = true,
    background = Color(0xFF0F241C),
    surface = Color(0xFF16382C),
    surfaceVariant = Color(0xFF1B4234),
    border = Color(0xFF225241),
    borderLight = Color(0xFF1D4537),
    divider = Color(0xFF245543),
    textPrimary = Color(0xFFF7F5EE),
    textDark = Color(0xFFFFFFFF),
    textSecondary = Color(0xFF9EBAAE),
    textMuted = Color(0xFF7A988D),
    title = Color(0xFF34D399),
    iconBoxBg = Color(0xFF204D3D),
    iconBoxTint = Color(0xFF34D399),
    pillBg = Color(0xFF204D3D),
    pillText = Color(0xFF34D399),
    bottomNavBg = Color(0xFF122C22),
    bottomNavBorder = Color(0xFF225241),
    fabBorder = Color(0xFF122C22),
    cardShadow = Color(0x35000000),
    logoutBg = Color(0xFF351B20),
    logoutBorder = Color(0xFF5A2A32),
    logoutIconBg = Color(0xFF5A2A32),
    logoutText = Color(0xFFF87171)
)
