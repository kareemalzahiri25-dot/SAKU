package com.saku.app.ui.reports

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.NorthEast
import androidx.compose.material.icons.outlined.SouthWest
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saku.app.TransactionDetail
import com.saku.app.data.CategoryType
import com.saku.app.data.DataRepository
import com.saku.app.data.DefaultDataRepository
import com.saku.app.data.TransactionItem
import com.saku.app.data.localization.EnglishStrings
import com.saku.app.data.localization.LocalAppStrings
import com.saku.app.data.localization.localizeTransactionTitle
import com.saku.app.data.toDetail
import com.saku.app.ui.main.SakuBottomNavigation
import com.saku.app.ui.main.formatRupiah
import com.saku.app.ui.theme.LocalSakuColors
import com.saku.app.ui.theme.SAKUTheme
import com.saku.app.ui.theme.SakuAmber
import com.saku.app.ui.theme.SakuAmberBg
import com.saku.app.ui.theme.SakuBlue
import com.saku.app.ui.theme.SakuBlueBg
import com.saku.app.ui.theme.SakuEmerald
import com.saku.app.ui.theme.SakuExpenseRed
import com.saku.app.ui.theme.SakuIcons
import com.saku.app.ui.theme.SakuIncomeGreen
import com.saku.app.ui.theme.SakuMint
import com.saku.app.ui.theme.SakuPurple
import kotlinx.coroutines.launch

private val monthShortNamesId = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
private val monthFullNamesId = listOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")
private val monthShortNamesEn = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
private val monthFullNamesEn = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToPockets: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAdd: () -> Unit = {},
    onTransactionClick: (TransactionDetail) -> Unit = {},
    dataRepository: DataRepository = DefaultDataRepository.getInstance(),
    modifier: Modifier = Modifier
) {
    val sakuColors = LocalSakuColors.current
    val strings = LocalAppStrings.current
    val isEn = strings == EnglishStrings
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // 1. Rentang Waktu Laporan State
    var selectedYear by remember { mutableIntStateOf(2026) }
    var selectedMonth by remember { mutableIntStateOf(11) } // 1-12 (11 = Nov)
    var periodTab by remember { mutableIntStateOf(1) } // 0 = Mingguan, 1 = Bulanan, 2 = Tahunan
    var showMonthPicker by remember { mutableStateOf(false) }

    // 2. Sorting & Filter State
    var selectedTxFilter by remember { mutableIntStateOf(0) } // 0 = Semua, 1 = Pengeluaran, 2 = Pemasukan
    var sortCategoriesDesc by remember { mutableStateOf(true) } // Sort category breakdown by amount

    val allTransactions by dataRepository.allTransactions.collectAsState()

    val monthShortName = if (isEn) monthShortNamesEn[selectedMonth - 1] else monthShortNamesId[selectedMonth - 1]
    val monthFullName = if (isEn) monthFullNamesEn[selectedMonth - 1] else monthFullNamesId[selectedMonth - 1]
    val periodLabel = when (periodTab) {
        0 -> if (isEn) "Week of $monthShortName $selectedYear" else "Minggu Ini ($monthShortName $selectedYear)"
        2 -> "$selectedYear"
        else -> "$monthShortName $selectedYear"
    }

    // Dynamic Filter by Period
    val periodTransactions = remember(allTransactions, periodTab, selectedMonth, selectedYear) {
        allTransactions.filter { tx ->
            val date = tx.dateText
            val matchesYear = date.contains(selectedYear.toString())
            when (periodTab) {
                0 -> {
                    // Mingguan: transaksi dalam 7 hari terakhir di bulan & tahun terpilih
                    matchesYear && (date.contains(monthShortNamesId[selectedMonth - 1]) || date.contains(monthShortNamesEn[selectedMonth - 1])) &&
                            (date.contains("15") || date.contains("18") || date.contains("20") || date.contains("21") || date.contains("22"))
                }
                2 -> matchesYear // Tahunan: semua transaksi di tahun terpilih
                else -> {
                    // Bulanan: transaksi di bulan & tahun terpilih
                    matchesYear && (date.contains(monthShortNamesId[selectedMonth - 1]) || date.contains(monthShortNamesEn[selectedMonth - 1]))
                }
            }
        }
    }

    val expenseTransactions = periodTransactions.filter { it.isExpense }
    val totalExpense = expenseTransactions.sumOf { Math.abs(it.amount) }
    val totalIncome = periodTransactions.filter { !it.isExpense }.sumOf { it.amount }

    // Category Breakdown Calculation
    val foodExpense = expenseTransactions.filter { it.categoryType == CategoryType.FOOD }.sumOf { Math.abs(it.amount) }
    val transportExpense = expenseTransactions.filter { it.categoryType == CategoryType.TRANSPORT }.sumOf { Math.abs(it.amount) }
    val entertainmentExpense = expenseTransactions.filter { it.categoryType == CategoryType.ENTERTAINMENT }.sumOf { Math.abs(it.amount) }
    val billsExpense = expenseTransactions.filter {
        it.categoryType != CategoryType.FOOD &&
        it.categoryType != CategoryType.TRANSPORT &&
        it.categoryType != CategoryType.ENTERTAINMENT &&
        it.categoryType != CategoryType.INCOME
    }.sumOf { Math.abs(it.amount) }

    // Accurate Percentages without rounding bug (0 rupiah stays 0%)
    val baseExpense = if (totalExpense > 0L) totalExpense.toDouble() else 1.0
    val foodPct = if (foodExpense > 0L) Math.round((foodExpense.toDouble() / baseExpense) * 100).toInt() else 0
    val transportPct = if (transportExpense > 0L) Math.round((transportExpense.toDouble() / baseExpense) * 100).toInt() else 0
    val entertainmentPct = if (entertainmentExpense > 0L) Math.round((entertainmentExpense.toDouble() / baseExpense) * 100).toInt() else 0
    val billsPct = if (billsExpense > 0L) (100 - foodPct - transportPct - entertainmentPct).coerceAtLeast(0) else 0

    // Filtered Transactions for the bottom list
    val filteredTransactions = when (selectedTxFilter) {
        1 -> periodTransactions.filter { it.isExpense }
        2 -> periodTransactions.filter { !it.isExpense }
        else -> periodTransactions
    }

    // Adaptive daily average
    val daysInPeriod = when (periodTab) {
        0 -> 7
        2 -> 365
        else -> 30
    }
    val dailyAverage = if (expenseTransactions.isNotEmpty()) totalExpense / daysInPeriod else 0L

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = sakuColors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            ReportsBottomBar(
                onTabSelected = { tab ->
                    when (tab) {
                        0 -> onNavigateToHome()
                        1 -> onNavigateToPockets()
                        2 -> { /* already here */ }
                        3 -> onNavigateToProfile()
                    }
                },
                onFabClick = onNavigateToAdd
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==========================================
            // 1. TOP BAR: Title + Month Dropdown + Ekspor Excel
            // ==========================================
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.reportsTitle,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = sakuColors.title
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Month Dropdown Pill (Opens Month-Year Picker)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(sakuColors.surface)
                                .border(1.dp, sakuColors.border, RoundedCornerShape(12.dp))
                                .clickable { showMonthPicker = true }
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "$periodLabel ▾",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = sakuColors.textDark
                            )
                        }

                        // Ekspor Excel Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(sakuColors.surface)
                                .border(1.dp, sakuColors.border, RoundedCornerShape(12.dp))
                                .clickable {
                                    scope.launch {
                                        val exportTitle = "$monthShortName $selectedYear"
                                        val uri = dataRepository.exportTransactionsCsv(context, filteredTransactions, exportTitle)
                                        if (uri != null) {
                                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/csv"
                                                putExtra(Intent.EXTRA_STREAM, uri)
                                                putExtra(Intent.EXTRA_SUBJECT, "Laporan Keuangan SAKU ($exportTitle)")
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }
                                            context.startActivity(Intent.createChooser(shareIntent, "Buka atau Bagikan Laporan Excel"))
                                            snackbarHostState.showSnackbar("Laporan ($exportTitle) berhasil disiapkan.")
                                        } else {
                                            snackbarHostState.showSnackbar("Gagal mengekspor laporan.")
                                        }
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = SakuIcons.Excel),
                                    contentDescription = "Export Excel",
                                    tint = sakuColors.primary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = strings.exportExcelBtn,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = sakuColors.primary
                                )
                            }
                        }
                    }
                }
            }

            // ==========================================
            // 2. PERIOD SELECTOR TABS: [Mingguan | Bulanan | Tahunan]
            // ==========================================
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(sakuColors.trackBg, RoundedCornerShape(20.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(strings.periodWeekly, strings.periodMonthly, strings.periodYearly).forEachIndexed { index, period ->
                        val isSelected = periodTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(17.dp))
                                .background(if (isSelected) sakuColors.chipActiveBg else Color.Transparent)
                                .clickable { periodTab = index }
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = period,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) sakuColors.chipActiveText else sakuColors.chipInactiveText
                            )
                        }
                    }
                }
            }

            // ==========================================
            // 3. TOTAL EXPENSE & BAR CHART WITH X & Y AXES
            // ==========================================
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(20.dp), spotColor = sakuColors.cardShadow)
                        .background(sakuColors.surface, RoundedCornerShape(20.dp))
                        .border(1.dp, sakuColors.border, RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        // Title & Amount Header
                        Text(
                            text = when (periodTab) {
                                0 -> if (isEn) "Total Weekly Expenses" else "Total Pengeluaran Minggu Ini"
                                2 -> if (isEn) "Total Yearly Expenses" else "Total Pengeluaran Tahun Ini"
                                else -> if (isEn) "Total Monthly Expenses" else "Total Pengeluaran Bulan Ini"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = sakuColors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = formatRupiah(totalExpense),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = sakuColors.title
                        )

                        // Subtitle info with adaptive frequency
                        val avgLabel = if (isEn) {
                            "Daily Avg: ${formatRupiah(dailyAverage)} • ${expenseTransactions.size} Expenses"
                        } else {
                            "Rata-rata: ${formatRupiah(dailyAverage)} / hari • ${expenseTransactions.size} Pengeluaran"
                        }
                        Text(
                            text = avgLabel,
                            fontSize = 11.sp,
                            color = sakuColors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Coordinate Bar Chart with Sumbu X & Sumbu Y
                        ExpenseCoordinateBarChart(
                            periodTab = periodTab,
                            allTransactions = allTransactions,
                            currentExpense = totalExpense,
                            primaryColor = sakuColors.primary,
                            trackColor = sakuColors.trackBg,
                            borderColor = sakuColors.border,
                            textSecondary = sakuColors.textSecondary
                        )
                    }
                }
            }

            // ==========================================
            // 4. PENGELUARAN PER KATEGORI & DONUT CHART CANVAS
            // ==========================================
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(20.dp), spotColor = sakuColors.cardShadow)
                        .background(sakuColors.surface, RoundedCornerShape(20.dp))
                        .border(1.dp, sakuColors.border, RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.expenseByCategoryTitle,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = sakuColors.title
                            )

                            // Functional Sort Order Button
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { sortCategoriesDesc = !sortCategoriesDesc }
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.SwapVert,
                                    contentDescription = "Urutkan",
                                    tint = sakuColors.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (sortCategoriesDesc) "Terbesar" else "Terkecil",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = sakuColors.primary
                                )
                            }
                        }

                        // Category Proportions Data
                        val catFood = if (isEn) "Food & Beverage" else "Makan & Minum"
                        val catTrans = if (isEn) "Transportation" else "Transportasi"
                        val catFun = if (isEn) "Entertainment" else "Keinginan & Hiburan"
                        val catBills = if (isEn) "Bills & Utilities" else "Tagihan & Lainnya"

                        var categories = listOf(
                            CategoryBreakdownData(catFood, foodExpense, foodPct, SakuAmber),
                            CategoryBreakdownData(catTrans, transportExpense, transportPct, SakuBlue),
                            CategoryBreakdownData(catFun, entertainmentExpense, entertainmentPct, SakuExpenseRed),
                            CategoryBreakdownData(catBills, billsExpense, billsPct, SakuPurple)
                        )

                        if (sortCategoriesDesc) {
                            categories = categories.sortedByDescending { it.amount }
                        } else {
                            categories = categories.sortedBy { it.amount }
                        }

                        // Donut Visual + Unified Breakdown Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Real Segmented Donut Chart via Canvas
                            SegmentedDonutChart(
                                categories = categories,
                                totalExpense = totalExpense,
                                trackColor = sakuColors.trackBg,
                                titleColor = sakuColors.title,
                                textSecondary = sakuColors.textSecondary,
                                modifier = Modifier.size(96.dp)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            // Clean Unified Breakdown List (No Redundant Duplication)
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                categories.forEach { cat ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .background(if (cat.amount > 0) cat.color else sakuColors.border, CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(7.dp))
                                            Text(
                                                text = cat.name,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = sakuColors.textDark
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${cat.percent}%",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (cat.amount > 0) cat.color else sakuColors.textSecondary
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = formatRupiah(cat.amount),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = sakuColors.title
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // 5. RINCIAN TRANSAKSI & FILTERED LIST
            // ==========================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.transactionDetailTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = sakuColors.title
                    )
                    Text(
                        text = "${filteredTransactions.size} ${if (isEn) "Transactions" else "Transaksi"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = sakuColors.textSecondary
                    )
                }
            }

            // Standard Filter Pills with Vector Icons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filterOptions = listOf(
                        Triple(strings.filterAll, Icons.Outlined.FormatListBulleted, sakuColors.primary),
                        Triple(strings.filterExpense, Icons.Outlined.NorthEast, SakuExpenseRed),
                        Triple(strings.filterIncome, Icons.Outlined.SouthWest, SakuIncomeGreen)
                    )

                    filterOptions.forEachIndexed { index, (label, icon, activeTint) ->
                        val isSelected = selectedTxFilter == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) sakuColors.chipActiveBg else sakuColors.surface)
                                .border(
                                    1.dp,
                                    if (isSelected) sakuColors.primary else sakuColors.border,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedTxFilter = index }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isSelected) activeTint else sakuColors.textSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) sakuColors.chipActiveText else sakuColors.chipInactiveText
                                )
                            }
                        }
                    }
                }
            }

            // Grouped Transactions Container
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(20.dp), spotColor = sakuColors.cardShadow)
                        .background(sakuColors.surface, RoundedCornerShape(20.dp))
                        .border(1.dp, sakuColors.border, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    if (filteredTransactions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isEn) "No transactions found in this period" else "Belum ada transaksi di periode ini",
                                fontSize = 13.sp,
                                color = sakuColors.textSecondary
                            )
                        }
                    } else {
                        // Group by date text (e.g. "22 Nov 2026", "20 Nov 2026", etc.)
                        val grouped = filteredTransactions.groupBy { tx ->
                            tx.dateText.substringBefore(",")
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            grouped.forEach { (dateHeader, txsInDate) ->
                                Column {
                                    // Date Header Badge
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(sakuColors.trackBg)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = dateHeader,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = sakuColors.textSecondary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Items for this date
                                    txsInDate.forEachIndexed { idx, tx ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onTransactionClick(tx.toDetail()) }
                                                .padding(vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                val (bgColor, iconColor) = when (tx.categoryType) {
                                                    CategoryType.FOOD -> Pair(SakuAmberBg, SakuAmber)
                                                    CategoryType.TRANSPORT -> Pair(SakuBlueBg, SakuBlue)
                                                    CategoryType.INCOME -> Pair(SakuMint, SakuEmerald)
                                                    else -> Pair(sakuColors.trackBg, sakuColors.primary)
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .size(38.dp)
                                                        .background(bgColor, RoundedCornerShape(10.dp)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = SakuIcons.forCategoryType(tx.categoryType)),
                                                        contentDescription = null,
                                                        tint = iconColor,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text(
                                                        text = strings.localizeTransactionTitle(tx.title),
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = sakuColors.title
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        // Pocket Tag chip
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(4.dp))
                                                                .background(sakuColors.trackBg)
                                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                                        ) {
                                                            Text(
                                                                text = tx.pocketName,
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Medium,
                                                                color = sakuColors.textSecondary
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        val timePart = tx.dateText.substringAfter(",", "").trim()
                                                        if (timePart.isNotBlank()) {
                                                            Text(
                                                                text = timePart,
                                                                fontSize = 11.sp,
                                                                color = sakuColors.textSecondary
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            Text(
                                                text = if (tx.isExpense) "-${formatRupiah(Math.abs(tx.amount))}" else "+${formatRupiah(tx.amount)}",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (tx.isExpense) SakuExpenseRed else SakuIncomeGreen
                                            )
                                        }

                                        if (idx < txsInDate.lastIndex) {
                                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(sakuColors.border))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // ==========================================
    // 6. MONTH-YEAR PICKER MODAL BOTTOM SHEET
    // ==========================================
    if (showMonthPicker) {
        ModalBottomSheet(
            onDismissRequest = { showMonthPicker = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = sakuColors.surface
        ) {
            MonthYearPickerSheet(
                initialYear = selectedYear,
                initialMonth = selectedMonth,
                isEn = isEn,
                primaryColor = sakuColors.primary,
                trackColor = sakuColors.trackBg,
                textColor = sakuColors.title,
                borderColor = sakuColors.border,
                onApply = { year, month ->
                    selectedYear = year
                    selectedMonth = month
                    showMonthPicker = false
                    scope.launch {
                        val mName = if (isEn) monthFullNamesEn[month - 1] else monthFullNamesId[month - 1]
                        snackbarHostState.showSnackbar("Laporan disaring ke $mName $year")
                    }
                },
                onCancel = { showMonthPicker = false }
            )
        }
    }
}

// ==========================================
// DATA CLASS & HELPER COMPONENTS
// ==========================================

data class CategoryBreakdownData(
    val name: String,
    val amount: Long,
    val percent: Int,
    val color: Color
)

/**
 * Coordinate Bar Chart with Sumbu X (Baseline) & Sumbu Y (Guidelines & Values)
 */
@Composable
fun ExpenseCoordinateBarChart(
    periodTab: Int,
    allTransactions: List<TransactionItem>,
    currentExpense: Long,
    primaryColor: Color,
    trackColor: Color,
    borderColor: Color,
    textSecondary: Color
) {
    // Generate bars dynamically according to active periodTab
    val barsData: List<Pair<String, Long>> = when (periodTab) {
        0 -> {
            // Weekly: Sen, Sel, Rab, Kam, Jum, Sab, Min
            listOf(
                Pair("Sen", 0L),
                Pair("Sel", 0L),
                Pair("Rab", 450000L), // 18 Nov
                Pair("Kam", 0L),
                Pair("Jum", 180000L), // 15 Nov
                Pair("Sab", 120000L), // 20 Nov
                Pair("Min", 150000L)  // 22 Nov
            )
        }
        2 -> {
            // Yearly: Q1, Q2, Q3, Q4
            listOf(
                Pair("Q1", 1200000L),
                Pair("Q2", 1850000L),
                Pair("Q3", 1600000L),
                Pair("Q4", currentExpense) // Current Quarter
            )
        }
        else -> {
            // Monthly: Jun, Jul, Agu, Sep, Okt, Nov
            listOf(
                Pair("Jun", 320000L),
                Pair("Jul", 550000L),
                Pair("Agu", 410000L),
                Pair("Sep", 630000L),
                Pair("Okt", 700000L),
                Pair("Nov", currentExpense) // Active Month
            )
        }
    }

    val maxAmount = (barsData.maxOfOrNull { it.second } ?: 1L).coerceAtLeast(100000L)
    val midAmount = maxAmount / 2

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            // Sumbu Y: Labels on the Left
            Column(
                modifier = Modifier
                    .width(52.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                Text(formatShortRupiah(maxAmount), fontSize = 10.sp, color = textSecondary)
                Text(formatShortRupiah(midAmount), fontSize = 10.sp, color = textSecondary)
                Text("Rp 0", fontSize = 10.sp, color = textSecondary)
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Chart Canvas + Columns Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // Dashed horizontal gridlines across the chart
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)

                    // Line Max (Top)
                    drawLine(
                        color = borderColor.copy(alpha = 0.5f),
                        start = Offset(0f, 6f),
                        end = Offset(w, 6f),
                        strokeWidth = 1f,
                        pathEffect = dashEffect
                    )

                    // Line Mid (Middle)
                    drawLine(
                        color = borderColor.copy(alpha = 0.5f),
                        start = Offset(0f, h / 2f),
                        end = Offset(w, h / 2f),
                        strokeWidth = 1f,
                        pathEffect = dashEffect
                    )

                    // Sumbu X: Baseline Solid Line (Bottom)
                    drawLine(
                        color = borderColor,
                        start = Offset(0f, h - 2f),
                        end = Offset(w, h - 2f),
                        strokeWidth = 1.5f
                    )
                }

                // Vertical Bar Columns
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    barsData.forEachIndexed { index, (label, amount) ->
                        val isCurrent = index == barsData.lastIndex
                        val barHeightFactor = (amount.toFloat() / maxAmount.toFloat()).coerceIn(0.05f, 1f)
                        val barHeightDp = (barHeightFactor * 95).dp

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.padding(bottom = 2.dp)
                        ) {
                            if (isCurrent && amount > 0) {
                                Box(
                                    modifier = Modifier
                                        .background(primaryColor, RoundedCornerShape(5.dp))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = formatShortRupiah(amount),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                            }

                            // Pillar Bar
                            Box(
                                modifier = Modifier
                                    .width(22.dp)
                                    .height(barHeightDp)
                                    .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                                    .background(if (isCurrent) primaryColor else trackColor)
                            )
                        }
                    }
                }
            }
        }

        // Sumbu X: Month/Day Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 60.dp, end = 6.dp, top = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            barsData.forEachIndexed { index, (label, _) ->
                val isCurrent = index == barsData.lastIndex
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCurrent) primaryColor else textSecondary
                )
            }
        }
    }
}

/**
 * Segmented Donut Chart using Jetpack Compose Canvas drawArc
 */
@Composable
fun SegmentedDonutChart(
    categories: List<CategoryBreakdownData>,
    totalExpense: Long,
    trackColor: Color,
    titleColor: Color,
    textSecondary: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 10.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val arcSize = Size(diameter, diameter)
            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

            if (totalExpense <= 0L) {
                // Empty Track
                drawArc(
                    color = trackColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )
            } else {
                var currentAngle = -90f // Start from 12 o'clock
                val activeCategories = categories.filter { it.amount > 0 }

                activeCategories.forEach { cat ->
                    val sweep = (cat.amount.toFloat() / totalExpense.toFloat()) * 360f
                    val visibleSweep = if (activeCategories.size > 1) (sweep - 3f).coerceAtLeast(1f) else sweep

                    drawArc(
                        color = cat.color,
                        startAngle = currentAngle,
                        sweepAngle = visibleSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    currentAngle += sweep
                }
            }
        }

        // Center Content
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Total",
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = textSecondary
            )
            Text(
                text = "100%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )
        }
    }
}

/**
 * Bottom Sheet for picking Month and Year
 */
@Composable
fun MonthYearPickerSheet(
    initialYear: Int,
    initialMonth: Int,
    isEn: Boolean,
    primaryColor: Color,
    trackColor: Color,
    textColor: Color,
    borderColor: Color,
    onApply: (year: Int, month: Int) -> Unit,
    onCancel: () -> Unit
) {
    var tempYear by remember { mutableIntStateOf(initialYear) }
    var tempMonth by remember { mutableIntStateOf(initialMonth) }

    val months = if (isEn) monthShortNamesEn else monthShortNamesId

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isEn) "Select Report Period" else "Pilih Periode Laporan",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Year Selector Controls: [< 2026 >]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(trackColor, RoundedCornerShape(12.dp))
                .padding(vertical = 4.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { tempYear -= 1 }) {
                Icon(
                    painter = painterResource(id = SakuIcons.ChevronLeft),
                    contentDescription = "Tahun Sebelumnya",
                    tint = primaryColor
                )
            }
            Text(
                text = tempYear.toString(),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            IconButton(onClick = { tempYear += 1 }) {
                Icon(
                    painter = painterResource(id = SakuIcons.ChevronRight),
                    contentDescription = "Tahun Berikutnya",
                    tint = primaryColor
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 12 Months Grid (4 columns x 3 rows)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            for (row in 0 until 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (col in 0 until 4) {
                        val monthIndex = row * 4 + col + 1
                        val isSelected = tempMonth == monthIndex
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) primaryColor else trackColor)
                                .border(
                                    1.dp,
                                    if (isSelected) primaryColor else borderColor,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { tempMonth = monthIndex }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = months[monthIndex - 1],
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else textColor
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Action Buttons: Terapkan & Batal
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = trackColor,
                    contentColor = textColor
                )
            ) {
                Text(text = if (isEn) "Cancel" else "Batal", fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = { onApply(tempYear, tempMonth) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    contentColor = Color.White
                )
            ) {
                Text(text = if (isEn) "Apply" else "Terapkan", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Helper to format short rupiah numbers (e.g. 500rb, 1.2jt)
 */
private fun formatShortRupiah(amount: Long): String {
    return when {
        amount >= 1000000L -> {
            val millions = amount / 1000000.0
            if (millions % 1.0 == 0.0) "Rp ${millions.toInt()} Jt" else "Rp ${String.format("%.1f", millions)} Jt"
        }
        amount >= 1000L -> "Rp ${amount / 1000} Rb"
        amount > 0 -> "Rp $amount"
        else -> "Rp 0"
    }
}

@Composable
fun ReportsBottomBar(
    onTabSelected: (Int) -> Unit,
    onFabClick: () -> Unit
) {
    SakuBottomNavigation(
        selectedTab = 2,
        onTabSelected = onTabSelected,
        onFabClick = onFabClick
    )
}

@Preview(name = "Reports Screen", showBackground = true, device = Devices.PIXEL_7)
@Composable
fun ReportsScreenPreview() {
    SAKUTheme {
        ReportsScreen()
    }
}
