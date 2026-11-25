package com.saku.app

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Main : NavKey

@Serializable
data class PocketsAndAssets(val initialTab: Int = 0) : NavKey

@Serializable
data object CreatePocket : NavKey

@Serializable
data object AddAsset : NavKey

@Serializable
data class AddTransaction(val initialTab: Int = 0) : NavKey

@Serializable
data object Reports : NavKey

@Serializable
data class TransactionDetail(
    val id: String = "t1",
    val title: String = "Belanja Supermarket",
    val amount: Long = -450000L,
    val category: String = "Makan & Minum",
    val pocket: String = "Kebutuhan Pokok",
    val date: String = "18 Nov 2026, 14:32",
    val method: String = "OCR Scan Struk (Gemini AI)",
    val notes: String = "Superindo - Bahan Makanan Bulanan"
) : NavKey

@Serializable
data object Profile : NavKey

@Serializable
data object EditProfileAndApi : NavKey

@Serializable
data object Login : NavKey

@Serializable
data object Register : NavKey

@Serializable
data object ForgotPassword : NavKey

@Serializable
data object OtpVerification : NavKey
