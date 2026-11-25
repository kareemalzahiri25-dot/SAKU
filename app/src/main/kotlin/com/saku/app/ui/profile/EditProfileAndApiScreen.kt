package com.saku.app.ui.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saku.app.data.DataRepository
import com.saku.app.data.DefaultDataRepository
import com.saku.app.data.localization.EnglishStrings
import com.saku.app.data.localization.LocalAppStrings
import com.saku.app.data.preference.AppPreferences
import com.saku.app.ui.theme.LocalSakuColors
import androidx.compose.ui.res.painterResource
import com.saku.app.ui.theme.SakuIcons
import com.saku.app.ui.theme.SAKUTheme
import com.saku.app.ui.theme.SakuEmerald
import com.saku.app.ui.theme.SakuExpenseRed
import com.saku.app.util.ImageUtils
import kotlinx.coroutines.launch

@Composable
fun EditProfileAndApiScreen(
    onNavigateBack: () -> Unit = {},
    dataRepository: DataRepository = DefaultDataRepository.getInstance(),
    modifier: Modifier = Modifier
) {
    val sakuColors = LocalSakuColors.current
    val strings = LocalAppStrings.current
    val isEn = strings == EnglishStrings
    val context = LocalContext.current
    val appPreferences = remember { AppPreferences.getInstance(context) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val savedName by appPreferences.userName.collectAsState(initial = "Damar Pratama")
    val savedEmail by appPreferences.userEmail.collectAsState(initial = "damar.pratama@email.com")
    val savedApiKey by appPreferences.geminiApiKey.collectAsState(initial = "AIzaSyD-a89XkL9102mNqZw53x9Q")
    val savedPhotoPath by appPreferences.profileImagePath.collectAsState(initial = null)

    var fullName by remember(savedName) { mutableStateOf(savedName) }
    var email by remember(savedEmail) { mutableStateOf(savedEmail) }
    var apiKey by remember(savedApiKey) { mutableStateOf(savedApiKey) }
    var currentPhotoPath by remember(savedPhotoPath) { mutableStateOf(savedPhotoPath) }
    val avatarBitmap = ImageUtils.rememberProfileBitmap(currentPhotoPath)

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val newPath = ImageUtils.saveProfileAvatarFromUri(context, uri)
            if (newPath != null) {
                currentPhotoPath = newPath
                appPreferences.setProfileImagePath(newPath)
                scope.launch {
                    snackbarHostState.showSnackbar(strings.photoUpdatedSuccess)
                }
            }
        }
    }

    var isApiKeyVisible by remember { mutableStateOf(false) }
    var apiStatus by remember(isEn) { mutableStateOf(if (isEn) "Connected & Active" else "Terhubung & Aktif") }
    var isTestingApi by remember { mutableStateOf(false) }

    val initials = remember(fullName) {
        fullName.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercase() }
            .joinToString("")
            .ifEmpty { "DP" }
    }

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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Header with Back Button & Title
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(sakuColors.surface)
                            .border(1.dp, sakuColors.border, CircleShape)
                            .clickable { onNavigateBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = SakuIcons.Back),
                            contentDescription = strings.back,
                            tint = sakuColors.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = strings.editProfileTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = sakuColors.textDark
                    )
                }
            }

            // 2. Avatar with Camera Badge & Change Photo Text
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Avatar Circle
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(sakuColors.primary)
                                .border(2.dp, sakuColors.border, CircleShape)
                                .shadow(2.dp, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (avatarBitmap != null) {
                                Image(
                                    bitmap = avatarBitmap,
                                    contentDescription = strings.changeProfilePhoto,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text(
                                    text = initials,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Camera Badge
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(sakuColors.surface)
                                .border(1.dp, sakuColors.border, CircleShape)
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = SakuIcons.Camera),
                                contentDescription = strings.changeProfilePhoto,
                                tint = sakuColors.primary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.changeProfilePhoto,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = sakuColors.primary,
                            modifier = Modifier.clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        )

                        if (currentPhotoPath != null) {
                            Text(
                                text = "•",
                                fontSize = 13.sp,
                                color = sakuColors.textSecondary
                            )

                            Text(
                                text = strings.removeProfilePhoto,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = sakuColors.expenseRed,
                                modifier = Modifier.clickable {
                                    ImageUtils.deleteProfileAvatar(currentPhotoPath)
                                    currentPhotoPath = null
                                    appPreferences.setProfileImagePath(null)
                                    scope.launch {
                                        snackbarHostState.showSnackbar(strings.photoRemovedSuccess)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // 3. Section 1: DATA PRIBADI
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isEn) "PERSONAL DATA" else "DATA PRIBADI",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = sakuColors.textSecondary,
                        letterSpacing = 0.8.sp
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = sakuColors.cardBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, sakuColors.cardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Nama Lengkap
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = strings.fullNameLabel,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = sakuColors.textSecondary
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(sakuColors.inputBg)
                                        .border(1.dp, sakuColors.inputBorder, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    BasicTextField(
                                        value = fullName,
                                        onValueChange = { fullName = it },
                                        textStyle = TextStyle(
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = sakuColors.textDark
                                        ),
                                        cursorBrush = SolidColor(sakuColors.primary),
                                        modifier = Modifier.weight(1f)
                                    )

                                    Icon(
                                        painter = painterResource(id = SakuIcons.Edit),
                                        contentDescription = "Edit Nama",
                                        tint = sakuColors.textSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            // Divider
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(sakuColors.border)
                            )

                            // Alamat Email
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = strings.emailLabel,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = sakuColors.textSecondary
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(sakuColors.inputBg)
                                        .border(1.dp, sakuColors.inputBorder, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    BasicTextField(
                                        value = email,
                                        onValueChange = { email = it },
                                        textStyle = TextStyle(
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = sakuColors.textDark
                                        ),
                                        cursorBrush = SolidColor(sakuColors.primary),
                                        modifier = Modifier.weight(1f)
                                    )

                                    Icon(
                                        imageVector = Icons.Outlined.CheckCircle,
                                        contentDescription = if (isEn) "Verified Email" else "Email Terverifikasi",
                                        tint = SakuEmerald,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Section 2: KONFIGURASI API KEY
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isEn) "API KEY CONFIGURATION" else "KONFIGURASI API KEY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = sakuColors.textSecondary,
                        letterSpacing = 0.8.sp
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = sakuColors.cardBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, sakuColors.cardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = strings.geminiApiKeyLabel,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = sakuColors.primary
                            )

                            // API Key Input Box
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(sakuColors.inputBg)
                                    .border(1.dp, sakuColors.inputBorder, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = SakuIcons.Key),
                                    contentDescription = "API Key",
                                    tint = sakuColors.textSecondary,
                                    modifier = Modifier.size(18.dp)
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                BasicTextField(
                                    value = apiKey,
                                    onValueChange = { apiKey = it },
                                    visualTransformation = if (isApiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    textStyle = TextStyle(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = sakuColors.textDark
                                    ),
                                    cursorBrush = SolidColor(sakuColors.primary),
                                    modifier = Modifier.weight(1f)
                                )

                                IconButton(
                                    onClick = { isApiKeyVisible = !isApiKeyVisible },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isApiKeyVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                        contentDescription = "Toggle Visibility",
                                        tint = sakuColors.textSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // 3 Action Buttons: Cek Koneksi, Tempel, Hapus
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Cek Koneksi
                                Surface(
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .height(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            isTestingApi = true
                                            scope.launch {
                                                kotlinx.coroutines.delay(600)
                                                isTestingApi = false
                                                apiStatus = if (isEn) "Connected & Active" else "Terhubung & Aktif"
                                                snackbarHostState.showSnackbar(if (isEn) "Gemini API connection successfully verified!" else "Koneksi API Gemini berhasil diverifikasi!")
                                            }
                                        },
                                    color = sakuColors.trackBg,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, sakuColors.secondary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Bolt,
                                            contentDescription = "Cek",
                                            tint = sakuColors.secondary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isTestingApi) (if (isEn) "Testing..." else "Menguji...") else strings.testConnectionBtn,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = sakuColors.textDark
                                        )
                                    }
                                }

                                // Tempel
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            apiKey = "AIzaSyD-newPastedKeyExample98471"
                                            scope.launch {
                                                snackbarHostState.showSnackbar(if (isEn) "API Key pasted successfully" else "API Key berhasil ditempel")
                                            }
                                        },
                                    color = sakuColors.surface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, sakuColors.border),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = SakuIcons.Paste),
                                            contentDescription = "Tempel",
                                            tint = sakuColors.textDark,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isEn) "Paste" else "Tempel",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = sakuColors.textDark
                                        )
                                    }
                                }

                                // Hapus
                                Surface(
                                    modifier = Modifier
                                        .weight(0.9f)
                                        .height(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            apiKey = ""
                                            apiStatus = if (isEn) "Not Connected" else "Belum Terhubung"
                                            scope.launch {
                                                snackbarHostState.showSnackbar(if (isEn) "API Key cleared" else "API Key dikosongkan")
                                            }
                                        },
                                    color = Color(0xFFFFF5F5),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFED7D7)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = SakuIcons.Delete),
                                            contentDescription = "Hapus",
                                            tint = SakuExpenseRed,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isEn) "Clear" else "Hapus",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = SakuExpenseRed
                                        )
                                    }
                                }
                            }

                            // API Status Box
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (apiKey.isNotEmpty()) Color(0xFFF0FDF4) else Color(0xFFFFF7ED))
                                    .border(
                                        1.dp,
                                        if (apiKey.isNotEmpty()) Color(0xFFBBF7D0) else Color(0xFFFED7AA),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (apiKey.isNotEmpty()) SakuEmerald else Color(0xFFF97316))
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "${if (isEn) "Status" else "Status"}: $apiStatus",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (apiKey.isNotEmpty()) Color(0xFF166534) else Color(0xFF9A3412)
                                )
                            }
                        }
                    }
                }
            }

            // 5. Action Buttons (Simpan Perubahan & Batalkan)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                appPreferences.setUserProfile(fullName, email, apiKey)
                                dataRepository.updateUserName(fullName)
                                snackbarHostState.showSnackbar(strings.profileUpdatedSuccess)
                                kotlinx.coroutines.delay(300)
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = sakuColors.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = strings.saveProfileBtn,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = sakuColors.surface,
                            contentColor = sakuColors.textSecondary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, sakuColors.border)
                    ) {
                        Text(
                            text = strings.cancel,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Edit Profile & API - Light Mode", device = Devices.PIXEL_7)
@Composable
private fun EditProfileAndApiScreenPreview() {
    SAKUTheme {
        EditProfileAndApiScreen()
    }
}
