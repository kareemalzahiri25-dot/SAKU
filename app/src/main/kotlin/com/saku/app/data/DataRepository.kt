package com.saku.app.data

import android.content.Context
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.core.content.FileProvider
import com.saku.app.ui.theme.SakuAmber
import com.saku.app.ui.theme.SakuAmberBg
import com.saku.app.ui.theme.SakuBlue
import com.saku.app.ui.theme.SakuBlueBg
import com.saku.app.ui.theme.SakuEmerald
import com.saku.app.ui.theme.SakuPurple
import com.saku.app.ui.theme.SakuPurpleBg
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

interface DataRepository {
    val dashboardData: StateFlow<DashboardSummary>
    val pocketsData: StateFlow<PocketsOverviewData>
    val assetsData: StateFlow<AssetsOverviewData>
    val allTransactions: StateFlow<List<TransactionItem>>

    fun addTransaction(
        title: String,
        amount: Long,
        isExpense: Boolean,
        categoryType: CategoryType = CategoryType.GENERAL,
        pocketName: String = "Kebutuhan Pokok",
        dateText: String = "Hari ini, 21 Nov 2026",
        notes: String = "",
        method: String = "Manual"
    )

    fun deleteTransaction(id: String)

    fun addPocket(
        name: String,
        category: PocketCategory,
        limit: Long,
        accentColor: Color = SakuAmber,
        bgColor: Color = SakuAmberBg,
        categoryType: CategoryType = CategoryType.GENERAL,
        @DrawableRes iconRes: Int? = null
    )

    fun updatePocket(
        id: String,
        name: String,
        category: PocketCategory,
        limit: Long
    )

    fun deletePocket(id: String)

    fun addAsset(
        name: String,
        institution: String,
        amount: Long,
        assetClass: AssetClass,
        yieldText: String = "+5.0%"
    )

    fun updateUserName(name: String)

    fun exportTransactionsCsv(
        context: Context,
        transactions: List<TransactionItem>? = null,
        periodName: String = "Semua Periode"
    ): Uri?
}

class DefaultDataRepository internal constructor() : DataRepository {

    private var currentUserName = "Damar Pratama"

    // Master Pockets
    private val _pocketsList = MutableStateFlow(
        listOf(
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
                bgColor = SakuAmberBg,
                categoryType = CategoryType.FOOD
            ),
            PocketDetailItem(
                id = "p2",
                name = "Transportasi",
                category = PocketCategory.POKOK,
                spent = 480000L,
                limit = 800000L,
                remaining = 320000L,
                statusText = "Aman (60%)",
                statusColor = SakuEmerald,
                statusBg = Color(0xFFE8F8F2),
                accentColor = SakuBlue,
                bgColor = SakuBlueBg,
                categoryType = CategoryType.TRANSPORT
            ),
            PocketDetailItem(
                id = "p3",
                name = "Dana Darurat",
                category = PocketCategory.DARURAT,
                spent = 250000L,
                limit = 1000000L,
                remaining = 750000L,
                statusText = "Aman (25%)",
                statusColor = SakuEmerald,
                statusBg = Color(0xFFE8F8F2),
                accentColor = Color(0xFF059669),
                bgColor = Color(0xFFECFDF5),
                categoryType = CategoryType.GENERAL
            ),
            PocketDetailItem(
                id = "p4",
                name = "Tabungan Masa Depan",
                category = PocketCategory.TABUNGAN,
                spent = 600000L,
                limit = 1000000L,
                remaining = 400000L,
                statusText = "Aman (60%)",
                statusColor = SakuEmerald,
                statusBg = Color(0xFFE8F8F2),
                accentColor = SakuBlue,
                bgColor = SakuBlueBg,
                categoryType = CategoryType.GENERAL
            ),
            PocketDetailItem(
                id = "p5",
                name = "Keinginan & Hiburan",
                category = PocketCategory.KEINGINAN,
                spent = 900000L,
                limit = 1000000L,
                remaining = 100000L,
                statusText = "Waspada (90%)",
                statusColor = Color(0xFFD97706),
                statusBg = Color(0xFFFEF3C7),
                accentColor = SakuPurple,
                bgColor = SakuPurpleBg,
                categoryType = CategoryType.ENTERTAINMENT
            )
        )
    )

    // Master Transactions
    private val _transactionsList = MutableStateFlow(
        listOf(
            TransactionItem(
                id = "t6",
                title = "Langganan WiFi & Listrik",
                subtitle = "22 Nov • Tagihan & Lainnya",
                amount = -150000L,
                isExpense = true,
                categoryType = CategoryType.GENERAL,
                pocketName = "Kebutuhan Pokok",
                dateText = "22 Nov 2026, 11:20",
                method = "Auto Debit BCA",
                notes = "Tagihan Listrik PLN & Indihome"
            ),
            TransactionItem(
                id = "t5",
                title = "Nonton Bioskop & Snack",
                subtitle = "20 Nov • Keinginan & Hiburan",
                amount = -120000L,
                isExpense = true,
                categoryType = CategoryType.ENTERTAINMENT,
                pocketName = "Keinginan & Hiburan",
                dateText = "20 Nov 2026, 19:45",
                method = "QRIS BCA",
                notes = "XXI Cinema Premiere & Popcorn"
            ),
            TransactionItem(
                id = "t1",
                title = "Belanja Supermarket",
                subtitle = "18 Nov • Makan & Minum",
                amount = -450000L,
                isExpense = true,
                categoryType = CategoryType.FOOD,
                pocketName = "Kebutuhan Pokok",
                dateText = "18 Nov 2026, 14:32",
                method = "OCR Scan Struk (Gemini AI)",
                notes = "Superindo - Bahan Makanan Bulanan"
            ),
            TransactionItem(
                id = "t2",
                title = "Bensin Pertamax & Tol",
                subtitle = "15 Nov • Transportasi",
                amount = -180000L,
                isExpense = true,
                categoryType = CategoryType.TRANSPORT,
                pocketName = "Transportasi",
                dateText = "15 Nov 2026, 09:15",
                method = "Input Manual",
                notes = "Pengisian BBM Pertamax & Tol Cawang"
            ),
            TransactionItem(
                id = "t3",
                title = "Bonus Insentif Proyek",
                subtitle = "14 Nov • Pemasukan",
                amount = 300000L,
                isExpense = false,
                categoryType = CategoryType.INCOME,
                pocketName = "Kas Likuid",
                dateText = "14 Nov 2026, 17:00",
                method = "Transfer Bank Masuk",
                notes = "Bonus Sprint Milestone SAKU"
            ),
            TransactionItem(
                id = "t4",
                title = "Gaji Bulanan Pokok",
                subtitle = "01 Nov • Pemasukan",
                amount = 6500000L,
                isExpense = false,
                categoryType = CategoryType.INCOME,
                pocketName = "Kas Likuid",
                dateText = "01 Nov 2026, 10:00",
                method = "Payroll Transfer BCA",
                notes = "Gaji Pokok Karyawan"
            ),
            TransactionItem(
                id = "t7",
                title = "Belanja Bulanan Superindo",
                subtitle = "25 Okt • Makan & Minum",
                amount = -420000L,
                isExpense = true,
                categoryType = CategoryType.FOOD,
                pocketName = "Kebutuhan Pokok",
                dateText = "25 Okt 2026, 16:10",
                method = "Debit BCA",
                notes = "Kebutuhan Dapur & Sayuran"
            ),
            TransactionItem(
                id = "t8",
                title = "Servis Motor & Ganti Oli",
                subtitle = "18 Okt • Transportasi",
                amount = -280000L,
                isExpense = true,
                categoryType = CategoryType.TRANSPORT,
                pocketName = "Transportasi",
                dateText = "18 Okt 2026, 10:30",
                method = "Tunai",
                notes = "Bengkel Resmi AHASS"
            ),
            TransactionItem(
                id = "t9",
                title = "Gaji Bulanan Pokok",
                subtitle = "01 Okt • Pemasukan",
                amount = 6500000L,
                isExpense = false,
                categoryType = CategoryType.INCOME,
                pocketName = "Kas Likuid",
                dateText = "01 Okt 2026, 10:00",
                method = "Payroll Transfer BCA",
                notes = "Gaji Pokok Karyawan"
            ),
            TransactionItem(
                id = "t10",
                title = "Makan Malam Restoran",
                subtitle = "20 Sep • Makan & Minum",
                amount = -350000L,
                isExpense = true,
                categoryType = CategoryType.FOOD,
                pocketName = "Kebutuhan Pokok",
                dateText = "20 Sep 2026, 20:15",
                method = "QRIS BCA",
                notes = "Makan Bersama Keluarga"
            ),
            TransactionItem(
                id = "t11",
                title = "Gaji Bulanan Pokok",
                subtitle = "01 Sep • Pemasukan",
                amount = 6500000L,
                isExpense = false,
                categoryType = CategoryType.INCOME,
                pocketName = "Kas Likuid",
                dateText = "01 Sep 2026, 10:00",
                method = "Payroll Transfer BCA",
                notes = "Gaji Pokok Karyawan"
            )
        )
    )

    // Master Assets
    private val _assetsList = MutableStateFlow(
        listOf(
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

    // Exposed Flows
    private val _dashboardData = MutableStateFlow(buildDashboardSummary())
    override val dashboardData: StateFlow<DashboardSummary> = _dashboardData.asStateFlow()

    private val _pocketsData = MutableStateFlow(buildPocketsOverview())
    override val pocketsData: StateFlow<PocketsOverviewData> = _pocketsData.asStateFlow()

    private val _assetsData = MutableStateFlow(buildAssetsOverview())
    override val assetsData: StateFlow<AssetsOverviewData> = _assetsData.asStateFlow()

    override val allTransactions: StateFlow<List<TransactionItem>> = _transactionsList.asStateFlow()

    private fun recomputeState() {
        _dashboardData.value = buildDashboardSummary()
        _pocketsData.value = buildPocketsOverview()
        _assetsData.value = buildAssetsOverview()
    }

    private fun buildDashboardSummary(): DashboardSummary {
        val txs = _transactionsList.value
        val totalIncome = txs.filter { !it.isExpense }.sumOf { it.amount }
        val totalExpense = txs.filter { it.isExpense }.sumOf { Math.abs(it.amount) }
        val balance = (3250000L + totalIncome - totalExpense).coerceAtLeast(0L)

        // Convert top pockets for Dashboard display
        val dashboardPockets = _pocketsList.value.take(3).map { p ->
            val limitDisplay = if (p.limit >= 1000000) "${p.limit / 1000000} Jt" else "${p.limit / 1000}rb"
            PocketItem(
                id = p.id,
                name = p.name,
                spent = p.spent,
                limit = p.limit,
                remaining = p.remaining,
                spentText = "Rp ${formatThousand(p.spent)} / $limitDisplay",
                accentColor = p.accentColor,
                bgColor = p.bgColor,
                categoryType = p.categoryType,
                iconRes = p.iconRes
            )
        }

        return DashboardSummary(
            userName = currentUserName.split(" ").firstOrNull() ?: currentUserName,
            totalBalance = balance,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            pockets = dashboardPockets,
            recentTransactions = txs.take(5)
        )
    }

    private fun buildPocketsOverview(): PocketsOverviewData {
        val pockets = _pocketsList.value
        val totalBudget = pockets.sumOf { it.limit }
        val totalSpent = pockets.sumOf { it.spent }
        val remaining = (totalBudget - totalSpent).coerceAtLeast(0L)
        val percent = if (totalBudget > 0) ((totalSpent.toFloat() / totalBudget) * 100).toInt().coerceIn(0, 100) else 0

        return PocketsOverviewData(
            totalBudget = totalBudget,
            usedAmount = totalSpent,
            remainingQuota = remaining,
            percentUsed = percent,
            pockets = pockets
        )
    }

    private fun buildAssetsOverview(): AssetsOverviewData {
        val assets = _assetsList.value
        val total = assets.sumOf { it.amount.coerceAtLeast(0L) }
        val itemShares = computePercentages(assets.map { it.amount })
        val updatedAssets = assets.mapIndexed { index, asset ->
            asset.copy(sharePercent = itemShares[index])
        }
        val classTotals = listOf(
            AssetClass.CASH,
            AssetClass.EMERGENCY,
            AssetClass.INVESTMENT,
            AssetClass.CASH_RESERVE
        ).map { assetClass ->
            assets.filter { it.assetClass == assetClass }.sumOf { it.amount.coerceAtLeast(0L) }
        }
        val classShares = computePercentages(classTotals)

        return AssetsOverviewData(
            totalNetWorth = total,
            monthlyGrowth = "+8.4%",
            kasShare = classShares[0],
            daruratShare = classShares[1],
            investShare = classShares[2],
            tunaiShare = classShares[3],
            assets = updatedAssets
        )
    }

    private fun computePercentages(amounts: List<Long>): List<Int> {
        if (amounts.isEmpty()) return emptyList()
        val positive = amounts.map { it.coerceAtLeast(0L) }
        val total = positive.sum()
        if (total <= 0L) return positive.map { 0 }

        val exacts = positive.map { it.toDouble() * 100.0 / total.toDouble() }
        val floors = exacts.map { it.toInt() }.toMutableList()
        val remainingPoints = (100 - floors.sum()).coerceAtLeast(0)
        val rankedIndices = exacts.indices.sortedWith(
            compareByDescending<Int> { exacts[it] - floors[it] }
                .thenByDescending { positive[it] }
                .thenBy { it }
        )
        for (i in 0 until minOf(remainingPoints, rankedIndices.size)) {
            floors[rankedIndices[i]] += 1
        }
        return floors
    }

    private fun computePocketStatus(spent: Long, limit: Long): Triple<String, Color, Color> {
        val ratio = if (limit > 0) spent.toFloat() / limit.toFloat() else 0f
        val percent = (ratio * 100).toInt().coerceAtLeast(0)
        return when {
            ratio >= 1.0f -> Triple("Overbudget", Color(0xFFDC2626), Color(0xFFFFE4E6))
            ratio >= 0.8f -> Triple("Waspada ($percent%)", Color(0xFFD97706), Color(0xFFFEF3C7))
            else -> Triple("Aman ($percent%)", SakuEmerald, Color(0xFFE8F8F2))
        }
    }

    private fun adjustPocketSpent(
        pocketName: String,
        categoryType: CategoryType,
        delta: Long
    ) {
        val current = _pocketsList.value
        val hasExactNameMatch = current.any { it.name.equals(pocketName, ignoreCase = true) }
        _pocketsList.value = current.map { pocket ->
            val shouldUpdate = if (hasExactNameMatch) {
                pocket.name.equals(pocketName, ignoreCase = true)
            } else {
                categoryType != CategoryType.GENERAL && pocket.categoryType == categoryType
            }
            if (shouldUpdate) {
                val newSpent = (pocket.spent + delta).coerceAtLeast(0L)
                val newRemaining = (pocket.limit - newSpent).coerceAtLeast(0L)
                val (statusText, statusColor, statusBg) = computePocketStatus(newSpent, pocket.limit)
                pocket.copy(
                    spent = newSpent,
                    remaining = newRemaining,
                    statusText = statusText,
                    statusColor = statusColor,
                    statusBg = statusBg
                )
            } else {
                pocket
            }
        }
    }

    override fun addTransaction(
        title: String,
        amount: Long,
        isExpense: Boolean,
        categoryType: CategoryType,
        pocketName: String,
        dateText: String,
        notes: String,
        method: String
    ) {
        val newId = "tx_${System.currentTimeMillis()}"
        val signedAmount = if (isExpense) -Math.abs(amount) else Math.abs(amount)
        val categoryLabel = when (categoryType) {
            CategoryType.FOOD -> "Makan & Minum"
            CategoryType.TRANSPORT -> "Transportasi"
            CategoryType.ENTERTAINMENT -> "Hiburan"
            CategoryType.INCOME -> "Pemasukan"
            CategoryType.GENERAL -> "Umum"
        }
        val subtitle = "Hari ini • $categoryLabel"

        val newItem = TransactionItem(
            id = newId,
            title = title,
            subtitle = subtitle,
            amount = signedAmount,
            isExpense = isExpense,
            categoryType = categoryType,
            pocketName = pocketName,
            dateText = dateText,
            method = method,
            notes = notes
        )

        // Prepend transaction
        _transactionsList.value = listOf(newItem) + _transactionsList.value

        // Update pocket spent & remaining if expense
        if (isExpense) {
            adjustPocketSpent(
                pocketName = pocketName,
                categoryType = categoryType,
                delta = Math.abs(amount)
            )
        }

        recomputeState()
    }

    override fun deleteTransaction(id: String) {
        val itemToDelete = _transactionsList.value.find { it.id == id } ?: return
        _transactionsList.value = _transactionsList.value.filter { it.id != id }

        // Revert pocket expense if it was expense
        if (itemToDelete.isExpense) {
            adjustPocketSpent(
                pocketName = itemToDelete.pocketName,
                categoryType = itemToDelete.categoryType,
                delta = -Math.abs(itemToDelete.amount)
            )
        }

        recomputeState()
    }

    override fun addPocket(
        name: String,
        category: PocketCategory,
        limit: Long,
        accentColor: Color,
        bgColor: Color,
        categoryType: CategoryType,
        @DrawableRes iconRes: Int?
    ) {
        val newId = "p_${System.currentTimeMillis()}"
        val safeLimit = limit.coerceAtLeast(0L)
        val (statusText, statusColor, statusBg) = computePocketStatus(0L, safeLimit)
        val newPocket = PocketDetailItem(
            id = newId,
            name = name,
            category = category,
            spent = 0L,
            limit = safeLimit,
            remaining = safeLimit,
            statusText = statusText,
            statusColor = statusColor,
            statusBg = statusBg,
            accentColor = accentColor,
            bgColor = bgColor,
            categoryType = categoryType,
            iconRes = iconRes
        )
        _pocketsList.value = _pocketsList.value + newPocket
        recomputeState()
    }

    override fun updatePocket(
        id: String,
        name: String,
        category: PocketCategory,
        limit: Long
    ) {
        val safeLimit = limit.coerceAtLeast(0L)
        _pocketsList.value = _pocketsList.value.map { pocket ->
            if (pocket.id == id) {
                val newRemaining = (safeLimit - pocket.spent).coerceAtLeast(0L)
                val (statusText, statusColor, statusBg) = computePocketStatus(pocket.spent, safeLimit)
                pocket.copy(
                    name = name.ifBlank { pocket.name },
                    category = category,
                    limit = safeLimit,
                    remaining = newRemaining,
                    statusText = statusText,
                    statusColor = statusColor,
                    statusBg = statusBg
                )
            } else {
                pocket
            }
        }
        recomputeState()
    }

    override fun deletePocket(id: String) {
        _pocketsList.value = _pocketsList.value.filterNot { it.id == id }
        recomputeState()
    }

    override fun addAsset(
        name: String,
        institution: String,
        amount: Long,
        assetClass: AssetClass,
        yieldText: String
    ) {
        val newId = "a_${System.currentTimeMillis()}"
        val (badgeColor, badgeBg, accentColor, bgColor) = when (assetClass) {
            AssetClass.CASH -> Quad(Color(0xFF2563EB), Color(0xFFEFF6FF), Color(0xFF2563EB), Color(0xFFEFF6FF))
            AssetClass.EMERGENCY -> Quad(SakuEmerald, Color(0xFFECFDF5), SakuEmerald, Color(0xFFECFDF5))
            AssetClass.INVESTMENT -> Quad(Color(0xFFD97706), Color(0xFFFEF3C7), Color(0xFFD97706), Color(0xFFFEF3C7))
            AssetClass.CASH_RESERVE -> Quad(Color(0xFF7C3AED), Color(0xFFF5F3FF), Color(0xFF7C3AED), Color(0xFFF5F3FF))
        }

        val newAsset = AssetItem(
            id = newId,
            name = name,
            institution = institution,
            amount = amount.coerceAtLeast(0L),
            sharePercent = 0,
            badgeText = yieldText,
            badgeColor = badgeColor,
            badgeBg = badgeBg,
            accentColor = accentColor,
            bgColor = bgColor,
            assetClass = assetClass
        )
        _assetsList.value = _assetsList.value + newAsset
        recomputeState()
    }

    override fun updateUserName(name: String) {
        currentUserName = name
        recomputeState()
    }

    override fun exportTransactionsCsv(
        context: Context,
        transactions: List<TransactionItem>?,
        periodName: String
    ): Uri? {
        return try {
            val listToExport = transactions ?: _transactionsList.value
            val safePeriod = periodName.replace(" ", "_").replace("▾", "").trim()
            val cacheDir = File(context.cacheDir, "exports")
            if (!cacheDir.exists()) cacheDir.mkdirs()
            val csvFile = File(cacheDir, "Rekap_Keuangan_SAKU_${safePeriod}.csv")

            val totalIncome = listToExport.filter { !it.isExpense }.sumOf { it.amount }
            val totalExpense = listToExport.filter { it.isExpense }.sumOf { Math.abs(it.amount) }
            val netBalance = totalIncome - totalExpense

            FileOutputStream(csvFile).use { fos ->
                OutputStreamWriter(fos, Charsets.UTF_8).use { writer ->
                    // UTF-8 BOM for Microsoft Excel auto-detect UTF-8
                    writer.write("\uFEFF")
                    // Header Ringkasan
                    writer.write("LAPORAN KEUANGAN SAKU\n")
                    writer.write("Periode Laporan;$periodName\n")
                    writer.write("Total Pemasukan;Rp ${formatThousand(totalIncome)}\n")
                    writer.write("Total Pengeluaran;Rp ${formatThousand(totalExpense)}\n")
                    writer.write("Saldo Bersih;Rp ${formatThousand(netBalance)}\n")
                    writer.write("Jumlah Transaksi;${listToExport.size}\n\n")

                    // Header Kolom Tabel (Pemisah titik koma ';' agar langsung terbagi rapi di Excel)
                    writer.write("ID;Tanggal;Nama Transaksi;Kategori;Pos Kantong;Tipe;Nominal (Rp);Metode;Catatan\n")
                    listToExport.forEach { tx ->
                        val typeStr = if (tx.isExpense) "Pengeluaran" else "Pemasukan"
                        val safeTitle = tx.title.replace("\"", "\"\"").replace(";", ",")
                        val safeNotes = tx.notes.replace("\"", "\"\"").replace(";", ",")
                        val safeCat = tx.categoryType.name
                        val safePocket = tx.pocketName.replace("\"", "\"\"").replace(";", ",")
                        val safeMethod = tx.method.replace("\"", "\"\"").replace(";", ",")
                        writer.write("\"${tx.id}\";\"${tx.dateText}\";\"$safeTitle\";\"$safeCat\";\"$safePocket\";\"$typeStr\";${tx.amount};\"$safeMethod\";\"$safeNotes\"\n")
                    }
                    writer.flush()
                }
            }

            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", csvFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

    companion object {
        @Volatile
        private var instance: DefaultDataRepository? = null

        fun getInstance(): DefaultDataRepository {
            return instance ?: synchronized(this) {
                instance ?: DefaultDataRepository().also { instance = it }
            }
        }
    }
}

private fun formatThousand(value: Long): String {
    val symbols = DecimalFormatSymbols(Locale("id", "ID"))
    symbols.groupingSeparator = '.'
    return DecimalFormat("#,###", symbols).format(value)
}
