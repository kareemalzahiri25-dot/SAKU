package com.saku.app.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.saku.app.R
import com.saku.app.data.AssetClass
import com.saku.app.data.CategoryType
import com.saku.app.data.PocketCategory

/**
 * SAKU Custom Handcrafted Iconography System
 * Semua ikon dirancang secara manual dengan gaya monoline vektor 24dp (stroke 1.75dp, round cap/join)
 * untuk menciptakan identitas visual Organic Fintech yang seragam dan anti-AI-slop.
 */
object SakuIcons {
    // 1. Navigasi & Struktur Inti
    val Home = R.drawable.ic_saku_nav_home
    val Pockets = R.drawable.ic_saku_nav_pockets
    val Reports = R.drawable.ic_saku_nav_reports
    val Profile = R.drawable.ic_saku_nav_profile
    val Add = R.drawable.ic_saku_action_add
    val Back = R.drawable.ic_saku_nav_back
    val Close = R.drawable.ic_saku_action_close
    val ChevronRight = R.drawable.ic_saku_chevron_right
    val ChevronLeft = R.drawable.ic_saku_chevron_left
    val LinkExt = R.drawable.ic_saku_nav_link_ext

    // 2. Kategori Anggaran & Transaksi (Menggantikan seluruh emoji)
    val Groceries = R.drawable.ic_saku_cat_groceries
    val Food = R.drawable.ic_saku_cat_food
    val Transport = R.drawable.ic_saku_cat_transport
    val Entertainment = R.drawable.ic_saku_cat_entertainment
    val Bills = R.drawable.ic_saku_cat_bills
    val Savings = R.drawable.ic_saku_cat_savings
    val Health = R.drawable.ic_saku_cat_health
    val Gift = R.drawable.ic_saku_cat_gift
    val Education = R.drawable.ic_saku_cat_education
    val Income = R.drawable.ic_saku_cat_income

    // 3. Portofolio & Kelas Aset
    val AssetBank = R.drawable.ic_saku_asset_bank
    val AssetCash = R.drawable.ic_saku_asset_cash
    val AssetInvest = R.drawable.ic_saku_asset_invest
    val AssetGold = R.drawable.ic_saku_asset_gold

    // 4. Pemindai Struk & AI Intelligence
    val ScanOcr = R.drawable.ic_saku_scan_ocr
    val Camera = R.drawable.ic_saku_scan_camera
    val Flash = R.drawable.ic_saku_flash
    val AiGemini = R.drawable.ic_saku_ai_gemini

    // 5. Keamanan, Autentikasi & Akun
    val Fingerprint = R.drawable.ic_saku_fingerprint
    val Eye = R.drawable.ic_saku_eye
    val EyeOff = R.drawable.ic_saku_eye_off
    val Logout = R.drawable.ic_saku_logout
    val Lock = R.drawable.ic_saku_security_lock
    val Key = R.drawable.ic_saku_security_key
    val ShieldCheck = R.drawable.ic_saku_shield_check
    val GoogleLogo = R.drawable.ic_google_logo

    // 6. Aksi & Utilitas
    val Edit = R.drawable.ic_saku_action_edit
    val Delete = R.drawable.ic_saku_action_delete
    val Paste = R.drawable.ic_saku_action_paste
    val Excel = R.drawable.ic_saku_action_excel
    val Check = R.drawable.ic_saku_check
    val Share = R.drawable.ic_saku_action_share
    val Notification = R.drawable.ic_saku_notification

    /**
     * Resolusi Ikon Kategori Transaksi
     */
    @DrawableRes
    fun forCategoryType(type: CategoryType): Int {
        return when (type) {
            CategoryType.FOOD -> Food
            CategoryType.TRANSPORT -> Transport
            CategoryType.ENTERTAINMENT -> Entertainment
            CategoryType.INCOME -> Income
            CategoryType.GENERAL -> Groceries
        }
    }

    /**
     * Resolusi Ikon Pos Kantong
     */
    @DrawableRes
    fun forPocketCategory(category: PocketCategory): Int {
        return when (category) {
            PocketCategory.POKOK -> Groceries
            PocketCategory.DARURAT -> Health
            PocketCategory.TABUNGAN -> Savings
            PocketCategory.KEINGINAN -> Entertainment
            PocketCategory.ALL -> Pockets
        }
    }

    /**
     * Resolusi Ikon Kelas Aset
     */
    @DrawableRes
    fun forAssetClass(assetClass: AssetClass): Int {
        return when (assetClass) {
            AssetClass.CASH -> AssetBank
            AssetClass.EMERGENCY -> Health
            AssetClass.INVESTMENT -> AssetInvest
            AssetClass.CASH_RESERVE -> AssetGold
        }
    }
}

/**
 * Model Pilihan Ikon Manual untuk Form Pembuat Kantong Baru
 * Bebas dari emoji, 100% menggunakan Custom Monoline Vector.
 */
data class SakuPocketIconOption(
    val id: String,
    val label: String,
    @get:DrawableRes val iconRes: Int,
    val defaultColor: Color,
    val defaultBgColor: Color
)

val SAKU_POCKET_ICON_OPTIONS = listOf(
    SakuPocketIconOption(
        id = "groceries",
        label = "Pokok",
        iconRes = SakuIcons.Groceries,
        defaultColor = SakuAmber,
        defaultBgColor = SakuAmberBg
    ),
    SakuPocketIconOption(
        id = "food",
        label = "Kuliner",
        iconRes = SakuIcons.Food,
        defaultColor = SakuAmber,
        defaultBgColor = SakuAmberBg
    ),
    SakuPocketIconOption(
        id = "transport",
        label = "Transport",
        iconRes = SakuIcons.Transport,
        defaultColor = SakuBlue,
        defaultBgColor = SakuBlueBg
    ),
    SakuPocketIconOption(
        id = "entertainment",
        label = "Hiburan",
        iconRes = SakuIcons.Entertainment,
        defaultColor = SakuPurple,
        defaultBgColor = SakuPurpleBg
    ),
    SakuPocketIconOption(
        id = "savings",
        label = "Tabungan",
        iconRes = SakuIcons.Savings,
        defaultColor = SakuEmerald,
        defaultBgColor = SakuMintLight
    ),
    SakuPocketIconOption(
        id = "emergency",
        label = "Darurat",
        iconRes = SakuIcons.Health,
        defaultColor = Color(0xFF059669),
        defaultBgColor = Color(0xFFECFDF5)
    ),
    SakuPocketIconOption(
        id = "bills",
        label = "Tagihan",
        iconRes = SakuIcons.Bills,
        defaultColor = Color(0xFF4B5563),
        defaultBgColor = Color(0xFFF3F4F6)
    ),
    SakuPocketIconOption(
        id = "gift",
        label = "Sedekah",
        iconRes = SakuIcons.Gift,
        defaultColor = Color(0xFFDB2777),
        defaultBgColor = Color(0xFFFDF2F8)
    )
)
