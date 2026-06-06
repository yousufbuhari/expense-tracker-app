package com.buhari.moneymate.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.buhari.moneymate.R

// GoogleSans - Not good for regional languages
val GoogleSans = FontFamily(
    Font(R.font.google_sans_regular, FontWeight.Normal),
    Font(R.font.google_sans_medium, FontWeight.Medium),
    Font(R.font.google_sans_semibold, FontWeight.SemiBold),
    Font(R.font.google_sans_bold, FontWeight.Bold)
)

val notoSans = FontFamily(
    Font(R.font.noto_sans_regular, FontWeight.Normal),
    Font(R.font.noto_sans_medium, FontWeight.Medium),
    Font(R.font.noto_sans_semibold, FontWeight.SemiBold),
    Font(R.font.noto_sans_bold, FontWeight.Bold)
)

val manrope = FontFamily(
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_medium, FontWeight.Medium),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
    Font(R.font.manrope_bold, FontWeight.Bold)
)

fun getTypography(language: String): Typography {
    val isIndic = language in listOf("ta", "ml", "te", "kn")
    val activeFontFamily = manrope
    val scaleFactor = if (isIndic) 0.9f else 1f

    return Typography(
        displayLarge = TextStyle(fontFamily = activeFontFamily, fontWeight = FontWeight.Normal, fontSize = (57 * scaleFactor).sp),
        displayMedium = TextStyle(fontFamily = activeFontFamily, fontWeight = FontWeight.Normal, fontSize = (45 * scaleFactor).sp),
        displaySmall = TextStyle(fontFamily = activeFontFamily, fontWeight = FontWeight.Normal, fontSize = (36 * scaleFactor).sp),
        headlineLarge = TextStyle(fontFamily = activeFontFamily, fontWeight = FontWeight.Normal, fontSize = (32 * scaleFactor).sp),
        headlineMedium = TextStyle(fontFamily = activeFontFamily, fontWeight = FontWeight.Normal, fontSize = (28 * scaleFactor).sp),
        headlineSmall = TextStyle(fontFamily = activeFontFamily, fontWeight = FontWeight.Normal, fontSize = (24 * scaleFactor).sp),
        titleLarge = TextStyle(fontFamily = activeFontFamily, fontWeight = FontWeight.Bold, fontSize = (22 * scaleFactor).sp),
        titleMedium = TextStyle(fontFamily = activeFontFamily, fontWeight = FontWeight.Medium, fontSize = (16 * scaleFactor).sp),
        titleSmall = TextStyle(fontFamily = activeFontFamily, fontWeight = FontWeight.Medium, fontSize = (14 * scaleFactor).sp),
        bodyLarge = TextStyle(fontFamily = activeFontFamily, fontWeight = FontWeight.Normal, fontSize = (16 * scaleFactor).sp),
        bodyMedium = TextStyle(fontFamily = activeFontFamily, fontWeight = FontWeight.Normal, fontSize = (14 * scaleFactor).sp),
        bodySmall = TextStyle(fontFamily = activeFontFamily, fontWeight = FontWeight.Normal, fontSize = (12 * scaleFactor).sp),
        labelLarge = TextStyle(fontFamily = activeFontFamily, fontWeight = FontWeight.Medium, fontSize = (14 * scaleFactor).sp),
        labelMedium = TextStyle(fontFamily = activeFontFamily, fontWeight = FontWeight.Medium, fontSize = (12 * scaleFactor).sp),
        labelSmall = TextStyle(fontFamily = activeFontFamily, fontWeight = FontWeight.Medium, fontSize = (11 * scaleFactor).sp)
    )
}