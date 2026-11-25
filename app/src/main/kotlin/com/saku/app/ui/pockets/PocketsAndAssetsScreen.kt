package com.saku.app.ui.pockets

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saku.app.data.AssetItem
import com.saku.app.data.AssetsOverviewData
import com.saku.app.data.CategoryType
import com.saku.app.data.DataRepository
import com.saku.app.data.DefaultDataRepository
import com.saku.app.data.PocketCategory
import com.saku.app.data.PocketDetailItem
import com.saku.app.data.PocketsOverviewData
import com.saku.app.data.localization.EnglishStrings
import com.saku.app.data.localization.LocalAppStrings
import com.saku.app.data.localization.localizeAssetBadge
import com.saku.app.data.localization.localizePocketCategory
import com.saku.app.data.localization.localizePocketName
import com.saku.app.data.localization.localizeStatus
import com.saku.app.ui.main.SakuBottomNavigation
import com.saku.app.ui.main.formatRupiah
import com.saku.app.ui.theme.SAKUTheme
import com.saku.app.ui.theme.SakuAmber
import com.saku.app.ui.theme.SakuDarkGreen
import com.saku.app.ui.theme.SakuEmerald
import com.saku.app.ui.theme.SakuEmeraldLight
import com.saku.app.ui.theme.SakuIcons
import com.saku.app.ui.theme.SakuMint
import com.saku.app.ui.theme.sakuColors
import kotlinx.coroutines.launch

private val TabularStyle = TextStyle(fontFeatureSettings = "tnum")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocketsAndAssetsScreen(
    initialTab: Int = 0,
    onNavigateToCreatePocket: () -> Unit = {},
    onNavigateToAddAsset: () -> Unit = {},
    onNavigateToAdd: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    dataRepository: DataRepository = DefaultDataRepository.getInstance(),
    modifier: Modifier = Modifier
) {
    var selectedTab by remember(initialTab) { mutableIntStateOf(initialTab) }
    var selectedCategory by remember { mutableStateOf(PocketCategory.ALL) }
    var editingPocket by remember { mutableStateOf<PocketDetailItem?>(null) }

    val pocketsData by dataRepository.pocketsData.collectAsState()
    val assetsData by dataRepository.assetsData.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val strings = LocalAppStrings.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = sakuColors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            PocketsBottomBar(
                onTabSelected = { tabIndex ->
                    when (tabIndex) {
                        0 -> onNavigateToHome()
                        1 -> { /* already here */ }
                        2 -> onNavigateToReports()
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
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Header Row: Title & Action Button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.pocketsAndAssetsTitle,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = sakuColors.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    // Button Action Switch (+ Buat Kantong vs + Tambah Aset)
                    Box(
                        modifier = Modifier
                            .heightIn(min = 38.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(sakuColors.pillBg)
                            .clickable {
                                if (selectedTab == 0) onNavigateToCreatePocket() else onNavigateToAddAsset()
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (selectedTab == 0) strings.btnCreatePocket else strings.btnAddAsset,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = sakuColors.pillText,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            // 2. Segmented Slider Track [Kantong Anggaran | Aset Saya]
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .background(sakuColors.pillBg, RoundedCornerShape(20.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SegmentedTabButton(
                        label = strings.tabBudgetPockets,
                        isSelected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    SegmentedTabButton(
                        label = strings.tabMyAssets,
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 3. TAB CONTENT
            if (selectedTab == 0) {
                // ==========================================
                // TAB 0: KANTONG ANGGARAN (Frame 380)
                // ==========================================

                // Total Budget Overview Card
                item {
                    TotalBudgetOverviewCard(
                        data = pocketsData,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }

                // Category Filter Chips (edge-to-edge scrollable without right clipping)
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(PocketCategory.values()) { cat ->
                            val isSelected = selectedCategory == cat
                            Box(
                                modifier = Modifier
                                    .heightIn(min = 36.dp)
                                    .clip(RoundedCornerShape(15.dp))
                                    .background(if (isSelected) sakuColors.title else sakuColors.surface)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) sakuColors.title else sakuColors.border,
                                        shape = RoundedCornerShape(15.dp)
                                    )
                                    .clickable { selectedCategory = cat }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = strings.localizePocketCategory(cat),
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                    color = if (isSelected) sakuColors.chipActiveText else sakuColors.textSecondary,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }

                // Filtered Pockets List
                val filteredPockets = if (selectedCategory == PocketCategory.ALL) {
                    pocketsData.pockets
                } else {
                    pocketsData.pockets.filter { it.category == selectedCategory }
                }

                if (filteredPockets.isEmpty()) {
                    item {
                        EmptyPocketsStateCard(
                            onResetFilter = { selectedCategory = PocketCategory.ALL },
                            onCreatePocket = onNavigateToCreatePocket,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                } else {
                    items(filteredPockets, key = { it.id }) { pocket ->
                        DetailedPocketCard(
                            pocket = pocket,
                            onClick = { editingPocket = pocket },
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
            } else {
                // ==========================================
                // TAB 1: ASET SAYA (Frame 469)
                // ==========================================

                // Total Net Worth Card
                item {
                    TotalNetWorthCard(
                        data = assetsData,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }

                // Section Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.assetPortfolioListTitle,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = sakuColors.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${strings.totalLabel}: ${assetsData.assets.size} ${strings.assetAccountsSuffix}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = sakuColors.textSecondary,
                            style = TabularStyle,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                // Asset Cards List
                items(assetsData.assets, key = { it.id }) { asset ->
                    DetailedAssetCard(
                        asset = asset,
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("${strings.assetLabel}: ${asset.name} (${formatRupiah(asset.amount)})")
                            }
                        },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }

            // Bottom Spacing
            item {
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    editingPocket?.let { targetPocket ->
        ManagePocketBottomSheet(
            pocket = targetPocket,
            onDismiss = { editingPocket = null },
            onSave = { newName, newCategory, newLimit ->
                dataRepository.updatePocket(
                    id = targetPocket.id,
                    name = newName,
                    category = newCategory,
                    limit = newLimit
                )
                editingPocket = null
                scope.launch {
                    snackbarHostState.showSnackbar(strings.pocketUpdatedSuccess)
                }
            },
            onDelete = {
                dataRepository.deletePocket(targetPocket.id)
                editingPocket = null
                scope.launch {
                    snackbarHostState.showSnackbar(strings.pocketDeletedSuccess)
                }
            }
        )
    }
}

@Composable
private fun SegmentedTabButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(17.dp))
            .background(if (isSelected) sakuColors.title else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) sakuColors.chipActiveText else sakuColors.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun EmptyPocketsStateCard(
    onResetFilter: () -> Unit,
    onCreatePocket: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp), spotColor = sakuColors.cardShadow)
            .background(sakuColors.surface, RoundedCornerShape(20.dp))
            .border(1.dp, sakuColors.border, RoundedCornerShape(20.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(sakuColors.pillBg, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = SakuIcons.Pockets),
                    contentDescription = null,
                    tint = sakuColors.pillText,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = strings.emptyPocketsTitle,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = sakuColors.textDark,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = strings.emptyPocketsDesc,
                fontSize = 12.sp,
                color = sakuColors.textSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(sakuColors.surfaceVariant)
                        .border(1.dp, sakuColors.border, RoundedCornerShape(14.dp))
                        .clickable(onClick = onResetFilter)
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = strings.resetFilterBtn,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = sakuColors.textDark
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(sakuColors.title)
                        .clickable(onClick = onCreatePocket)
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = strings.btnCreatePocket,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = sakuColors.chipActiveText
                    )
                }
            }
        }
    }
}

// ==========================================
// KANTONG SUB-COMPONENTS
// ==========================================
@Composable
fun TotalBudgetOverviewCard(
    data: PocketsOverviewData,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp), spotColor = sakuColors.cardShadow)
            .background(sakuColors.surface, RoundedCornerShape(20.dp))
            .border(1.dp, sakuColors.border, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.totalBudget,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = sakuColors.textSecondary
                )

                // Percent Badge
                Box(
                    modifier = Modifier
                        .background(sakuColors.pillBg, RoundedCornerShape(11.dp))
                        .padding(horizontal = 9.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${data.percentUsed}% ${strings.usedBudget}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = sakuColors.pillText,
                        style = TabularStyle,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatRupiah(data.totalBudget),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = sakuColors.textDark,
                style = TabularStyle
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Smooth Progress Bar Track without trailing stop dot
            LinearProgressIndicator(
                progress = { data.percentUsed / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = sakuColors.title,
                trackColor = sakuColors.trackBg,
                strokeCap = StrokeCap.Round,
                gapSize = 0.dp,
                drawStopIndicator = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Two In-Card Status Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Terpakai Pill
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (sakuColors.isDark) Color(0xFF1B4234) else Color(0xFFF9F8F3), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(SakuAmber, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = strings.usedBudget,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = sakuColors.textSecondary
                        )
                        Text(
                            text = formatRupiah(data.usedAmount),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = sakuColors.textDark,
                            style = TabularStyle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Sisa Kuota Pill
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .background(sakuColors.pillBg, RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(SakuEmerald, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = strings.remainingQuota,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = sakuColors.textSecondary
                        )
                        Text(
                            text = formatRupiah(data.remainingQuota),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = sakuColors.pillText,
                            style = TabularStyle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailedPocketCard(
    pocket: PocketDetailItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val isDark = sakuColors.isDark
    val iconTint = adaptiveAccentColor(pocket.accentColor, isDark)
    val iconBg = if (isDark) pocket.accentColor.copy(alpha = 0.18f) else pocket.bgColor
    val badgeBg = if (isDark) pocket.statusColor.copy(alpha = 0.18f) else pocket.statusBg
    val badgeTextColor = adaptiveAccentColor(pocket.statusColor, isDark)
    val iconRes = pocket.iconRes
        ?: if (pocket.categoryType != CategoryType.GENERAL) {
            SakuIcons.forCategoryType(pocket.categoryType)
        } else {
            SakuIcons.forPocketCategory(pocket.category)
        }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(18.dp), spotColor = sakuColors.cardShadow)
            .clip(RoundedCornerShape(18.dp))
            .background(sakuColors.surface)
            .border(1.dp, sakuColors.border, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Category Icon Box
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(iconBg, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = pocket.name,
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = strings.localizePocketName(pocket.name),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = sakuColors.textDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Status Badge (Aman / Waspada / Overbudget)
                Box(
                    modifier = Modifier
                        .background(badgeBg, RoundedCornerShape(11.dp))
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = strings.localizeStatus(pocket.statusText),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = badgeTextColor,
                        style = TabularStyle,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Amounts Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${formatRupiah(pocket.spent)} / ${formatRupiah(pocket.limit)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = sakuColors.textDark,
                    style = TabularStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${strings.remainingQuota}: ${formatRupiah(pocket.remaining)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = sakuColors.textSecondary,
                    style = TabularStyle,
                    maxLines = 1,
                    softWrap = false
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Smooth Progress Bar
            LinearProgressIndicator(
                progress = { pocket.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = iconTint,
                trackColor = sakuColors.trackBg,
                strokeCap = StrokeCap.Round,
                gapSize = 0.dp,
                drawStopIndicator = {}
            )
        }
    }
}

// ==========================================
// MANAGE POCKET BOTTOM SHEET
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManagePocketBottomSheet(
    pocket: PocketDetailItem,
    onDismiss: () -> Unit,
    onSave: (String, PocketCategory, Long) -> Unit,
    onDelete: () -> Unit
) {
    val strings = LocalAppStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var nameInput by remember(pocket.id) { mutableStateOf(pocket.name) }
    var limitInput by remember(pocket.id) { mutableLongStateOf(pocket.limit) }
    var limitText by remember(pocket.id) { mutableStateOf(pocket.limit.toString()) }
    var selectedCategory by remember(pocket.id) { mutableStateOf(pocket.category) }
    var confirmDelete by remember(pocket.id) { mutableStateOf(false) }

    val quickDeltas = listOf(
        Pair(if (strings == EnglishStrings) "-100k" else "-100rb", -100000L),
        Pair(if (strings == EnglishStrings) "+100k" else "+100rb", 100000L),
        Pair(if (strings == EnglishStrings) "+500k" else "+500rb", 500000L)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = sakuColors.surface,
        contentColor = sakuColors.textDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.managePocketTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = sakuColors.title
                )
                Text(
                    text = "${strings.usedBudget}: ${formatRupiah(pocket.spent)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = sakuColors.textSecondary,
                    style = TabularStyle
                )
            }

            // Name Field
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = strings.pocketNameLabel,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = sakuColors.textDark
                )
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = sakuColors.surfaceVariant,
                        unfocusedContainerColor = sakuColors.surfaceVariant,
                        focusedBorderColor = sakuColors.title,
                        unfocusedBorderColor = sakuColors.border,
                        focusedTextColor = sakuColors.textDark,
                        unfocusedTextColor = sakuColors.textDark
                    )
                )
            }

            // Limit Field + Quick Adjust Chips
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.pocketLimitLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = sakuColors.textDark
                    )
                    Text(
                        text = formatRupiah(limitInput),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = sakuColors.title,
                        style = TabularStyle
                    )
                }
                OutlinedTextField(
                    value = limitText,
                    onValueChange = { raw ->
                        val digitsOnly = raw.filter { it.isDigit() }
                        limitText = digitsOnly
                        limitInput = digitsOnly.toLongOrNull() ?: 0L
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = TabularStyle,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = sakuColors.surfaceVariant,
                        unfocusedContainerColor = sakuColors.surfaceVariant,
                        focusedBorderColor = sakuColors.title,
                        unfocusedBorderColor = sakuColors.border,
                        focusedTextColor = sakuColors.textDark,
                        unfocusedTextColor = sakuColors.textDark
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickDeltas.forEach { (label, delta) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(sakuColors.surfaceVariant)
                                .border(1.dp, sakuColors.border, RoundedCornerShape(10.dp))
                                .clickable {
                                    limitInput = (limitInput + delta).coerceAtLeast(0L)
                                    limitText = limitInput.toString()
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = sakuColors.textDark,
                                style = TabularStyle
                            )
                        }
                    }
                }
            }

            // Category Chips
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = strings.pocketCategoryLabel,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = sakuColors.textDark
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        PocketCategory.POKOK,
                        PocketCategory.DARURAT,
                        PocketCategory.TABUNGAN,
                        PocketCategory.KEINGINAN
                    ).forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) sakuColors.title else sakuColors.surfaceVariant)
                                .border(
                                    1.dp,
                                    if (isSelected) sakuColors.title else sakuColors.border,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = strings.localizePocketCategory(cat),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) sakuColors.chipActiveText else sakuColors.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Save Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(sakuColors.title)
                    .clickable {
                        onSave(
                            nameInput.trim().ifEmpty { pocket.name },
                            selectedCategory,
                            limitInput.coerceAtLeast(0L)
                        )
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = strings.saveChangesBtn,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = sakuColors.chipActiveText
                )
            }

            // Delete Button with 2-step confirmation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(sakuColors.logoutBg)
                    .border(1.dp, sakuColors.logoutBorder, RoundedCornerShape(14.dp))
                    .clickable {
                        if (confirmDelete) onDelete() else confirmDelete = true
                    }
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (confirmDelete) strings.deletePocketConfirmBtn else strings.deletePocketBtn,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = sakuColors.logoutText
                )
            }
        }
    }
}

// ==========================================
// ASSET SUB-COMPONENTS (Frame 469)
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TotalNetWorthCard(
    data: AssetsOverviewData,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val badgeBg = if (sakuColors.isDark) SakuEmerald else SakuDarkGreen
    val badgeFg = if (sakuColors.isDark) Color(0xFF0F241C) else Color.White

    val segments = listOf(
        Pair(data.kasShare, Color(0xFF2563EB)),
        Pair(data.daruratShare, SakuEmerald),
        Pair(data.investShare, SakuAmber),
        Pair(data.tunaiShare, Color(0xFF7C3AED))
    ).filter { it.first > 0 }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp), spotColor = sakuColors.cardShadow)
            .background(if (sakuColors.isDark) Color(0xFF163E2F) else SakuMint, RoundedCornerShape(20.dp))
            .border(1.dp, if (sakuColors.isDark) Color(0xFF235541) else Color(0xFFD1E6DC), RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.totalNetWorth.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (sakuColors.isDark) Color(0xFFD1FAE5) else Color(0xFF1A5A45),
                    letterSpacing = 0.5.sp
                )

                // Growth Badge with vector icon (no raw arrow char)
                Row(
                    modifier = Modifier
                        .background(badgeBg, RoundedCornerShape(11.dp))
                        .padding(horizontal = 9.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = SakuIcons.LinkExt),
                        contentDescription = null,
                        tint = badgeFg,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${data.monthlyGrowth} ${strings.thisMonthShort}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = badgeFg,
                        style = TabularStyle,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = formatRupiah(data.totalNetWorth),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = sakuColors.title,
                style = TabularStyle
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Multi-segment asset bar (safe against zero shares)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(sakuColors.trackBg)
            ) {
                segments.forEach { (share, color) ->
                    Box(
                        modifier = Modifier
                            .weight(share.toFloat())
                            .height(8.dp)
                            .background(color)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend Row using clean vector circles (zero emojis)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AssetLegendItem(color = Color(0xFF2563EB), label = strings.legendCash, percent = data.kasShare)
                AssetLegendItem(color = SakuEmerald, label = strings.legendEmergency, percent = data.daruratShare)
                AssetLegendItem(color = SakuAmber, label = strings.legendInvest, percent = data.investShare)
                AssetLegendItem(color = Color(0xFF7C3AED), label = strings.legendWallet, percent = data.tunaiShare)
            }
        }
    }
}

@Composable
private fun AssetLegendItem(
    color: Color,
    label: String,
    percent: Int
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "$label $percent%",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = sakuColors.textDark,
            style = TabularStyle,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun DetailedAssetCard(
    asset: AssetItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val isDark = sakuColors.isDark
    val iconTint = adaptiveAccentColor(asset.accentColor, isDark)
    val iconBg = if (isDark) asset.accentColor.copy(alpha = 0.18f) else asset.bgColor
    val badgeBg = if (isDark) asset.badgeColor.copy(alpha = 0.18f) else asset.badgeBg
    val badgeTextColor = adaptiveAccentColor(asset.badgeColor, isDark)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(18.dp), spotColor = sakuColors.cardShadow)
            .clip(RoundedCornerShape(18.dp))
            .background(sakuColors.surface)
            .border(1.dp, sakuColors.border, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Asset Icon Box
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(iconBg, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    val iconRes = SakuIcons.forAssetClass(asset.assetClass)
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = asset.name,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = asset.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = sakuColors.textDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = asset.institution,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = sakuColors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    // Asset Badge below institution so asset name never gets truncated
                    Box(
                        modifier = Modifier
                            .background(badgeBg, RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = strings.localizeAssetBadge(asset.badgeText),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = badgeTextColor,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Amount & Share Percent
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatRupiah(asset.amount),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = sakuColors.title,
                    style = TabularStyle,
                    maxLines = 1,
                    softWrap = false
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${asset.sharePercent}% ${strings.assetShareSuffix}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = sakuColors.textSecondary,
                    style = TabularStyle,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

private fun adaptiveAccentColor(color: Color, isDark: Boolean): Color {
    if (!isDark) return color
    return when (color) {
        SakuEmerald, Color(0xFF059669) -> SakuEmeraldLight
        Color(0xFFD97706) -> Color(0xFFFBBF24)
        Color(0xFF2563EB) -> Color(0xFF60A5FA)
        Color(0xFF7C3AED) -> Color(0xFFA78BFA)
        Color(0xFFDC2626) -> Color(0xFFF87171)
        else -> color
    }
}

// ==========================================
// BOTTOM NAVIGATION
// ==========================================
@Composable
fun PocketsBottomBar(
    onTabSelected: (Int) -> Unit,
    onFabClick: () -> Unit
) {
    SakuBottomNavigation(
        selectedTab = 1,
        onTabSelected = onTabSelected,
        onFabClick = onFabClick
    )
}

// ==========================================
// PREVIEWS
// ==========================================
@Preview(name = "Page Kantong Light", showBackground = true, device = Devices.PIXEL_7)
@Composable
fun PocketsScreenPreview() {
    SAKUTheme {
        PocketsAndAssetsScreen(initialTab = 0)
    }
}

@Preview(
    name = "Page Kantong Dark",
    showBackground = true,
    device = Devices.PIXEL_7,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PocketsScreenDarkPreview() {
    SAKUTheme(darkTheme = true) {
        PocketsAndAssetsScreen(initialTab = 0)
    }
}

@Preview(name = "Page Aset Light", showBackground = true, device = Devices.PIXEL_7)
@Composable
fun AssetsScreenPreview() {
    SAKUTheme {
        PocketsAndAssetsScreen(initialTab = 1)
    }
}
