package com.saku.app.data.preference

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode(val code: String, val displayNameId: String, val displayNameEn: String) {
    SYSTEM("system", "Sistem", "System"),
    LIGHT("light", "Terang", "Light"),
    DARK("dark", "Gelap", "Dark");

    fun getDisplayName(language: AppLanguage): String {
        return if (language == AppLanguage.INDONESIAN) displayNameId else displayNameEn
    }

    companion object {
        fun fromCode(code: String?): ThemeMode =
            entries.find { it.code.equals(code, ignoreCase = true) } ?: SYSTEM
    }
}

enum class AppLanguage(val code: String, val displayName: String) {
    INDONESIAN("id", "Indonesia"),
    ENGLISH("en", "English");

    companion object {
        fun fromCode(code: String?): AppLanguage =
            entries.find { it.code.equals(code, ignoreCase = true) } ?: INDONESIAN
    }
}

class AppPreferences private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(
        ThemeMode.fromCode(prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.code))
    )
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _appLanguage = MutableStateFlow(
        AppLanguage.fromCode(prefs.getString(KEY_APP_LANGUAGE, AppLanguage.INDONESIAN.code))
    )
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _isBiometricEnabled = MutableStateFlow(
        prefs.getBoolean(KEY_BIOMETRIC_ENABLED, true)
    )
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()

    private val _isNotificationEnabled = MutableStateFlow(
        prefs.getBoolean(KEY_NOTIFICATION_ENABLED, true)
    )
    val isNotificationEnabled: StateFlow<Boolean> = _isNotificationEnabled.asStateFlow()

    private val _userName = MutableStateFlow(
        prefs.getString(KEY_USER_NAME, "Damar Pratama") ?: "Damar Pratama"
    )
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow(
        prefs.getString(KEY_USER_EMAIL, "damar.pratama@email.com") ?: "damar.pratama@email.com"
    )
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _geminiApiKey = MutableStateFlow(
        prefs.getString(KEY_GEMINI_API_KEY, "AIzaSyD-a89XkL9102mNqZw53x9Q") ?: "AIzaSyD-a89XkL9102mNqZw53x9Q"
    )
    val geminiApiKey: StateFlow<String> = _geminiApiKey.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(
        prefs.getBoolean(KEY_IS_LOGGED_IN, true)
    )
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _profileImagePath = MutableStateFlow<String?>(
        prefs.getString(KEY_PROFILE_IMAGE_PATH, null)
    )
    val profileImagePath: StateFlow<String?> = _profileImagePath.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString(KEY_THEME_MODE, mode.code).apply()
    }

    fun setAppLanguage(language: AppLanguage) {
        _appLanguage.value = language
        prefs.edit().putString(KEY_APP_LANGUAGE, language.code).apply()
    }

    fun setBiometricEnabled(enabled: Boolean) {
        _isBiometricEnabled.value = enabled
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    fun setNotificationEnabled(enabled: Boolean) {
        _isNotificationEnabled.value = enabled
        prefs.edit().putBoolean(KEY_NOTIFICATION_ENABLED, enabled).apply()
    }

    fun setUserProfile(name: String, email: String, apiKey: String) {
        _userName.value = name
        _userEmail.value = email
        _geminiApiKey.value = apiKey
        prefs.edit()
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_GEMINI_API_KEY, apiKey)
            .apply()
    }

    fun setLoggedIn(loggedIn: Boolean) {
        _isLoggedIn.value = loggedIn
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, loggedIn).apply()
    }

    fun setProfileImagePath(path: String?) {
        _profileImagePath.value = path
        prefs.edit().putString(KEY_PROFILE_IMAGE_PATH, path).apply()
    }

    companion object {
        private const val PREFS_NAME = "saku_user_preferences"
        private const val KEY_THEME_MODE = "key_theme_mode"
        private const val KEY_APP_LANGUAGE = "key_app_language"
        private const val KEY_BIOMETRIC_ENABLED = "key_biometric_enabled"
        private const val KEY_NOTIFICATION_ENABLED = "key_notification_enabled"
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_USER_EMAIL = "key_user_email"
        private const val KEY_GEMINI_API_KEY = "key_gemini_api_key"
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
        private const val KEY_PROFILE_IMAGE_PATH = "key_profile_image_path"

        @Volatile
        private var INSTANCE: AppPreferences? = null

        fun getInstance(context: Context): AppPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppPreferences(context).also { INSTANCE = it }
            }
        }
    }
}
