package com.saku.app.ui.profile

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import com.saku.app.ui.theme.SakuIcons
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.saku.app.data.localization.LocalAppStrings
import com.saku.app.data.preference.AppLanguage
import com.saku.app.data.preference.AppPreferences
import com.saku.app.data.preference.ThemeMode
import com.saku.app.security.BiometricAvailability
import com.saku.app.security.BiometricHelper
import com.saku.app.ui.main.SakuBottomNavigation
import com.saku.app.ui.theme.SAKUTheme
import com.saku.app.ui.theme.SakuDarkGreen
import com.saku.app.ui.theme.SakuEmerald
import com.saku.app.ui.theme.SakuEmeraldLight
import com.saku.app.ui.theme.sakuColors
import com.saku.app.util.ImageUtils
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToPockets: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToAdd: () -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val appPreferences = remember { AppPreferences.getInstance(context) }

    val themeMode by appPreferences.themeMode.collectAsState()
    val appLanguage by appPreferences.appLanguage.collectAsState()
    val biometricEnabled by appPreferences.isBiometricEnabled.collectAsState()
    val notificationEnabled by appPreferences.isNotificationEnabled.collectAsState()
    val userName by appPreferences.userName.collectAsState(initial = "Damar Pratama")
    val userEmail by appPreferences.userEmail.collectAsState(initial = "damar.pratama@email.com")
    val profileImagePath by appPreferences.profileImagePath.collectAsState(initial = null)
    val avatarBitmap = ImageUtils.rememberProfileBitmap(profileImagePath)

    val dashboardData by com.saku.app.data.DefaultDataRepository.getInstance().dashboardData.collectAsState()

    val userInitials = remember(userName) {
        userName.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercase() }
            .joinToString("")
            .ifEmpty { "DP" }
    }

    val strings = LocalAppStrings.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var themeMenuExpanded by remember { mutableStateOf(false) }
    var languageMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = sakuColors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            ProfileBottomBar(
                onTabSelected = { tab ->
                    when (tab) {
                        0 -> onNavigateToHome()
                        1 -> onNavigateToPockets()
                        2 -> onNavigateToReports()
                        3 -> { /* already here */ }
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
            // 1. Screen Title
            item {
                Text(
                    text = strings.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = sakuColors.title,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // 2. User Overview Card (Frame 1311)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(20.dp), spotColor = sakuColors.cardShadow)
                        .background(sakuColors.surface, RoundedCornerShape(20.dp))
                        .border(1.dp, sakuColors.border, RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Avatar with camera badge
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clickable { onNavigateToEditProfile() }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(if (sakuColors.isDark) SakuEmerald else SakuDarkGreen),
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
                                            text = userInitials,
                                            fontSize = 19.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .align(Alignment.BottomEnd)
                                        .background(sakuColors.surface, CircleShape)
                                        .border(1.dp, sakuColors.border, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = SakuIcons.Camera),
                                        contentDescription = null,
                                        tint = sakuColors.title,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(verticalArrangement = Arrangement.Center) {
                                Text(
                                    text = userName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = sakuColors.textDark
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = userEmail,
                                    fontSize = 12.sp,
                                    color = sakuColors.textSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(sakuColors.divider))
                        Spacer(modifier = Modifier.height(14.dp))

                        // 3-Column Metrics Strip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text(com.saku.app.ui.main.formatRupiah(dashboardData.totalBalance), fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = sakuColors.textDark)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(strings.totalBalance, fontSize = 11.sp, color = sakuColors.textSecondary)
                            }
                            Box(modifier = Modifier.width(1.dp).height(28.dp).background(sakuColors.divider))
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text(com.saku.app.ui.main.formatRupiah(dashboardData.totalIncome), fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = SakuEmeraldLight)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(strings.income, fontSize = 11.sp, color = sakuColors.textSecondary)
                            }
                            Box(modifier = Modifier.width(1.dp).height(28.dp).background(sakuColors.divider))
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text(com.saku.app.ui.main.formatRupiah(dashboardData.totalExpense), fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = sakuColors.textDark)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(strings.expense, fontSize = 11.sp, color = sakuColors.textSecondary)
                            }
                        }
                    }
                }
            }

            // 3. Section Title
            item {
                Text(
                    text = strings.sectionAppSettings,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = sakuColors.textSecondary,
                    letterSpacing = 0.5.sp
                )
            }

            // 4. Settings List Container
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = sakuColors.cardShadow)
                        .background(sakuColors.surface, RoundedCornerShape(18.dp))
                        .border(1.dp, sakuColors.border, RoundedCornerShape(18.dp))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Column {
                        // 1. Biometrik
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                SettingIconBox(Icons.Outlined.Fingerprint)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(strings.biometricTitle, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = sakuColors.textDark)
                                    Text(strings.biometricSubtitle, fontSize = 11.sp, color = sakuColors.textSecondary)
                                }
                            }
                            Switch(
                                checked = biometricEnabled,
                                onCheckedChange = { targetState ->
                                    if (targetState) {
                                        if (activity != null) {
                                            val availability = BiometricHelper.checkAvailability(context)
                                            when (availability) {
                                                BiometricAvailability.AVAILABLE -> {
                                                    BiometricHelper.showBiometricPrompt(
                                                        activity = activity,
                                                        title = strings.biometricPromptTitle,
                                                        subtitle = strings.biometricPromptSubtitle,
                                                        negativeButtonText = strings.biometricPromptNegative,
                                                        onSuccess = {
                                                            appPreferences.setBiometricEnabled(true)
                                                            scope.launch {
                                                                snackbarHostState.showSnackbar(strings.biometricEnabledSuccess)
                                                            }
                                                        },
                                                        onError = {
                                                            scope.launch {
                                                                snackbarHostState.showSnackbar(strings.biometricAuthFailed)
                                                            }
                                                        }
                                                    )
                                                }
                                                BiometricAvailability.NOT_ENROLLED -> {
                                                    appPreferences.setBiometricEnabled(true)
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar("${strings.biometricEnabledSuccess} (${strings.biometricNotEnrolled})")
                                                    }
                                                }
                                                BiometricAvailability.NO_HARDWARE -> {
                                                    appPreferences.setBiometricEnabled(true)
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar("${strings.biometricEnabledSuccess} (${strings.biometricNotAvailable})")
                                                    }
                                                }
                                                else -> {
                                                    appPreferences.setBiometricEnabled(true)
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar(strings.biometricEnabledSuccess)
                                                    }
                                                }
                                            }
                                        } else {
                                            appPreferences.setBiometricEnabled(true)
                                        }
                                    } else {
                                        appPreferences.setBiometricEnabled(false)
                                        scope.launch {
                                            snackbarHostState.showSnackbar(strings.biometricDisabledSuccess)
                                        }
                                    }
                                },
                                modifier = Modifier.scale(0.82f),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = if (sakuColors.isDark) SakuEmerald else SakuDarkGreen,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = if (sakuColors.isDark) Color(0xFF225241) else Color(0xFFE5E7EB)
                                )
                            )
                        }

                        SettingDivider()

                        // 2. Pengaturan Aplikasi
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(onClick = onNavigateToEditProfile)
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                SettingIconBox(Icons.Outlined.Tune)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(strings.appSettingsTitle, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = sakuColors.textDark)
                                    Text(strings.appSettingsSubtitle, fontSize = 11.sp, color = sakuColors.textSecondary)
                                }
                            }
                            Icon(
                                painter = painterResource(id = SakuIcons.ChevronRight),
                                contentDescription = null,
                                tint = sakuColors.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        SettingDivider()

                        // 3. Tema
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                SettingIconBox(Icons.Outlined.DarkMode)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(strings.themeTitle, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = sakuColors.textDark)
                                    Text(strings.themeSubtitle, fontSize = 11.sp, color = sakuColors.textSecondary)
                                }
                            }

                            Box {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(sakuColors.pillBg)
                                        .clickable { themeMenuExpanded = true }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = "${themeMode.getDisplayName(appLanguage)} ▾",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = sakuColors.pillText
                                    )
                                }

                                DropdownMenu(
                                    expanded = themeMenuExpanded,
                                    onDismissRequest = { themeMenuExpanded = false }
                                ) {
                                    ThemeMode.entries.forEach { mode ->
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(mode.getDisplayName(appLanguage))
                                                    if (mode == themeMode) {
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Icon(
                                                            imageVector = Icons.Outlined.Check,
                                                            contentDescription = null,
                                                            tint = if (sakuColors.isDark) SakuEmeraldLight else SakuDarkGreen,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }
                                            },
                                            onClick = {
                                                appPreferences.setThemeMode(mode)
                                                themeMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        SettingDivider()

                        // 4. Bahasa
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                SettingIconBox(Icons.Outlined.Language)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(strings.languageTitle, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = sakuColors.textDark)
                                    Text(strings.languageSubtitle, fontSize = 11.sp, color = sakuColors.textSecondary)
                                }
                            }

                            Box {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(sakuColors.pillBg)
                                        .clickable { languageMenuExpanded = true }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = "${appLanguage.displayName} ▾",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = sakuColors.pillText
                                    )
                                }

                                DropdownMenu(
                                    expanded = languageMenuExpanded,
                                    onDismissRequest = { languageMenuExpanded = false }
                                ) {
                                    AppLanguage.entries.forEach { lang ->
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(lang.displayName)
                                                    if (lang == appLanguage) {
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Icon(
                                                            imageVector = Icons.Outlined.Check,
                                                            contentDescription = null,
                                                            tint = if (sakuColors.isDark) SakuEmeraldLight else SakuDarkGreen,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }
                                            },
                                            onClick = {
                                                appPreferences.setAppLanguage(lang)
                                                languageMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        SettingDivider()

                        // 5. Notifikasi
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                SettingIconBox(Icons.Outlined.Notifications)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(strings.notificationTitle, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = sakuColors.textDark)
                                    Text(strings.notificationSubtitle, fontSize = 11.sp, color = sakuColors.textSecondary)
                                }
                            }
                            Switch(
                                checked = notificationEnabled,
                                onCheckedChange = { appPreferences.setNotificationEnabled(it) },
                                modifier = Modifier.scale(0.82f),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = if (sakuColors.isDark) SakuEmerald else SakuDarkGreen,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = if (sakuColors.isDark) Color(0xFF225241) else Color(0xFFE5E7EB)
                                )
                            )
                        }
                    }
                }
            }

            // 5. Button: Keluar dari Akun (Frame 1311)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(sakuColors.logoutBg)
                        .border(1.dp, sakuColors.logoutBorder, RoundedCornerShape(16.dp))
                        .clickable {
                            scope.launch {
                                appPreferences.setLoggedIn(false)
                                onLogout()
                            }
                        },
                        contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Icon Out Box (24x24dp, rounded 6dp)
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(sakuColors.logoutIconBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = SakuIcons.Logout),
                                contentDescription = null,
                                tint = sakuColors.logoutText,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.logoutButton,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = sakuColors.logoutText
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
fun SettingIconBox(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(sakuColors.iconBoxBg, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = sakuColors.iconBoxTint,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun SettingDivider() {
    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.width(44.dp)) // Indented to align with text
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(sakuColors.divider)
        )
    }
}

@Composable
fun ProfileBottomBar(
    onTabSelected: (Int) -> Unit,
    onFabClick: () -> Unit
) {
    SakuBottomNavigation(
        selectedTab = 3,
        onTabSelected = onTabSelected,
        onFabClick = onFabClick
    )
}

@Preview(name = "Profile Screen", showBackground = true, device = Devices.PIXEL_7)
@Composable
fun ProfileScreenPreview() {
    SAKUTheme(darkTheme = false) {
        ProfileScreen()
    }
}

@Preview(name = "Profile Screen - Dark", showBackground = true, device = Devices.PIXEL_7)
@Composable
fun ProfileScreenDarkPreview() {
    SAKUTheme(darkTheme = true) {
        ProfileScreen()
    }
}
