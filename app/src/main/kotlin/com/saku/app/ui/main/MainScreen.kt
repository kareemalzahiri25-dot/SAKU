package com.saku.app.ui.main

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.NorthEast
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.saku.app.R
import com.saku.app.data.CategoryType
import com.saku.app.data.DashboardSummary
import com.saku.app.data.DefaultDataRepository
import com.saku.app.data.PocketItem
import com.saku.app.data.TransactionItem
import com.saku.app.data.localization.EnglishStrings
import com.saku.app.data.localization.LocalAppStrings
import com.saku.app.data.localization.localizePocketName
import com.saku.app.data.localization.localizeTransactionTitle
import com.saku.app.data.preference.AppPreferences
import com.saku.app.data.toDetail
import com.saku.app.ui.theme.SAKUTheme
import com.saku.app.ui.theme.SakuAmber
import com.saku.app.ui.theme.SakuAmberBg
import com.saku.app.ui.theme.SakuBlue
import com.saku.app.ui.theme.SakuBlueBg
import com.saku.app.ui.theme.SakuDarkGreen
import com.saku.app.ui.theme.SakuDarkGreenBorder
import com.saku.app.ui.theme.SakuEmerald
import com.saku.app.ui.theme.SakuEmeraldLight
import com.saku.app.ui.theme.SakuExpenseRed
import com.saku.app.ui.theme.SakuIcons
import com.saku.app.ui.theme.SakuIncomeGreen
import com.saku.app.ui.theme.SakuMint
import com.saku.app.ui.theme.sakuColors
import com.saku.app.util.ImageUtils

@Composable
fun MainScreen(
    onItemClick: (NavKey) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = viewModel { MainScreenViewModel(DefaultDataRepository.getInstance()) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        MainScreenUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize().background(sakuColors.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = sakuColors.title)
            }
        }
        is MainScreenUiState.Success -> {
            DashboardContent(
                data = state.data,
                onItemClick = onItemClick,
                modifier = modifier
            )
        }
        is MainScreenUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize().background(sakuColors.background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Gagal memuat data: ${state.throwable.message}",
                    color = SakuExpenseRed,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DashboardContent(
    data: DashboardSummary,
    onItemClick: (NavKey) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isBalanceVisible by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) }
    var showBrandDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = sakuColors.background,
        bottomBar = {
            SakuBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    when (tab) {
                        0 -> { /* home */ }
                        1 -> onItemClick(com.saku.app.PocketsAndAssets(initialTab = 0))
                        2 -> onItemClick(com.saku.app.Reports)
                        3 -> onItemClick(com.saku.app.Profile)
                    }
                },
                onFabClick = {
                    onItemClick(com.saku.app.AddTransaction(initialTab = 0))
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Header & Greeting with SAKU App Branding
            item {
                DashboardHeader(
                    userName = data.userName,
                    onAvatarClick = { showBrandDialog = true }
                )
            }

            // 2. Hero Balance Card (Identik dengan Frame 133 FigCraft)
            item {
                HeroBalanceCard(
                    totalBalance = data.totalBalance,
                    totalIncome = data.totalIncome,
                    totalExpense = data.totalExpense,
                    isVisible = isBalanceVisible,
                    onToggleVisibility = { isBalanceVisible = !isBalanceVisible }
                )
            }

            // 3. Quick Actions Row (Scan Struk, Aset Saya, Buat Kantong, Rekap Excel)
            item {
                QuickActionsRow(
                    onScanClick = {
                        onItemClick(com.saku.app.AddTransaction(initialTab = 0))
                    },
                    onAsetClick = {
                        onItemClick(com.saku.app.PocketsAndAssets(initialTab = 1))
                    },
                    onKantongClick = {
                        onItemClick(com.saku.app.CreatePocket)
                    },
                    onExcelClick = {
                        onItemClick(com.saku.app.Reports)
                    }
                )
            }

            // 4. Section Kantong Saya
            item {
                PocketsSection(
                    pockets = data.pockets,
                    onManageClick = {
                        onItemClick(com.saku.app.PocketsAndAssets(initialTab = 0))
                    }
                )
            }

            // 5. Section Riwayat Transaksi
            item {
                RecentTransactionsSection(
                    transactions = data.recentTransactions,
                    onViewAllClick = {
                        onItemClick(com.saku.app.Reports)
                    },
                    onItemClick = { tx ->
                        onItemClick(tx.toDetail())
                    }
                )
            }

            // Bottom spacer for ergonomics
            item {
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    // Brand Identity & About Dialog (Menampilkan saku_app_icon resmi)
    if (showBrandDialog) {
        SakuBrandInfoDialog(onDismiss = { showBrandDialog = false })
    }
}


// ==========================================
// 1. DASHBOARD HEADER
// ==========================================
@Composable
fun DashboardHeader(
    userName: String,
    onAvatarClick: () -> Unit
) {
    val context = LocalContext.current
    val appPreferences = remember { AppPreferences.getInstance(context) }
    val profileImagePath by appPreferences.profileImagePath.collectAsState(initial = null)
    val avatarBitmap = ImageUtils.rememberProfileBitmap(profileImagePath)
    val strings = LocalAppStrings.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = strings.greetingPrefix,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = sakuColors.textSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$userName! 👋",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = sakuColors.title
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = strings.greetingSubtitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = sakuColors.textSecondary
            )
        }

        // Profile Avatar with Official SAKU Emblem
        Box(
            modifier = Modifier
                .size(46.dp)
                .shadow(elevation = 3.dp, shape = CircleShape, spotColor = Color(0x20114232))
                .clip(CircleShape)
                .background(SakuDarkGreen)
                .border(1.5.dp, Color(0xFF226B54), CircleShape)
                .clickable(onClick = onAvatarClick),
            contentAlignment = Alignment.Center
        ) {
            if (avatarBitmap != null) {
                Image(
                    bitmap = avatarBitmap,
                    contentDescription = "Foto Profil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = userName.take(1).uppercase(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// ==========================================
// 2. HERO BALANCE CARD & FLOW ARROW
// ==========================================
@Composable
fun FlowArrowIcon(
    isIncome: Boolean,
    modifier: Modifier = Modifier
) {
    val arrowColor = if (isIncome) Color(0xFF34D399) else Color(0xFFFCA5A5)
    Canvas(modifier = modifier.size(13.dp)) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        if (isIncome) {
            // Inflow: ↙ from top-right to bottom-left
            val start = Offset(w * 0.82f, h * 0.18f)
            val end = Offset(w * 0.18f, h * 0.82f)
            drawLine(
                color = arrowColor,
                start = start,
                end = end,
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            val fin = w * 0.46f
            drawLine(
                color = arrowColor,
                start = end,
                end = Offset(end.x + fin, end.y),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = arrowColor,
                start = end,
                end = Offset(end.x, end.y - fin),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        } else {
            // Outflow: ↗ from bottom-left to top-right
            val start = Offset(w * 0.18f, h * 0.82f)
            val end = Offset(w * 0.82f, h * 0.18f)
            drawLine(
                color = arrowColor,
                start = start,
                end = end,
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            val fin = w * 0.46f
            drawLine(
                color = arrowColor,
                start = end,
                end = Offset(end.x - fin, end.y),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = arrowColor,
                start = end,
                end = Offset(end.x, end.y + fin),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun HeroBalanceCard(
    totalBalance: Long,
    totalIncome: Long,
    totalExpense: Long,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit
) {
    val strings = LocalAppStrings.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color(0x33114232)
            )
            .background(SakuDarkGreen, RoundedCornerShape(24.dp))
            .border(1.dp, SakuDarkGreenBorder, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column {
            // Label & Visibility Toggle Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(SakuEmerald, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.totalBalance,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFEAF3EF)
                    )
                }

                IconButton(
                    onClick = onToggleVisibility,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = if (isVisible) SakuIcons.Eye else SakuIcons.EyeOff),
                        contentDescription = "Toggle saldo visibility",
                        tint = Color(0xFFEAF3EF),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Balance Number
            Text(
                text = if (isVisible) formatRupiah(totalBalance) else "••••••••••••",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = (-0.5).sp,
                style = TextStyle(fontFeatureSettings = "tnum")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Flow Summary Box (Pemasukan & Pengeluaran)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A5A45), RoundedCornerShape(14.dp))
                    .border(1.dp, Color(0xFF2B775E), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Inflow (Pemasukan)
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color(0xFF226B54), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        FlowArrowIcon(isIncome = true)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = strings.monthlyIncome,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFFD1FAE5),
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isVisible) "+${formatRupiah(Math.abs(totalIncome))}" else "••••••",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF34D399),
                            style = TextStyle(fontFeatureSettings = "tnum"),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Centered Vertical Divider with Balanced Spacing
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .width(1.dp)
                        .height(28.dp)
                        .background(Color(0xFF2B775E))
                )

                // Outflow (Pengeluaran)
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color(0xFF522932), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        FlowArrowIcon(isIncome = false)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = strings.monthlyExpense,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFFFFE4E6),
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isVisible) "-${formatRupiah(Math.abs(totalExpense))}" else "••••••",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFF87171),
                            style = TextStyle(fontFeatureSettings = "tnum"),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. QUICK ACTIONS ROW
// ==========================================
@Composable
fun QuickActionsRow(
    onScanClick: () -> Unit,
    onAsetClick: () -> Unit,
    onKantongClick: () -> Unit,
    onExcelClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        QuickActionButton(
            label = strings.actionScanReceipt,
            iconRes = SakuIcons.ScanOcr,
            tint = sakuColors.iconBoxTint,
            onClick = onScanClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            label = strings.actionMyAssets,
            iconRes = SakuIcons.AssetBank,
            tint = sakuColors.iconBoxTint,
            onClick = onAsetClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            label = strings.actionCreatePocket,
            iconRes = SakuIcons.Pockets,
            tint = sakuColors.iconBoxTint,
            onClick = onKantongClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            label = strings.actionExcelRecap,
            iconRes = SakuIcons.Excel,
            tint = sakuColors.iconBoxTint,
            onClick = onExcelClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickActionButton(
    label: String,
    @DrawableRes iconRes: Int,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp), spotColor = sakuColors.cardShadow)
                .background(sakuColors.surface, RoundedCornerShape(20.dp))
                .border(1.dp, sakuColors.border, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = sakuColors.textDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ==========================================
// 4. POCKETS SECTION
// ==========================================
@Composable
fun PocketsSection(
    pockets: List<PocketItem>,
    onManageClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Section Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = strings.myPocketsSection,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = sakuColors.title
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Badge Count
                Box(
                    modifier = Modifier
                        .background(sakuColors.pillBg, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${pockets.size} ${strings.pocketCountSuffix}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = sakuColors.pillText
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(sakuColors.surface)
                    .border(1.dp, sakuColors.border, RoundedCornerShape(14.dp))
                    .clickable(onClick = onManageClick)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = strings.manage,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = sakuColors.textPrimary
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    painter = painterResource(id = SakuIcons.LinkExt),
                    contentDescription = null,
                    tint = sakuColors.textPrimary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        // Pocket Cards List
        pockets.forEach { pocket ->
            PocketCard(pocket = pocket, onClick = onManageClick)
        }
    }
}

@Composable
fun PocketCard(
    pocket: PocketItem,
    onClick: () -> Unit = {}
) {
    val strings = LocalAppStrings.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(18.dp), spotColor = sakuColors.cardShadow)
            .background(sakuColors.surface, RoundedCornerShape(18.dp))
            .border(1.dp, sakuColors.border, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
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
                    // Category Icon Container
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(pocket.bgColor, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        val iconRes = SakuIcons.forCategoryType(pocket.categoryType)
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = pocket.name,
                            tint = pocket.accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = strings.localizePocketName(pocket.name),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = sakuColors.textDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${strings.remainingBudgetPrefix}: ${formatRupiah(pocket.remaining)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = sakuColors.textSecondary
                        )
                    }
                }

                // Spent / Limit Text
                Text(
                    text = pocket.spentText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = sakuColors.textDark
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Bar Track & Indicator
            LinearProgressIndicator(
                progress = { pocket.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = pocket.accentColor,
                trackColor = sakuColors.trackBg
            )
        }
    }
}

// ==========================================
// 5. RECENT TRANSACTIONS SECTION
// ==========================================
@Composable
fun RecentTransactionsSection(
    transactions: List<TransactionItem>,
    onViewAllClick: () -> Unit,
    onItemClick: (TransactionItem) -> Unit = {}
) {
    val strings = LocalAppStrings.current
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Section Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(sakuColors.iconBoxBg, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = null,
                        tint = sakuColors.iconBoxTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.recentTransactionsSection,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = sakuColors.title
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(sakuColors.surface)
                    .border(1.dp, sakuColors.border, RoundedCornerShape(14.dp))
                    .clickable(onClick = onViewAllClick)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = strings.viewAll,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = sakuColors.textSecondary
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    painter = painterResource(id = SakuIcons.LinkExt),
                    contentDescription = null,
                    tint = sakuColors.textSecondary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        // Consolidated Transactions Card Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp), spotColor = sakuColors.cardShadow)
                .background(sakuColors.surface, RoundedCornerShape(20.dp))
                .border(1.dp, sakuColors.border, RoundedCornerShape(20.dp))
                .padding(vertical = 4.dp, horizontal = 16.dp)
        ) {
            Column {
                if (transactions.isEmpty()) {
                    Text(
                        text = strings.emptyTransactions,
                        fontSize = 13.sp,
                        color = sakuColors.textSecondary,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    transactions.forEachIndexed { index, tx ->
                        TransactionRowItem(item = tx, onClick = { onItemClick(tx) })
                        if (index < transactions.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(sakuColors.divider)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionRowItem(
    item: TransactionItem,
    onClick: () -> Unit = {}
) {
    val strings = LocalAppStrings.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Category Icon Container
            val (bgColor, iconColor) = when (item.categoryType) {
                CategoryType.FOOD -> Pair(SakuAmberBg, SakuAmber)
                CategoryType.TRANSPORT -> Pair(SakuBlueBg, SakuBlue)
                CategoryType.INCOME -> Pair(if (sakuColors.isDark) Color(0xFF163E2F) else SakuMint, SakuEmerald)
                else -> Pair(sakuColors.surfaceVariant, sakuColors.title)
            }
            val iconRes = SakuIcons.forCategoryType(item.categoryType)

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(bgColor, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = item.title,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = strings.localizeTransactionTitle(item.title),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = sakuColors.textDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                val localizedSubtitle = if (strings == EnglishStrings) {
                    item.subtitle.replace("Makan & Minum", "Food & Beverage")
                        .replace("Transportasi", "Transportation")
                        .replace("Hiburan", "Entertainment")
                        .replace("Pemasukan", "Income")
                        .replace("Umum", "General")
                } else {
                    item.subtitle
                }
                Text(
                    text = localizedSubtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = if (item.isExpense) sakuColors.textSecondary else SakuEmerald
                )
            }
        }

        // Amount with sign and color coding
        Text(
            text = if (item.isExpense) "-${formatRupiah(Math.abs(item.amount))}" else "+${formatRupiah(item.amount)}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (item.isExpense) SakuExpenseRed else SakuIncomeGreen
        )
    }
}

// ==========================================
// 6. BOTTOM NAVIGATION BAR & ELEVATED FAB
// ==========================================
@Composable
fun SakuBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onFabClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
    ) {
        // Bottom Bar Background Surface
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .align(Alignment.BottomCenter),
            color = sakuColors.bottomNavBg,
            shadowElevation = 8.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Top border hairline
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(sakuColors.bottomNavBorder)
                )

                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Item 0: Beranda
                    BottomNavItem(
                        modifier = Modifier.weight(1f),
                        label = strings.navHome,
                        iconRes = SakuIcons.Home,
                        isSelected = selectedTab == 0,
                        onClick = { onTabSelected(0) }
                    )

                    // Item 1: Kantong
                    BottomNavItem(
                        modifier = Modifier.weight(1f),
                        label = strings.navPocket,
                        iconRes = SakuIcons.Pockets,
                        isSelected = selectedTab == 1,
                        onClick = { onTabSelected(1) }
                    )

                    // Slot 2: Center Slot for FAB + "Tambah" Label (Exact same weight & vertical alignment)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(onClick = onFabClick)
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = strings.navAdd,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (sakuColors.isDark) SakuEmeraldLight else SakuDarkGreen
                        )
                    }

                    // Item 2: Laporan
                    BottomNavItem(
                        modifier = Modifier.weight(1f),
                        label = strings.navReport,
                        iconRes = SakuIcons.Reports,
                        isSelected = selectedTab == 2,
                        onClick = { onTabSelected(2) }
                    )

                    // Item 3: Profil
                    BottomNavItem(
                        modifier = Modifier.weight(1f),
                        label = strings.navProfile,
                        iconRes = SakuIcons.Profile,
                        isSelected = selectedTab == 3,
                        onClick = { onTabSelected(3) }
                    )
                }
            }
        }

        // Floating Action Button Center directly above the "Tambah" label
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 10.dp)
                .size(48.dp)
                .shadow(elevation = 6.dp, shape = CircleShape, spotColor = Color(0x35114232))
                .background(if (sakuColors.isDark) SakuEmerald else SakuDarkGreen, CircleShape)
                .border(3.5.dp, sakuColors.fabBorder, CircleShape)
                .clip(CircleShape)
                .clickable(onClick = onFabClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = SakuIcons.Add),
                contentDescription = "Tambah Transaksi",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun BottomNavItem(
    label: String,
    @DrawableRes iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = if (sakuColors.isDark) SakuEmeraldLight else SakuDarkGreen
    val inactiveColor = sakuColors.textMuted
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) activeColor else inactiveColor
        )
    }
}

// ==========================================
// 7. SAKU BRAND & ABOUT DIALOG
// ==========================================
@Composable
fun SakuBrandInfoDialog(onDismiss: () -> Unit) {
    val strings = LocalAppStrings.current
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = sakuColors.surface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Official saku_app_icon loaded from res/drawable/saku_app_icon.png
                Image(
                    painter = painterResource(id = R.drawable.saku_app_icon),
                    contentDescription = "SAKU App Icon",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .shadow(4.dp, RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "SAKU",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = sakuColors.title
                )
                Text(
                    text = strings.brandDialogSubtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = SakuEmerald,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = strings.brandDialogDesc,
                    fontSize = 13.sp,
                    color = sakuColors.textSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(sakuColors.pillBg, RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = strings.brandDialogVersion,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = sakuColors.pillText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = strings.close,
                    color = sakuColors.title,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

// ==========================================
// UTILITY / FORMATTERS
// ==========================================
fun formatRupiah(amount: Long): String {
    val isNegative = amount < 0
    val absAmount = Math.abs(amount)
    val formatted = String.format("%,d", absAmount).replace(',', '.')
    return if (isNegative) "-Rp $formatted" else "Rp $formatted"
}

// ==========================================
// PREVIEWS FOR ANDROID STUDIO
// ==========================================
@Preview(
    name = "SAKU Dashboard - Phone Light",
    showBackground = true,
    device = Devices.PIXEL_7,
    showSystemUi = true
)
@Composable
fun SakuDashboardPreview() {
    SAKUTheme {
        DashboardContent(
            data = DashboardSummary()
        )
    }
}

@Preview(
    name = "SAKU Dashboard - Dark Mode",
    showBackground = true,
    device = Devices.PIXEL_7,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun SakuDashboardDarkPreview() {
    SAKUTheme(darkTheme = true) {
        DashboardContent(
            data = DashboardSummary()
        )
    }
}

@Preview(
    name = "Hero Balance Card - Component",
    showBackground = true,
    backgroundColor = 0xFFF7F5EE
)
@Composable
fun HeroBalanceCardPreview() {
    SAKUTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            HeroBalanceCard(
                totalBalance = 3250000L,
                totalIncome = 6800000L,
                totalExpense = 3250000L,
                isVisible = true,
                onToggleVisibility = {}
            )
        }
    }
}
