package com.saku.app

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.saku.app.data.localization.LocalAppStrings
import com.saku.app.data.localization.getStrings
import com.saku.app.data.preference.AppPreferences
import com.saku.app.data.preference.ThemeMode
import com.saku.app.ui.theme.LocalThemeMode
import com.saku.app.ui.theme.SAKUTheme

class MainActivity : FragmentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val appPreferences = AppPreferences.getInstance(this)

    enableEdgeToEdge()
    setContent {
      val themeMode by appPreferences.themeMode.collectAsState()
      val appLanguage by appPreferences.appLanguage.collectAsState()
      val strings = getStrings(appLanguage)
      val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
      }

      LaunchedEffect(darkTheme) {
        enableEdgeToEdge(
          statusBarStyle = if (darkTheme) {
            SystemBarStyle.dark(AndroidColor.TRANSPARENT)
          } else {
            SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
          },
          navigationBarStyle = if (darkTheme) {
            SystemBarStyle.dark(AndroidColor.TRANSPARENT)
          } else {
            SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
          }
        )
      }

      CompositionLocalProvider(
        LocalThemeMode provides themeMode,
        LocalAppStrings provides strings
      ) {
        SAKUTheme(themeMode = themeMode, darkTheme = darkTheme) {
          Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
          ) {
            MainNavigation()
          }
        }
      }
    }
  }
}
