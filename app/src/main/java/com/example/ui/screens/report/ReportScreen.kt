package com.example.ui.screens.report

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.Formatters
import com.example.domain.model.CategoryExpenseSummary
import com.example.domain.model.TransactionType
import com.example.ui.components.SakuCard
import com.example.ui.components.SakuTopBar
import com.example.ui.components.getCategoryIconVector
import com.example.ui.theme.SakuCreamBackground
import com.example.ui.theme.SakuCreamBorder
import com.example.ui.theme.SakuCreamSurface
import com.example.ui.theme.SakuDarkGreen
import com.example.ui.theme.SakuExpenseRed
import com.example.ui.theme.SakuExpenseRedBg
import com.example.ui.theme.SakuGoldAccent
import com.example.ui.theme.SakuGoldLight
import com.example.ui.theme.SakuIncomeGreen
import com.example.ui.theme.SakuIncomeGreenBg
import com.example.ui.theme.SakuLightGreen
import com.example.ui.theme.SakuTextMuted
import com.example.ui.theme.SakuTextPrimary
import com.example.ui.theme.SakuTextSecondary
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel = viewModel(),
    onNavigateToExport: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedCat by viewModel.selectedCategory.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SakuCreamBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Bar with Quick Export Action
            item {
                SakuTopBar(
                    title = "Laporan",
                    canNavigateBack = false,
                    actions = {
                        Surface(
                            shape = CircleShape,
                            color = SakuCreamSurface,
                            border = BorderStroke(1.dp, SakuCreamBorder),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            IconButton(
                                onClick = onNavigateToExport,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "Unduh Laporan",
                                    tint = SakuDarkGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                )
            }

            // 2. Filter Periode Utama (Sleek Horizontal Segmented Pills)
            item {
                PeriodFilterSection(
                    selectedPeriod = uiState.period,
                    periodTitle = uiState.selectedPeriodTitle,
                    onSelectPeriod = { viewModel.selectPeriod(it) },
                    onSelectCustomRange = { start, end -> viewModel.selectCustomRange(start, end) }
                )
            }

            // 3. Hero Financial Summary Card (Net Cashflow + In/Out Breakdown)
            item {
                HeroSummaryCard(
                    netCashflow = uiState.netCashflow,
                    totalIncome = uiState.totalIncome,
                    totalExpense = uiState.totalExpense,
                    totalTransfer = uiState.totalTransfer,
                    incomeChangePercent = uiState.incomeChangePercent,
                    expenseChangePercent = uiState.expenseChangePercent,
                    netChangePercent = uiState.netChangePercent
                )
            }

            // 4. Unified Analytics Hub (Tabbed: Arus Kas / Kategori / Tren Saldo)
            item {
                AnalyticsHubSection(
                    selectedTab = uiState.selectedChartTab,
                    onTabSelected = { viewModel.selectChartTab(it) },
                    cashflowPoints = uiState.cashflowPoints,
                    segments = uiState.donutSegments,
                    categoryBreakdown = uiState.categoryBreakdown,
                    totalExpense = uiState.totalExpense,
                    selectedCategory = selectedCat,
                    onCategoryTap = { segment ->
                        if (selectedCat == segment) {
                            viewModel.clearSelection()
                        } else {
                            viewModel.selectCategory(segment)
                        }
                    },
                    balancePoints = uiState.balancePoints
                )
            }

            // 5. Transaksi Terbesar (Top 5 / 10 / 20 & Tipe Filter)
            item {
                TopTransactionsSection(
                    transactions = uiState.topTransactions,
                    currentFilter = uiState.topTransactionFilter,
                    currentTypeFilter = uiState.transactionTypeFilter,
                    onFilterChange = { viewModel.setTopTransactionFilter(it) },
                    onTypeFilterChange = { viewModel.setTransactionTypeFilter(it) }
                )
            }
        }
    }
}

// ============================================================
// 2. Filter Periode Utama (Horizontal Segmented Pills)
// ============================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodFilterSection(
    selectedPeriod: ReportPeriod,
    periodTitle: String,
    onSelectPeriod: (ReportPeriod) -> Unit,
    onSelectCustomRange: (Long, Long) -> Unit
) {
    var showRangePicker by remember { mutableStateOf(false) }

    val periodOptions = listOf(
        ReportPeriod.TODAY to "Harian",
        ReportPeriod.THIS_WEEK to "Mingguan",
        ReportPeriod.THIS_MONTH to "Bulanan",
        ReportPeriod.THIS_YEAR to "Tahun Ini",
        ReportPeriod.CUSTOM to "Kustom"
    )

    SakuCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = SakuCreamSurface,
        cornerRadius = 14.dp,
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(periodOptions.size) { index ->
                    val (period, label) = periodOptions[index]
                    val isSelected = selectedPeriod == period

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                if (period == ReportPeriod.CUSTOM) {
                                    showRangePicker = true
                                } else {
                                    onSelectPeriod(period)
                                }
                            },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) SakuDarkGreen else SakuCreamBackground,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) SakuDarkGreen else SakuCreamBorder
                        )
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) Color.White else SakuTextSecondary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            if (selectedPeriod == ReportPeriod.CUSTOM && periodTitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = SakuGoldAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Rentang: $periodTitle",
                        style = MaterialTheme.typography.labelSmall,
                        color = SakuDarkGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    if (showRangePicker) {
        val dateRangePickerState = rememberDateRangePickerState()
        DatePickerDialog(
            onDismissRequest = { showRangePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        val start = dateRangePickerState.selectedStartDateMillis
                        val end = dateRangePickerState.selectedEndDateMillis ?: start
                        if (start != null && end != null) {
                            onSelectCustomRange(start, end)
                        }
                        showRangePicker = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SakuDarkGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text("Pilih", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRangePicker = false }) {
                    Text("Batal", color = SakuTextSecondary)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = SakuCreamSurface
            )
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                title = {
                    Text(
                        text = "Pilih Rentang Tanggal",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = SakuDarkGreen,
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp)
                    )
                },
                headline = {
                    Text(
                        text = "Laporan Periode",
                        style = MaterialTheme.typography.bodySmall,
                        color = SakuTextMuted,
                        modifier = Modifier.padding(start = 24.dp)
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ============================================================
// 3. Hero Financial Summary Card
// ============================================================
@Composable
fun HeroSummaryCard(
    netCashflow: Double,
    totalIncome: Double,
    totalExpense: Double,
    totalTransfer: Double,
    incomeChangePercent: Double,
    expenseChangePercent: Double,
    netChangePercent: Double
) {
    val isSurplus = netCashflow > 0
    val isDeficit = netCashflow < 0
    val netFormatted = if (isSurplus) {
        "+${Formatters.formatRupiah(netCashflow)}"
    } else if (isDeficit) {
        "-${Formatters.formatRupiah(kotlin.math.abs(netCashflow))}"
    } else {
        Formatters.formatRupiah(0.0)
    }

    SakuCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = SakuCreamSurface,
        cornerRadius = 16.dp,
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Net Cashflow Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Arus Kas Bersih",
                        style = MaterialTheme.typography.labelMedium,
                        color = SakuTextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = netFormatted,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isSurplus) SakuIncomeGreen else if (isDeficit) SakuExpenseRed else SakuTextPrimary
                    )
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSurplus) SakuIncomeGreenBg else if (isDeficit) SakuExpenseRedBg else SakuCreamBackground,
                    border = BorderStroke(
                        1.dp,
                        if (isSurplus) SakuIncomeGreen.copy(alpha = 0.3f) else if (isDeficit) SakuExpenseRed.copy(alpha = 0.3f) else SakuCreamBorder
                    )
                ) {
                    Text(
                        text = if (isSurplus) "Surplus" else if (isDeficit) "Defisit" else "Seimbang",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isSurplus) SakuIncomeGreen else if (isDeficit) SakuExpenseRed else SakuTextSecondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Divider
            HorizontalDivider(
                color = SakuCreamBorder.copy(alpha = 0.7f),
                thickness = 1.dp
            )

            // 2-Column: Pemasukan & Pengeluaran
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Pemasukan
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(SakuIncomeGreenBg, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = SakuIncomeGreen,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Text(
                            text = "Pemasukan",
                            style = MaterialTheme.typography.labelSmall,
                            color = SakuTextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Formatters.formatRupiah(totalIncome),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = SakuTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (incomeChangePercent != 0.0) {
                            val sign = if (incomeChangePercent > 0) "+" else ""
                            "$sign%.1f%% vs lalu".format(incomeChangePercent)
                        } else "Stabil vs lalu",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (incomeChangePercent >= 0) SakuIncomeGreen else SakuExpenseRed
                    )
                }

                // Pengeluaran
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(SakuExpenseRedBg, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = null,
                                tint = SakuExpenseRed,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Text(
                            text = "Pengeluaran",
                            style = MaterialTheme.typography.labelSmall,
                            color = SakuTextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Formatters.formatRupiah(totalExpense),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = SakuTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (expenseChangePercent != 0.0) {
                            val sign = if (expenseChangePercent > 0) "+" else ""
                            "$sign%.1f%% vs lalu".format(expenseChangePercent)
                        } else "Stabil vs lalu",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (expenseChangePercent <= 0) SakuIncomeGreen else SakuExpenseRed
                    )
                }
            }

            // Transfer Footnote if exists
            if (totalTransfer > 0.0) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SakuGoldLight.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = SakuGoldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Perpindahan Aset",
                                style = MaterialTheme.typography.labelSmall,
                                color = SakuTextSecondary
                            )
                        }
                        Text(
                            text = Formatters.formatRupiah(totalTransfer),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = SakuTextPrimary
                        )
                    }
                }
            }
        }
    }
}

// Backward-compatible alias for existing callers/tests
@Composable
fun SummarySection(
    totalIncome: Double,
    totalExpense: Double,
    totalTransfer: Double,
    incomeChangePercent: Double,
    expenseChangePercent: Double
) {
    HeroSummaryCard(
        netCashflow = totalIncome - totalExpense,
        totalIncome = totalIncome,
        totalExpense = totalExpense,
        totalTransfer = totalTransfer,
        incomeChangePercent = incomeChangePercent,
        expenseChangePercent = expenseChangePercent,
        netChangePercent = 0.0
    )
}

// ============================================================
// 4. Unified Analytics Hub (Tabbed Switcher)
// ============================================================
@Composable
fun AnalyticsHubSection(
    selectedTab: ReportChartTab,
    onTabSelected: (ReportChartTab) -> Unit,
    cashflowPoints: List<CashflowPoint>,
    segments: List<DonutCategorySegment>,
    categoryBreakdown: List<CategoryExpenseSummary>,
    totalExpense: Double,
    selectedCategory: DonutCategorySegment?,
    onCategoryTap: (DonutCategorySegment) -> Unit,
    balancePoints: List<BalancePoint>
) {
    SakuCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = SakuCreamSurface,
        cornerRadius = 16.dp,
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Segmented Switcher Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SakuCreamBackground, RoundedCornerShape(12.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ReportChartTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) SakuDarkGreen else Color.Transparent)
                            .clickable { onTabSelected(tab) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) Color.White else SakuTextSecondary,
                            maxLines = 1
                        )
                    }
                }
            }

            // Dynamic Chart Content
            when (selectedTab) {
                ReportChartTab.CASHFLOW -> {
                    CashflowChartContent(points = cashflowPoints)
                }
                ReportChartTab.CATEGORY -> {
                    CategoryDonutContent(
                        segments = segments,
                        categoryBreakdown = categoryBreakdown,
                        totalExpense = totalExpense,
                        selectedCategory = selectedCategory,
                        onCategoryTap = onCategoryTap
                    )
                }
                ReportChartTab.BALANCE_TREND -> {
                    BalanceTrendContent(points = balancePoints)
                }
            }
        }
    }
}

// ------------------------------------------------------------
// 4A. Tab 1: Cashflow Line Chart Content
// ------------------------------------------------------------
@Composable
fun CashflowChartContent(points: List<CashflowPoint>) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Arus Pemasukan & Pengeluaran",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = SakuDarkGreen
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ChartLegend(color = SakuIncomeGreen, label = "Masuk")
                ChartLegend(color = SakuExpenseRed, label = "Keluar")
            }
        }

        if (points.isEmpty()) {
            EmptyChartPlaceholder(message = "Belum ada transaksi di periode ini")
        } else {
            // Tooltip banner on tap
            selectedIndex?.let { idx ->
                if (idx in points.indices) {
                    val pt = points[idx]
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SakuCreamBackground,
                        border = BorderStroke(1.dp, SakuCreamBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = pt.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = SakuDarkGreen
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "+${Formatters.formatRupiah(pt.income)}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = SakuIncomeGreen
                                )
                                Text(
                                    text = "-${Formatters.formatRupiah(pt.expense)}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = SakuExpenseRed
                                )
                            }
                        }
                    }
                }
            }

            // Line Chart with Dedicated Clean Y-Axis
            val maxVal = if (points.isNotEmpty()) max(1.0, points.maxOf { max(it.income, it.expense) }) else 1.0
            val pointsCount = points.size

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Y-axis labels Column (left side)
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = 8.dp, top = 8.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = formatCompactAmount(maxVal),
                        style = MaterialTheme.typography.labelSmall,
                        color = SakuTextMuted
                    )
                    Text(
                        text = formatCompactAmount(maxVal / 2),
                        style = MaterialTheme.typography.labelSmall,
                        color = SakuTextMuted
                    )
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.labelSmall,
                        color = SakuTextMuted
                    )
                }

                // Line Chart Canvas
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .pointerInput(points) {
                            detectTapGestures { offset ->
                                val padH = 12.dp.toPx()
                                val availableWidth = size.width - (padH * 2)
                                if (pointsCount > 1) {
                                    val step = availableWidth / (pointsCount - 1)
                                    val touchedIndex = ((offset.x - padH + (step / 2)) / step).toInt()
                                    selectedIndex = touchedIndex.coerceIn(0, pointsCount - 1)
                                } else {
                                    selectedIndex = 0
                                }
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val padH = 12.dp.toPx()
                    val padV = 16.dp.toPx()
                    val chartW = width - (padH * 2)
                    val chartH = height - (padV * 2)

                    // Guidelines
                    for (i in 0..2) {
                        val y = padV + (chartH * (i / 2f))
                        drawLine(
                            color = SakuCreamBorder.copy(alpha = 0.6f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                    }

                    if (pointsCount == 1) {
                        val cx = width / 2f
                        val cyIncome = padV + chartH * (1f - (points[0].income / maxVal).toFloat())
                        val cyExpense = padV + chartH * (1f - (points[0].expense / maxVal).toFloat())

                        drawCircle(color = SakuIncomeGreen, radius = 5.dp.toPx(), center = Offset(cx, cyIncome))
                        drawCircle(color = SakuExpenseRed, radius = 5.dp.toPx(), center = Offset(cx, cyExpense))
                    } else {
                        val incomePath = Path()
                        val expensePath = Path()

                        points.forEachIndexed { i, pt ->
                            val x = padH + (i.toFloat() / (pointsCount - 1)) * chartW
                            val yInc = padV + chartH * (1f - (pt.income / maxVal).toFloat().coerceIn(0f, 1f))
                            val yExp = padV + chartH * (1f - (pt.expense / maxVal).toFloat().coerceIn(0f, 1f))

                            if (i == 0) {
                                incomePath.moveTo(x, yInc)
                                expensePath.moveTo(x, yExp)
                            } else {
                                incomePath.lineTo(x, yInc)
                                expensePath.lineTo(x, yExp)
                            }
                        }

                        // Draw lines
                        drawPath(
                            path = incomePath,
                            color = SakuIncomeGreen,
                            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawPath(
                            path = expensePath,
                            color = SakuExpenseRed,
                            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Draw point circles
                        points.forEachIndexed { i, pt ->
                            val x = padH + (i.toFloat() / (pointsCount - 1)) * chartW
                            val yInc = padV + chartH * (1f - (pt.income / maxVal).toFloat().coerceIn(0f, 1f))
                            val yExp = padV + chartH * (1f - (pt.expense / maxVal).toFloat().coerceIn(0f, 1f))

                            val isSel = selectedIndex == i
                            val radius = if (isSel) 5.5.dp.toPx() else 3.dp.toPx()

                            drawCircle(color = SakuIncomeGreen, radius = radius, center = Offset(x, yInc))
                            drawCircle(color = Color.White, radius = radius / 2f, center = Offset(x, yInc))

                            drawCircle(color = SakuExpenseRed, radius = radius, center = Offset(x, yExp))
                            drawCircle(color = Color.White, radius = radius / 2f, center = Offset(x, yExp))
                        }
                    }
                }
            }

            // X-Axis labels
            if (points.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 36.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = points.first().label,
                        style = MaterialTheme.typography.labelSmall,
                        color = SakuTextMuted
                    )
                    if (points.size > 2) {
                        Text(
                            text = points[points.size / 2].label,
                            style = MaterialTheme.typography.labelSmall,
                            color = SakuTextMuted
                        )
                    }
                    Text(
                        text = points.last().label,
                        style = MaterialTheme.typography.labelSmall,
                        color = SakuTextMuted
                    )
                }
            }
        }
    }
}

// Backward-compatible alias
@Composable
fun CashflowLineChartSection(points: List<CashflowPoint>) {
    SakuCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = SakuCreamSurface,
        cornerRadius = 16.dp,
        elevation = 2.dp
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            CashflowChartContent(points = points)
        }
    }
}

// ------------------------------------------------------------
// 4B. Tab 2: Category Donut & Breakdown Content
// ------------------------------------------------------------
@Composable
fun CategoryDonutContent(
    segments: List<DonutCategorySegment>,
    categoryBreakdown: List<CategoryExpenseSummary>,
    totalExpense: Double,
    selectedCategory: DonutCategorySegment? = null,
    onCategoryTap: (DonutCategorySegment) -> Unit = {}
) {
    var showAllCategories by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Pengeluaran Berdasarkan Kategori",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = SakuDarkGreen
        )

        if (segments.isEmpty() || totalExpense <= 0) {
            EmptyChartPlaceholder(message = "Belum ada pengeluaran pada periode ini")
        } else {
            // Donut Visual + Center Text
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .size(150.dp)
                        .pointerInput(segments) {
                            detectTapGestures { offset ->
                                val centerX = size.width / 2f
                                val centerY = size.height / 2f
                                val dx = offset.x - centerX
                                val dy = offset.y - centerY
                                val distance = kotlin.math.sqrt(dx * dx + dy * dy)
                                val strokeWidth = 26.dp.toPx()
                                val outerRadius = min(size.width, size.height) / 2f
                                val innerRadius = outerRadius - strokeWidth

                                if (distance in innerRadius..outerRadius) {
                                    var angle = Math.toDegrees(kotlin.math.atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                    angle = (angle + 90f + 360f) % 360f

                                    var startAngle = 0f
                                    for (seg in segments) {
                                        val sweepAngle = (seg.percentage * 360f).coerceAtLeast(1.5f)
                                        if (angle >= startAngle && angle < startAngle + sweepAngle) {
                                            onCategoryTap(seg)
                                            break
                                        }
                                        startAngle += sweepAngle
                                    }
                                }
                            }
                        }
                ) {
                    val strokeWidth = 26.dp.toPx()
                    val arcRadius = (size.minDimension - strokeWidth) / 2f
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val arcTopLeft = Offset(center.x - arcRadius, center.y - arcRadius)
                    val arcSize = Size(arcRadius * 2, arcRadius * 2)

                    var startAngle = -90f
                    segments.forEach { seg ->
                        val sweepAngle = (seg.percentage * 360f).coerceAtLeast(1.5f)
                        val color = try {
                            Color(android.graphics.Color.parseColor(seg.colorHex))
                        } catch (e: Exception) {
                            SakuDarkGreen
                        }

                        val isSelected = selectedCategory == null || selectedCategory == seg
                        val alpha = if (isSelected) 1f else 0.35f

                        drawArc(
                            color = color.copy(alpha = alpha),
                            startAngle = startAngle,
                            sweepAngle = sweepAngle - 1f,
                            useCenter = false,
                            topLeft = arcTopLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        startAngle += sweepAngle
                    }
                }

                // Donut center label
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Total Keluar",
                        style = MaterialTheme.typography.labelSmall,
                        color = SakuTextMuted
                    )
                    Text(
                        text = Formatters.formatRupiah(totalExpense),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = SakuTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Kartu detail kategori terpilih (tepat di bawah donat)
            selectedCategory?.let { sel ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCategoryTap(sel) },
                    color = SakuDarkGreen,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = getCategoryIconVector(sel.categoryIcon),
                            contentDescription = null,
                            tint = SakuCreamSurface,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = sel.categoryName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = SakuCreamSurface,
                                maxLines = 1
                            )
                            Text(
                                text = "${Formatters.formatRupiah(sel.amount)} • ${(sel.percentage * 100).roundToInt()}% • ${sel.transactionCount} transaksi",
                                style = MaterialTheme.typography.labelSmall,
                                color = SakuCreamSurface.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            // Category List Progress Rows
            val displayList = if (showAllCategories) categoryBreakdown else categoryBreakdown.take(5)

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                displayList.forEachIndexed { idx, cat ->
                    val color = try {
                        Color(android.graphics.Color.parseColor(CATEGORY_CHART_COLORS.getOrElse(idx) { "#133E35" }))
                    } catch (e: Exception) {
                        SakuDarkGreen
                    }
                    val isSelected = selectedCategory == null || selectedCategory.categoryName == cat.categoryName
                    val alpha = if (isSelected) 1f else 0.35f
                    CategoryProgressRow(
                        name = cat.categoryName,
                        icon = cat.categoryIcon,
                        amount = cat.totalAmount,
                        percentage = cat.percentage,
                        indicatorColor = color,
                        alpha = alpha,
                        onClick = {
                            val target = segments.find { it.categoryName == cat.categoryName }
                            if (target != null) {
                                onCategoryTap(target)
                            }
                        }
                    )
                }
            }

            // Toggle "Lihat Semua / Sembunyikan"
            if (categoryBreakdown.size > 5) {
                TextButton(
                    onClick = { showAllCategories = !showAllCategories },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = if (showAllCategories) "Sembunyikan" else "Lihat Semua (${categoryBreakdown.size} Kategori)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = SakuDarkGreen
                    )
                    Icon(
                        imageVector = if (showAllCategories) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = SakuDarkGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// Backward-compatible alias
@Composable
fun CategoryDonutSection(
    segments: List<DonutCategorySegment>,
    categoryBreakdown: List<CategoryExpenseSummary>,
    totalExpense: Double,
    selectedCategory: DonutCategorySegment? = null,
    onCategoryTap: (DonutCategorySegment) -> Unit = {}
) {
    SakuCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = SakuCreamSurface,
        cornerRadius = 16.dp,
        elevation = 2.dp
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            CategoryDonutContent(
                segments = segments,
                categoryBreakdown = categoryBreakdown,
                totalExpense = totalExpense,
                selectedCategory = selectedCategory,
                onCategoryTap = onCategoryTap
            )
        }
    }
}

// ------------------------------------------------------------
// 4C. Tab 3: Balance Trend Content
// ------------------------------------------------------------
@Composable
fun BalanceTrendContent(points: List<BalancePoint>) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Histori Pertumbuhan Saldo",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = SakuDarkGreen
            )
            ChartLegend(color = SakuGoldAccent, label = "Total Saldo")
        }

        if (points.isEmpty()) {
            EmptyChartPlaceholder(message = "Belum ada histori saldo")
        } else {
            // Tooltip banner on tap
            selectedIndex?.let { idx ->
                if (idx in points.indices) {
                    val pt = points[idx]
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SakuCreamBackground,
                        border = BorderStroke(1.dp, SakuCreamBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = pt.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = SakuDarkGreen
                            )
                            Text(
                                text = Formatters.formatRupiah(pt.balance),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = SakuGoldAccent
                            )
                        }
                    }
                }
            }

            // Balance Line Chart Canvas with Left Y-Axis
            val minVal = if (points.isNotEmpty()) points.minOf { it.balance } else 0.0
            val maxVal = if (points.isNotEmpty()) points.maxOf { it.balance } else 1.0
            val range = max(1.0, maxVal - minVal)
            val pointsCount = points.size

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Y-axis labels Column (left side)
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = 8.dp, top = 8.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = formatCompactAmount(maxVal),
                        style = MaterialTheme.typography.labelSmall,
                        color = SakuTextMuted
                    )
                    Text(
                        text = formatCompactAmount(minVal),
                        style = MaterialTheme.typography.labelSmall,
                        color = SakuTextMuted
                    )
                }

                // Balance Line Canvas
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .pointerInput(points) {
                            detectTapGestures { offset ->
                                val padH = 12.dp.toPx()
                                val availableW = size.width - (padH * 2)
                                if (pointsCount > 1) {
                                    val step = availableW / (pointsCount - 1)
                                    val touched = ((offset.x - padH + (step / 2)) / step).toInt()
                                    selectedIndex = touched.coerceIn(0, pointsCount - 1)
                                } else {
                                    selectedIndex = 0
                                }
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val padH = 12.dp.toPx()
                    val padV = 16.dp.toPx()
                    val chartW = width - (padH * 2)
                    val chartH = height - (padV * 2)

                    // Guidelines
                    for (i in 0..2) {
                        val y = padV + (chartH * (i / 2f))
                        drawLine(
                            color = SakuCreamBorder.copy(alpha = 0.6f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                    }

                    if (pointsCount == 1) {
                        val cx = width / 2f
                        val cy = padV + (chartH / 2f)
                        drawCircle(color = SakuGoldAccent, radius = 5.dp.toPx(), center = Offset(cx, cy))
                    } else {
                        val linePath = Path()
                        val fillPath = Path()

                        points.forEachIndexed { i, pt ->
                            val x = padH + (i.toFloat() / (pointsCount - 1)) * chartW
                            val y = padV + chartH * (1f - ((pt.balance - minVal) / range).toFloat().coerceIn(0f, 1f))

                            if (i == 0) {
                                linePath.moveTo(x, y)
                                fillPath.moveTo(x, padV + chartH)
                                fillPath.lineTo(x, y)
                            } else {
                                linePath.lineTo(x, y)
                                fillPath.lineTo(x, y)
                            }

                            if (i == pointsCount - 1) {
                                fillPath.lineTo(x, padV + chartH)
                                fillPath.close()
                            }
                        }

                        // Gradient area fill
                        drawPath(
                            path = fillPath,
                            color = SakuGoldAccent.copy(alpha = 0.12f),
                            style = Fill
                        )

                        // Line path
                        drawPath(
                            path = linePath,
                            color = SakuGoldAccent,
                            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Point circles
                        points.forEachIndexed { i, pt ->
                            val x = padH + (i.toFloat() / (pointsCount - 1)) * chartW
                            val y = padV + chartH * (1f - ((pt.balance - minVal) / range).toFloat().coerceIn(0f, 1f))
                            val isSel = selectedIndex == i
                            val radius = if (isSel) 5.5.dp.toPx() else 3.dp.toPx()

                            drawCircle(color = SakuGoldAccent, radius = radius, center = Offset(x, y))
                            drawCircle(color = Color.White, radius = radius / 2f, center = Offset(x, y))
                        }
                    }
                }
            }

            // X-Axis labels
            if (points.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 36.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = points.first().label,
                        style = MaterialTheme.typography.labelSmall,
                        color = SakuTextMuted
                    )
                    Text(
                        text = points.last().label,
                        style = MaterialTheme.typography.labelSmall,
                        color = SakuTextMuted
                    )
                }
            }
        }
    }
}

// Backward-compatible alias
@Composable
fun BalanceTrendSection(points: List<BalancePoint>) {
    SakuCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = SakuCreamSurface,
        cornerRadius = 16.dp,
        elevation = 2.dp
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            BalanceTrendContent(points = points)
        }
    }
}

// ============================================================
// 5. Transaksi Terbesar (Top 5 / 10 / 20 with clean filters)
// ============================================================
@Composable
fun TopTransactionsSection(
    transactions: List<TopTransactionItem>,
    currentFilter: TopTransactionFilter,
    currentTypeFilter: TransactionTypeFilter,
    onFilterChange: (TopTransactionFilter) -> Unit,
    onTypeFilterChange: (TransactionTypeFilter) -> Unit
) {
    SakuCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = SakuCreamSurface,
        cornerRadius = 16.dp,
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header with Limit Selector (Top 5 / 10 / 20)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transaksi Terbesar",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = SakuDarkGreen
                )

                // Limit Selector Pills
                Row(
                    modifier = Modifier
                        .background(SakuCreamBackground, RoundedCornerShape(8.dp))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    TopTransactionFilter.entries.forEach { filter ->
                        val isSelected = currentFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) SakuDarkGreen else Color.Transparent)
                                .clickable { onFilterChange(filter) }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = filter.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) Color.White else SakuTextSecondary
                            )
                        }
                    }
                }
            }

            // Type filter horizontal pill bar
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(TransactionTypeFilter.entries.size) { index ->
                    val typeFilter = TransactionTypeFilter.entries[index]
                    val isSelected = currentTypeFilter == typeFilter
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onTypeFilterChange(typeFilter) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) SakuDarkGreen else SakuCreamBackground,
                        border = BorderStroke(1.dp, if (isSelected) SakuDarkGreen else SakuCreamBorder)
                    ) {
                        Text(
                            text = typeFilter.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) Color.White else SakuTextSecondary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Transaction items with dividers
            if (transactions.isEmpty()) {
                EmptyChartPlaceholder(message = "Tidak ada transaksi yang sesuai kriteria")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    transactions.forEachIndexed { index, tx ->
                        TopTransactionItemRow(item = tx)
                        if (index < transactions.size - 1) {
                            HorizontalDivider(
                                color = SakuCreamBorder.copy(alpha = 0.5f),
                                thickness = 0.5.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopTransactionItemRow(item: TopTransactionItem) {
    val isIncome = item.type == TransactionType.INCOME
    val isTransfer = item.type == TransactionType.TRANSFER

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        if (isIncome) SakuIncomeGreenBg
                        else if (isTransfer) SakuGoldLight
                        else SakuExpenseRedBg,
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isTransfer) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = null,
                        tint = SakuGoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Icon(
                        imageVector = getCategoryIconVector(item.categoryIcon),
                        contentDescription = null,
                        tint = if (isIncome) SakuIncomeGreen else SakuExpenseRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            // Title & Info
            Column {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = SakuTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (isTransfer) {
                    val source = item.sourceAssetName?.takeIf { it.isNotBlank() } ?: "-"
                    val target = item.targetAssetName?.takeIf { it.isNotBlank() } ?: "-"
                    Text(
                        text = "$source → $target • ${Formatters.formatShortDateIndo(item.dateMillis)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = SakuTextMuted
                    )
                } else {
                    Text(
                        text = "${item.categoryName} • ${Formatters.formatShortDateIndo(item.dateMillis)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = SakuTextMuted
                    )
                }
            }
        }

        // Amount
        Text(
            text = if (isIncome) {
                "+${Formatters.formatRupiah(item.amount)}"
            } else if (isTransfer) {
                Formatters.formatRupiah(item.amount)
            } else {
                "-${Formatters.formatRupiah(item.amount)}"
            },
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = if (isIncome) SakuIncomeGreen else if (isTransfer) SakuGoldAccent else SakuExpenseRed
        )
    }
}

// ============================================================
// 6. Tombol Export Laporan
// ============================================================
@Composable
fun ExportActionCard(onClick: () -> Unit) {
    SakuCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        backgroundColor = SakuDarkGreen,
        borderColor = SakuDarkGreen,
        cornerRadius = 16.dp,
        elevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Ekspor Laporan",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Excel, Cadangan Internal, & Cloud",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// ============================================================
// Shared Sub-components
// ============================================================
@Composable
fun CategoryProgressRow(
    name: String,
    icon: String,
    amount: Double,
    percentage: Float,
    indicatorColor: Color,
    alpha: Float = 1f,
    onClick: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .alpha(alpha),
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
                        .background(indicatorColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = getCategoryIconVector(icon),
                    contentDescription = null,
                    tint = SakuDarkGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SakuTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = Formatters.formatRupiah(amount),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = SakuTextPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${(percentage * 100).roundToInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = SakuTextMuted,
                    modifier = Modifier.width(36.dp),
                    textAlign = TextAlign.End
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        // Linear Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(SakuCreamBorder, RoundedCornerShape(2.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage.coerceIn(0f, 1f))
                    .height(4.dp)
                    .background(indicatorColor, RoundedCornerShape(2.dp))
            )
        }
    }
}

@Composable
fun ChartLegend(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = SakuTextSecondary
        )
    }
}

@Composable
fun EmptyChartPlaceholder(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = SakuTextMuted,
            textAlign = TextAlign.Center
        )
    }
}

private fun formatCompactAmount(amount: Double): String {
    return when {
        amount >= 1_000_000_000 -> "%.1fM".format(amount / 1_000_000_000)
        amount >= 1_000_000 -> "%.1fjt".format(amount / 1_000_000)
        amount >= 1_000 -> "%.0frb".format(amount / 1_000)
        else -> "%.0f".format(amount)
    }
}
