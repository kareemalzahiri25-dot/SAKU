package com.saku.app.ui.transaction

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
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.saku.app.TransactionDetail
import com.saku.app.data.DefaultDataRepository
import androidx.compose.ui.res.painterResource
import com.saku.app.ui.theme.SakuIcons
import com.saku.app.data.localization.EnglishStrings
import com.saku.app.data.localization.LocalAppStrings
import com.saku.app.data.localization.localizePocketName
import com.saku.app.data.localization.localizeTransactionTitle
import com.saku.app.ui.main.formatRupiah
import com.saku.app.ui.theme.SAKUTheme
import com.saku.app.ui.theme.SakuAmber
import com.saku.app.ui.theme.SakuAmberBg
import com.saku.app.ui.theme.SakuEmerald
import com.saku.app.ui.theme.SakuExpenseRed
import com.saku.app.ui.theme.sakuColors
import kotlinx.coroutines.launch

@Composable
fun TransactionDetailScreen(
    detail: TransactionDetail = TransactionDetail(),
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val strings = LocalAppStrings.current
    val isEn = strings == EnglishStrings

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
            // 1. Top Bar: Back + Title + Share
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
                        text = strings.transactionDetailTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = sakuColors.title
                    )

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(2.dp, CircleShape, spotColor = sakuColors.cardShadow)
                            .background(sakuColors.surface, CircleShape)
                            .border(1.dp, sakuColors.border, CircleShape)
                            .clickable {
                                try {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "SAKU ${strings.transactionDetailTitle}:\n${strings.localizeTransactionTitle(detail.title)} (${detail.category})\n${strings.transactionAmountLabel}: ${formatRupiah(detail.amount)}\n${strings.detailSourcePocket}: ${strings.localizePocketName(detail.pocket)}\n${strings.detailTime}: ${detail.date}\n${strings.detailMethod}: ${detail.method}\n${strings.detailNotes}: ${detail.notes}"
                                        )
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, strings.detailShareBtn))
                                } catch (e: Exception) {
                                    scope.launch { snackbarHostState.showSnackbar(if (isEn) "Failed to share transaction details" else "Gagal membagikan rincian transaksi") }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = SakuIcons.Share),
                            contentDescription = strings.detailShareBtn,
                            tint = sakuColors.title,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 2. Receipt Hero Box (Frame 2008)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(24.dp), spotColor = sakuColors.cardShadow)
                        .background(sakuColors.surface, RoundedCornerShape(24.dp))
                        .border(1.dp, sakuColors.border, RoundedCornerShape(24.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Category Icon
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(SakuAmberBg, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingCart,
                                contentDescription = null,
                                tint = SakuAmber,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = strings.localizeTransactionTitle(detail.title),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = sakuColors.textDark
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (detail.amount < 0) "-${formatRupiah(Math.abs(detail.amount))}" else "+${formatRupiah(detail.amount)}",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (detail.amount < 0) SakuExpenseRed else SakuEmerald
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Status Badge
                        Box(
                            modifier = Modifier
                                .background(if (sakuColors.isDark) Color(0xFF133E2B) else Color(0xFFECFDF5), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = SakuIcons.Check),
                                    contentDescription = null,
                                    tint = SakuEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = strings.success,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SakuEmerald
                                )
                            }
                        }
                    }
                }
            }

            // 3. Metadata Key-Value Details Card (Frame 2008)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(20.dp), spotColor = sakuColors.cardShadow)
                        .background(sakuColors.surface, RoundedCornerShape(20.dp))
                        .border(1.dp, sakuColors.border, RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        DetailRow(
                            if (isEn) "Transaction Type" else "Jenis Transaksi",
                            if (detail.amount < 0) (if (isEn) "Expense (Debit)" else "Pengeluaran (Debit)") else (if (isEn) "Income (Credit)" else "Pemasukan (Kredit)"),
                            isHighlightRed = detail.amount < 0
                        )
                        DetailDivider()
                        DetailRow(strings.detailCategory, detail.category)
                        DetailDivider()
                        DetailRow(strings.detailSourcePocket, strings.localizePocketName(detail.pocket))
                        DetailDivider()
                        DetailRow(strings.detailTime, detail.date)
                        DetailDivider()
                        DetailRow(strings.detailMethod, detail.method, isHighlightGreen = true)
                        DetailDivider()
                        DetailRow(strings.detailNotes, detail.notes)
                    }
                }
            }

            // 4. Struk Belanja Terlampir Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = sakuColors.cardShadow)
                        .background(sakuColors.surface, RoundedCornerShape(16.dp))
                        .border(1.dp, sakuColors.border, RoundedCornerShape(16.dp))
                        .clickable {
                            scope.launch { snackbarHostState.showSnackbar(if (isEn) "Opening original receipt photo preview..." else "Membuka pratinjau foto struk asli...") }
                        }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(sakuColors.pillBg, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Description,
                                    contentDescription = null,
                                    tint = sakuColors.pillText,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isEn) "Attached Purchase Receipt" else "Struk Belanja Terlampir",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = sakuColors.textDark
                                )
                                Text(
                                    text = if (isEn) "Shopping items recorded in SAKU" else "Item belanja tercatat di SAKU",
                                    fontSize = 11.sp,
                                    color = sakuColors.textSecondary
                                )
                            }
                        }

                        Text(
                            text = if (isEn) "View ›" else "Lihat ›",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SakuEmerald
                        )
                    }
                }
            }

            // 5. Action Buttons (Edit Transaksi & Hapus Transaksi)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Edit Transaksi
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = sakuColors.cardShadow)
                            .clip(RoundedCornerShape(16.dp))
                            .background(sakuColors.title)
                            .clickable {
                                scope.launch { snackbarHostState.showSnackbar(if (isEn) "Edit mode ready: customize transaction notes." else "Mode edit siap: silakan sesuaikan catatan transaksi.") }
                            }
                            .padding(vertical = 15.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = SakuIcons.Edit),
                                contentDescription = null,
                                tint = if (sakuColors.isDark) Color(0xFF0F241C) else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isEn) "Edit Transaction" else "Edit Transaksi",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (sakuColors.isDark) Color(0xFF0F241C) else Color.White
                            )
                        }
                    }

                    // Hapus Transaksi
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(sakuColors.logoutBg)
                            .border(1.dp, sakuColors.logoutBorder, RoundedCornerShape(16.dp))
                            .clickable {
                                DefaultDataRepository.getInstance().deleteTransaction(detail.id)
                                onNavigateBack()
                            }
                            .padding(vertical = 15.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = SakuIcons.Delete),
                                contentDescription = null,
                                tint = sakuColors.logoutText,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.detailDeleteBtn,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = sakuColors.logoutText
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

@Composable
fun DetailRow(
    label: String,
    value: String,
    isHighlightRed: Boolean = false,
    isHighlightGreen: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = sakuColors.textSecondary
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = when {
                isHighlightRed -> SakuExpenseRed
                isHighlightGreen -> SakuEmerald
                else -> sakuColors.textDark
            }
        )
    }
}

@Composable
fun DetailDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(sakuColors.divider)
    )
}

@Preview(name = "Transaction Detail", showBackground = true, device = Devices.PIXEL_7)
@Composable
fun TransactionDetailPreview() {
    SAKUTheme {
        TransactionDetailScreen()
    }
}
