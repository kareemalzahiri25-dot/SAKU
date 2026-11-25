package com.saku.app.ui.reports

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
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
import kotlin.math.atan2
import kotlin.math.hypot

private val monthShortNamesId = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")

private data class TxFilterOption(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val activeBg: Color,
    val activeContent: Color
)
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
    var selectedCategoryName by remember { mutableStateOf<String?>(null) }
    var showCategoryDetailSheet by remember { mutableStateOf(false) }

    // 2. Sorting & Filter State
    var selectedTxFilter by remember { mutableIntStateOf(0) } // 0 = Semua, 1 = Pengeluaran, 2 = Pemasukan
    var sortCategoriesDesc by remember { mutableStateOf(true) } // Sort category breakdown by amount

    val allTransactions by dataRepository.allTransactions.collectAsState()

    val monthShortName = if (isEn) monthShortNamesEn[selectedMonth - 1] else monthShortNamesId[selectedMonth - 1]
    val monthFullName = if (isEn) monthFullNamesEn[selectedMonth - 1] else monthFullNamesId[selectedMonth - 1]
    val periodLabel = when (periodTab) {
        0 -> "16 - 22 $monthShortName $selectedYear"
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

    // Category Proportions Data (shared between summary card and detail sheet)
    val catFood = if (isEn) "Food & Beverage" else "Makan & Minum"
    val catTrans = if (isEn) "Transportation" else "Transportasi"
    val catFun = if (isEn) "Entertainment" else "Keinginan & Hiburan"
    val catBills = if (isEn) "Bills & Utilities" else "Tagihan & Lainnya"

    val rawCategories = listOf(
        CategoryBreakdownData(catFood, foodExpense, foodPct, SakuAmber),
        CategoryBreakdownData(catTrans, transportExpense, transportPct, SakuBlue),
        CategoryBreakdownData(catFun, entertainmentExpense, entertainmentPct, SakuExpenseRed),
        CategoryBreakdownData(catBills, billsExpense, billsPct, SakuPurple)
    )
    val categories = if (sortCategoriesDesc) {
        rawCategories.sortedByDescending { it.amount }
    } else {
        rawCategories.sortedBy { it.amount }
    }

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
            // 1. TOP BAR: Title + Month Dropdown + Ekspor Excel (Adaptive ConstraintLayout)
            // ==========================================
            item {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    val (titleRef, periodRef, exportRef) = createRefs()
                    val topRowBarrier = createBottomBarrier(titleRef, exportRef)

                    Text(
                        text = strings.reportsTitle,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = sakuColors.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.constrainAs(titleRef) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(exportRef.start, margin = 12.dp)
                            width = Dimension.fillToConstraints
                        }
                    )

                    // Ekspor Excel Button
                    Box(
                        modifier = Modifier
                            .constrainAs(exportRef) {
                                top.linkTo(titleRef.top)
                                bottom.linkTo(titleRef.bottom)
                                end.linkTo(parent.end)
                                width = Dimension.preferredWrapContent
                            }
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
                                color = sakuColors.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Month Dropdown Pill / Keterangan Tanggal (Opens Month-Year Picker)
                    Box(
                        modifier = Modifier
                            .constrainAs(periodRef) {
                                top.linkTo(topRowBarrier, margin = 10.dp)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                horizontalBias = 0f
                                width = Dimension.preferredWrapContent
                            }
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
                            color = sakuColors.textDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
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

                        Spacer(modifier = Modifier.height(18.dp))

                        // Coordinate Bar Chart with Sumbu X & Sumbu Y
                        ExpenseCoordinateBarChart(
                            periodTab = periodTab,
                            allTransactions = allTransactions,
                            currentExpense = totalExpense,
                            isEn = isEn,
                            primaryColor = sakuColors.primary,
                            trackColor = sakuColors.trackBg,
                            borderColor = sakuColors.border,
                            titleColor = sakuColors.title,
                            textSecondary = sakuColors.textSecondary
                        )
                    }
                }
            }

            // ==========================================
            // 4. PENGELUARAN PER KATEGORI & DONUT CHART CANVAS (ConstraintLayout)
            // ==========================================
            item {
                val selectedCategory = categories.firstOrNull { it.name == selectedCategoryName }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(20.dp), spotColor = sakuColors.cardShadow)
                        .background(sakuColors.surface, RoundedCornerShape(20.dp))
                        .border(1.dp, sakuColors.border, RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                        val (catTitleRef, sortBtnRef, donutChartRef, breakdownRef, detailInfoRef, viewDetailBtnRef) = createRefs()
                        val headerBarrier = createBottomBarrier(catTitleRef, sortBtnRef)
                        val chartBarrier = createBottomBarrier(donutChartRef, breakdownRef)

                        Text(
                            text = strings.expenseByCategoryTitle,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = sakuColors.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.constrainAs(catTitleRef) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(sortBtnRef.start, margin = 8.dp)
                                width = Dimension.fillToConstraints
                            }
                        )

                        // Functional Sort Order Button
                        Row(
                            modifier = Modifier
                                .constrainAs(sortBtnRef) {
                                    top.linkTo(catTitleRef.top)
                                    bottom.linkTo(catTitleRef.bottom)
                                    end.linkTo(parent.end)
                                    width = Dimension.preferredWrapContent
                                }
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

                        // Real Segmented Donut Chart via Canvas (Tap slice to reveal % & nominal)
                        SegmentedDonutChart(
                            categories = categories,
                            totalExpense = totalExpense,
                            trackColor = sakuColors.trackBg,
                            titleColor = sakuColors.title,
                            textSecondary = sakuColors.textSecondary,
                            selectedCategoryName = selectedCategoryName,
                            onCategorySelected = { tappedName ->
                                selectedCategoryName = if (selectedCategoryName == tappedName) null else tappedName
                            },
                            modifier = Modifier
                                .size(96.dp)
                                .constrainAs(donutChartRef) {
                                    top.linkTo(headerBarrier, margin = 14.dp)
                                    start.linkTo(parent.start)
                                    bottom.linkTo(chartBarrier)
                                    verticalBias = 0.5f
                                }
                        )

                        // Category names only by default; percentages & nominals appear when pie chart is tapped
                        Column(
                            modifier = Modifier.constrainAs(breakdownRef) {
                                top.linkTo(headerBarrier, margin = 14.dp)
                                start.linkTo(donutChartRef.end, margin = 16.dp)
                                end.linkTo(parent.end)
                                width = Dimension.fillToConstraints
                            },
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categories.forEach { cat ->
                                val isSelectedCat = selectedCategoryName == cat.name
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSelectedCat) cat.color.copy(alpha = 0.1f) else Color.Transparent
                                        )
                                        .padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(if (cat.amount > 0) cat.color else sakuColors.border, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = cat.name,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelectedCat) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelectedCat) cat.color else sakuColors.textDark,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // Tapped Pie Chart Info Banner (shows percentage & nominal when pie chart is pressed)
                        if (selectedCategory != null) {
                            Row(
                                modifier = Modifier
                                    .constrainAs(detailInfoRef) {
                                        top.linkTo(chartBarrier, margin = 12.dp)
                                        start.linkTo(parent.start)
                                        end.linkTo(parent.end)
                                        width = Dimension.fillToConstraints
                                    }
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(selectedCategory.color.copy(alpha = 0.12f))
                                    .border(1.dp, selectedCategory.color.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 9.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(9.dp)
                                            .background(selectedCategory.color, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = selectedCategory.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = sakuColors.title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${selectedCategory.percent}%",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = selectedCategory.color
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = formatRupiah(selectedCategory.amount),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = sakuColors.title
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = if (isEn) "Tap the pie chart to see percentage & amount" else "Ketuk pie chart untuk melihat persentase & nominal",
                                fontSize = 11.sp,
                                color = sakuColors.textSecondary,
                                modifier = Modifier.constrainAs(detailInfoRef) {
                                    top.linkTo(chartBarrier, margin = 10.dp)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                    width = Dimension.fillToConstraints
                                }
                            )
                        }

                        // "Lihat Detail" Button
                        Box(
                            modifier = Modifier
                                .constrainAs(viewDetailBtnRef) {
                                    top.linkTo(detailInfoRef.bottom, margin = 12.dp)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                    bottom.linkTo(parent.bottom)
                                    width = Dimension.fillToConstraints
                                }
                                .clip(RoundedCornerShape(12.dp))
                                .background(sakuColors.trackBg)
                                .border(1.dp, sakuColors.border, RoundedCornerShape(12.dp))
                                .clickable { showCategoryDetailSheet = true }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isEn) "View Details" else "Lihat Detail",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = sakuColors.primary
                            )
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
                    val yellowFilter = Color(0xFFEAB308)
                    val brightGreenFilter = Color(0xFF22C55E)
                    val filterOptions = listOf(
                        TxFilterOption(strings.filterAll, Icons.Outlined.FormatListBulleted, yellowFilter, Color(0xFF142921)),
                        TxFilterOption(strings.filterExpense, Icons.Outlined.NorthEast, SakuExpenseRed, Color.White),
                        TxFilterOption(strings.filterIncome, Icons.Outlined.SouthWest, brightGreenFilter, Color.White)
                    )

                    filterOptions.forEachIndexed { index, option ->
                        val label = option.label
                        val icon = option.icon
                        val activeBg = option.activeBg
                        val activeContent = option.activeContent
                        val isSelected = selectedTxFilter == index

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) activeBg else sakuColors.surface)
                                .border(
                                    1.dp,
                                    if (isSelected) activeBg else activeBg.copy(alpha = 0.5f),
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
                                    tint = if (isSelected) activeContent else activeBg,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) activeContent else sakuColors.chipInactiveText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
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
    // 6. CATEGORY DETAIL MODAL BOTTOM SHEET
    // ==========================================
    if (showCategoryDetailSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCategoryDetailSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = sakuColors.surface
        ) {
            CategoryDetailSheet(
                title = strings.expenseByCategoryTitle,
                periodLabel = periodLabel,
                categories = categories,
                totalExpense = totalExpense,
                isEn = isEn,
                primaryColor = sakuColors.primary,
                trackColor = sakuColors.trackBg,
                titleColor = sakuColors.title,
                textDark = sakuColors.textDark,
                textSecondary = sakuColors.textSecondary,
                borderColor = sakuColors.border,
                onClose = { showCategoryDetailSheet = false }
            )
        }
    }

    // ==========================================
    // 7. MONTH-YEAR PICKER MODAL BOTTOM SHEET
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
    isEn: Boolean = false,
    primaryColor: Color,
    trackColor: Color,
    borderColor: Color,
    titleColor: Color = Color.Unspecified,
    textSecondary: Color
) {
    var selectedBarIndex by remember(periodTab) { mutableIntStateOf(-1) }

    // Generate bars dynamically according to active periodTab: (shortLabel, fullDetailLabel, amount)
    val barsData: List<Triple<String, String, Long>> = when (periodTab) {
        0 -> {
            // Weekly: Sen, Sel, Rab, Kam, Jum, Sab, Min
            listOf(
                Triple("Sen", if (isEn) "Monday" else "Senin", 0L),
                Triple("Sel", if (isEn) "Tuesday" else "Selasa", 0L),
                Triple("Rab", if (isEn) "Wednesday" else "Rabu", 450000L),
                Triple("Kam", if (isEn) "Thursday" else "Kamis", 0L),
                Triple("Jum", if (isEn) "Friday" else "Jumat", 180000L),
                Triple("Sab", if (isEn) "Saturday" else "Sabtu", 120000L),
                Triple("Min", if (isEn) "Sunday" else "Minggu", 150000L)
            )
        }
        2 -> {
            // Yearly: Q1, Q2, Q3, Q4
            listOf(
                Triple("Q1", if (isEn) "Quarter 1 (Jan–Mar)" else "Kuartal 1 (Jan–Mar)", 1200000L),
                Triple("Q2", if (isEn) "Quarter 2 (Apr–Jun)" else "Kuartal 2 (Apr–Jun)", 1850000L),
                Triple("Q3", if (isEn) "Quarter 3 (Jul–Sep)" else "Kuartal 3 (Jul–Sep)", 1600000L),
                Triple("Q4", if (isEn) "Quarter 4 (Oct–Dec)" else "Kuartal 4 (Okt–Des)", currentExpense)
            )
        }
        else -> {
            // Monthly: Jun, Jul, Agu, Sep, Okt, Nov
            listOf(
                Triple("Jun", if (isEn) "June" else "Juni", 320000L),
                Triple("Jul", if (isEn) "July" else "Juli", 550000L),
                Triple("Agu", if (isEn) "August" else "Agustus", 410000L),
                Triple("Sep", if (isEn) "September" else "September", 630000L),
                Triple("Okt", if (isEn) "October" else "Oktober", 700000L),
                Triple("Nov", if (isEn) "November" else "November", currentExpense)
            )
        }
    }

    val maxBarValue = barsData.maxOfOrNull { it.third } ?: 0L
    val maxAmount = maxBarValue.coerceAtLeast(100000L)
    val midAmount = maxAmount / 2
    val selectedBar = barsData.getOrNull(selectedBarIndex)

    Column(modifier = Modifier.fillMaxWidth()) {
        // Green Detail Card when a bar is tapped; subtle hint when none is selected
        if (selectedBar != null) {
            val isHighest = selectedBar.third > 0L && selectedBar.third == maxBarValue
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(primaryColor.copy(alpha = 0.12f))
                    .border(1.dp, primaryColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .background(primaryColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = selectedBar.second,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (titleColor != Color.Unspecified) titleColor else primaryColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = when {
                                selectedBar.third == 0L -> if (isEn) "No expenses recorded" else "Belum ada pengeluaran"
                                isHighest -> if (isEn) "Highest expense in period" else "Pengeluaran tertinggi periode ini"
                                else -> if (isEn) "Total expenses this period" else "Total pengeluaran periode ini"
                            },
                            fontSize = 10.sp,
                            color = textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatRupiah(selectedBar.third),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
            }
        } else {
            Text(
                text = if (isEn) "Tap a bar to see expense details" else "Ketuk salah satu batang untuk melihat detail",
                fontSize = 11.sp,
                color = textSecondary,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(136.dp)
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
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    barsData.forEachIndexed { index, (_, _, amount) ->
                        val isSelected = selectedBarIndex == index
                        val barHeightFactor = (amount.toFloat() / maxAmount.toFloat()).coerceIn(0.05f, 1f)
                        val barHeightDp = (barHeightFactor * 95).dp

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable {
                                    selectedBarIndex = if (selectedBarIndex == index) -1 else index
                                }
                                .padding(bottom = 2.dp)
                        ) {
                            // Subtle nominal above every bar (no green pill badge)
                            Text(
                                text = if (amount > 0L) formatShortRupiah(amount) else "-",
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) primaryColor else textSecondary,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(3.dp))

                            // Pillar Bar (neutral outline/track when unselected, green when tapped)
                            Box(
                                modifier = Modifier
                                    .width(22.dp)
                                    .height(barHeightDp)
                                    .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                                    .background(if (isSelected) primaryColor else trackColor)
                                    .then(
                                        if (!isSelected) {
                                            Modifier.border(
                                                1.dp,
                                                borderColor,
                                                RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)
                                            )
                                        } else {
                                            Modifier
                                        }
                                    )
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
                .padding(start = 64.dp, end = 4.dp, top = 6.dp)
        ) {
            barsData.forEachIndexed { index, (label, _, _) ->
                val isSelected = selectedBarIndex == index
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) primaryColor else textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedBarIndex = if (selectedBarIndex == index) -1 else index
                        }
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
    modifier: Modifier = Modifier,
    selectedCategoryName: String? = null,
    onCategorySelected: ((String?) -> Unit)? = null
) {
    val activeCategories = remember(categories) { categories.filter { it.amount > 0 } }
    val selectedCategory = remember(categories, selectedCategoryName) {
        categories.firstOrNull { it.name == selectedCategoryName }
    }

    val tapModifier = if (onCategorySelected != null) {
        Modifier.pointerInput(activeCategories, totalExpense, selectedCategoryName) {
            detectTapGestures { offset ->
                if (totalExpense <= 0L || activeCategories.isEmpty()) {
                    onCategorySelected(null)
                    return@detectTapGestures
                }
                val centerX = size.width / 2f
                val centerY = size.height / 2f
                val dx = offset.x - centerX
                val dy = offset.y - centerY
                val dist = hypot(dx, dy)
                val innerRadius = (minOf(size.width, size.height) / 2f) * 0.45f

                if (dist < innerRadius) {
                    // Tapping the center toggles between cleared and the top category
                    val nextName = if (selectedCategoryName != null) null else activeCategories.first().name
                    onCategorySelected(nextName)
                    return@detectTapGestures
                }

                // Convert tap angle to degrees starting at 12 o'clock (-90 deg), clockwise [0, 360)
                var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat() + 90f
                if (angle < 0f) angle += 360f

                var startDeg = 0f
                var tappedName: String? = null
                for (cat in activeCategories) {
                    val sweep = (cat.amount.toFloat() / totalExpense.toFloat()) * 360f
                    if (angle >= startDeg && angle <= startDeg + sweep) {
                        tappedName = cat.name
                        break
                    }
                    startDeg += sweep
                }
                onCategorySelected(tappedName)
            }
        }
    } else {
        Modifier
    }

    Box(
        modifier = modifier.then(tapModifier),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val baseStrokeWidth = 10.dp.toPx()
            val maxStrokeWidth = 13.dp.toPx()
            val diameter = size.minDimension - maxStrokeWidth
            val arcSize = Size(diameter, diameter)
            val topLeft = Offset(maxStrokeWidth / 2f, maxStrokeWidth / 2f)

            if (totalExpense <= 0L) {
                // Empty Track
                drawArc(
                    color = trackColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = baseStrokeWidth)
                )
            } else {
                var currentAngle = -90f // Start from 12 o'clock

                activeCategories.forEach { cat ->
                    val sweep = (cat.amount.toFloat() / totalExpense.toFloat()) * 360f
                    val visibleSweep = if (activeCategories.size > 1) (sweep - 3f).coerceAtLeast(1f) else sweep
                    val isSelected = selectedCategoryName == null || selectedCategoryName == cat.name
                    val stroke = if (selectedCategoryName == cat.name) maxStrokeWidth else baseStrokeWidth

                    drawArc(
                        color = if (isSelected) cat.color else cat.color.copy(alpha = 0.3f),
                        startAngle = currentAngle,
                        sweepAngle = visibleSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                    currentAngle += sweep
                }
            }
        }

        // Center Content
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (selectedCategory != null) {
                Text(
                    text = "${selectedCategory.percent}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = selectedCategory.color
                )
                Text(
                    text = formatShortRupiah(selectedCategory.amount),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = titleColor
                )
            } else {
                Text(
                    text = "Total",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    color = textSecondary
                )
                Text(
                    text = if (totalExpense > 0L) "100%" else "0%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
            }
        }
    }
}

/**
 * Bottom Sheet showing centered Pie Chart at the top and complete Category Detail list below
 */
@Composable
fun CategoryDetailSheet(
    title: String,
    periodLabel: String,
    categories: List<CategoryBreakdownData>,
    totalExpense: Long,
    isEn: Boolean,
    primaryColor: Color,
    trackColor: Color,
    titleColor: Color,
    textDark: Color,
    textSecondary: Color,
    borderColor: Color,
    onClose: () -> Unit
) {
    var sheetSelectedCategory by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$periodLabel • ${formatRupiah(totalExpense)}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Pie chart di tengah
        SegmentedDonutChart(
            categories = categories,
            totalExpense = totalExpense,
            trackColor = trackColor,
            titleColor = titleColor,
            textSecondary = textSecondary,
            selectedCategoryName = sheetSelectedCategory,
            onCategorySelected = { tapped ->
                sheetSelectedCategory = if (sheetSelectedCategory == tapped) null else tapped
            },
            modifier = Modifier.size(156.dp)
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Penjelasan detail di bawah pie chart
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(trackColor.copy(alpha = 0.55f))
                .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                .size(10.dp)
                                .background(if (cat.amount > 0) cat.color else borderColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = cat.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = textDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${cat.percent}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (cat.amount > 0) cat.color else textSecondary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = formatRupiah(cat.amount),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = titleColor
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor,
                contentColor = Color.White
            )
        ) {
            Text(
                text = if (isEn) "Close" else "Tutup",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
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
