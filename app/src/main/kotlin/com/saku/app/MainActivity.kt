package com.saku.app

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saku.app.data.localization.AppStrings
import com.saku.app.data.localization.LocalAppStrings
import com.saku.app.data.localization.getStrings
import com.saku.app.data.preference.AppPreferences
import com.saku.app.data.preference.ThemeMode
import com.saku.app.security.BiometricAvailability
import com.saku.app.security.BiometricHelper
import com.saku.app.ui.theme.LocalSakuColors
import com.saku.app.ui.theme.LocalThemeMode
import com.saku.app.ui.theme.SAKUTheme
import com.saku.app.ui.theme.SakuIcons

class MainActivity : FragmentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val appPreferences = AppPreferences.getInstance(this)

    enableEdgeToEdge()
    setContent {
      val themeMode by appPreferences.themeMode.collectAsState()
      val appLanguage by appPreferences.appLanguage.collectAsState()
      val isBiometricEnabled by appPreferences.isBiometricEnabled.collectAsState()
      val isLoggedIn by appPreferences.isLoggedIn.collectAsState()
      val strings = getStrings(appLanguage)
      val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
      }

      val biometricAvailable = remember {
        BiometricHelper.checkAvailability(this@MainActivity) == BiometricAvailability.AVAILABLE
      }
      var isBiometricUnlocked by rememberSaveable { mutableStateOf(false) }
      var biometricError by remember { mutableStateOf<String?>(null) }

      val shouldRequireBiometric = isLoggedIn && isBiometricEnabled && biometricAvailable && !isBiometricUnlocked

      fun triggerBiometricUnlock() {
        biometricError = null
        BiometricHelper.showBiometricPrompt(
          activity = this@MainActivity,
          title = strings.biometricPromptTitle,
          subtitle = strings.biometricPromptSubtitle,
          negativeButtonText = strings.biometricPromptNegative,
          onSuccess = {
            biometricError = null
            isBiometricUnlocked = true
          },
          onError = { err ->
            biometricError = err
          }
        )
      }

      LaunchedEffect(shouldRequireBiometric) {
        if (shouldRequireBiometric) {
          triggerBiometricUnlock()
        }
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
            if (shouldRequireBiometric) {
              BiometricLockScreen(
                strings = strings,
                errorMessage = biometricError,
                onUnlockClick = { triggerBiometricUnlock() }
              )
            } else {
              MainNavigation()
            }
          }
        }
      }
    }
  }
}

@Composable
private fun BiometricLockScreen(
  strings: AppStrings,
  errorMessage: String?,
  onUnlockClick: () -> Unit
) {
  val sakuColors = LocalSakuColors.current

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(sakuColors.background)
      .padding(horizontal = 28.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .size(84.dp)
        .clip(CircleShape)
        .background(sakuColors.chipActiveBg),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        painter = painterResource(id = SakuIcons.Fingerprint),
        contentDescription = strings.biometricTitle,
        tint = sakuColors.primary,
        modifier = Modifier.size(42.dp)
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
      text = "SAKU",
      fontSize = 24.sp,
      fontWeight = FontWeight.Bold,
      color = sakuColors.textDark
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = strings.biometricPromptSubtitle,
      fontSize = 14.sp,
      color = sakuColors.textSecondary,
      textAlign = TextAlign.Center
    )

    if (!errorMessage.isNullOrBlank()) {
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = errorMessage,
        fontSize = 12.sp,
        color = sakuColors.expenseRed,
        textAlign = TextAlign.Center
      )
    }

    Spacer(modifier = Modifier.height(28.dp))

    Button(
      onClick = onUnlockClick,
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp),
      shape = RoundedCornerShape(14.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = sakuColors.primary,
        contentColor = Color.White
      )
    ) {
      Icon(
        painter = painterResource(id = SakuIcons.Fingerprint),
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = strings.signInWithFingerprintBtn,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}
