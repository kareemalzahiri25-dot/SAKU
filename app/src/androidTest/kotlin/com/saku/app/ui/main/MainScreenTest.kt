package com.saku.app.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.saku.app.data.DashboardSummary
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** UI tests for [com.saku.app.ui.main.MainScreen]. */
class MainScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    composeTestRule.setContent { DashboardContent(data = DashboardSummary()) }
  }

  @Test
  fun greeting_exists() {
    composeTestRule.onNodeWithText("Halo,").assertExists()
    composeTestRule.onNodeWithText("Damar! 👋").assertExists()
  }

  @Test
  fun balance_exists() {
    composeTestRule.onNodeWithText("Total Saldo Anda").assertExists()
  }
}
