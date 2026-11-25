package com.saku.app.ui.pockets

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saku.app.data.DefaultDataRepository
import com.saku.app.data.PocketCategory
import com.saku.app.data.localization.EnglishStrings
import com.saku.app.data.localization.LocalAppStrings
import com.saku.app.data.localization.localizePocketCategory
import com.saku.app.ui.main.formatRupiah
import androidx.compose.ui.res.painterResource
import com.saku.app.ui.theme.SAKU_POCKET_ICON_OPTIONS
import com.saku.app.ui.theme.SAKUTheme
import com.saku.app.ui.theme.SakuAmber
import com.saku.app.ui.theme.SakuIcons
import com.saku.app.ui.theme.SakuAmberBg
import com.saku.app.ui.theme.SakuEmerald
import com.saku.app.ui.theme.LocalSakuColors

@Composable
fun CreatePocketScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val sakuColors = LocalSakuColors.current
    val strings = LocalAppStrings.current
    val isEn = strings == EnglishStrings

    var pocketName by remember(isEn) { mutableStateOf(if (isEn) "Digital Subscriptions & Fun" else "Langganan & Hiburan Digital") }
    var limitAmount by remember { mutableLongStateOf(1000000L) }
    var selectedCategory by remember { mutableStateOf(PocketCategory.KEINGINAN) }
    var selectedColorIndex by remember { mutableStateOf(3) } // Purple by default
    var selectedIconIndex by remember { mutableStateOf(0) } // Sparkle
    var isAlertEnabled by remember { mutableStateOf(true) }
    var resetCycle by remember(isEn) { mutableStateOf(if (isEn) "1st of Every Month" else "Setiap Tanggal 1 Awal Bulan") }

    val colors = listOf(
        Color(0xFFD97706), // Amber
        Color(0xFF059669), // Green
        Color(0xFF2563EB), // Blue
        Color(0xFF7C3AED), // Purple
        Color(0xFFEF4444), // Red
        Color(0xFF114232)  // Dark Green
    )

    val iconOptions = SAKU_POCKET_ICON_OPTIONS

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = sakuColors.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Header Row: Back + Title + Batal
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
                            .size(40.dp)
                            .shadow(2.dp, CircleShape, spotColor = sakuColors.cardShadow)
                            .background(sakuColors.surface, CircleShape)
                            .border(1.dp, sakuColors.border, CircleShape)
                            .clickable(onClick = onNavigateBack),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = SakuIcons.Back),
                            contentDescription = strings.back,
                            tint = sakuColors.title,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = strings.createPocketTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = sakuColors.title
                    )

                    Text(
                        text = strings.cancel,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = sakuColors.textSecondary,
                        modifier = Modifier
                            .clickable(onClick = onNavigateBack)
                            .padding(4.dp)
                    )
                }
            }

            // 2. Card Hero Limit (Frame 1823)
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
                        Text(
                            text = if (isEn) "Monthly Limit Allocation" else "Alokasi Limit Bulanan",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = sakuColors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = formatRupiah(limitAmount),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = sakuColors.textDark
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            // Blinking cursor
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(26.dp)
                                    .background(SakuEmerald, RoundedCornerShape(1.dp))
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Chips: +100rb, +250rb, +500rb, +1 Juta
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val chips = listOf(
                                Pair(if (isEn) "+100k" else "+100rb", 100000L),
                                Pair(if (isEn) "+250k" else "+250rb", 250000L),
                                Pair(if (isEn) "+500k" else "+500rb", 500000L),
                                Pair(if (isEn) "+1M" else "+1 Juta", 1000000L)
                            )
                            chips.forEach { (label, value) ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (value == 1000000L) sakuColors.pillBg else sakuColors.surfaceVariant)
                                        .border(
                                            width = if (value == 1000000L) 1.dp else 0.dp,
                                            color = if (value == 1000000L) SakuEmerald else Color.Transparent,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable { limitAmount += value }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (value == 1000000L) sakuColors.pillText else sakuColors.textDark
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Card Form Details
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(20.dp), spotColor = sakuColors.cardShadow)
                        .background(sakuColors.surface, RoundedCornerShape(20.dp))
                        .border(1.dp, sakuColors.border, RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // A. Nama Kantong
                        Column {
                            Text(
                                text = strings.pocketNameLabel,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = sakuColors.textDark
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = pocketName,
                                onValueChange = { pocketName = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = sakuColors.surfaceVariant,
                                    unfocusedContainerColor = sakuColors.surfaceVariant,
                                    focusedBorderColor = sakuColors.title,
                                    unfocusedBorderColor = sakuColors.border,
                                    focusedTextColor = sakuColors.textDark,
                                    unfocusedTextColor = sakuColors.textDark
                                ),
                                singleLine = true
                            )
                        }

                        // B. Kategori Pos Anggaran
                        Column {
                            Text(
                                text = strings.pocketCategoryLabel,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = sakuColors.textDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))
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
                                            .clip(RoundedCornerShape(15.dp))
                                            .background(if (isSelected) sakuColors.title else sakuColors.surfaceVariant)
                                            .border(
                                                1.dp,
                                                if (isSelected) sakuColors.title else sakuColors.border,
                                                RoundedCornerShape(15.dp)
                                            )
                                            .clickable { selectedCategory = cat }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = strings.localizePocketCategory(cat),
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) (if (sakuColors.isDark) Color(0xFF0F241C) else Color.White) else sakuColors.textSecondary
                                        )
                                    }
                                }
                            }
                        }

                        // C. Warna & Ikon Identitas
                        Column {
                            Text(
                                text = strings.pocketIconColorLabel,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = sakuColors.textDark
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            // Color Dots Palette
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                colors.forEachIndexed { index, color ->
                                    val isSelected = selectedColorIndex == index
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .border(
                                                width = if (isSelected) 3.dp else 1.5.dp,
                                                color = if (isSelected) sakuColors.title else Color.White,
                                                shape = CircleShape
                                            )
                                            .clickable { selectedColorIndex = index }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Icon Boxes Selector
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    iconOptions.take(4).forEachIndexed { index, option ->
                                        val isSelected = selectedIconIndex == index
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(46.dp)
                                                .shadow(1.dp, RoundedCornerShape(12.dp), spotColor = sakuColors.cardShadow)
                                                .background(
                                                    if (isSelected) sakuColors.pillBg else sakuColors.surfaceVariant,
                                                    RoundedCornerShape(12.dp)
                                                )
                                                .border(
                                                    width = if (isSelected) 1.5.dp else 1.dp,
                                                    color = if (isSelected) sakuColors.title else sakuColors.border,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .clickable { selectedIconIndex = index },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(id = option.iconRes),
                                                contentDescription = option.label,
                                                tint = if (isSelected) sakuColors.pillText else sakuColors.textSecondary,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    iconOptions.drop(4).forEachIndexed { i, option ->
                                        val index = i + 4
                                        val isSelected = selectedIconIndex == index
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(46.dp)
                                                .shadow(1.dp, RoundedCornerShape(12.dp), spotColor = sakuColors.cardShadow)
                                                .background(
                                                    if (isSelected) sakuColors.pillBg else sakuColors.surfaceVariant,
                                                    RoundedCornerShape(12.dp)
                                                )
                                                .border(
                                                    width = if (isSelected) 1.5.dp else 1.dp,
                                                    color = if (isSelected) sakuColors.title else sakuColors.border,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .clickable { selectedIconIndex = index },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(id = option.iconRes),
                                                contentDescription = option.label,
                                                tint = if (isSelected) sakuColors.pillText else sakuColors.textSecondary,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Divider
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(sakuColors.divider)
                        )

                        // D. Peringatan Batas Anggaran
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isEn) "Budget Limit Warning" else "Peringatan Batas Anggaran",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = sakuColors.textDark
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isEn) "Send warning alert when pocket quota reaches ≥ 80%" else "Beri sinyal peringatan waspada saat kuota kantong terpakai ≥ 80%",
                                    fontSize = 11.sp,
                                    color = sakuColors.textSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Switch(
                                checked = isAlertEnabled,
                                onCheckedChange = { isAlertEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = sakuColors.title,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = sakuColors.border
                                )
                            )
                        }

                        // E. Siklus Reset Anggaran
                        Column {
                            Text(
                                text = strings.pocketResetCycleLabel,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = sakuColors.textDark
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(sakuColors.surfaceVariant, RoundedCornerShape(12.dp))
                                    .border(1.dp, sakuColors.border, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "📅  $resetCycle",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = sakuColors.textDark
                                    )
                                    Text(
                                        text = "▾",
                                        fontSize = 14.sp,
                                        color = sakuColors.textSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. CTA Buttons
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Primary Submit Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = sakuColors.cardShadow)
                            .clip(RoundedCornerShape(16.dp))
                            .background(sakuColors.title)
                            .clickable {
                                val chosenColor = colors.getOrElse(selectedColorIndex) { SakuAmber }
                                val chosenIcon = iconOptions.getOrElse(selectedIconIndex) { iconOptions.first() }
                                DefaultDataRepository.getInstance().addPocket(
                                    name = pocketName.trim().ifEmpty { if (isEn) "New Pocket" else "Kantong Baru" },
                                    category = selectedCategory,
                                    limit = limitAmount.coerceAtLeast(0L),
                                    accentColor = chosenColor,
                                    bgColor = chosenColor.copy(alpha = 0.14f),
                                    iconRes = chosenIcon.iconRes
                                )
                                onNavigateBack()
                            }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${strings.pocketSaveBtn}  ✓",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (sakuColors.isDark) Color(0xFF0F241C) else Color.White
                        )
                    }

                    // Secondary Cancel Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(sakuColors.surface)
                            .border(1.dp, sakuColors.border, RoundedCornerShape(14.dp))
                            .clickable(onClick = onNavigateBack)
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isEn) "Back to Pockets List" else "Kembali ke Daftar Kantong",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = sakuColors.textSecondary
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Preview(name = "Buat Kantong Screen", showBackground = true, device = Devices.PIXEL_7)
@Composable
fun CreatePocketScreenPreview() {
    SAKUTheme {
        CreatePocketScreen()
    }
}
