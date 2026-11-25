package com.saku.app.ui.auth

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.saku.app.R
import com.saku.app.data.DefaultDataRepository
import com.saku.app.data.localization.EnglishStrings
import com.saku.app.data.localization.LocalAppStrings
import com.saku.app.data.preference.AppPreferences
import com.saku.app.security.BiometricAvailability
import com.saku.app.security.BiometricHelper
import com.saku.app.ui.theme.LocalSakuColors
import com.saku.app.ui.theme.SAKUTheme
import com.saku.app.ui.theme.SakuDarkGreen
import com.saku.app.ui.theme.SakuDarkGreenBorder
import com.saku.app.ui.theme.SakuEmerald
import com.saku.app.ui.theme.SakuEmeraldLight
import com.saku.app.ui.theme.SakuIcons
import kotlinx.coroutines.launch

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

private fun isValidEmail(email: String): Boolean = EMAIL_REGEX.matches(email.trim())

/**
 * Centered max-width scrollable container for auth screens with optional vertical centering.
 */
@Composable
private fun AuthScreenContainer(
    innerPadding: PaddingValues,
    horizontalAlignment: Alignment.Horizontal,
    spacing: Dp,
    contentPadding: PaddingValues,
    centerVertically: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .imePadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        val viewportHeight = maxHeight
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .then(if (centerVertically) Modifier.heightIn(min = viewportHeight) else Modifier)
                .padding(contentPadding),
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = Arrangement.spacedBy(
                space = spacing,
                alignment = if (centerVertically) Alignment.CenterVertically else Alignment.Top
            ),
            content = content
        )
    }
}

/**
 * Shared top navigation header with a 48dp touch-target back button and screen title.
 */
@Composable
private fun AuthTopBar(
    title: String,
    backDescription: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sakuColors = LocalSakuColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(sakuColors.surface)
                    .border(1.dp, sakuColors.border, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = SakuIcons.Back),
                    contentDescription = backDescription,
                    tint = sakuColors.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = sakuColors.textDark
        )
    }
}

/**
 * Reusable auth input field with focus indicator, inline validation error, and 48dp password toggle.
 */
@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    helperText: String? = null,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePasswordVisibility: (() -> Unit)? = null,
    showPasswordDescription: String = "",
    hidePasswordDescription: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val sakuColors = LocalSakuColors.current
    var isFocused by remember { mutableStateOf(false) }

    val errorColor = if (sakuColors.isDark) sakuColors.logoutText else sakuColors.expenseRed
    val helperColor = if (sakuColors.isDark) SakuEmeraldLight else SakuDarkGreen
    val borderColor = when {
        errorMessage != null -> errorColor
        isFocused -> sakuColors.primary
        else -> sakuColors.inputBorder
    }
    val borderWidth = if (isFocused || errorMessage != null) 1.5.dp else 1.dp

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = sakuColors.textDark
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(sakuColors.inputBg)
                .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
                .padding(start = 14.dp, end = if (isPassword) 2.dp else 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = 14.sp,
                        color = sakuColors.textMuted
                    )
                }

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    visualTransformation = if (isPassword && !isPasswordVisible) {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    },
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = sakuColors.textDark
                    ),
                    cursorBrush = SolidColor(sakuColors.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocused = it.isFocused }
                )
            }

            if (isPassword && onTogglePasswordVisibility != null) {
                IconButton(
                    onClick = onTogglePasswordVisibility,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isPasswordVisible) SakuIcons.EyeOff else SakuIcons.Eye
                        ),
                        contentDescription = if (isPasswordVisible) {
                            hidePasswordDescription
                        } else {
                            showPasswordDescription
                        },
                        tint = sakuColors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = errorColor,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            )
        } else if (helperText != null) {
            Text(
                text = helperText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = helperColor
            )
        }
    }
}

/**
 * Accessible checkbox row with minimum 48dp tap target and high-contrast check icon.
 */
@Composable
private fun AuthCheckboxRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    errorMessage: String? = null
) {
    val sakuColors = LocalSakuColors.current
    val errorColor = if (sakuColors.isDark) sakuColors.logoutText else sakuColors.expenseRed

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .heightIn(min = 48.dp)
                .clip(RoundedCornerShape(8.dp))
                .toggleable(
                    value = checked,
                    role = Role.Checkbox,
                    onValueChange = onCheckedChange
                )
                .padding(horizontal = 4.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (checked) sakuColors.primary else sakuColors.inputBg)
                    .border(
                        width = 1.dp,
                        color = when {
                            errorMessage != null && !checked -> errorColor
                            checked -> sakuColors.primary
                            else -> sakuColors.inputBorder
                        },
                        shape = RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (checked) {
                    Icon(
                        painter = painterResource(id = SakuIcons.Check),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = label,
                fontSize = 13.sp,
                color = sakuColors.textSecondary,
                lineHeight = 18.sp
            )
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = errorColor,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite }
            )
        }
    }
}

/**
 * Primary CTA button using theme onPrimary for WCAG-compliant contrast in both light and dark modes.
 */
@Composable
private fun AuthPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sakuColors = LocalSakuColors.current

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = sakuColors.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Google Sign-In / Sign-Up button displaying the official 4-color Google vector mark.
 */
@Composable
private fun GoogleSignInButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sakuColors = LocalSakuColors.current

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, sakuColors.border),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = sakuColors.surface,
            contentColor = sakuColors.textDark
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Render via Image so Compose never applies a monochrome tint over the 4 Google brand colors.
            Image(
                painter = painterResource(id = SakuIcons.GoogleLogo),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = sakuColors.textDark
            )
        }
    }
}

@Composable
private fun AuthDivider(
    text: String,
    modifier: Modifier = Modifier
) {
    val sakuColors = LocalSakuColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = sakuColors.border
        )
        Text(
            text = text,
            fontSize = 12.sp,
            color = sakuColors.textSecondary,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = sakuColors.border
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AuthFooterLinkRow(
    prompt: String,
    actionText: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sakuColors = LocalSakuColors.current

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        itemVerticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = prompt,
            fontSize = 13.sp,
            color = sakuColors.textSecondary,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .heightIn(min = 48.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable(role = Role.Button, onClick = onActionClick)
                .padding(horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = actionText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = sakuColors.primary
            )
        }
    }
}

// ==========================================
// 1. LOGIN SCREEN (Frame 1525)
// ==========================================
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val sakuColors = LocalSakuColors.current
    val strings = LocalAppStrings.current
    val focusManager = LocalFocusManager.current

    var email by remember { mutableStateOf("damar.prasetyo@email.com") }
    var password by remember { mutableStateOf("password123") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var rememberMe by remember { mutableStateOf(true) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val preferences = remember { AppPreferences.getInstance(context) }

    fun submitLogin() {
        val trimmedEmail = email.trim()
        val nextEmailError = when {
            trimmedEmail.isEmpty() -> strings.emailRequiredError
            !isValidEmail(trimmedEmail) -> strings.emailInvalidError
            else -> null
        }
        val nextPasswordError = if (password.isBlank()) strings.passwordRequiredError else null

        emailError = nextEmailError
        passwordError = nextPasswordError

        if (nextEmailError == null && nextPasswordError == null) {
            scope.launch {
                preferences.setLoggedIn(true)
                onLoginSuccess()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = sakuColors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        AuthScreenContainer(
            innerPadding = innerPadding,
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 44.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            spacing = 20.dp,
            centerVertically = true
        ) {
            // App Logo & Titles
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(18.dp),
                            ambientColor = SakuDarkGreen,
                            spotColor = SakuDarkGreen
                        )
                        .clip(RoundedCornerShape(18.dp))
                        .background(SakuDarkGreen)
                        .border(1.dp, SakuDarkGreenBorder, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.saku_app_icon),
                        contentDescription = strings.appLogoDesc,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "SAKU",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = sakuColors.textDark,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = strings.loginSubtitle,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = sakuColors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }

            // Login Card Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = sakuColors.cardBg,
                border = BorderStroke(1.dp, sakuColors.cardBorder),
                shadowElevation = if (sakuColors.isDark) 0.dp else 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    AuthTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (emailError != null) emailError = null
                        },
                        label = strings.emailLabel,
                        placeholder = strings.emailPlaceholder,
                        errorMessage = emailError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AuthTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (passwordError != null) passwordError = null
                        },
                        label = strings.passwordLabel,
                        placeholder = strings.passwordPlaceholder,
                        errorMessage = passwordError,
                        isPassword = true,
                        isPasswordVisible = isPasswordVisible,
                        onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                        showPasswordDescription = strings.showPasswordDesc,
                        hidePasswordDescription = strings.hidePasswordDesc,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                submitLogin()
                            }
                        )
                    )

                    // 4dp spacing compensates for the 14dp internal vertical touch padding of the 48dp row.
                    Spacer(modifier = Modifier.height(4.dp))

                    // Remember Me & Forgot Password
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AuthCheckboxRow(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            label = strings.rememberMeLabel,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(role = Role.Button, onClick = onNavigateToForgotPassword)
                                .padding(horizontal = 6.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = strings.forgotPasswordLink,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = sakuColors.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Action Buttons: Masuk & Biometrik
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        AuthPrimaryButton(
                            text = strings.loginBtn,
                            onClick = {
                                focusManager.clearFocus()
                                submitLogin()
                            }
                        )

                        OutlinedButton(
                            onClick = {
                                val isEnabled = preferences.isBiometricEnabled.value
                                val act = context as? FragmentActivity
                                if (!isEnabled) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(strings.biometricNotEnabledYet)
                                    }
                                } else if (act != null) {
                                    val availability = BiometricHelper.checkAvailability(context)
                                    if (availability == BiometricAvailability.AVAILABLE) {
                                        BiometricHelper.showBiometricPrompt(
                                            activity = act,
                                            title = strings.biometricPromptTitle,
                                            subtitle = strings.biometricPromptSubtitle,
                                            negativeButtonText = strings.biometricPromptNegative,
                                            onSuccess = {
                                                scope.launch {
                                                    preferences.setLoggedIn(true)
                                                    snackbarHostState.showSnackbar(strings.biometricAuthSuccess)
                                                    onLoginSuccess()
                                                }
                                            },
                                            onError = {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(strings.biometricAuthFailed)
                                                }
                                            }
                                        )
                                    } else {
                                        scope.launch {
                                            preferences.setLoggedIn(true)
                                            snackbarHostState.showSnackbar(strings.biometricAuthSuccess)
                                            kotlinx.coroutines.delay(300)
                                            onLoginSuccess()
                                        }
                                    }
                                } else {
                                    scope.launch {
                                        preferences.setLoggedIn(true)
                                        onLoginSuccess()
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, sakuColors.border),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = sakuColors.surfaceVariant,
                                contentColor = sakuColors.primary
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = SakuIcons.Fingerprint),
                                contentDescription = null,
                                tint = sakuColors.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = strings.signInWithFingerprintBtn,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = sakuColors.primary
                            )
                        }
                    }
                }
            }

            // Divider: atau masuk dengan
            AuthDivider(text = strings.orSignInWith)

            // Google Login Button
            GoogleSignInButton(
                text = strings.signInWithGoogleBtn,
                onClick = {
                    scope.launch {
                        preferences.setLoggedIn(true)
                        onLoginSuccess()
                    }
                }
            )

            // Register Prompt Footer
            AuthFooterLinkRow(
                prompt = strings.noAccountPrompt,
                actionText = strings.registerBtn,
                onActionClick = onNavigateToRegister
            )
        }
    }
}

// ==========================================
// 2. REGISTER SCREEN (Frame 1707)
// ==========================================
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val sakuColors = LocalSakuColors.current
    val strings = LocalAppStrings.current
    val focusManager = LocalFocusManager.current

    var fullName by remember { mutableStateOf("Damar Prasetyo") }
    var email by remember { mutableStateOf("damar.prasetyo@email.com") }
    var password by remember { mutableStateOf("rahasia123") }
    var confirmPassword by remember { mutableStateOf("rahasia123") }
    var agreeTerms by remember { mutableStateOf(true) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var termsError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val preferences = remember { AppPreferences.getInstance(context) }

    fun submitRegister() {
        val trimmedName = fullName.trim()
        val trimmedEmail = email.trim()

        val nextNameError = if (trimmedName.isEmpty()) strings.fullNameRequiredError else null
        val nextEmailError = when {
            trimmedEmail.isEmpty() -> strings.emailRequiredError
            !isValidEmail(trimmedEmail) -> strings.emailInvalidError
            else -> null
        }
        val nextPasswordError = when {
            password.isBlank() -> strings.passwordRequiredError
            password.length < 8 -> strings.passwordMinLengthError
            else -> null
        }
        val nextConfirmError = if (confirmPassword != password) {
            strings.confirmPasswordMismatchError
        } else {
            null
        }
        val nextTermsError = if (!agreeTerms) strings.agreeTermsRequiredError else null

        fullNameError = nextNameError
        emailError = nextEmailError
        passwordError = nextPasswordError
        confirmPasswordError = nextConfirmError
        termsError = nextTermsError

        if (nextNameError == null &&
            nextEmailError == null &&
            nextPasswordError == null &&
            nextConfirmError == null &&
            nextTermsError == null
        ) {
            scope.launch {
                preferences.setUserProfile(trimmedName, trimmedEmail, "")
                preferences.setLoggedIn(true)
                DefaultDataRepository.getInstance().updateUserName(trimmedName)
                onRegisterSuccess()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = sakuColors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        AuthScreenContainer(
            innerPadding = innerPadding,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.Start,
            spacing = 16.dp
        ) {
            // Header with Back Button
            AuthTopBar(
                title = strings.registerTitle,
                backDescription = strings.back,
                onBackClick = onNavigateBack
            )

            // Intro Titles
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = strings.registerHeroTitle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = sakuColors.textDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = strings.registerSubtitle,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = sakuColors.textSecondary
                )
            }

            // Form Card Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = sakuColors.cardBg,
                border = BorderStroke(1.dp, sakuColors.cardBorder),
                shadowElevation = if (sakuColors.isDark) 0.dp else 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    AuthTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            if (fullNameError != null) fullNameError = null
                        },
                        label = strings.fullNameLabel,
                        placeholder = strings.fullNamePlaceholder,
                        errorMessage = fullNameError,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AuthTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (emailError != null) emailError = null
                        },
                        label = strings.emailLabel,
                        placeholder = strings.emailPlaceholder,
                        errorMessage = emailError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AuthTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (passwordError != null) passwordError = null
                        },
                        label = strings.createPasswordLabel,
                        placeholder = strings.passwordPlaceholder,
                        errorMessage = passwordError,
                        helperText = strings.passwordRuleHint,
                        isPassword = true,
                        isPasswordVisible = isPasswordVisible,
                        onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                        showPasswordDescription = strings.showPasswordDesc,
                        hidePasswordDescription = strings.hidePasswordDesc,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AuthTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            if (confirmPasswordError != null) confirmPasswordError = null
                        },
                        label = strings.confirmPasswordLabel,
                        placeholder = strings.confirmPasswordPlaceholder,
                        errorMessage = confirmPasswordError,
                        isPassword = true,
                        isPasswordVisible = isConfirmPasswordVisible,
                        onTogglePasswordVisibility = {
                            isConfirmPasswordVisible = !isConfirmPasswordVisible
                        },
                        showPasswordDescription = strings.showPasswordDesc,
                        hidePasswordDescription = strings.hidePasswordDesc,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                submitRegister()
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    AuthCheckboxRow(
                        checked = agreeTerms,
                        onCheckedChange = {
                            agreeTerms = it
                            if (it) termsError = null
                        },
                        label = strings.agreeTermsLabel,
                        errorMessage = termsError
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        AuthPrimaryButton(
                            text = strings.registerBtn,
                            onClick = {
                                focusManager.clearFocus()
                                submitRegister()
                            }
                        )

                        GoogleSignInButton(
                            text = strings.signUpWithGoogleBtn,
                            onClick = {
                                scope.launch {
                                    val name = fullName.trim().ifEmpty { "Damar Prasetyo" }
                                    val mail = email.trim().ifEmpty { "damar.prasetyo@email.com" }
                                    preferences.setUserProfile(name, mail, "")
                                    preferences.setLoggedIn(true)
                                    DefaultDataRepository.getInstance().updateUserName(name)
                                    onRegisterSuccess()
                                }
                            }
                        )
                    }
                }
            }

            // Login Link Footer
            AuthFooterLinkRow(
                prompt = strings.alreadyHaveAccountPrompt,
                actionText = strings.signInToAccountLink,
                onActionClick = onNavigateToLogin
            )
        }
    }
}

// ==========================================
// 3. FORGOT PASSWORD SCREEN (Frame 1706)
// ==========================================
@Composable
fun ForgotPasswordScreen(
    onNavigateToOtp: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val sakuColors = LocalSakuColors.current
    val strings = LocalAppStrings.current
    val isEn = strings == EnglishStrings
    var email by remember { mutableStateOf("damar.prasetyo@email.com") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = sakuColors.background
    ) { innerPadding ->
        AuthScreenContainer(
            innerPadding = innerPadding,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            spacing = 20.dp
        ) {
            // Header
            AuthTopBar(
                title = strings.forgotPasswordTitle,
                backDescription = strings.back,
                onBackClick = onNavigateBack
            )

            // Lock Icon Badge & Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .shadow(4.dp, RoundedCornerShape(20.dp), ambientColor = SakuDarkGreen, spotColor = SakuDarkGreen)
                        .clip(RoundedCornerShape(20.dp))
                        .background(SakuDarkGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = SakuIcons.Lock),
                        contentDescription = null,
                        tint = SakuEmerald,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${strings.forgotPasswordTitle}?",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = sakuColors.textDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = strings.forgotPasswordSubtitle,
                    fontSize = 14.sp,
                    color = sakuColors.textSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }

            // Input Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = sakuColors.cardBg,
                border = BorderStroke(1.dp, sakuColors.cardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AuthTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = if (isEn) "Registered Email" else "Email Terdaftar",
                        placeholder = strings.emailPlaceholder,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { onNavigateToOtp() })
                    )

                    // Info Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(sakuColors.trackBg)
                            .border(1.dp, sakuColors.border, RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = sakuColors.primary,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 1.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEn) {
                                "A 6-digit OTP code will be sent to your registered email and is valid for 5 minutes."
                            } else {
                                "Kode OTP 6 digit akan dikirimkan ke email terdaftar Anda dan berlaku selama 5 menit."
                            },
                            fontSize = 13.sp,
                            color = sakuColors.textDark,
                            lineHeight = 18.sp
                        )
                    }

                    AuthPrimaryButton(
                        text = strings.sendOtpBtn,
                        onClick = onNavigateToOtp
                    )
                }
            }

            // Return to Login Footer
            AuthFooterLinkRow(
                prompt = if (isEn) "Remember your password?" else "Ingat kata sandi Anda?",
                actionText = strings.signInToAccountLink,
                onActionClick = onNavigateToLogin
            )
        }
    }
}

// ==========================================
// 4. OTP VERIFICATION SCREEN (Frame 2156)
// ==========================================
@Composable
fun OtpVerificationScreen(
    onResetSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val sakuColors = LocalSakuColors.current
    val strings = LocalAppStrings.current
    val isEn = strings == EnglishStrings
    val focusManager = LocalFocusManager.current

    val otpDigits = remember { mutableStateListOf("4", "8", "2", "9", "1", "0") }
    var newPassword by remember { mutableStateOf("sandiBaru2026") }
    var confirmNewPassword by remember { mutableStateOf("sandiBaru2026") }
    var isNewPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmNewPasswordVisible by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = sakuColors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        AuthScreenContainer(
            innerPadding = innerPadding,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            spacing = 18.dp
        ) {
            // Header
            AuthTopBar(
                title = strings.otpTitle,
                backDescription = strings.back,
                onBackClick = onNavigateBack
            )

            // Key Badge & Header text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .shadow(4.dp, RoundedCornerShape(20.dp), ambientColor = SakuDarkGreen, spotColor = SakuDarkGreen)
                        .clip(RoundedCornerShape(20.dp))
                        .background(SakuDarkGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = SakuIcons.Key),
                        contentDescription = null,
                        tint = SakuEmerald,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isEn) "Enter OTP Code" else "Masukkan Kode OTP",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = sakuColors.textDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${strings.otpSubtitle}\ndamar.prasetyo@email.com",
                    fontSize = 14.sp,
                    color = sakuColors.textSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }

            // OTP Card Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = sakuColors.cardBg,
                border = BorderStroke(1.dp, sakuColors.cardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (isEn) "Verification Code (6 Digits)" else "Kode Verifikasi (6 Digit)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = sakuColors.textDark
                    )

                    // 6 Digit Boxes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (i in 0 until 6) {
                            val digit = otpDigits.getOrElse(i) { "" }
                            val isFilled = digit.isNotEmpty()
                            Box(
                                modifier = Modifier
                                    .size(44.dp, 52.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(sakuColors.inputBg)
                                    .border(
                                        width = if (isFilled) 1.5.dp else 1.dp,
                                        color = if (isFilled) sakuColors.primary else sakuColors.inputBorder,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = digit,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = sakuColors.primary
                                )
                            }
                        }
                    }

                    // Resend OTP countdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isEn) "Didn't receive code? " else "Tidak menerima kode? ",
                            fontSize = 12.sp,
                            color = sakuColors.textSecondary
                        )
                        Text(
                            text = if (isEn) "Resend (00:45)" else "Kirim Ulang (00:45)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SakuEmerald,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (isEn) {
                                                "New OTP code has been sent to your email"
                                            } else {
                                                "Kode OTP baru telah dikirimkan ke email Anda"
                                            }
                                        )
                                    }
                                }
                                .padding(horizontal = 4.dp, vertical = 6.dp)
                        )
                    }

                    HorizontalDivider(thickness = 1.dp, color = sakuColors.border)

                    AuthTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = if (isEn) "New Password" else "Kata Sandi Baru",
                        placeholder = strings.passwordPlaceholder,
                        isPassword = true,
                        isPasswordVisible = isNewPasswordVisible,
                        onTogglePasswordVisibility = { isNewPasswordVisible = !isNewPasswordVisible },
                        showPasswordDescription = strings.showPasswordDesc,
                        hidePasswordDescription = strings.hidePasswordDesc,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    AuthTextField(
                        value = confirmNewPassword,
                        onValueChange = { confirmNewPassword = it },
                        label = if (isEn) "Confirm New Password" else "Konfirmasi Kata Sandi Baru",
                        placeholder = strings.confirmPasswordPlaceholder,
                        isPassword = true,
                        isPasswordVisible = isConfirmNewPasswordVisible,
                        onTogglePasswordVisibility = {
                            isConfirmNewPasswordVisible = !isConfirmNewPasswordVisible
                        },
                        showPasswordDescription = strings.showPasswordDesc,
                        hidePasswordDescription = strings.hidePasswordDesc,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )

                    AuthPrimaryButton(
                        text = strings.verifyOtpBtn,
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (isEn) "Password updated successfully!" else "Kata sandi berhasil diperbarui!"
                                )
                                kotlinx.coroutines.delay(400)
                                onResetSuccess()
                            }
                        }
                    )
                }
            }

            // Cancel and Back to Login
            Box(
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(role = Role.Button, onClick = onNavigateToLogin)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isEn) "Cancel and Return to Sign In" else "Batal dan Kembali ke Login",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = sakuColors.primary
                )
            }
        }
    }
}

// Previews
@Preview(name = "Login Screen", showBackground = true, device = Devices.PIXEL_7)
@Composable
private fun LoginScreenPreview() {
    SAKUTheme(darkTheme = false) {
        LoginScreen()
    }
}

@Preview(
    name = "Login Screen - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_7
)
@Composable
private fun LoginScreenDarkPreview() {
    SAKUTheme(darkTheme = true) {
        LoginScreen()
    }
}

@Preview(name = "Register Screen", showBackground = true, device = Devices.PIXEL_7)
@Composable
private fun RegisterScreenPreview() {
    SAKUTheme(darkTheme = false) {
        RegisterScreen()
    }
}

@Preview(
    name = "Register Screen - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_7
)
@Composable
private fun RegisterScreenDarkPreview() {
    SAKUTheme(darkTheme = true) {
        RegisterScreen()
    }
}

@Preview(name = "Forgot Password Screen", showBackground = true, device = Devices.PIXEL_7)
@Composable
private fun ForgotPasswordScreenPreview() {
    SAKUTheme(darkTheme = false) {
        ForgotPasswordScreen()
    }
}

@Preview(name = "OTP Verification Screen", showBackground = true, device = Devices.PIXEL_7)
@Composable
private fun OtpVerificationScreenPreview() {
    SAKUTheme(darkTheme = false) {
        OtpVerificationScreen()
    }
}
