package com.saku.app.ui.transaction

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.NorthEast
import androidx.compose.material.icons.outlined.SouthWest
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import com.saku.app.ui.theme.SakuIcons
import com.saku.app.util.ImageUtils
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.saku.app.data.preference.AppPreferences
import com.saku.app.data.remote.GeminiReceiptService
import com.saku.app.data.remote.GeminiResult
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
        names.distinct()
    }

    // Manual input states
    var isExpense by remember { mutableStateOf(true) }
    var manualAmount by remember { mutableLongStateOf(150000L) }
    var manualPocket by remember { mutableStateOf(availablePocketNames.firstOrNull() ?: "Kebutuhan Pokok") }
    var manualNote by remember(isEn) { mutableStateOf(if (isEn) "Fuel refill & monthly oil change" else "Isi bensin motor & ganti oli bulanan") }
    var manualDate by remember(isEn) { mutableStateOf(if (isEn) "Today, 21 Nov 2026 (18:30 WIB)" else "Hari ini, 21 November 2026 (18:30 WIB)") }

    // OCR states
    var ocrPocket by remember { mutableStateOf(availablePocketNames.firstOrNull() ?: "Kebutuhan Pokok") }
    var ocrAmount by remember { mutableLongStateOf(348500L) }
    var ocrMerchant by remember { mutableStateOf("Superindo - Grand Mall") }
    var ocrDate by remember { mutableStateOf("21 Nov 2026, 14:30") }
    var ocrNotes by remember(isEn) {
        mutableStateOf(
            if (isEn) "Weekly Groceries, Meat & Fruits (OCR Scan)"
            else "Belanja Sayur, Daging & Buah Mingguan (OCR Scan)"
        )
    }
    var ocrCategoryType by remember { mutableStateOf(CategoryType.FOOD) }
    var isScanningWithGemini by remember { mutableStateOf(false) }
    var geminiScanError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val appPreferences = remember { AppPreferences.getInstance(context) }
    val savedGeminiApiKey by appPreferences.geminiApiKey.collectAsState()
    val hasGeminiKey = savedGeminiApiKey.isNotBlank()

    fun analyzeReceiptWithGemini(uri: Uri) {
        if (isScanningWithGemini) return
        isScanningWithGemini = true
        geminiScanError = null
        scope.launch {
            when (
                val result = GeminiReceiptService.scanReceipt(
                    context = context,
                    imageUri = uri,
                    apiKey = savedGeminiApiKey,
                    availablePockets = availablePocketNames,
                    isEn = isEn
                )
            ) {
                is GeminiResult.Success -> {
                    val data = result.data
                    ocrAmount = data.amount
                    ocrMerchant = data.merchant
                    ocrDate = data.date
                    ocrNotes = data.notes.ifBlank {
                        if (isEn) "Scanned via Gemini Vision AI" else "Dipindai dengan Gemini Vision AI"
                    }
                    ocrCategoryType = data.categoryType
                    data.suggestedPocket?.let { ocrPocket = it }
                    snackbarHostState.showSnackbar(
                        if (isEn) "Receipt analyzed with Gemini AI" else "Struk berhasil dianalisis Gemini AI"
                    )
                }
                is GeminiResult.Error -> {
                    geminiScanError = result.message
                    snackbarHostState.showSnackbar(result.message)
                }
            }
            isScanningWithGemini = false
        }
    }

    // Camera & gallery photo states (0 = Scan Struk tab, 1 = Manual proof attachment)
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var capturedReceiptUri by remember { mutableStateOf<Uri?>(null) }
    var manualProofUri by remember { mutableStateOf<Uri?>(null) }
    var activeCaptureTarget by remember { mutableIntStateOf(0) }
    var showManualProofPickerDialog by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = pendingCameraUri
        if (success && uri != null) {
            if (activeCaptureTarget == 0) {
                capturedReceiptUri = uri
                if (ocrEngine == 1) {
                    analyzeReceiptWithGemini(uri)
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (isEn) "Receipt photo captured" else "Foto struk berhasil diambil"
                        )
                    }
                }
            } else {
                manualProofUri = uri
                scope.launch {
                    snackbarHostState.showSnackbar(
                        if (isEn) "Transaction proof attached" else "Bukti transaksi berhasil dilampirkan"
                    )
                }
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = ImageUtils.createTempReceiptUri(context)
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            scope.launch {
                snackbarHostState.showSnackbar(
                    if (isEn) "Camera permission is required to capture receipts"
                    else "Izin kamera diperlukan untuk memotret struk"
                )
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            if (activeCaptureTarget == 0) {
                capturedReceiptUri = uri
                if (ocrEngine == 1) {
                    analyzeReceiptWithGemini(uri)
                }
            } else {
                manualProofUri = uri
            }
        }
    }

    fun launchSystemCamera(target: Int) {
        activeCaptureTarget = target
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            val uri = ImageUtils.createTempReceiptUri(context)
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val capturedReceiptBitmap = ImageUtils.rememberUriBitmap(capturedReceiptUri)
    val manualProofBitmap = ImageUtils.rememberUriBitmap(manualProofUri)

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
                    // Tab 0: Pindai Struk
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
                            text = strings.scanReceiptTab,
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
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
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
                                .clickable {
                                    ocrEngine = 1
                                    capturedReceiptUri?.let { uri ->
                                        analyzeReceiptWithGemini(uri)
                                    }
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
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
                        }
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
                            .clickable { launchSystemCamera(target = 0) }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (capturedReceiptBitmap != null) {
                            Image(
                                bitmap = capturedReceiptBitmap,
                                contentDescription = if (isEn) "Captured receipt" else "Foto struk",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(14.dp))
                            )
                        } else {
                            // Simulated Receipt in Center (placeholder before taking photo)
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
                        }

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

                        // Camera Hint Pill at Top
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .clip(RoundedCornerShape(13.dp))
                                .background(Color(0xCC142921))
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.CameraAlt,
                                    contentDescription = null,
                                    tint = SakuEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (capturedReceiptBitmap != null) {
                                        if (isEn) "Tap to Retake" else "Ketuk untuk Foto Ulang"
                                    } else {
                                        if (isEn) "Tap to Open Camera" else "Ketuk untuk Buka Kamera"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }

                        // Buka Galeri Button at Bottom
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .clip(RoundedCornerShape(13.dp))
                                .background(Color(0xFF142921))
                                .clickable {
                                    activeCaptureTarget = 0
                                    galleryLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
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

                            // Editable Nominal
                            Column {
                                Text(
                                    text = if (isEn) "Expense Amount (Rp)" else "Nominal Pengeluaran (Rp)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = sakuColors.textDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = if (ocrAmount > 0L) ocrAmount.toString() else "",
                                    onValueChange = { input ->
                                        val digits = input.filter { it.isDigit() }
                                        ocrAmount = digits.toLongOrNull() ?: 0L
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    placeholder = {
                                        Text(
                                            text = "0",
                                            fontSize = 13.sp,
                                            color = sakuColors.textMuted
                                        )
                                    },
                                    prefix = {
                                        Text(
                                            text = "Rp ",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = sakuColors.textSecondary
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = sakuColors.inputBg,
                                        unfocusedContainerColor = sakuColors.inputBg,
                                        focusedBorderColor = sakuColors.primary,
                                        unfocusedBorderColor = sakuColors.inputBorder
                                    )
                                )
                            }

                            if (isScanningWithGemini) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(sakuColors.trackBg, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = sakuColors.primary
                                    )
                                    Text(
                                        text = if (isEn) "Extracting receipt details with Gemini AI..." else "Mengekstrak detail struk dengan Gemini AI...",
                                        fontSize = 12.sp,
                                        color = sakuColors.textDark
                                    )
                                }
                            } else if (geminiScanError != null && ocrEngine == 1) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFFFF5F5), RoundedCornerShape(10.dp))
                                        .border(1.dp, Color(0xFFFED7D7), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = geminiScanError.orEmpty(),
                                        fontSize = 11.sp,
                                        color = SakuExpenseRed,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (capturedReceiptUri != null) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isEn) "Retry" else "Coba Lagi",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = sakuColors.primary,
                                            modifier = Modifier.clickable {
                                                capturedReceiptUri?.let { analyzeReceiptWithGemini(it) }
                                            }
                                        )
                                    }
                                }
                            }

                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(sakuColors.border))

                            // Simpan ke Pos Kantong
                            PocketSelectorSection(
                                label = "${strings.selectPocketLabel}:",
                                pockets = availablePocketNames,
                                selectedPocket = ocrPocket,
                                onSelectPocket = { ocrPocket = it },
                                localizeName = { strings.localizePocketName(it) },
                                isBoldLabel = false
                            )

                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(sakuColors.border))

                            // Editable Expense Name / Merchant
                            Column {
                                Text(
                                    text = if (isEn) "Expense / Merchant Name" else "Nama Pengeluaran / Merchant",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = sakuColors.textDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = ocrMerchant,
                                    onValueChange = { ocrMerchant = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    placeholder = {
                                        Text(
                                            text = if (isEn) "e.g. Lunch / Superindo" else "Contoh: Makan Siang / Superindo",
                                            fontSize = 13.sp,
                                            color = sakuColors.textMuted
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = sakuColors.inputBg,
                                        unfocusedContainerColor = sakuColors.inputBg,
                                        focusedBorderColor = sakuColors.primary,
                                        unfocusedBorderColor = sakuColors.inputBorder
                                    )
                                )
                            }

                            // Category Quick Chips
                            Column {
                                Text(
                                    text = if (isEn) "Category" else "Kategori",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = sakuColors.textDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                val categoryOptions = listOf(
                                    CategoryType.FOOD to (if (isEn) "Food" else "Makan"),
                                    CategoryType.TRANSPORT to (if (isEn) "Transport" else "Transport"),
                                    CategoryType.ENTERTAINMENT to (if (isEn) "Leisure" else "Hiburan"),
                                    CategoryType.GENERAL to (if (isEn) "Bills" else "Tagihan")
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    categoryOptions.forEach { (catType, catLabel) ->
                                        val isSelected = ocrCategoryType == catType
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isSelected) sakuColors.chipActiveBg else sakuColors.trackBg)
                                                .border(
                                                    1.dp,
                                                    if (isSelected) sakuColors.primary else sakuColors.border,
                                                    RoundedCornerShape(10.dp)
                                                )
                                                .clickable { ocrCategoryType = catType }
                                                .padding(vertical = 7.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = catLabel,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) sakuColors.chipActiveText else sakuColors.textDark,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }

                            // Editable Date & Time
                            Column {
                                Text(
                                    text = strings.transactionDateLabel,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = sakuColors.textDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = ocrDate,
                                    onValueChange = { ocrDate = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    placeholder = {
                                        Text(
                                            text = if (isEn) "e.g. 21 Nov 2026, 14:30" else "Contoh: 21 Nov 2026, 14:30",
                                            fontSize = 13.sp,
                                            color = sakuColors.textMuted
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = sakuColors.inputBg,
                                        unfocusedContainerColor = sakuColors.inputBg,
                                        focusedBorderColor = sakuColors.primary,
                                        unfocusedBorderColor = sakuColors.inputBorder
                                    )
                                )
                            }

                            // Editable Transaction Notes
                            Column {
                                Text(
                                    text = strings.detailNotes,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = sakuColors.textDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = ocrNotes,
                                    onValueChange = { ocrNotes = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 2,
                                    maxLines = 3,
                                    placeholder = {
                                        Text(
                                            text = if (isEn) "Add transaction notes..." else "Tambahkan catatan transaksi...",
                                            fontSize = 13.sp,
                                            color = sakuColors.textMuted
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = sakuColors.inputBg,
                                        unfocusedContainerColor = sakuColors.inputBg,
                                        focusedBorderColor = sakuColors.primary,
                                        unfocusedBorderColor = sakuColors.inputBorder
                                    )
                                )
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
                                    title = ocrMerchant,
                                    amount = ocrAmount,
                                    isExpense = ocrCategoryType != CategoryType.INCOME,
                                    categoryType = ocrCategoryType,
                                    pocketName = ocrPocket,
                                    dateText = ocrDate,
                                    notes = ocrNotes,
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
                            PocketSelectorSection(
                                label = "${strings.selectPocketLabel}:",
                                pockets = availablePocketNames,
                                selectedPocket = manualPocket,
                                onSelectPocket = { manualPocket = it },
                                localizeName = { strings.localizePocketName(it) },
                                isBoldLabel = true
                            )

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
                                if (manualProofBitmap != null) {
                                    Image(
                                        bitmap = manualProofBitmap,
                                        contentDescription = if (isEn) "Transaction proof" else "Bukti transaksi",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(140.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .border(1.dp, sakuColors.border, RoundedCornerShape(12.dp))
                                            .clickable { showManualProofPickerDialog = true }
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(sakuColors.surface)
                                        .border(1.dp, sakuColors.border, RoundedCornerShape(12.dp))
                                        .clickable { showManualProofPickerDialog = true }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (manualProofBitmap != null) {
                                            if (isEn) "Change Photo / Payment Receipt" else "Ganti Foto / Bukti Bayar"
                                        } else {
                                            if (isEn) "+ Attach Photo / Payment Receipt" else "+ Lampirkan Foto / Bukti Bayar"
                                        },
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

    if (showManualProofPickerDialog) {
        AlertDialog(
            onDismissRequest = { showManualProofPickerDialog = false },
            title = {
                Text(
                    text = if (isEn) "Attach Transaction Proof" else "Lampirkan Bukti Transaksi",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isEn) "Choose how to add the receipt or payment proof photo."
                    else "Pilih sumber foto untuk melampirkan bukti pembayaran."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showManualProofPickerDialog = false
                        launchSystemCamera(target = 1)
                    }
                ) {
                    Text(if (isEn) "Camera" else "Kamera")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showManualProofPickerDialog = false
                        activeCaptureTarget = 1
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Text(if (isEn) "Gallery" else "Galeri")
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PocketSelectorSection(
    label: String,
    pockets: List<String>,
    selectedPocket: String,
    onSelectPocket: (String) -> Unit,
    localizeName: (String) -> String,
    isBoldLabel: Boolean
) {
    val sakuColors = LocalSakuColors.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = if (isBoldLabel) 13.sp else 12.sp,
            fontWeight = if (isBoldLabel) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isBoldLabel) sakuColors.textDark else sakuColors.textSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            pockets.forEach { pkt ->
                val isSelected = selectedPocket == pkt
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) sakuColors.chipActiveBg else sakuColors.chipInactiveBg)
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) sakuColors.primary else sakuColors.border,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onSelectPocket(pkt) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            tint = sakuColors.chipActiveText,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = localizeName(pkt),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) sakuColors.chipActiveText else sakuColors.chipInactiveText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
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
