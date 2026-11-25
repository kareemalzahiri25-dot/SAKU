package com.saku.app.ui.transaction

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.NorthEast
import androidx.compose.material.icons.outlined.SouthWest
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import com.saku.app.ui.theme.SakuIcons
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saku.app.data.CategoryType
import com.saku.app.data.DataRepository
import com.saku.app.data.DefaultDataRepository
import com.saku.app.data.localization.EnglishStrings
import com.saku.app.data.localization.LocalAppStrings
import com.saku.app.data.localization.localizePocketName
import com.saku.app.ui.main.formatRupiah
import com.saku.app.ui.theme.LocalSakuColors
import com.saku.app.ui.theme.SAKUTheme
import com.saku.app.ui.theme.SakuEmerald
import com.saku.app.ui.theme.SakuExpenseRed
import com.saku.app.ui.theme.SakuIncomeGreen
import com.saku.app.ui.theme.SakuMint
import kotlinx.coroutines.launch

@Composable
fun AddTransactionScreen(
    initialTab: Int = 0, // 0 = Scan Struk, 1 = Input Manual
    onNavigateBack: () -> Unit = {},
    dataRepository: DataRepository = DefaultDataRepository.getInstance(),
    modifier: Modifier = Modifier
) {
    val sakuColors = LocalSakuColors.current
    val strings = LocalAppStrings.current
    val isEn = strings == EnglishStrings
    var modeTab by remember { mutableIntStateOf(initialTab) }
    var ocrEngine by remember { mutableIntStateOf(0) } // 0 = Offline ML Kit, 1 = Gemini AI
    var isFlashOn by remember { mutableStateOf(false) }

    val pocketsData by dataRepository.pocketsData.collectAsState()
    val availablePocketNames = remember(pocketsData) {
        val names = pocketsData.pockets.map { it.name }.toMutableList()
        if (!names.contains("Kebutuhan Pokok")) names.add(0, "Kebutuhan Pokok")
        if (!names.contains("Transportasi")) names.add("Transportasi")
        if (!names.contains("Hiburan")) names.add("Hiburan")
        names.take(4)
    }

    // Manual input states
    var isExpense by remember { mutableStateOf(true) }
    var manualAmount by remember { mutableLongStateOf(150000L) }
    var manualPocket by remember { mutableStateOf(availablePocketNames.firstOrNull() ?: "Kebutuhan Pokok") }
    var manualNote by remember(isEn) { mutableStateOf(if (isEn) "Fuel refill & monthly oil change" else "Isi bensin motor & ganti oli bulanan") }
    var manualDate by remember(isEn) { mutableStateOf(if (isEn) "Today, 21 Nov 2026 (18:30 WIB)" else "Hari ini, 21 November 2026 (18:30 WIB)") }

    // OCR states
    var ocrPocket by remember { mutableStateOf(availablePocketNames.firstOrNull() ?: "Kebutuhan Pokok") }
    val ocrAmount = 348500L

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Scanner animation
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 180f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = sakuColors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Header Row: Close/Back + Title + Action
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(2.dp, CircleShape, spotColor = Color(0x10000000))
                            .background(sakuColors.surface, CircleShape)
                            .border(1.dp, sakuColors.border, CircleShape)
                            .clickable(onClick = onNavigateBack),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = SakuIcons.Close),
                            contentDescription = strings.close,
                            tint = sakuColors.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = if (modeTab == 0) strings.scanReceiptTab else strings.manualInputTab,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = sakuColors.textDark
                    )

                    if (modeTab == 0) {
                        // Flash Button
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isFlashOn) sakuColors.secondary else sakuColors.trackBg)
                                .clickable { isFlashOn = !isFlashOn },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = SakuIcons.Flash),
                                contentDescription = "Flash",
                                tint = if (isFlashOn) Color.White else sakuColors.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        // Reset Button
                        Text(
                            text = strings.reset,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = sakuColors.textSecondary,
                            modifier = Modifier
                                .clickable {
                                    manualAmount = 0L
                                    manualNote = ""
                                }
                                .padding(4.dp)
                        )
                    }
                }
            }

            // 2. Mode Slider: [Pindai Struk (OCR) | Input Manual]
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(sakuColors.trackBg, RoundedCornerShape(20.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Tab 0: Pindai Struk (OCR)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(17.dp))
                            .background(if (modeTab == 0) sakuColors.chipActiveBg else Color.Transparent)
                            .clickable { modeTab = 0 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${strings.scanReceiptTab} (OCR)",
                            fontSize = 13.sp,
                            fontWeight = if (modeTab == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (modeTab == 0) sakuColors.chipActiveText else sakuColors.chipInactiveText
                        )
                    }

                    // Tab 1: Input Manual
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(17.dp))
                            .background(if (modeTab == 1) sakuColors.chipActiveBg else Color.Transparent)
                            .clickable { modeTab = 1 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.manualInputTab,
                            fontSize = 13.sp,
                            fontWeight = if (modeTab == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (modeTab == 1) sakuColors.chipActiveText else sakuColors.chipInactiveText
                        )
                    }
                }
            }

            if (modeTab == 0) {
                // ==========================================
                // TAB 0: PINDAI STRUK OCR (Frame 729)
                // ==========================================

                // OCR Engine Switcher: [Offline (Lokal) | Gemini AI]
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(sakuColors.trackBg, RoundedCornerShape(12.dp))
                            .border(1.dp, sakuColors.border, RoundedCornerShape(12.dp))
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Engine 1: Offline
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(9.dp))
                                .background(if (ocrEngine == 0) sakuColors.surface else Color.Transparent)
                                .border(
                                    width = if (ocrEngine == 0) 1.dp else 0.dp,
                                    color = if (ocrEngine == 0) sakuColors.primary else Color.Transparent,
                                    shape = RoundedCornerShape(9.dp)
                                )
                                .clickable { ocrEngine = 0 }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.Bolt,
                                        contentDescription = null,
                                        tint = sakuColors.secondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isEn) "Offline (Local)" else "Offline (Lokal)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = sakuColors.textDark
                                    )
                                }
                                Text(
                                    text = if (isEn) "No Quota • Fast" else "Tanpa Kuota • Cepat",
                                    fontSize = 10.sp,
                                    color = sakuColors.secondary
                                )
                            }
                        }

                        // Engine 2: Gemini AI
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(9.dp))
                                .background(if (ocrEngine == 1) sakuColors.surface else Color.Transparent)
                                .border(
                                    width = if (ocrEngine == 1) 1.dp else 0.dp,
                                    color = if (ocrEngine == 1) sakuColors.primary else Color.Transparent,
                                    shape = RoundedCornerShape(9.dp)
                                )
                                .clickable { ocrEngine = 1 }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = SakuIcons.AiGemini),
                                        contentDescription = null,
                                        tint = sakuColors.secondary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Gemini AI",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = sakuColors.textDark
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .background(sakuColors.secondary, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isEn) "Active API Key" else "API Key Aktif",
                                        fontSize = 10.sp,
                                        color = sakuColors.secondary
                                    )
                                }
                            }
                        }
                    }
                }

                // Info Tag
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(sakuColors.trackBg, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (ocrEngine == 0)
                                (if (isEn) "Offline Mode: Instant receipt scan without internet" else "Mode Offline: Pindai struk instan tanpa kuota internet")
                            else
                                (if (isEn) "Gemini AI Mode: Smart extraction of items & categories via cloud" else "Mode Gemini AI: Ekstraksi pintar item belanja & kategori via cloud"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = sakuColors.textDark,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Camera Viewfinder Hero Frame
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .shadow(3.dp, RoundedCornerShape(20.dp), spotColor = Color(0x33000000))
                            .background(Color(0xFF1E292B), RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // 4 Targeting Corners
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Top Left Corner
                            Box(modifier = Modifier.size(24.dp).align(Alignment.TopStart)) {
                                Box(modifier = Modifier.width(20.dp).height(3.dp).background(SakuEmerald))
                                Box(modifier = Modifier.width(3.dp).height(20.dp).background(SakuEmerald))
                            }
                            // Top Right Corner
                            Box(modifier = Modifier.size(24.dp).align(Alignment.TopEnd)) {
                                Box(modifier = Modifier.width(20.dp).height(3.dp).align(Alignment.TopEnd).background(SakuEmerald))
                                Box(modifier = Modifier.width(3.dp).height(20.dp).align(Alignment.TopEnd).background(SakuEmerald))
                            }
                            // Bottom Left Corner
                            Box(modifier = Modifier.size(24.dp).align(Alignment.BottomStart)) {
                                Box(modifier = Modifier.width(20.dp).height(3.dp).align(Alignment.BottomStart).background(SakuEmerald))
                                Box(modifier = Modifier.width(3.dp).height(20.dp).align(Alignment.BottomStart).background(SakuEmerald))
                            }
                            // Bottom Right Corner
                            Box(modifier = Modifier.size(24.dp).align(Alignment.BottomEnd)) {
                                Box(modifier = Modifier.width(20.dp).height(3.dp).align(Alignment.BottomEnd).background(SakuEmerald))
                                Box(modifier = Modifier.width(3.dp).height(20.dp).align(Alignment.BottomEnd).background(SakuEmerald))
                            }
                        }

                        // Simulated Receipt in Center
                        Box(
                            modifier = Modifier
                                .width(200.dp)
                                .shadow(2.dp, RoundedCornerShape(10.dp))
                                .background(Color.White, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "SUPERINDO",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937)
                                )
                                Text(
                                    text = "21 Nov 2026 • 14:30",
                                    fontSize = 9.sp,
                                    color = Color(0xFF6B7280)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE5E7EB)))
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Apel Fuji 1kg", fontSize = 9.sp, color = Color(0xFF1F2937))
                                    Text("Rp 48.500", fontSize = 9.sp, color = Color(0xFF1F2937))
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Daging Sapi Segar", fontSize = 9.sp, color = Color(0xFF1F2937))
                                    Text("Rp 185.000", fontSize = 9.sp, color = Color(0xFF1F2937))
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Minyak Goreng 2L", fontSize = 9.sp, color = Color(0xFF1F2937))
                                    Text("Rp 115.000", fontSize = 9.sp, color = Color(0xFF1F2937))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE5E7EB)))
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("TOTAL BELANJA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF114232))
                                    Text("Rp 348.500", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF114232))
                                }
                            }
                        }

                        // Animated Laser Line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.5.dp)
                                .offset(y = (laserOffset - 90).dp)
                                .background(SakuEmerald)
                        )

                        // Buka Galeri Button at Bottom
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .clip(RoundedCornerShape(13.dp))
                                .background(Color(0xFF142921))
                                .clickable {
                                    scope.launch { snackbarHostState.showSnackbar(if (isEn) "Opening gallery photo picker..." else "Membuka pemilih foto galeri...") }
                                }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Image,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isEn) "Open Gallery" else "Buka Galeri",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // OCR Result Summary Card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(20.dp), spotColor = Color(0x0A000000))
                            .background(sakuColors.cardBg, RoundedCornerShape(20.dp))
                            .border(1.dp, sakuColors.cardBorder, RoundedCornerShape(20.dp))
                            .padding(18.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (isEn) "Detected Amount" else "Nominal Terdeteksi",
                                        fontSize = 12.sp,
                                        color = sakuColors.textSecondary
                                    )
                                    Text(
                                        text = formatRupiah(ocrAmount),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = sakuColors.textDark
                                    )
                                }

                                // Engine Status Tag
                                Box(
                                    modifier = Modifier
                                        .background(sakuColors.trackBg, RoundedCornerShape(11.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (ocrEngine == 0) "⚡ OCR On-Device" else "✨ Gemini Vision",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = sakuColors.primary
                                    )
                                }
                            }

                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(sakuColors.border))

                            // Simpan ke Pos Kantong
                            Column {
                                Text(
                                    text = "${strings.selectPocketLabel}:",
                                    fontSize = 12.sp,
                                    color = sakuColors.textSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    availablePocketNames.forEach { pkt ->
                                        val isSelected = ocrPocket == pkt
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(15.dp))
                                                .background(if (isSelected) sakuColors.chipActiveBg else sakuColors.chipInactiveBg)
                                                .border(
                                                    1.dp,
                                                    if (isSelected) sakuColors.primary else sakuColors.border,
                                                    RoundedCornerShape(15.dp)
                                                )
                                                .clickable { ocrPocket = pkt }
                                                .padding(vertical = 7.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = strings.localizePocketName(pkt),
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) sakuColors.chipActiveText else sakuColors.chipInactiveText,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }

                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(sakuColors.border))

                            // Metadata rows
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(if (isEn) "Merchant Name" else "Nama Merchant", fontSize = 11.sp, color = sakuColors.textSecondary)
                                    Text("Superindo - Grand Mall", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = sakuColors.textDark)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(strings.detailTime, fontSize = 11.sp, color = sakuColors.textSecondary)
                                    Text("21 Nov 2026, 14:30", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = sakuColors.textDark)
                                }
                            }

                            Column {
                                Text(strings.detailNotes, fontSize = 11.sp, color = sakuColors.textSecondary)
                                Text(if (isEn) "Weekly Groceries, Meat & Fruits" else "Belanja Sayur, Daging & Buah Mingguan", fontSize = 12.sp, color = sakuColors.textSecondary)
                            }
                        }
                    }
                }

                // CTA Button: Simpan Transaksi Struk
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color(0x33114232))
                            .clip(RoundedCornerShape(16.dp))
                            .background(sakuColors.primary)
                            .clickable {
                                dataRepository.addTransaction(
                                    title = "Superindo - Grand Mall",
                                    amount = ocrAmount,
                                    isExpense = true,
                                    categoryType = CategoryType.FOOD,
                                    pocketName = ocrPocket,
                                    dateText = "21 Nov 2026, 14:30",
                                    notes = if (isEn) "Weekly Groceries, Meat & Fruits (OCR Scan)" else "Belanja Sayur, Daging & Buah Mingguan (OCR Scan)",
                                    method = if (ocrEngine == 0) "Offline ML Kit OCR" else "Gemini Vision AI"
                                )
                                onNavigateBack()
                            }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isEn) "Save Scanned Receipt" else "Simpan Transaksi Struk",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                painter = painterResource(id = SakuIcons.Check),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            } else {
                // ==========================================
                // TAB 1: INPUT MANUAL (Frame 808)
                // ==========================================

                // Toggle Pengeluaran vs Pemasukan
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(sakuColors.trackBg, RoundedCornerShape(14.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Pengeluaran
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(11.dp))
                                .background(if (isExpense) Color(0xFFFFF1F2) else Color.Transparent)
                                .clickable { isExpense = true }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.NorthEast,
                                    contentDescription = null,
                                    tint = if (isExpense) SakuExpenseRed else sakuColors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = strings.transactionTypeExpense,
                                    fontSize = 13.sp,
                                    fontWeight = if (isExpense) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isExpense) SakuExpenseRed else sakuColors.textSecondary
                                )
                            }
                        }

                        // Pemasukan
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(11.dp))
                                .background(if (!isExpense) SakuMint else Color.Transparent)
                                .clickable { isExpense = false }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.SouthWest,
                                    contentDescription = null,
                                    tint = if (!isExpense) SakuIncomeGreen else sakuColors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = strings.transactionTypeIncome,
                                    fontSize = 13.sp,
                                    fontWeight = if (!isExpense) FontWeight.Bold else FontWeight.Medium,
                                    color = if (!isExpense) SakuIncomeGreen else sakuColors.textSecondary
                                )
                            }
                        }
                    }
                }

                // Nominal Amount Hero Card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(20.dp), spotColor = Color(0x0A000000))
                            .background(sakuColors.cardBg, RoundedCornerShape(20.dp))
                            .border(1.dp, sakuColors.cardBorder, RoundedCornerShape(20.dp))
                            .padding(18.dp)
                    ) {
                        Column {
                            Text(
                                text = if (isExpense) (if (isEn) "Expense Amount" else "Nominal Pengeluaran") else (if (isEn) "Income Amount" else "Nominal Pemasukan"),
                                fontSize = 13.sp,
                                color = sakuColors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = formatRupiah(manualAmount),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = sakuColors.textDark
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(26.dp)
                                        .background(sakuColors.secondary, RoundedCornerShape(1.dp))
                                    )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Quick chips: +10rb, +50rb, +100rb, +500rb
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    Pair(if (isEn) "+10k" else "+10rb", 10000L),
                                    Pair(if (isEn) "+50k" else "+50rb", 50000L),
                                    Pair(if (isEn) "+100k" else "+100rb", 100000L),
                                    Pair(if (isEn) "+500k" else "+500rb", 500000L)
                                ).forEach { (label, value) ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(sakuColors.trackBg)
                                            .clickable { manualAmount += value }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = sakuColors.textDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Form Details Card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(20.dp), spotColor = Color(0x0A000000))
                            .background(sakuColors.cardBg, RoundedCornerShape(20.dp))
                            .border(1.dp, sakuColors.cardBorder, RoundedCornerShape(20.dp))
                            .padding(18.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            // Pos Kantong Tujuan
                            Column {
                                Text(
                                    text = "${strings.selectPocketLabel}:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = sakuColors.textDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    availablePocketNames.forEach { pkt ->
                                        val isSelected = manualPocket == pkt
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(15.dp))
                                                .background(if (isSelected) sakuColors.chipActiveBg else sakuColors.chipInactiveBg)
                                                .border(
                                                    1.dp,
                                                    if (isSelected) sakuColors.primary else sakuColors.border,
                                                    RoundedCornerShape(15.dp)
                                                )
                                                .clickable { manualPocket = pkt }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = strings.localizePocketName(pkt),
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) sakuColors.chipActiveText else sakuColors.chipInactiveText,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }

                            // Tanggal & Waktu
                            Column {
                                Text(
                                    text = strings.transactionDateLabel,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = sakuColors.textDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(sakuColors.inputBg, RoundedCornerShape(12.dp))
                                        .border(1.dp, sakuColors.inputBorder, RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = manualDate,
                                        fontSize = 13.sp,
                                        color = sakuColors.textDark
                                    )
                                }
                            }

                            // Catatan / Keterangan
                            Column {
                                Text(
                                    text = strings.transactionNotesLabel,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = sakuColors.textDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = manualNote,
                                    onValueChange = { manualNote = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = sakuColors.inputBg,
                                        unfocusedContainerColor = sakuColors.inputBg,
                                        focusedBorderColor = sakuColors.primary,
                                        unfocusedBorderColor = sakuColors.inputBorder,
                                        focusedTextColor = sakuColors.textDark,
                                        unfocusedTextColor = sakuColors.textDark
                                    )
                                )
                            }

                            // Bukti Transaksi Button
                            Column {
                                Text(
                                    text = if (isEn) "Transaction Proof (Optional)" else "Bukti Transaksi (Opsional)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = sakuColors.textDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(sakuColors.surface)
                                        .border(1.dp, sakuColors.border, RoundedCornerShape(12.dp))
                                        .clickable {
                                            scope.launch { snackbarHostState.showSnackbar(if (isEn) "Select transaction proof from camera/gallery..." else "Pilih bukti transaksi dari kamera/galeri...") }
                                        }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isEn) "+ Attach Photo / Payment Receipt" else "+ Lampirkan Foto / Bukti Bayar",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = sakuColors.textSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // CTA Button: Simpan Transaksi Manual
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color(0x33114232))
                            .clip(RoundedCornerShape(16.dp))
                            .background(sakuColors.primary)
                            .clickable {
                                val catType = when (manualPocket) {
                                    "Kebutuhan Pokok" -> CategoryType.FOOD
                                    "Transportasi" -> CategoryType.TRANSPORT
                                    "Keinginan", "Hiburan" -> CategoryType.ENTERTAINMENT
                                    else -> if (isExpense) CategoryType.GENERAL else CategoryType.INCOME
                                }
                                dataRepository.addTransaction(
                                    title = if (manualNote.isNotBlank()) manualNote else (if (isExpense) (if (isEn) "Manual Expense" else "Pengeluaran Manual") else (if (isEn) "Manual Income" else "Pemasukan Manual")),
                                    amount = manualAmount,
                                    isExpense = isExpense,
                                    categoryType = if (isExpense) catType else CategoryType.INCOME,
                                    pocketName = manualPocket,
                                    dateText = manualDate,
                                    notes = manualNote,
                                    method = if (isEn) "Manual Input" else "Input Manual"
                                )
                                onNavigateBack()
                            }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = strings.saveTransactionBtn,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                painter = painterResource(id = SakuIcons.Check),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Preview(name = "Scan Struk Screen", showBackground = true, device = Devices.PIXEL_7)
@Composable
fun AddTransactionScanPreview() {
    SAKUTheme {
        AddTransactionScreen(initialTab = 0)
    }
}

@Preview(name = "Input Manual Screen", showBackground = true, device = Devices.PIXEL_7)
@Composable
fun AddTransactionManualPreview() {
    SAKUTheme {
        AddTransactionScreen(initialTab = 1)
    }
}
