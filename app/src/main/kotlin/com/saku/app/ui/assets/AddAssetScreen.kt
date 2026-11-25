package com.saku.app.ui.assets

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
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saku.app.data.AssetClass
import com.saku.app.data.DefaultDataRepository
import com.saku.app.data.localization.EnglishStrings
import com.saku.app.data.localization.LocalAppStrings
import com.saku.app.ui.main.formatRupiah
import com.saku.app.ui.theme.SAKUTheme
import com.saku.app.ui.theme.SakuEmerald
import com.saku.app.ui.theme.LocalSakuColors
import androidx.compose.ui.res.painterResource
import com.saku.app.ui.theme.SakuIcons

@Composable
fun AddAssetScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val sakuColors = LocalSakuColors.current
    val strings = LocalAppStrings.current
    val isEn = strings == EnglishStrings

    var assetName by remember(isEn) { mutableStateOf(if (isEn) "Mandiri Payroll Bank Savings" else "Tabungan Bank Mandiri Payroll") }
    var assetAmount by remember { mutableLongStateOf(5000000L) }
    var accountNumber by remember { mutableStateOf("137-00-xxxx-889 • PT Bank Mandiri") }
    var selectedClass by remember { mutableStateOf(AssetClass.CASH) }
    var selectedYieldIndex by remember { mutableStateOf(0) } // 0% (Kas)
    var isLiquid by remember { mutableStateOf(true) }

    val yieldChips = listOf(
        if (isEn) "0% (Cash)" else "0% (Kas)",
        if (isEn) "4-6% (Money Market)" else "4-6% (Pasar Uang)",
        if (isEn) "8-12% (Equity)" else "8-12% (Saham)"
    )

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
                        text = strings.addAssetTitle,
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

            // 2. Card Hero Saldo (Frame 1888)
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
                            text = if (isEn) "Current Asset Balance / Valuation" else "Nilai Saldo / Valuasi Aset Saat Ini",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = sakuColors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = formatRupiah(assetAmount),
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

                        // Quick Chips: +500rb, +1 Juta, +5 Juta, +10 Jt
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val chips = listOf(
                                Pair(if (isEn) "+500k" else "+500rb", 500000L),
                                Pair(if (isEn) "+1M" else "+1 Juta", 1000000L),
                                Pair(if (isEn) "+5M" else "+5 Juta", 5000000L),
                                Pair(if (isEn) "+10M" else "+10 Jt", 10000000L)
                            )
                            chips.forEach { (label, value) ->
                                val isSelected = value == 5000000L
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) sakuColors.pillBg else sakuColors.surfaceVariant)
                                        .border(
                                            width = if (isSelected) 1.dp else 0.dp,
                                            color = if (isSelected) SakuEmerald else Color.Transparent,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable { assetAmount += value }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) sakuColors.pillText else sakuColors.textDark
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
                        // A. Nama Portofolio Aset
                        Column {
                            Text(
                                text = strings.assetNameLabel,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = sakuColors.textDark
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = assetName,
                                onValueChange = { assetName = it },
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

                        // B. Kategori & Kelas Aset (2x2 Grid)
                        Column {
                            Text(
                                text = strings.assetClassLabel,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = sakuColors.textDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            val assetClasses = listOf(
                                Triple(AssetClass.CASH, if (isEn) "Cash & Bank" else "Kas & Bank", Icons.Outlined.AccountBalance),
                                Triple(AssetClass.EMERGENCY, if (isEn) "Liquid Emergency" else "Darurat Likuid", Icons.Outlined.Shield),
                                Triple(AssetClass.INVESTMENT, if (isEn) "Investment / Stocks" else "Investasi / Saham", Icons.AutoMirrored.Outlined.TrendingUp),
                                Triple(AssetClass.CASH_RESERVE, if (isEn) "Cash & Gold" else "Tunai & Emas", Icons.Outlined.Payments)
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    assetClasses.take(2).forEach { (ac, label, icon) ->
                                        AssetClassCard(
                                            label = label,
                                            icon = icon,
                                            isSelected = selectedClass == ac,
                                            onClick = { selectedClass = ac },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    assetClasses.drop(2).forEach { (ac, label, icon) ->
                                        AssetClassCard(
                                            label = label,
                                            icon = icon,
                                            isSelected = selectedClass == ac,
                                            onClick = { selectedClass = ac },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }

                        // C. Nomor Rekening / Institusi (Opsional)
                        Column {
                            Text(
                                text = if (isEn) "Account Number / Institution (Optional)" else "Nomor Rekening / Institusi (Opsional)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = sakuColors.textDark
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = accountNumber,
                                onValueChange = { accountNumber = it },
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

                        // D. Ekspektasi Imbal Hasil Tahunan (p.a)
                        Column {
                            Text(
                                text = if (isEn) "Expected Annual Yield (p.a)" else "Ekspektasi Imbal Hasil Tahunan (p.a)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = sakuColors.textDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                yieldChips.forEachIndexed { index, label ->
                                    val isSelected = selectedYieldIndex == index
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
                                            .clickable { selectedYieldIndex = index }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) (if (sakuColors.isDark) Color(0xFF0F241C) else Color.White) else sakuColors.textSecondary
                                        )
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

                        // E. Status Likuiditas Aset
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isEn) "Asset Liquidity Status" else "Status Likuiditas Aset",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = sakuColors.textDark
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isEn) "Funds can be withdrawn / used at any time" else "Dana dapat ditarik/digunakan sewaktu-waktu",
                                    fontSize = 11.sp,
                                    color = sakuColors.textSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            // Badge Likuid
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(13.dp))
                                    .background(if (sakuColors.isDark) Color(0xFF133E2B) else Color(0xFFECFDF5))
                                    .border(1.dp, SakuEmerald, RoundedCornerShape(13.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = if (isEn) "●  Liquid" else "●  Likuid",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SakuEmerald
                                )
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
                                DefaultDataRepository.getInstance().addAsset(
                                    name = assetName,
                                    institution = accountNumber,
                                    amount = assetAmount,
                                    assetClass = selectedClass,
                                    yieldText = yieldChips.getOrElse(selectedYieldIndex) { "+5.0%" }
                                )
                                onNavigateBack()
                            }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${strings.assetSaveBtn}  ✓",
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
                            text = if (isEn) "Back to Asset Portfolio" else "Kembali ke Portofolio Aset",
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

@Composable
fun AssetClassCard(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sakuColors = LocalSakuColors.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) sakuColors.pillBg else sakuColors.surfaceVariant)
            .border(
                1.dp,
                if (isSelected) sakuColors.title else sakuColors.border,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) sakuColors.title else sakuColors.textSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) sakuColors.title else sakuColors.textSecondary
        )
    }
}

@Preview(name = "Tambah Aset Screen", showBackground = true, device = Devices.PIXEL_7)
@Composable
fun AddAssetScreenPreview() {
    SAKUTheme {
        AddAssetScreen()
    }
}
