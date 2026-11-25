package com.saku.app

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.saku.app.ui.assets.AddAssetScreen
import com.saku.app.ui.auth.ForgotPasswordScreen
import com.saku.app.ui.auth.LoginScreen
import com.saku.app.ui.auth.OtpVerificationScreen
import com.saku.app.ui.auth.RegisterScreen
import com.saku.app.ui.main.MainScreen
import com.saku.app.ui.pockets.CreatePocketScreen
import com.saku.app.ui.pockets.PocketsAndAssetsScreen
import com.saku.app.ui.profile.EditProfileAndApiScreen
import com.saku.app.ui.profile.ProfileScreen
import com.saku.app.ui.reports.ReportsScreen
import com.saku.app.ui.transaction.AddTransactionScreen
import com.saku.app.ui.transaction.TransactionDetailScreen

@Composable
fun MainNavigation() {
    val backStack = rememberNavBackStack(Main)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider =
            entryProvider {
                // 1. Beranda / Dashboard (Frame 133)
                entry<Main> {
                    MainScreen(onItemClick = { navKey -> backStack.add(navKey) })
                }

                // 2. Page Kantong & Page Aset (Frames 380 & 469)
                entry<PocketsAndAssets> { key ->
                    PocketsAndAssetsScreen(
                        initialTab = key.initialTab,
                        onNavigateToCreatePocket = { backStack.add(CreatePocket) },
                        onNavigateToAddAsset = { backStack.add(AddAsset) },
                        onNavigateToAdd = { backStack.add(AddTransaction(initialTab = 0)) },
                        onNavigateToHome = {
                            backStack.clear()
                            backStack.add(Main)
                        },
                        onNavigateToReports = {
                            backStack.clear()
                            backStack.add(Reports)
                        },
                        onNavigateToProfile = {
                            backStack.clear()
                            backStack.add(Profile)
                        }
                    )
                }

                // 3. Page Buat Kantong (Frame 1823)
                entry<CreatePocket> {
                    CreatePocketScreen(
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                }

                // 4. Page Tambah Aset (Frame 1888)
                entry<AddAsset> {
                    AddAssetScreen(
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                }

                // 5. Page Tambah - Scan Struk & Input Manual (Frames 729 & 808)
                entry<AddTransaction> { key ->
                    AddTransactionScreen(
                        initialTab = key.initialTab,
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                }

                // 6. Page Laporan & Statistik (Frame 952)
                entry<Reports> {
                    ReportsScreen(
                        onNavigateToHome = {
                            backStack.clear()
                            backStack.add(Main)
                        },
                        onNavigateToPockets = {
                            backStack.clear()
                            backStack.add(PocketsAndAssets(initialTab = 0))
                        },
                        onNavigateToProfile = {
                            backStack.clear()
                            backStack.add(Profile)
                        },
                        onNavigateToAdd = { backStack.add(AddTransaction(initialTab = 0)) },
                        onTransactionClick = { detail ->
                            backStack.add(detail)
                        }
                    )
                }

                // 7. Page Detail Transaksi (Frame 2008)
                entry<TransactionDetail> { key ->
                    TransactionDetailScreen(
                        detail = key,
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                }

                // 8. Page Profil & Pengaturan (Frame 1311)
                entry<Profile> {
                    ProfileScreen(
                        onNavigateToHome = {
                            backStack.clear()
                            backStack.add(Main)
                        },
                        onNavigateToPockets = {
                            backStack.clear()
                            backStack.add(PocketsAndAssets(initialTab = 0))
                        },
                        onNavigateToReports = {
                            backStack.clear()
                            backStack.add(Reports)
                        },
                        onNavigateToEditProfile = { backStack.add(EditProfileAndApi) },
                        onNavigateToAdd = { backStack.add(AddTransaction(initialTab = 0)) },
                        onLogout = {
                            backStack.clear()
                            backStack.add(Login)
                        }
                    )
                }

                // 9. Page Edit Profil & API (Frame 1436)
                entry<EditProfileAndApi> {
                    EditProfileAndApiScreen(
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                }

                // 10. Page Login (Frame 1525)
                entry<Login> {
                    LoginScreen(
                        onLoginSuccess = {
                            backStack.clear()
                            backStack.add(Main)
                        },
                        onNavigateToRegister = { backStack.add(Register) },
                        onNavigateToForgotPassword = { backStack.add(ForgotPassword) }
                    )
                }

                // 11. Page Daftar Akun (Frame 1707)
                entry<Register> {
                    RegisterScreen(
                        onRegisterSuccess = {
                            backStack.clear()
                            backStack.add(Main)
                        },
                        onNavigateToLogin = {
                            backStack.clear()
                            backStack.add(Login)
                        },
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                }

                // 12. Page Lupa Sandi (Frame 1706)
                entry<ForgotPassword> {
                    ForgotPasswordScreen(
                        onNavigateToOtp = { backStack.add(OtpVerification) },
                        onNavigateToLogin = {
                            backStack.clear()
                            backStack.add(Login)
                        },
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                }

                // 13. Page Verifikasi OTP (Frame 2156)
                entry<OtpVerification> {
                    OtpVerificationScreen(
                        onResetSuccess = {
                            backStack.clear()
                            backStack.add(Main)
                        },
                        onNavigateToLogin = {
                            backStack.clear()
                            backStack.add(Login)
                        },
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                }
            },
    )
}
