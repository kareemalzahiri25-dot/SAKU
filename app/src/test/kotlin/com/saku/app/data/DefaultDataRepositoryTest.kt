package com.saku.app.data

import androidx.compose.ui.graphics.Color
import com.saku.app.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class DefaultDataRepositoryTest {

    @Test
    fun buildAssetsOverview_computesDynamicSharesSummingTo100() {
        val repo = DefaultDataRepository()
        val initial = repo.assetsData.value
        assertEquals(28_500_000L, initial.totalNetWorth)
        assertEquals(35, initial.kasShare)
        assertEquals(30, initial.daruratShare)
        assertEquals(25, initial.investShare)
        assertEquals(10, initial.tunaiShare)
        assertEquals(100, initial.assets.sumOf { it.sharePercent })

        // Adding another 10M to CASH increases net worth and updates shares dynamically
        repo.addAsset(
            name = "Mandiri Payroll",
            institution = "Bank Mandiri",
            amount = 11_500_000L,
            assetClass = AssetClass.CASH,
            yieldText = "0% (Kas)"
        )

        val updated = repo.assetsData.value
        assertEquals(40_000_000L, updated.totalNetWorth)
        assertEquals(
            100,
            updated.kasShare + updated.daruratShare + updated.investShare + updated.tunaiShare
        )
        assertEquals(54, updated.kasShare) // (10M + 11.5M) / 40M = 53.75% -> 54%
        assertEquals(100, updated.assets.sumOf { it.sharePercent })
    }

    @Test
    fun addUpdateAndDeletePocket_updatesOverviewAndPreservesCustomIcon() {
        val repo = DefaultDataRepository()
        val initialBudget = repo.pocketsData.value.totalBudget

        repo.addPocket(
            name = "Langganan Digital",
            category = PocketCategory.KEINGINAN,
            limit = 500_000L,
            accentColor = Color(0xFF7C3AED),
            bgColor = Color(0xFFF5F3FF),
            iconRes = R.drawable.ic_saku_cat_bills
        )

        val added = repo.pocketsData.value.pockets.find { it.name == "Langganan Digital" }
        assertNotNull(added)
        assertEquals(R.drawable.ic_saku_cat_bills, added!!.iconRes)
        assertEquals(500_000L, added.remaining)
        assertEquals("Aman (0%)", added.statusText)
        assertEquals(initialBudget + 500_000L, repo.pocketsData.value.totalBudget)

        repo.updatePocket(
            id = added.id,
            name = "Streaming & Cloud",
            category = PocketCategory.TABUNGAN,
            limit = 750_000L
        )

        val edited = repo.pocketsData.value.pockets.find { it.id == added.id }
        assertNotNull(edited)
        assertEquals("Streaming & Cloud", edited!!.name)
        assertEquals(PocketCategory.TABUNGAN, edited.category)
        assertEquals(750_000L, edited.limit)
        assertEquals(initialBudget + 750_000L, repo.pocketsData.value.totalBudget)

        repo.deletePocket(added.id)
        assertNull(repo.pocketsData.value.pockets.find { it.id == added.id })
        assertEquals(initialBudget, repo.pocketsData.value.totalBudget)
    }

    @Test
    fun addAndDeleteTransaction_onlyAdjustsTargetPocketByNameFirst() {
        val repo = DefaultDataRepository()
        val initialFoodSpent = repo.pocketsData.value.pockets.first { it.name == "Kebutuhan Pokok" }.spent
        val initialTransportSpent = repo.pocketsData.value.pockets.first { it.name == "Transportasi" }.spent
        val initialEmergencySpent = repo.pocketsData.value.pockets.first { it.name == "Dana Darurat" }.spent

        // Simulate OCR scan (categoryType = FOOD) assigned specifically to "Transportasi"
        repo.addTransaction(
            title = "Tol & Parkir",
            amount = 100_000L,
            isExpense = true,
            categoryType = CategoryType.FOOD,
            pocketName = "Transportasi"
        )

        val afterAdd = repo.pocketsData.value.pockets
        assertEquals(initialFoodSpent, afterAdd.first { it.name == "Kebutuhan Pokok" }.spent)
        assertEquals(initialTransportSpent + 100_000L, afterAdd.first { it.name == "Transportasi" }.spent)

        // Delete an existing GENERAL transaction in "Kebutuhan Pokok" ("t6" WiFi & Listrik)
        repo.deleteTransaction("t6")
        val afterDelete = repo.pocketsData.value.pockets
        assertEquals(initialFoodSpent - 150_000L, afterDelete.first { it.name == "Kebutuhan Pokok" }.spent)
        // Must NOT touch Dana Darurat even though both t6 and Dana Darurat have CategoryType.GENERAL
        assertEquals(initialEmergencySpent, afterDelete.first { it.name == "Dana Darurat" }.spent)
    }
}
