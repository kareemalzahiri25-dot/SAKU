package com.saku.app.data

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.saku.app.ui.theme.SakuAmber
import com.saku.app.ui.theme.SakuAmberBg
import com.saku.app.ui.theme.SakuBlue
import com.saku.app.ui.theme.SakuBlueBg
import com.saku.app.ui.theme.SakuEmerald
import com.saku.app.ui.theme.SakuPurple
import com.saku.app.ui.theme.SakuPurpleBg

data class DashboardSummary(
    val userName: String = "Damar",
    val totalBalance: Long = 3250000L,
    val totalIncome: Long = 6800000L,
    val totalExpense: Long = 3250000L,
    val pockets: List<PocketItem> = listOf(
        PocketItem(
            id = "1",
            name = "Makan & Minum",
            spent = 1050000L,
            limit = 1500000L,
            remaining = 450000L,
            spentText = "Rp 1.050.000 / 1,5 Jt",
            accentColor = SakuAmber,
            bgColor = SakuAmberBg,
            categoryType = CategoryType.FOOD
        ),
        PocketItem(
            id = "2",
            name = "Transportasi",
            spent = 480000L,
            limit = 800000L,
            remaining = 320000L,
            spentText = "Rp 480.000 / 800rb",
            accentColor = SakuBlue,
            bgColor = SakuBlueBg,
            categoryType = CategoryType.TRANSPORT
        ),
        PocketItem(
            id = "3",
            name = "Hiburan",
            spent = 350000L,
            limit = 500000L,
            remaining = 150000L,
            spentText = "Rp 350.000 / 500rb",
            accentColor = SakuPurple,
            bgColor = SakuPurpleBg,
            categoryType = CategoryType.ENTERTAINMENT
        )
    ),
    val recentTransactions: List<TransactionItem> = listOf(
        TransactionItem(
            id = "t1",
            title = "Belanja Supermarket",
            subtitle = "18 Nov • Makan & Minum",
            amount = -450000L,
            isExpense = true,
            categoryType = CategoryType.FOOD
        ),
        TransactionItem(
            id = "t2",
            title = "Bensin Pertamax & Tol",
            subtitle = "15 Nov • Transportasi",
            amount = -180000L,
            isExpense = true,
            categoryType = CategoryType.TRANSPORT
        ),
        TransactionItem(
            id = "t3",
            title = "Gaji Bulanan Pokok",
            subtitle = "01 Nov • Pemasukan",
            amount = 6500000L,
            isExpense = false,
            categoryType = CategoryType.INCOME
        )
    )
)

data class PocketItem(
    val id: String,
    val name: String,
    val spent: Long,
    val limit: Long,
    val remaining: Long,
    val spentText: String,
    val accentColor: Color,
    val bgColor: Color,
    val categoryType: CategoryType,
    @get:DrawableRes val iconRes: Int? = null
) {
    val progress: Float
        get() = if (limit > 0) (spent.toFloat() / limit.toFloat()).coerceIn(0f, 1f) else 0f
}

data class TransactionItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: Long,
    val isExpense: Boolean,
    val categoryType: CategoryType,
    val pocketName: String = "Kebutuhan Pokok",
    val dateText: String = "21 Nov 2026, 14:30",
    val method: String = "Manual",
    val notes: String = ""
)

fun TransactionItem.toDetail(): com.saku.app.TransactionDetail {
    val catName = when (categoryType) {
        CategoryType.FOOD -> "Makan & Minum"
        CategoryType.TRANSPORT -> "Transportasi"
        CategoryType.ENTERTAINMENT -> "Hiburan"
        CategoryType.INCOME -> "Pemasukan"
        CategoryType.GENERAL -> "Umum"
    }
    return com.saku.app.TransactionDetail(
        id = id,
        title = title,
        amount = amount,
        category = catName,
        pocket = pocketName,
        date = dateText,
        method = method,
        notes = if (notes.isNotBlank()) notes else "Transaksi SAKU - $title"
    )
}

enum class CategoryType {
    FOOD,
    TRANSPORT,
    ENTERTAINMENT,
    INCOME,
    GENERAL
}

// ==========================================
// POCKETS & ASSETS EXTENDED MODELS
// ==========================================

enum class PocketCategory(val label: String) {
    ALL("Semua"),
    POKOK("Pokok"),
    DARURAT("Darurat"),
    TABUNGAN("Tabungan"),
    KEINGINAN("Keinginan")
}

data class PocketDetailItem(
    val id: String,
    val name: String,
    val category: PocketCategory,
    val spent: Long,
    val limit: Long,
    val remaining: Long,
    val statusText: String,
    val statusColor: Color,
    val statusBg: Color,
    val accentColor: Color,
    val bgColor: Color,
    val categoryType: CategoryType = CategoryType.GENERAL,
    @get:DrawableRes val iconRes: Int? = null
) {
    val progress: Float
        get() = if (limit > 0) (spent.toFloat() / limit.toFloat()).coerceIn(0f, 1f) else 0f
}

data class PocketsOverviewData(
    val totalBudget: Long = 5000000L,
    val usedAmount: Long = 3250000L,
    val remainingQuota: Long = 1750000L,
    val percentUsed: Int = 65,
    val pockets: List<PocketDetailItem> = listOf(
        PocketDetailItem(
            id = "p1",
            name = "Kebutuhan Pokok",
            category = PocketCategory.POKOK,
            spent = 1500000L,
            limit = 2000000L,
            remaining = 500000L,
            statusText = "Aman (75%)",
            statusColor = SakuEmerald,
            statusBg = Color(0xFFE8F8F2),
            accentColor = SakuAmber,
            bgColor = SakuAmberBg
        ),
        PocketDetailItem(
            id = "p2",
            name = "Dana Darurat",
            category = PocketCategory.DARURAT,
            spent = 2500000L / 10L, // 250.000
            limit = 1000000L,
            remaining = 750000L,
            statusText = "Aman (25%)",
            statusColor = SakuEmerald,
            statusBg = Color(0xFFE8F8F2),
            accentColor = Color(0xFF059669),
            bgColor = Color(0xFFECFDF5)
        ),
        PocketDetailItem(
            id = "p3",
            name = "Tabungan Masa Depan",
            category = PocketCategory.TABUNGAN,
            spent = 600000L,
            limit = 1000000L,
            remaining = 400000L,
            statusText = "Aman (60%)",
            statusColor = SakuEmerald,
            statusBg = Color(0xFFE8F8F2),
            accentColor = SakuBlue,
            bgColor = SakuBlueBg
        ),
        PocketDetailItem(
            id = "p4",
            name = "Keinginan & Hiburan",
            category = PocketCategory.KEINGINAN,
            spent = 900000L,
            limit = 1000000L,
            remaining = 100000L,
            statusText = "Waspada (90%)",
            statusColor = Color(0xFFD97706),
            statusBg = Color(0xFFFEF3C7),
            accentColor = Color(0xFFD97706),
            bgColor = SakuPurpleBg
        )
    )
)

enum class AssetClass(val label: String) {
    CASH("Kas & Bank"),
    EMERGENCY("Darurat Likuid"),
    INVESTMENT("Investasi / Saham"),
    CASH_RESERVE("Tunai & Emas")
}

data class AssetItem(
    val id: String,
    val name: String,
    val institution: String,
    val amount: Long,
    val sharePercent: Int,
    val badgeText: String,
    val badgeColor: Color,
    val badgeBg: Color,
    val accentColor: Color,
    val bgColor: Color,
    val assetClass: AssetClass
)

data class AssetsOverviewData(
    val totalNetWorth: Long = 28500000L,
    val monthlyGrowth: String = "+8.4%",
    val kasShare: Int = 35,
    val daruratShare: Int = 30,
    val investShare: Int = 25,
    val tunaiShare: Int = 10,
    val assets: List<AssetItem> = listOf(
        AssetItem(
            id = "a1",
            name = "Rekening Bank Utama",
            institution = "Bank BCA • 5271-xxxx",
            amount = 10000000L,
            sharePercent = 35,
            badgeText = "Kas Likuid",
            badgeColor = Color(0xFF2563EB),
            badgeBg = Color(0xFFEFF6FF),
            accentColor = Color(0xFF2563EB),
            bgColor = Color(0xFFEFF6FF),
            assetClass = AssetClass.CASH
        ),
        AssetItem(
            id = "a2",
            name = "Dana Darurat Likuid",
            institution = "Pasar Uang • Reksadana",
            amount = 8500000L,
            sharePercent = 30,
            badgeText = "Bunga ~5%",
            badgeColor = SakuEmerald,
            badgeBg = Color(0xFFECFDF5),
            accentColor = SakuEmerald,
            bgColor = Color(0xFFECFDF5),
            assetClass = AssetClass.EMERGENCY
        ),
        AssetItem(
            id = "a3",
            name = "Investasi Emas & Saham",
            institution = "Bibit / Pluang Portofolio",
            amount = 7000000L,
            sharePercent = 25,
            badgeText = "+11.2% Untung",
            badgeColor = Color(0xFFD97706),
            badgeBg = Color(0xFFFEF3C7),
            accentColor = Color(0xFFD97706),
            bgColor = Color(0xFFFEF3C7),
            assetClass = AssetClass.INVESTMENT
        ),
        AssetItem(
            id = "a4",
            name = "Simpanan Kas Cadangan",
            institution = "Dompet Fisik / Tunai",
            amount = 3000000L,
            sharePercent = 10,
            badgeText = "Tunai Aman",
            badgeColor = Color(0xFF7C3AED),
            badgeBg = Color(0xFFF5F3FF),
            accentColor = Color(0xFF7C3AED),
            bgColor = Color(0xFFF5F3FF),
            assetClass = AssetClass.CASH_RESERVE
        )
    )
)
