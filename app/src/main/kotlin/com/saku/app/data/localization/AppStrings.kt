package com.saku.app.data.localization

import androidx.compose.runtime.staticCompositionLocalOf
import com.saku.app.data.AssetClass
import com.saku.app.data.CategoryType
import com.saku.app.data.PocketCategory
import com.saku.app.data.preference.AppLanguage

data class AppStrings(
    // Common / Actions
    val back: String,
    val cancel: String,
    val edit: String,
    val close: String,
    val reset: String,
    val manage: String,
    val viewAll: String,
    val success: String,

    // Bottom Navigation
    val navHome: String,
    val navPocket: String,
    val navAdd: String,
    val navReport: String,
    val navProfile: String,

    // Main / Dashboard Screen
    val greetingPrefix: String,
    val greetingSubtitle: String,
    val totalBalance: String,
    val monthlyIncome: String,
    val monthlyExpense: String,
    val actionScanReceipt: String,
    val actionMyAssets: String,
    val actionCreatePocket: String,
    val actionExcelRecap: String,
    val myPocketsSection: String,
    val recentTransactionsSection: String,
    val emptyTransactions: String,
    val remainingBudgetPrefix: String,
    val pocketCountSuffix: String,
    val brandDialogSubtitle: String,
    val brandDialogDesc: String,
    val brandDialogVersion: String,

    // Pockets & Assets Screen
    val pocketsAndAssetsTitle: String,
    val tabBudgetPockets: String,
    val tabMyAssets: String,
    val btnCreatePocket: String,
    val btnAddAsset: String,
    val totalBudget: String,
    val usedBudget: String,
    val remainingQuota: String,
    val catAll: String,
    val catEssential: String,
    val catEmergency: String,
    val catSavings: String,
    val catWishlist: String,
    val totalNetWorth: String,
    val classCash: String,
    val classInvestment: String,
    val assetPortfolioListTitle: String,
    val totalLabel: String,
    val assetAccountsSuffix: String,
    val assetShareSuffix: String,
    val thisMonthShort: String,
    val legendCash: String,
    val legendEmergency: String,
    val legendInvest: String,
    val legendWallet: String,
    val emptyPocketsTitle: String,
    val emptyPocketsDesc: String,
    val resetFilterBtn: String,
    val managePocketTitle: String,
    val pocketLimitLabel: String,
    val saveChangesBtn: String,
    val deletePocketBtn: String,
    val deletePocketConfirmBtn: String,
    val pocketUpdatedSuccess: String,
    val pocketDeletedSuccess: String,
    val assetLabel: String,

    // Create Pocket Screen
    val createPocketTitle: String,
    val pocketNameLabel: String,
    val pocketCategoryLabel: String,
    val pocketIconColorLabel: String,
    val pocketResetCycleLabel: String,
    val pocketSaveBtn: String,

    // Add Asset Screen
    val addAssetTitle: String,
    val assetNameLabel: String,
    val assetClassLabel: String,
    val assetSaveBtn: String,

    // Add Transaction Screen
    val scanReceiptTab: String,
    val manualInputTab: String,
    val transactionAmountLabel: String,
    val selectPocketLabel: String,
    val transactionDateLabel: String,
    val transactionNotesLabel: String,
    val transactionTypeExpense: String,
    val transactionTypeIncome: String,
    val saveTransactionBtn: String,

    // Transaction Detail Screen
    val transactionDetailTitle: String,
    val detailCategory: String,
    val detailSourcePocket: String,
    val detailTime: String,
    val detailMethod: String,
    val detailNotes: String,
    val detailDeleteBtn: String,
    val detailShareBtn: String,

    // Reports Screen
    val reportsTitle: String,
    val periodWeekly: String,
    val periodMonthly: String,
    val periodYearly: String,
    val exportExcelBtn: String,
    val filterAll: String,
    val filterExpense: String,
    val filterIncome: String,
    val dailyAverageLabel: String,
    val expenseByCategoryTitle: String,

    // Profile Screen
    val title: String,
    val income: String,
    val expense: String,
    val sectionAppSettings: String,
    val biometricTitle: String,
    val biometricSubtitle: String,
    val appSettingsTitle: String,
    val appSettingsSubtitle: String,
    val themeTitle: String,
    val themeSubtitle: String,
    val languageTitle: String,
    val languageSubtitle: String,
    val notificationTitle: String,
    val notificationSubtitle: String,
    val logoutButton: String,
    val biometricPromptTitle: String,
    val biometricPromptSubtitle: String,
    val biometricPromptNegative: String,
    val biometricEnabledSuccess: String,
    val biometricDisabledSuccess: String,
    val biometricAuthSuccess: String,
    val biometricAuthFailed: String,
    val biometricNotEnrolled: String,
    val biometricNotAvailable: String,
    val biometricNotEnabledYet: String,

    // Edit Profile Screen
    val editProfileTitle: String,
    val fullNameLabel: String,
    val emailLabel: String,
    val geminiApiKeyLabel: String,
    val testConnectionBtn: String,
    val saveProfileBtn: String,
    val profileUpdatedSuccess: String,
    val changeProfilePhoto: String,
    val removeProfilePhoto: String,
    val photoUpdatedSuccess: String,
    val photoRemovedSuccess: String,

    // Auth Screens
    val appLogoDesc: String,
    val loginSubtitle: String,
    val registerTitle: String,
    val registerSubtitle: String,
    val registerHeroTitle: String,
    val forgotPasswordTitle: String,
    val forgotPasswordSubtitle: String,
    val otpTitle: String,
    val otpSubtitle: String,
    val passwordLabel: String,
    val createPasswordLabel: String,
    val confirmPasswordLabel: String,
    val passwordRuleHint: String,
    val fullNamePlaceholder: String,
    val emailPlaceholder: String,
    val passwordPlaceholder: String,
    val confirmPasswordPlaceholder: String,
    val showPasswordDesc: String,
    val hidePasswordDesc: String,
    val rememberMeLabel: String,
    val forgotPasswordLink: String,
    val loginBtn: String,
    val signInWithFingerprintBtn: String,
    val orSignInWith: String,
    val signInWithGoogleBtn: String,
    val signUpWithGoogleBtn: String,
    val noAccountPrompt: String,
    val registerBtn: String,
    val agreeTermsLabel: String,
    val alreadyHaveAccountPrompt: String,
    val signInToAccountLink: String,
    val sendOtpBtn: String,
    val verifyOtpBtn: String,
    val fullNameRequiredError: String,
    val emailRequiredError: String,
    val emailInvalidError: String,
    val passwordRequiredError: String,
    val passwordMinLengthError: String,
    val confirmPasswordMismatchError: String,
    val agreeTermsRequiredError: String
)

val IndonesianStrings = AppStrings(
    // Common / Actions
    back = "Kembali",
    cancel = "Batal",
    edit = "Edit",
    close = "Tutup",
    reset = "Reset",
    manage = "Atur",
    viewAll = "Semua Riwayat",
    success = "Berhasil",

    // Bottom Navigation
    navHome = "Beranda",
    navPocket = "Kantong",
    navAdd = "Tambah",
    navReport = "Laporan",
    navProfile = "Profil",

    // Main / Dashboard Screen
    greetingPrefix = "Halo,",
    greetingSubtitle = "Yuk, kelola keuanganmu hari ini.",
    totalBalance = "Total Saldo Anda",
    monthlyIncome = "Pemasukan",
    monthlyExpense = "Pengeluaran",
    actionScanReceipt = "Scan Struk",
    actionMyAssets = "Aset Saya",
    actionCreatePocket = "Buat Kantong",
    actionExcelRecap = "Rekap Excel",
    myPocketsSection = "Kantong Saya",
    recentTransactionsSection = "Riwayat Transaksi",
    emptyTransactions = "Belum ada transaksi tercatat",
    remainingBudgetPrefix = "Sisa Anggaran",
    pocketCountSuffix = "Kantong",
    brandDialogSubtitle = "Sistem Alokasi Keuangan & Pemindai Struk",
    brandDialogDesc = "Kelola keuangan cerdas dalam genggaman: pos alokasi kantong disiplin, pemindai struk instan (On-Device OCR & Gemini AI), dan pemantau portofolio aset.",
    brandDialogVersion = "Versi 1.0.0 • Mobile Android Native • Material Design 3",

    // Pockets & Assets Screen
    pocketsAndAssetsTitle = "Kantong & Aset",
    tabBudgetPockets = "Kantong Anggaran",
    tabMyAssets = "Aset Saya",
    btnCreatePocket = "+ Buat Kantong",
    btnAddAsset = "+ Tambah Aset",
    totalBudget = "Total Anggaran",
    usedBudget = "Terpakai",
    remainingQuota = "Sisa Kuota",
    catAll = "Semua",
    catEssential = "Pokok",
    catEmergency = "Darurat",
    catSavings = "Tabungan",
    catWishlist = "Keinginan",
    totalNetWorth = "Total Nilai Bersih",
    classCash = "Kas & E-Wallet",
    classInvestment = "Investasi",
    assetPortfolioListTitle = "Daftar Portofolio Aset",
    totalLabel = "Total",
    assetAccountsSuffix = "Akun",
    assetShareSuffix = "porsi",
    thisMonthShort = "Bln Ini",
    legendCash = "Kas",
    legendEmergency = "Darurat",
    legendInvest = "Invest",
    legendWallet = "Tunai",
    emptyPocketsTitle = "Belum ada kantong di kategori ini",
    emptyPocketsDesc = "Buat kantong baru atau tampilkan semua kategori untuk melihat alokasi anggaran Anda.",
    resetFilterBtn = "Tampilkan Semua",
    managePocketTitle = "Kelola Kantong",
    pocketLimitLabel = "Batas Anggaran Bulanan (Rp)",
    saveChangesBtn = "Simpan Perubahan",
    deletePocketBtn = "Hapus Kantong",
    deletePocketConfirmBtn = "Konfirmasi Hapus",
    pocketUpdatedSuccess = "Kantong berhasil diperbarui!",
    pocketDeletedSuccess = "Kantong berhasil dihapus.",
    assetLabel = "Aset",

    // Create Pocket Screen
    createPocketTitle = "Buat Kantong Baru",
    pocketNameLabel = "Nama Kantong",
    pocketCategoryLabel = "Kategori Anggaran",
    pocketIconColorLabel = "Pilih Ikon & Warna",
    pocketResetCycleLabel = "Siklus Reset Anggaran",
    pocketSaveBtn = "Simpan Kantong",

    // Add Asset Screen
    addAssetTitle = "Tambah Aset Baru",
    assetNameLabel = "Nama Aset / Akun",
    assetClassLabel = "Kelas Aset",
    assetSaveBtn = "Simpan Aset",

    // Add Transaction Screen
    scanReceiptTab = "Pindai Struk",
    manualInputTab = "Input Manual",
    transactionAmountLabel = "Nominal Transaksi (Rp)",
    selectPocketLabel = "Pilih Kantong Sumber",
    transactionDateLabel = "Tanggal & Waktu",
    transactionNotesLabel = "Catatan Transaksi",
    transactionTypeExpense = "Pengeluaran",
    transactionTypeIncome = "Pemasukan",
    saveTransactionBtn = "Simpan Transaksi",

    // Transaction Detail Screen
    transactionDetailTitle = "Rincian Transaksi",
    detailCategory = "Kategori",
    detailSourcePocket = "Kantong Sumber",
    detailTime = "Waktu Transaksi",
    detailMethod = "Metode Pencatatan",
    detailNotes = "Catatan Transaksi",
    detailDeleteBtn = "Hapus Transaksi",
    detailShareBtn = "Bagikan Bukti",

    // Reports Screen
    reportsTitle = "Laporan",
    periodWeekly = "Mingguan",
    periodMonthly = "Bulanan",
    periodYearly = "Tahunan",
    exportExcelBtn = "Ekspor Excel",
    filterAll = "Semua",
    filterExpense = "Pengeluaran",
    filterIncome = "Pemasukan",
    dailyAverageLabel = "Rata-rata Harian",
    expenseByCategoryTitle = "Pengeluaran per Kategori",

    // Profile Screen
    title = "Profil Saya",
    income = "Pemasukan",
    expense = "Pengeluaran",
    sectionAppSettings = "PENGATURAN APLIKASI",
    biometricTitle = "Biometrik",
    biometricSubtitle = "Kunci sidik jari & Face ID",
    appSettingsTitle = "Pengaturan Aplikasi",
    appSettingsSubtitle = "Kelola data & preferensi umum",
    themeTitle = "Tema",
    themeSubtitle = "Mode tampilan antarmuka",
    languageTitle = "Bahasa",
    languageSubtitle = "Bahasa utama antarmuka",
    notificationTitle = "Notifikasi",
    notificationSubtitle = "Pengingat catat keuangan",
    logoutButton = "Keluar dari Akun",
    biometricPromptTitle = "Konfirmasi Biometrik",
    biometricPromptSubtitle = "Pindai sidik jari atau gunakan Face ID untuk verifikasi",
    biometricPromptNegative = "Batal",
    biometricEnabledSuccess = "Kunci biometrik berhasil diaktifkan!",
    biometricDisabledSuccess = "Kunci biometrik dinonaktifkan.",
    biometricAuthSuccess = "Autentikasi biometrik berhasil!",
    biometricAuthFailed = "Autentikasi biometrik gagal atau dibatalkan.",
    biometricNotEnrolled = "Biometrik belum didaftarkan di perangkat ini.",
    biometricNotAvailable = "Perangkat tidak memiliki sensor biometrik.",
    biometricNotEnabledYet = "Kunci biometrik belum diaktifkan di Profil.",

    // Edit Profile Screen
    editProfileTitle = "Edit Profil & API",
    fullNameLabel = "Nama Lengkap",
    emailLabel = "Alamat Email",
    geminiApiKeyLabel = "Gemini API Key",
    testConnectionBtn = "Uji Koneksi",
    saveProfileBtn = "Simpan Perubahan",
    profileUpdatedSuccess = "Profil & konfigurasi API berhasil disimpan!",
    changeProfilePhoto = "Ubah Foto Profil",
    removeProfilePhoto = "Hapus Foto",
    photoUpdatedSuccess = "Foto profil berhasil diperbarui!",
    photoRemovedSuccess = "Foto profil dihapus, kembali ke inisial nama.",

    // Auth Screens
    appLogoDesc = "Logo Aplikasi SAKU",
    loginSubtitle = "Kelola keuangan cerdas dalam genggaman",
    registerTitle = "Daftar Akun SAKU",
    registerSubtitle = "Mulai perjalanan finansial teratur hari ini",
    registerHeroTitle = "Mulai Langkah Finansialmu",
    forgotPasswordTitle = "Lupa Kata Sandi",
    forgotPasswordSubtitle = "Masukkan email untuk menerima instruksi reset",
    otpTitle = "Verifikasi Kode OTP",
    otpSubtitle = "Masukkan 6 digit kode yang dikirimkan ke email Anda",
    passwordLabel = "Kata Sandi",
    createPasswordLabel = "Buat Kata Sandi",
    confirmPasswordLabel = "Konfirmasi Kata Sandi",
    passwordRuleHint = "Minimal 8 karakter, kombinasi huruf & angka",
    fullNamePlaceholder = "Masukkan nama lengkap",
    emailPlaceholder = "nama@email.com",
    passwordPlaceholder = "Masukkan kata sandi",
    confirmPasswordPlaceholder = "Ulangi kata sandi",
    showPasswordDesc = "Tampilkan kata sandi",
    hidePasswordDesc = "Sembunyikan kata sandi",
    rememberMeLabel = "Ingat saya di perangkat ini",
    forgotPasswordLink = "Lupa Kata Sandi?",
    loginBtn = "Masuk Sekarang",
    signInWithFingerprintBtn = "Masuk dengan Sidik Jari",
    orSignInWith = "atau masuk dengan",
    signInWithGoogleBtn = "Masuk dengan Google",
    signUpWithGoogleBtn = "Daftar dengan Google",
    noAccountPrompt = "Belum memiliki akun SAKU?",
    registerBtn = "Daftar Akun Baru",
    agreeTermsLabel = "Saya menyetujui Syarat & Ketentuan SAKU",
    alreadyHaveAccountPrompt = "Sudah memiliki akun SAKU?",
    signInToAccountLink = "Masuk ke Akun Anda",
    sendOtpBtn = "Kirim Kode Verifikasi",
    verifyOtpBtn = "Verifikasi & Lanjutkan",
    fullNameRequiredError = "Nama lengkap wajib diisi",
    emailRequiredError = "Alamat email wajib diisi",
    emailInvalidError = "Format alamat email tidak valid",
    passwordRequiredError = "Kata sandi wajib diisi",
    passwordMinLengthError = "Kata sandi minimal 8 karakter",
    confirmPasswordMismatchError = "Konfirmasi kata sandi tidak cocok",
    agreeTermsRequiredError = "Setujui Syarat & Ketentuan untuk melanjutkan"
)

val EnglishStrings = AppStrings(
    // Common / Actions
    back = "Back",
    cancel = "Cancel",
    edit = "Edit",
    close = "Close",
    reset = "Reset",
    manage = "Manage",
    viewAll = "All History",
    success = "Success",

    // Bottom Navigation
    navHome = "Home",
    navPocket = "Pockets",
    navAdd = "Add",
    navReport = "Reports",
    navProfile = "Profile",

    // Main / Dashboard Screen
    greetingPrefix = "Hello,",
    greetingSubtitle = "Let's manage your finances today.",
    totalBalance = "Your Total Balance",
    monthlyIncome = "Income",
    monthlyExpense = "Expenses",
    actionScanReceipt = "Scan Receipt",
    actionMyAssets = "My Assets",
    actionCreatePocket = "Create Pocket",
    actionExcelRecap = "Excel Recap",
    myPocketsSection = "My Pockets",
    recentTransactionsSection = "Recent Transactions",
    emptyTransactions = "No transactions recorded yet",
    remainingBudgetPrefix = "Remaining Budget",
    pocketCountSuffix = "Pockets",
    brandDialogSubtitle = "Financial Allocation & Smart Receipt Scanner",
    brandDialogDesc = "Smart finance in the palm of your hand: disciplined pocket allocations, instant receipt scanning (On-Device OCR & Gemini AI), and comprehensive asset monitoring.",
    brandDialogVersion = "Version 1.0.0 • Mobile Android Native • Material Design 3",

    // Pockets & Assets Screen
    pocketsAndAssetsTitle = "Pockets & Assets",
    tabBudgetPockets = "Budget Pockets",
    tabMyAssets = "My Assets",
    btnCreatePocket = "+ Create Pocket",
    btnAddAsset = "+ Add Asset",
    totalBudget = "Total Budget",
    usedBudget = "Used",
    remainingQuota = "Remaining Quota",
    catAll = "All",
    catEssential = "Essential",
    catEmergency = "Emergency",
    catSavings = "Savings",
    catWishlist = "Wishlist",
    totalNetWorth = "Total Net Worth",
    classCash = "Cash & E-Wallet",
    classInvestment = "Investments",
    assetPortfolioListTitle = "Asset Portfolio List",
    totalLabel = "Total",
    assetAccountsSuffix = "Accounts",
    assetShareSuffix = "share",
    thisMonthShort = "This Month",
    legendCash = "Cash",
    legendEmergency = "Emergency",
    legendInvest = "Invest",
    legendWallet = "Wallet",
    emptyPocketsTitle = "No pockets in this category yet",
    emptyPocketsDesc = "Create a new pocket or show all categories to view your budget allocations.",
    resetFilterBtn = "Show All",
    managePocketTitle = "Manage Pocket",
    pocketLimitLabel = "Monthly Budget Limit (Rp)",
    saveChangesBtn = "Save Changes",
    deletePocketBtn = "Delete Pocket",
    deletePocketConfirmBtn = "Confirm Delete",
    pocketUpdatedSuccess = "Pocket updated successfully!",
    pocketDeletedSuccess = "Pocket deleted.",
    assetLabel = "Asset",

    // Create Pocket Screen
    createPocketTitle = "Create New Pocket",
    pocketNameLabel = "Pocket Name",
    pocketCategoryLabel = "Budget Category",
    pocketIconColorLabel = "Choose Icon & Color",
    pocketResetCycleLabel = "Budget Reset Cycle",
    pocketSaveBtn = "Save Pocket",

    // Add Asset Screen
    addAssetTitle = "Add New Asset",
    assetNameLabel = "Asset / Account Name",
    assetClassLabel = "Asset Class",
    assetSaveBtn = "Save Asset",

    // Add Transaction Screen
    scanReceiptTab = "Scan Receipt",
    manualInputTab = "Manual Input",
    transactionAmountLabel = "Transaction Amount (Rp)",
    selectPocketLabel = "Select Source Pocket",
    transactionDateLabel = "Date & Time",
    transactionNotesLabel = "Transaction Notes",
    transactionTypeExpense = "Expense",
    transactionTypeIncome = "Income",
    saveTransactionBtn = "Save Transaction",

    // Transaction Detail Screen
    transactionDetailTitle = "Transaction Details",
    detailCategory = "Category",
    detailSourcePocket = "Source Pocket",
    detailTime = "Transaction Time",
    detailMethod = "Recording Method",
    detailNotes = "Transaction Notes",
    detailDeleteBtn = "Delete Transaction",
    detailShareBtn = "Share Receipt",

    // Reports Screen
    reportsTitle = "Reports",
    periodWeekly = "Weekly",
    periodMonthly = "Monthly",
    periodYearly = "Yearly",
    exportExcelBtn = "Export Excel",
    filterAll = "All",
    filterExpense = "Expenses",
    filterIncome = "Income",
    dailyAverageLabel = "Daily Average",
    expenseByCategoryTitle = "Expenses by Category",

    // Profile Screen
    title = "My Profile",
    income = "Income",
    expense = "Expenses",
    sectionAppSettings = "APPLICATION SETTINGS",
    biometricTitle = "Biometrics",
    biometricSubtitle = "Fingerprint & Face ID lock",
    appSettingsTitle = "App Settings",
    appSettingsSubtitle = "Manage data & general preferences",
    themeTitle = "Theme",
    themeSubtitle = "Interface display mode",
    languageTitle = "Language",
    languageSubtitle = "Interface primary language",
    notificationTitle = "Notifications",
    notificationSubtitle = "Financial tracking reminders",
    logoutButton = "Log Out",
    biometricPromptTitle = "Biometric Confirmation",
    biometricPromptSubtitle = "Scan your fingerprint or use Face ID to verify",
    biometricPromptNegative = "Cancel",
    biometricEnabledSuccess = "Biometric lock enabled successfully!",
    biometricDisabledSuccess = "Biometric lock disabled.",
    biometricAuthSuccess = "Biometric authentication successful!",
    biometricAuthFailed = "Biometric authentication failed or cancelled.",
    biometricNotEnrolled = "No biometrics enrolled on this device.",
    biometricNotAvailable = "Biometric sensor is not available on this device.",
    biometricNotEnabledYet = "Biometric lock is not enabled in Profile.",

    // Edit Profile Screen
    editProfileTitle = "Edit Profile & API",
    fullNameLabel = "Full Name",
    emailLabel = "Email Address",
    geminiApiKeyLabel = "Gemini API Key",
    testConnectionBtn = "Test Connection",
    saveProfileBtn = "Save Changes",
    profileUpdatedSuccess = "Profile & API configuration saved!",
    changeProfilePhoto = "Change Profile Photo",
    removeProfilePhoto = "Remove Photo",
    photoUpdatedSuccess = "Profile photo updated successfully!",
    photoRemovedSuccess = "Profile photo removed, reverted to initials.",

    // Auth Screens
    appLogoDesc = "SAKU App Logo",
    loginSubtitle = "Smart financial management in your hand",
    registerTitle = "Create SAKU Account",
    registerSubtitle = "Begin your organized financial journey today",
    registerHeroTitle = "Start Your Financial Journey",
    forgotPasswordTitle = "Forgot Password",
    forgotPasswordSubtitle = "Enter your email to receive reset instructions",
    otpTitle = "Verify OTP Code",
    otpSubtitle = "Enter the 6-digit code sent to your email",
    passwordLabel = "Password",
    createPasswordLabel = "Create Password",
    confirmPasswordLabel = "Confirm Password",
    passwordRuleHint = "Min. 8 characters, letters & numbers",
    fullNamePlaceholder = "Enter full name",
    emailPlaceholder = "name@email.com",
    passwordPlaceholder = "Enter password",
    confirmPasswordPlaceholder = "Re-enter password",
    showPasswordDesc = "Show password",
    hidePasswordDesc = "Hide password",
    rememberMeLabel = "Remember me on this device",
    forgotPasswordLink = "Forgot Password?",
    loginBtn = "Sign In",
    signInWithFingerprintBtn = "Sign in with Fingerprint",
    orSignInWith = "or sign in with",
    signInWithGoogleBtn = "Sign in with Google",
    signUpWithGoogleBtn = "Sign up with Google",
    noAccountPrompt = "Don't have a SAKU account?",
    registerBtn = "Register New Account",
    agreeTermsLabel = "I agree to SAKU Terms & Conditions",
    alreadyHaveAccountPrompt = "Already have a SAKU account?",
    signInToAccountLink = "Sign in to Your Account",
    sendOtpBtn = "Send Verification Code",
    verifyOtpBtn = "Verify & Continue",
    fullNameRequiredError = "Full name is required",
    emailRequiredError = "Email address is required",
    emailInvalidError = "Invalid email address format",
    passwordRequiredError = "Password is required",
    passwordMinLengthError = "Password must be at least 8 characters",
    confirmPasswordMismatchError = "Passwords do not match",
    agreeTermsRequiredError = "Please agree to the Terms & Conditions to continue"
)

fun getStrings(language: AppLanguage): AppStrings {
    return when (language) {
        AppLanguage.INDONESIAN -> IndonesianStrings
        AppLanguage.ENGLISH -> EnglishStrings
    }
}

val LocalAppStrings = staticCompositionLocalOf { IndonesianStrings }

// Localization helper extensions
fun AppStrings.localizeCategory(type: CategoryType): String = when (type) {
    CategoryType.FOOD -> if (this == EnglishStrings) "Food & Beverage" else "Makan & Minum"
    CategoryType.TRANSPORT -> if (this == EnglishStrings) "Transportation" else "Transportasi"
    CategoryType.ENTERTAINMENT -> if (this == EnglishStrings) "Entertainment" else "Hiburan"
    CategoryType.INCOME -> if (this == EnglishStrings) "Income" else "Pemasukan"
    CategoryType.GENERAL -> if (this == EnglishStrings) "General" else "Umum"
}

fun AppStrings.localizePocketCategory(category: PocketCategory): String = when (category) {
    PocketCategory.ALL -> catAll
    PocketCategory.POKOK -> catEssential
    PocketCategory.DARURAT -> catEmergency
    PocketCategory.TABUNGAN -> catSavings
    PocketCategory.KEINGINAN -> catWishlist
}

fun AppStrings.localizeAssetClass(assetClass: AssetClass): String = when (assetClass) {
    AssetClass.CASH -> classCash
    AssetClass.EMERGENCY -> if (this == EnglishStrings) "Liquid Emergency" else "Darurat Likuid"
    AssetClass.INVESTMENT -> classInvestment
    AssetClass.CASH_RESERVE -> if (this == EnglishStrings) "Cash & Gold" else "Tunai & Emas"
}

fun AppStrings.localizePocketName(name: String): String {
    if (this != EnglishStrings) return name
    return when (name) {
        "Kebutuhan Pokok" -> "Essential Needs"
        "Dana Darurat" -> "Emergency Fund"
        "Tabungan Masa Depan" -> "Future Savings"
        "Keinginan & Hiburan" -> "Wants & Entertainment"
        "Hiburan & Hobi" -> "Entertainment & Hobbies"
        "Langganan & Hiburan Digital" -> "Digital Subscriptions & Fun"
        "Makan & Minum" -> "Food & Beverage"
        "Transportasi" -> "Transportation"
        "Hiburan" -> "Entertainment"
        else -> name
    }
}

fun AppStrings.localizeTransactionTitle(title: String): String {
    if (this != EnglishStrings) return title
    return when (title) {
        "Belanja Supermarket" -> "Supermarket Groceries"
        "Bensin Pertamax & Tol" -> "Pertamax Fuel & Toll"
        "Gaji Bulanan Pokok" -> "Monthly Base Salary"
        "Top Up Reksadana Saham" -> "Equity Mutual Fund Top Up"
        "Nonton Bioskop & Jajan" -> "Cinema & Snacks"
        "Beli Emas Antam 1 Gram" -> "Buy 1g Gold Bar"
        else -> title
    }
}

fun AppStrings.localizeStatus(status: String): String {
    if (this != EnglishStrings) return status
    return status
        .replace("Overbudget", "Over Budget")
        .replace("Aman", "Safe")
        .replace("Waspada", "Warning")
        .replace("Kritis", "Critical")
        .replace("Hati-hati", "Caution")
}

fun AppStrings.localizeAssetBadge(badge: String): String {
    if (this != EnglishStrings) return badge
    return when (badge) {
        "Kas Likuid" -> "Liquid Cash"
        "Bunga ~5%" -> "~5% Yield"
        "+11.2% Untung" -> "+11.2% Gain"
        "Tunai Aman" -> "Safe Cash"
        "0% (Kas)" -> "0% (Cash)"
        "4-6% (Pasar Uang)" -> "4-6% (Money Market)"
        "8-12% (Saham)" -> "8-12% (Equity)"
        else -> badge
    }
}
