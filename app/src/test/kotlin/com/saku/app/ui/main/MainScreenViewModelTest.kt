package com.saku.app.ui.main

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.saku.app.data.AssetClass
import com.saku.app.data.AssetsOverviewData
import com.saku.app.data.CategoryType
import com.saku.app.data.DashboardSummary
import com.saku.app.data.DataRepository
import com.saku.app.data.PocketCategory
import com.saku.app.data.PocketsOverviewData
import com.saku.app.data.TransactionItem
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MainScreenViewModelTest {
  @Test
  fun uiState_initiallyLoading() = runTest {
    val viewModel = MainScreenViewModel(FakeDataRepository())
    assertEquals(viewModel.uiState.first(), MainScreenUiState.Loading)
  }
}

private class FakeDataRepository : DataRepository {
  override val dashboardData: StateFlow<DashboardSummary> = MutableStateFlow(DashboardSummary())
  override val pocketsData: StateFlow<PocketsOverviewData> = MutableStateFlow(PocketsOverviewData())
  override val assetsData: StateFlow<AssetsOverviewData> = MutableStateFlow(AssetsOverviewData())
  override val allTransactions: StateFlow<List<TransactionItem>> = MutableStateFlow(emptyList())

  override fun addTransaction(
    title: String,
    amount: Long,
    isExpense: Boolean,
    categoryType: CategoryType,
    pocketName: String,
    dateText: String,
    notes: String,
    method: String
  ) = Unit

  override fun deleteTransaction(id: String) = Unit

  override fun addPocket(
    name: String,
    category: PocketCategory,
    limit: Long,
    accentColor: Color,
    bgColor: Color,
    categoryType: CategoryType,
    iconRes: Int?
  ) = Unit

  override fun updatePocket(
    id: String,
    name: String,
    category: PocketCategory,
    limit: Long
  ) = Unit

  override fun deletePocket(id: String) = Unit

  override fun addAsset(
    name: String,
    institution: String,
    amount: Long,
    assetClass: AssetClass,
    yieldText: String
  ) = Unit

  override fun updateUserName(name: String) = Unit

  override fun exportTransactionsCsv(
    context: Context,
    transactions: List<TransactionItem>?,
    periodName: String
  ): Uri? = null
}
