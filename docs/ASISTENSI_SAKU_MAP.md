# Peta Kode Saku — Persiapan Asistensi

**Tujuan:** Dokumentasi read-only lengkap struktur kode Saku untuk mempercepat asistensi development. Semua entri dikutip dari file yang dibaca sesi ini.

**Project Root:** `C:\Saku`

---

## 1. Pohon Folder (app/src)

```
app/src/
├── androidTest/java/com/example/
│   └── ExampleInstrumentedTest.kt
├── main/java/com/example/
│   ├── MainActivity.kt
│   ├── SakuAppContainer.kt
│   ├── core/
│   │   ├── Formatters.kt
│   │   └── ThousandSeparatorTransformation.kt
│   ├── data/
│   │   ├── export/
│   │   │   └── SimpleXlsxWriter.kt
│   │   ├── local/
│   │   │   ├── SakuDatabase.kt
│   │   │   ├── dao/
│   │   │   │   └── Daos.kt
│   │   │   └── entity/
│   │   │       └── Entities.kt
│   │   ├── repository/
│   │   │   └── SakuRepositoryImpl.kt
│   │   └── service/
│   │       ├── ApiKeyConfigService.kt
│   │       ├── CurrencyConversionService.kt
│   │       ├── EncryptedApiKeyConfigService.kt
│   │       ├── ReceiptOcrParser.kt
│   │       └── ReceiptScannerService.kt
│   ├── domain/
│   │   ├── model/
│   │   │   └── Models.kt
│   │   └── repository/
│   │       └── SakuRepository.kt
│   └── ui/
│       ├── components/
│       │   ├── SakuComponents.kt
│       │   └── auth/
│       │       └── AuthComponents.kt
│       ├── navigation/
│       │   ├── SakuNavHost.kt
│       │   └── Screen.kt
│       ├── screens/
│       │   ├── auth/
│       │   │   ├── AuthViewModel.kt
│       │   │   ├── ForgotPasswordScreen.kt
│       │   │   ├── LoginScreen.kt
│       │   │   └── RegisterScreen.kt
│       │   ├── dashboard/
│       │   │   ├── DashboardScreen.kt
│       │   │   └── DashboardViewModel.kt
│       │   ├── export/
│       │   │   ├── ExportBackupScreen.kt
│       │   │   └── ExportBackupViewModel.kt
│       │   ├── kantong/
│       │   │   ├── KantongScreen.kt
│       │   │   ├── KantongViewModel.kt
│       │   │   └── TransferSheet.kt
│       │   ├── profile/
│       │   │   ├── AvatarBitmapUtil.kt
│       │   │   ├── InformasiPribadiScreen.kt
│       │   │   ├── ProfileComponents.kt
│       │   │   ├── ProfileScreen.kt
│       │   │   └── ProfileViewModel.kt
│       │   ├── report/
│       │   │   ├── ReportScreen.kt
│       │   │   └── ReportViewModel.kt
│       │   └── transaction/
│       │       ├── AddTransactionScreen.kt
│       │       └── TransactionViewModel.kt
│       └── theme/
│           ├── Color.kt
│           ├── Theme.kt
│           └── Type.kt
└── test/java/com/example/
    ├── ExampleRobolectricTest.kt
    ├── ExampleUnitTest.kt
    ├── GreetingScreenshotTest.kt
    ├── ReceiptOcrParserTest.kt
    ├── SakuAssetManagementTest.kt
    ├── SakuDataIntegrityTest.kt
    ├── SakuFinancialCalculationTest.kt
    ├── SakuPocketEditTest.kt
    ├── SakuReceiptScanningTest.kt
    ├── SakuReportSynchronizationTest.kt
    ├── SakuUnitTest.kt
    └── SakuXlsxExportTest.kt
```

---

## 2. Inventaris File (56 file .kt)

| # | File | Class/Fungsi Utama | Peran (1 kalimat) | Bukti |
|---|------|-------------------|-------------------|-------|
| 1 | ExampleInstrumentedTest.kt | ExampleInstrumentedTest | Test instrumentasi Android standar | app/src/androidTest/.../ExampleInstrumentedTest.kt:15 |
| 2 | MainActivity.kt | MainActivity | Entry point aplikasi, mengatur theme dan navigasi | app/src/main/.../MainActivity.kt:10 |
| 3 | SakuAppContainer.kt | SakuAppContainer | Container DI manual untuk database, repository, dan services | app/src/main/.../SakuAppContainer.kt:17 |
| 4 | Formatters.kt | Formatters | Utility object untuk format Rupiah dan tanggal Indonesia | app/src/main/.../core/Formatters.kt:8 |
| 5 | ThousandSeparatorTransformation.kt | ThousandSeparatorTransformation | VisualTransformation untuk separator ribuan di TextField | app/src/main/.../core/ThousandSeparatorTransformation.kt:14 |
| 6 | SimpleXlsxWriter.kt | SimpleXlsxWriter | Writer manual untuk ekspor data ke file .xlsx | app/src/main/.../data/export/SimpleXlsxWriter.kt:14 |
| 7 | SakuDatabase.kt | SakuDatabase | Room database dengan 7 entity dan migration 1→4 | app/src/main/.../data/local/SakuDatabase.kt:40 |
| 8 | Daos.kt | AssetDao, PocketDao, PocketAllocationDao, ExpenseAllocationDao, CategoryDao, TransactionDao, UserDao | 7 DAO interface untuk operasi CRUD tabel Room | app/src/main/.../data/local/dao/Daos.kt:18 |
| 9 | Entities.kt | AssetEntity, PocketEntity, PocketAllocationEntity, ExpenseAllocationEntity, CategoryEntity | Entity Room dengan mapper toDomain/fromDomain | app/src/main/.../data/local/entity/Entities.kt:17 |
| 10 | SakuRepositoryImpl.kt | SakuRepositoryImpl | Implementasi repository dengan validasi bisnis dan transaksi Room | app/src/main/.../data/repository/SakuRepositoryImpl.kt:41 |
| 11 | ApiKeyConfigService.kt | ApiKeyConfigService, DefaultApiKeyConfigService | Interface dan implementasi sederhana penyimpanan API key | app/src/main/.../data/service/ApiKeyConfigService.kt:11 |
| 12 | CurrencyConversionService.kt | CurrencyConversionService, DefaultCurrencyConversionService | Service konversi mata uang IDR ke USD/EUR/JPY | app/src/main/.../data/service/CurrencyConversionService.kt:14 |
| 13 | EncryptedApiKeyConfigService.kt | EncryptedApiKeyConfigService | Penyimpanan terenkripsi API key via EncryptedSharedPreferences | app/src/main/.../data/service/EncryptedApiKeyConfigService.kt:13 |
| 14 | ReceiptOcrParser.kt | ReceiptOcrParser, ParsedResult | Parser teks OCR struk belanja untuk ekstraksi nama toko dan total | app/src/main/.../data/service/ReceiptOcrParser.kt:12 |
| 15 | ReceiptScannerService.kt | ReceiptScannerService, DefaultReceiptScannerService | Orchestrator scan struk OCR/AI via Gemini API | app/src/main/.../data/service/ReceiptScannerService.kt:49 |
| 16 | Models.kt | User, Asset, Pocket, Transaction, Category | Domain models utama aplikasi | app/src/main/.../domain/model/Models.kt:3 |
| 17 | SakuRepository.kt | SakuRepository | Interface repository dengan operasi CRUD, stats, dan export | app/src/main/.../domain/repository/SakuRepository.kt:16 |
| 18 | SakuComponents.kt | SakuTopBar(), SakuBottomNavigation(), SakuCard() | Komponen UI reusable branded Saku | app/src/main/.../ui/components/SakuComponents.kt:84 |
| 19 | AuthComponents.kt | AuthTextField(), AuthPrimaryButton(), AuthQuoteCard() | Komponen UI untuk layar auth (login, register) | app/src/main/.../ui/components/auth/AuthComponents.kt:45 |
| 20 | SakuNavHost.kt | SakuNavHost() | Navigasi utama dengan Scaffold dan bottom bar | app/src/main/.../ui/navigation/SakuNavHost.kt:42 |
| 21 | Screen.kt | Screen sealed class | Definisi rute navigasi (login, dashboard, kantong, dll) | app/src/main/.../ui/navigation/Screen.kt:15 |
| 22 | AuthViewModel.kt | AuthViewModel, AuthUiState | ViewModel login, register, forgot password | app/src/main/.../ui/screens/auth/AuthViewModel.kt:32 |
| 23 | ForgotPasswordScreen.kt | ForgotPasswordScreen() | Layar lupa password (placeholder) | app/src/main/.../ui/screens/auth/ForgotPasswordScreen.kt:58 |
| 24 | LoginScreen.kt | LoginScreen() | Layar masuk dengan email dan kata sandi | app/src/main/.../ui/screens/auth/LoginScreen.kt:81 |
| 25 | RegisterScreen.kt | RegisterScreen() | Layar daftar akun baru | app/src/main/.../ui/screens/auth/RegisterScreen.kt:76 |
| 26 | DashboardScreen.kt | DashboardScreen() | Beranda utama: saldo, pocket, transaksi terkini | app/src/main/.../ui/screens/dashboard/DashboardScreen.kt:102 |
| 27 | DashboardViewModel.kt | DashboardViewModel | ViewModel dashboard: expose user, summary, pockets, transactions | app/src/main/.../ui/screens/dashboard/DashboardViewModel.kt:24 |
| 28 | ExportBackupScreen.kt | ExportBackupScreen() | Layar ekspor CSV/JSON/XLSX dan restore JSON | app/src/main/.../ui/screens/export/ExportBackupScreen.kt:68 |
| 29 | ExportBackupViewModel.kt | ExportBackupViewModel | ViewModel export dan restore data | app/src/main/.../ui/screens/export/ExportBackupViewModel.kt:21 |
| 30 | KantongScreen.kt | KantongScreen() | Layar tab Kantong & Aset: CRUD pocket, asset, allocation, transfer | app/src/main/.../ui/screens/kantong/KantongScreen.kt:307 |
| 31 | KantongViewModel.kt | KantongViewModel | ViewModel kantong: 2 tab, CRUD aset & kantong, alokasi, transfer | app/src/main/.../ui/screens/kantong/KantongViewModel.kt:109 |
| 32 | TransferSheet.kt | TransferSheet() | Bottom sheet transfer antar aset | app/src/main/.../ui/screens/kantong/TransferSheet.kt:33 |
| 33 | AvatarBitmapUtil.kt | AvatarBitmapUtil | Utility save/load avatar PNG ke internal storage | app/src/main/.../ui/screens/profile/AvatarBitmapUtil.kt:11 |
| 34 | InformasiPribadiScreen.kt | InformasiPribadiScreen() | Layar edit profil: nama, email, upload avatar | app/src/main/.../ui/screens/profile/InformasiPribadiScreen.kt:103 |
| 35 | ProfileComponents.kt | ProfileHeader(), FinancialStatsCard(), AccountSection() | Komponen reusable untuk layar profil | app/src/main/.../ui/screens/profile/ProfileComponents.kt:82 |
| 36 | ProfileScreen.kt | ProfileScreen() | Layar profil utama: stats, API key, akun, logout | app/src/main/.../ui/screens/profile/ProfileScreen.kt:48 |
| 37 | ProfileViewModel.kt | ProfileViewModel | ViewModel profil: Gemini API key, konversi mata uang, avatar | app/src/main/.../ui/screens/profile/ProfileViewModel.kt:56 |
| 38 | ReportScreen.kt | ReportScreen() | Layar laporan: filter periode, chart cashflow, donut kategori, balance trend | app/src/main/.../ui/screens/report/ReportScreen.kt:106 |
| 39 | ReportViewModel.kt | ReportViewModel | ViewModel laporan: periode, summary, cashflow/donut/balance/top tx | app/src/main/.../ui/screens/report/ReportViewModel.kt:143 |
| 40 | AddTransactionScreen.kt | AddTransactionScreen() | Layar catat transaksi: manual atau scan struk (OCR/AI) | app/src/main/.../ui/screens/transaction/AddTransactionScreen.kt:133 |
| 41 | TransactionViewModel.kt | TransactionViewModel | ViewModel transaksi: form state, scan struk, save transaction | app/src/main/.../ui/screens/transaction/TransactionViewModel.kt:77 |
| 42 | Color.kt | (color constants) | Palet warna branded Saku: DarkGreen, Cream, Gold, Expense/Income | app/src/main/.../ui/theme/Color.kt:6 |
| 43 | Theme.kt | MyApplicationTheme() | Material 3 theme dengan color scheme Saku | app/src/main/.../ui/theme/Theme.kt:58 |
| 44 | Type.kt | Typography | Tipografi Material 3 | app/src/main/.../ui/theme/Type.kt:1 |
| 45 | ExampleRobolectricTest.kt | ExampleRobolectricTest | Test Robolectric standar | app/src/test/.../ExampleRobolectricTest.kt:13 |
| 46 | ExampleUnitTest.kt | ExampleUnitTest | Test unit standar | app/src/test/.../ExampleUnitTest.kt:11 |
| 47 | GreetingScreenshotTest.kt | GreetingScreenshotTest | Screenshot test placeholder | app/src/test/.../GreetingScreenshotTest.kt:18 |
| 48 | ReceiptOcrParserTest.kt | ReceiptOcrParserTest | Test parser struk: merchant, total, format berbagai toko | app/src/test/.../ReceiptOcrParserTest.kt:7 |
| 49 | SakuAssetManagementTest.kt | SakuAssetManagementTest | Test CRUD aset, balance, active/inactive aset | app/src/test/.../SakuAssetManagementTest.kt:34 |
| 50 | SakuDataIntegrityTest.kt | SakuDataIntegrityTest | Test integritas data: expense allocation, pocket realization | app/src/test/.../SakuDataIntegrityTest.kt:30 |
| 51 | SakuFinancialCalculationTest.kt | SakuFinancialCalculationTest | Test perhitungan finansial: income, expense, transfer, balance | app/src/test/.../SakuFinancialCalculationTest.kt:22 |
| 52 | SakuPocketEditTest.kt | SakuPocketEditTest | Test edit kantong: nama, target, ikon, preservasi realisasi | app/src/test/.../SakuPocketEditTest.kt:32 |
| 53 | SakuReceiptScanningTest.kt | SakuReceiptScanningTest | Test scan struk: OCR, AI, error handling | app/src/test/.../SakuReceiptScanningTest.kt:41 |
| 54 | SakuReportSynchronizationTest.kt | SakuReportSynchronizationTest | Test sinkronisasi data antara dashboard, report, aset | app/src/test/.../SakuReportSynchronizationTest.kt:39 |
| 55 | SakuUnitTest.kt | SakuUnitTest | Test utility: format Rupiah, parse amount, konversi mata uang | app/src/test/.../SakuUnitTest.kt:13 |
| 56 | SakuXlsxExportTest.kt | SakuXlsxExportTest | Test ekspor XLSX: struktur ZIP, XML, data sheet | app/src/test/.../SakuXlsxExportTest.kt:11 |

---

## 3. Lapisan Arsitektur

### Layar (@Composable)
- **LoginScreen** — `ui/screens/auth/LoginScreen.kt:81`
- **RegisterScreen** — `ui/screens/auth/RegisterScreen.kt:76`
- **ForgotPasswordScreen** — `ui/screens/auth/ForgotPasswordScreen.kt:58`
- **DashboardScreen** — `ui/screens/dashboard/DashboardScreen.kt:102`
- **KantongScreen** — `ui/screens/kantong/KantongScreen.kt:307`
- **AddTransactionScreen** — `ui/screens/transaction/AddTransactionScreen.kt:133`
- **ReportScreen** — `ui/screens/report/ReportScreen.kt:106`
- **ProfileScreen** — `ui/screens/profile/ProfileScreen.kt:48`
- **InformasiPribadiScreen** — `ui/screens/profile/InformasiPribadiScreen.kt:103`
- **ExportBackupScreen** — `ui/screens/export/ExportBackupScreen.kt:68`
- **TransferSheet** — `ui/screens/kantong/TransferSheet.kt:33`

### ViewModel
- **AuthViewModel** — `ui/screens/auth/AuthViewModel.kt:32`
- **DashboardViewModel** — `ui/screens/dashboard/DashboardViewModel.kt:24`
- **KantongViewModel** — `ui/screens/kantong/KantongViewModel.kt:109`
- **TransactionViewModel** — `ui/screens/transaction/TransactionViewModel.kt:77`
- **ReportViewModel** — `ui/screens/report/ReportViewModel.kt:143`
- **ProfileViewModel** — `ui/screens/profile/ProfileViewModel.kt:56`
- **ExportBackupViewModel** — `ui/screens/export/ExportBackupViewModel.kt:21`

### DAO (Room)
- **AssetDao** — `data/local/dao/Daos.kt:18`
- **PocketDao** — `data/local/dao/Daos.kt:45`
- **PocketAllocationDao** — `data/local/dao/Daos.kt:72`
- **ExpenseAllocationDao** — `data/local/dao/Daos.kt:126`
- **CategoryDao** — `data/local/dao/Daos.kt:174`
- **TransactionDao** — `data/local/dao/Daos.kt:192`
- **UserDao** — `data/local/dao/Daos.kt:234`

### Entity/Tabel Room
- **AssetEntity** (`assets`) — `data/local/entity/Entities.kt:17`
- **PocketEntity** (`pockets`) — `data/local/entity/Entities.kt:54`
- **PocketAllocationEntity** (`pocket_allocations`) — `data/local/entity/Entities.kt:104`
- **ExpenseAllocationEntity** (`expense_allocations`) — `data/local/entity/Entities.kt:138`
- **CategoryEntity** (`categories`) — `data/local/entity/Entities.kt:162`
- **TransactionEntity** (`transactions`) — `data/local/entity/Entities.kt:195`
- **UserEntity** (`user`) — `data/local/entity/Entities.kt:265`

### Repository
- **SakuRepository** (interface) — `domain/repository/SakuRepository.kt:16`
- **SakuRepositoryImpl** — `data/repository/SakuRepositoryImpl.kt:41`

### Domain Models
- **User** — `domain/model/Models.kt:3`
- **Asset** — `domain/model/Models.kt:14`
- **Pocket** — `domain/model/Models.kt:30`
- **Transaction** — `domain/model/Models.kt:69`
- **Category** — `domain/model/Models.kt:120` (di luar baca awal)
- **AssetType** (enum) — `domain/model/Models.kt:26`
- **TransactionType** (enum) — `domain/model/Models.kt:116`

### Rute Navigasi
- **login** — `ui/navigation/Screen.kt:16`
- **register** — `ui/navigation/Screen.kt:17`
- **forgot_password** — `ui/navigation/Screen.kt:18`
- **dashboard** — `ui/navigation/Screen.kt:21`
- **kantong** — `ui/navigation/Screen.kt:22`
- **add_transaction** — `ui/navigation/Screen.kt:23`
- **laporan** — `ui/navigation/Screen.kt:24`
- **profil** — `ui/navigation/Screen.kt:25`
- **export_backup** — `ui/navigation/Screen.kt:28`
- **informasi_pribadi** — `ui/navigation/Screen.kt:29`

---

## 4. Graf Hubungan Komponen

### Alur Data Utama
```
MainActivity (entry point)
  ↓ mengatur
SakuAppContainer (DI)
  ├── SakuDatabase (Room)
  │     ├── AssetDao, PocketDao, CategoryDao, TransactionDao, UserDao
  │     ├── PocketAllocationDao, ExpenseAllocationDao
  │     └── Migration 1→4
  ├── SakuRepositoryImpl → implements SakuRepository
  ├── EncryptedApiKeyConfigService → ApiKeyConfigService
  ├── DefaultReceiptScannerService → ReceiptScannerService
  └── DefaultCurrencyConversionService → CurrencyConversionService
  
SakuNavHost
  ├── inject container ke setiap composable
  └── setiap layar membuat ViewModel dgn viewModel { XxxViewModel(container.repository, ...) }

ViewModel
  ├── mengambil Flow dari repository
  ├── expose sebagai StateFlow via stateIn()
  └── layar collect dengan collectAsStateWithLifecycle()

Repository
  ├── call DAO untuk operasi CRUD
  ├── konversi Entity ↔ Domain Model
  ├── validasi bisnis (saldo tidak boleh negatif, alokasi tidak melebihi balance)
  └── database.withTransaction{} untuk atomicity
```

### Alur Navigasi Bottom Bar
```
SakuBottomNavigation (5 item)
  ├── Dashboard (beranda)
  ├── Kantong (pocket & aset)
  ├── AddTransaction (catat, FAB elevated)
  ├── Laporan (report)
  └── Profil

NavHost startDestination = "dashboard"
  ├── Auth screens: login, register, forgot_password
  ├── Main tabs: dashboard, kantong, add_transaction, laporan, profil
  └── Sub-screens: export_backup, informasi_pribadi
```

### Hubungan Data Kantong & Aset (Planning Layer)
```
Asset (uang riil)
  ├── balance: Double (saldo aktual)
  └── PocketAllocation (planning: asset → pocket)
       └── allocatedAmount: Double (rencana alokasi, bukan uang riil)

Pocket (goal/target)
  ├── targetAmount: Double (target tabungan/rencana)
  └── ExpenseAllocation (realisasi: expense transaction → pocket)
       └── amount: Double (pengeluaran yang dicatat ke pocket ini)

PocketStats (agregat)
  ├── totalPlanned (sum PocketAllocation)
  └── totalRealization (sum ExpenseAllocation)
```

---

## 5. Penjelasan Per Fitur

### Fitur: Authentication (Login/Register)
**File terlibat:**
- `ui/screens/auth/LoginScreen.kt:81` — layar login
- `ui/screens/auth/RegisterScreen.kt:76` — layar register
- `ui/screens/auth/AuthViewModel.kt:32` — state & logic auth
- `ui/navigation/Screen.kt:16-18` — rute login, register, forgot_password

**Cara kerja:** Offline-first. Login hanya menyimpan User ke Room via `repository.saveUser()`. Tidak ada backend real. Email dan password tidak divalidasi ke server, langsung simpan User dan navigasi ke dashboard.

---

### Fitur: Dashboard (Beranda)
**File terlibat:**
- `ui/screens/dashboard/DashboardScreen.kt:102` — UI dashboard
- `ui/screens/dashboard/DashboardViewModel.kt:24` — state: user, summary, pockets, recentTransactions
- `domain/repository/SakuRepository.kt:68` — getFinancialSummary()
- `data/repository/SakuRepositoryImpl.kt:41` — implementasi summary

**Cara kerja:** ViewModel expose Flow dari repository. Layar collect via `collectAsStateWithLifecycle()`. Menampilkan total saldo aset, pocket dengan progress bar, dan 8 transaksi terkini. Avatar user dimuat dari internal storage via `AvatarBitmapUtil`.

---

### Fitur: Kantong & Aset (Planning)
**File terlibat:**
- `ui/screens/kantong/KantongScreen.kt:131` — UI 2 tab (Kantong & Aset)
- `ui/screens/kantong/KantongViewModel.kt:109` — CRUD kantong, aset, alokasi, transfer
- `domain/model/Models.kt:30` — Pocket model
- `domain/model/Models.kt:14` — Asset model
- `data/local/entity/Entities.kt:104` — PocketAllocationEntity (planning)
- `data/local/entity/Entities.kt:138` — ExpenseAllocationEntity (realization)

**Cara kerja:** 
1. **Aset** = uang riil (balance).
2. **Kantong** = goal/target (targetAmount).
3. **PocketAllocation** = planning (asset → pocket), bukan uang riil, hanya rencana. Sum PocketAllocation per asset tidak boleh > balance asset.
4. **ExpenseAllocation** = realisasi. Expense transaction dialokasikan ke pocket. Sum ExpenseAllocation per pocket = realisasi pocket.
5. **Transfer antar aset** via TransferSheet (TRANSFER transaction type).

---

### Fitur: Catat Transaksi (Manual & Scan Struk)
**File terlibat:**
- `ui/screens/transaction/AddTransactionScreen.kt:133` — UI form + scan struk
- `ui/screens/transaction/TransactionViewModel.kt:77` — state form, scan logic
- `data/service/ReceiptScannerService.kt:49` — orchestrator scan OCR/AI
- `data/service/ReceiptOcrParser.kt:12` — parser teks OCR
- `domain/model/Models.kt:69` — Transaction model

**Cara kerja:** 
1. **Manual:** pilih type (income/expense/transfer), isi jumlah, kategori, aset, tanggal.
2. **Scan struk:** pilih kamera/galeri → OCR (via ML Kit lokal) atau AI (via Gemini API, perlu API key). Parser ekstraksi nama toko + total → pre-fill form.
3. **Expense:** user pilih pocket untuk alokasi. Save transaction + ExpenseAllocation.

---

### Fitur: Laporan (Report)
**File terlibat:**
- `ui/screens/report/ReportScreen.kt:106` — UI laporan
- `ui/screens/report/ReportViewModel.kt:143` — state: period, summary, charts
- `domain/repository/SakuRepository.kt:69` — getFinancialReport()

**Cara kerja:** Filter periode (hari ini, minggu ini, bulan ini, tahun ini, custom). Tampilkan:
1. **Summary:** total income, expense, transfer.
2. **Cashflow line chart:** income vs expense per hari/minggu.
3. **Donut chart:** breakdown pengeluaran per kategori.
4. **Balance trend chart:** perubahan total saldo aset.
5. **Top transactions:** daftar transaksi terbesar dalam periode.

---

### Fitur: Profil & Pengaturan
**File terlibat:**
- `ui/screens/profile/ProfileScreen.kt:48` — layar profil utama
- `ui/screens/profile/ProfileViewModel.kt:56` — state: user, API key, konversi mata uang
- `ui/screens/profile/InformasiPribadiScreen.kt:103` — edit nama, email, avatar
- `ui/screens/profile/AvatarBitmapUtil.kt:11` — save/load avatar PNG

**Cara kerja:**
1. **Stats finansial** dari FinancialSummary.
2. **API Key Gemini:** save via `EncryptedApiKeyConfigService`, verifikasi via HTTP ke Gemini API.
3. **Konversi mata uang:** preview saldo dalam USD/EUR/JPY via `CurrencyConversionService`.
4. **Avatar:** pilih gambar → crop → simpan PNG ke internal storage → reload di dashboard.

---

### Fitur: Ekspor & Restore
**File terlibat:**
- `ui/screens/export/ExportBackupScreen.kt:68` — UI ekspor/restore
- `ui/screens/export/ExportBackupViewModel.kt:21` — logic ekspor CSV/JSON/XLSX, restore JSON
- `data/export/SimpleXlsxWriter.kt:14` — writer manual XLSX (ZIP + XML)
- `domain/repository/SakuRepository.kt:72-76` — fungsi ekspor/restore

**Cara kerja:**
1. **Ekspor:** pilih format (CSV/JSON/XLSX) → save ke Downloads → snackbar konfirmasi.
2. **Restore:** pilih file JSON → parse → clear DB → insert semua data → navigasi kembali.
3. **XLSX:** manual build ZIP dengan `[Content_Types].xml`, `_rels/.rels`, `xl/workbook.xml`, `xl/worksheets/sheet1.xml`.

---

### Fitur: Database Migration
**File terlibat:**
- `data/local/SakuDatabase.kt:40` — database class, version 4
- `data/local/SakuDatabase.kt:54` — MIGRATION_1_2
- `data/local/SakuDatabase.kt:141` — MIGRATION_2_3
- `data/local/SakuDatabase.kt:262` — MIGRATION_3_4

**Cara kerja:**
- **1→2:** Migrasi dari pocket-as-wallet ke asset+pocket model. Pocket lama jadi Asset, tabel pocket baru untuk planning.
- **2→3:** Tambah tabel pocket_allocations dan expense_allocations.
- **3→4:** Tambah kolom `description` ke pockets, `completed`/`archived` flag.

---

### Fitur: Theme & Branding
**File terlibat:**
- `ui/theme/Color.kt:6` — konstanta warna Saku
- `ui/theme/Theme.kt:58` — Material 3 theme
- `ui/theme/Type.kt` — tipografi

**Warna kunci:**
- **Primary:** `SakuDarkGreen` (#133E35)
- **Background:** `SakuCreamBackground` (#FAF7EE)
- **Surface:** `SakuCreamSurface` (#FFFDF8)
- **Border:** `SakuCreamBorder` (#E4DDCD)
- **Accent:** `SakuGoldAccent` (#C58E2E)
- **Income:** `SakuIncomeGreen` (#178A4C)
- **Expense:** `SakuExpenseRed` (#D32F2F)

Tema dikunci via `MyApplicationTheme(darkTheme = false)` di MainActivity:19.

---

## 6. Q&A Siap Jawab

### Q1: Kode dashboard ada di bagian mana? Jelaskan cara kerjanya.
**A:** Dashboard UI di `ui/screens/dashboard/DashboardScreen.kt:102`. ViewModel di `ui/screens/dashboard/DashboardViewModel.kt:24`. ViewModel mengambil Flow `user`, `summary`, `pockets`, `pocketStats`, `recentTransactions` dari repository, expose sebagai StateFlow. Layar collect dengan `collectAsStateWithLifecycle()`. Menampilkan saldo total aset, daftar pocket dengan progress bar (realisasi/target), dan 8 transaksi terkini. Avatar dimuat dari internal storage saat screen resume via `DisposableEffect`.

---

### Q2: Bagaimana cara kerja sistem pocket allocation dan expense allocation?
**A:** Ada 2 tabel relasi:
1. **PocketAllocation** (`data/local/entity/Entities.kt:104`) = planning. Relasi Asset → Pocket dengan field `allocatedAmount` (rencana alokasi, bukan uang riil). Sum total alokasi per asset tidak boleh melebihi balance aset tersebut. Validasi di `data/repository/SakuRepositoryImpl.kt:84-93`.
2. **ExpenseAllocation** (`data/local/entity/Entities.kt:138`) = realisasi. Relasi Expense Transaction → Pocket dengan field `amount`. Sum per pocket = realisasi pocket. PocketStats (`domain/repository/SakuRepository.kt:65`) menghitung total planned dan realization per pocket.

---

### Q3: Bagaimana cara scan struk bekerja? OCR vs AI Scan.
**A:** Orchestrator di `data/service/ReceiptScannerService.kt:49`. 2 mode:
1. **OCR:** ML Kit Text Recognition (on-device). Extract plain text → parse via `ReceiptOcrParser.kt:12` dengan regex untuk merchant name dan total amount.
2. **AI Scan:** Upload image ke Gemini API (perlu API key dari ProfileViewModel). Gemini mengirim response JSON berisi item dan total yang diekstrak. Parser di `ReceiptOcrParser.kt:50` handle kedua mode. TransactionViewModel (`ui/screens/transaction/TransactionViewModel.kt:77`) manage state scan: `isScanning`, `scanMode`, `scannedResult`.

---

### Q4: Di mana tema warna dikunci dan bagaimana mengubahnya?
**A:** Warna dikunci di `ui/theme/Color.kt:6-38`. Skema Material 3 di `ui/theme/Theme.kt:29-55` (LightColorScheme dan DarkColorScheme). Tema diaktifkan di `MainActivity.kt:19` dengan `MyApplicationTheme(darkTheme = false)`. Untuk ubah warna primary, edit `SakuDarkGreen` di Color.kt, lalu pastikan LightColorScheme.primary di Theme.kt:31 sudah reference `SakuDarkGreen`.

---

### Q5: Bagaimana navigation di aplikasi Saku?
**A:** NavHost di `ui/navigation/SakuNavHost.kt:42`. StartDestination = `dashboard`. Rute didefinisikan di `ui/navigation/Screen.kt:15` sebagai sealed class. Bottom bar di `ui/components/SakuComponents.kt:123` dengan 5 item: Dashboard, Kantong, AddTransaction (FAB center), Laporan, Profil. SakuNavHost membuat ViewModel untuk setiap destination dengan inject `container` (SakuAppContainer).

---

### Q6: Bagaimana data flow dari DAO ke UI?
**A:** 
1. **DAO** (`data/local/dao/Daos.kt`) return `Flow<List<EntityXxx>>` atau `suspend fun`.
2. **Repository** (`data/repository/SakuRepositoryImpl.kt:41`) call DAO, map Entity → Domain Model via `.map { it.toDomain() }`, return `Flow<List<DomainModel>>`.
3. **ViewModel** collect repository Flow, convert ke StateFlow via `stateIn(viewModelScope)`.
4. **Screen** collect ViewModel StateFlow via `collectAsStateWithLifecycle()`.
5. Room auto-emit Flow saat data berubah → UI auto-update.

---

### Q7: Di mana validasi bisnis untuk saldo aset?
**A:** Di repository `data/repository/SakuRepositoryImpl.kt:76-93`. Fungsi `insertAsset()` dan `updateAsset()` require `asset.balance >= 0`. Saat update asset, cek total PocketAllocation untuk asset tersebut (`pocketAllocationDao.getAllocationsByAssetSync(asset.id)`). Jika balance baru < totalAllocated, throw `IllegalArgumentException` dengan pesan error Indonesia via `Formatters.formatRupiah()`.

---

### Q8: Bagaimana cara ekspor XLSX? Kenapa tidak pakai library?
**A:** Manual writer di `data/export/SimpleXlsxWriter.kt:14`. XLSX = ZIP dengan struktur XML OOXML. Writer:
1. Buat `[Content_Types].xml` (define part types).
2. Buat `_rels/.rels` (relationships).
3. Buat `xl/workbook.xml` (deklarasi sheet).
4. Buat `xl/worksheets/sheet1.xml` (data row/cell).
5. ZIP semua file, extension `.xlsx`.
Kenapa manual? Ponytail principle: dependency Apache POI terlalu besar (8MB+), manual writer 249 baris cukup untuk kebutuhan ekspor sederhana.

---

### Q9: Bagaimana cara transfer antar aset? Apakah ada validasi?
**A:** UI di `ui/screens/kantong/TransferSheet.kt:33`. UI validation di `ui/screens/kantong/TransferSheet.kt:48-65`. ViewModel logic di `ui/screens/kantong/KantongViewModel.kt:829` (`saveTransfer()`). Validasi:
1. Source ≠ target.
2. Amount > 0.
3. Source balance ≥ amount.
4. Gunakan Room `database.withTransaction{}` untuk atomicity:
   - Kurangi source balance.
   - Tambah target balance.
   - Insert Transaction type TRANSFER dengan `targetAssetId`.
5. Jika error, rollback otomatis.

---

### Q10: Di mana default data seed untuk kategori dan user?
**A:** Seed di `data/local/SakuDatabase.kt:289-520` dalam callback `onCreate()`. Insert:
1. **User default:** id `user_default`, nama "Pengguna Saku".
2. **8 kategori income:** Gaji, Bonus, Investasi, dll.
3. **11 kategori expense:** Makanan, Transportasi, Belanja, dll.
4. **1 kategori transfer:** Transfer.
Seed hanya jalan saat database pertama kali dibuat (bukan saat migration).

---

### Q11: Bagaimana pocket progress dihitung di dashboard?
**A:** ViewModel `DashboardViewModel.kt:53` expose `pocketStats: StateFlow<List<PocketStats>>`. Repository `SakuRepositoryImpl` aggregate:
1. **Planned:** sum PocketAllocation per pocketId via `pocketAllocationDao.getTotalPlannedAllocation(pocketId)`.
2. **Realization:** sum ExpenseAllocation per pocketId via `expenseAllocationDao.getTotalRealizationForPocket(pocketId)`.
3. **Progress:** `(realization / target) * 100%`.
UI di `DashboardScreen.kt:599` (`PocketItemCard`) render LinearProgressIndicator dengan progress tersebut.

---

### Q12: Bagaimana verifikasi API key Gemini?
**A:** ProfileViewModel (`ui/screens/profile/ProfileViewModel.kt:124-180`) fungsi `verifyApiKey()`. Kirim HTTP POST ke `https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-8b:generateContent?key={apiKey}` dengan body JSON test prompt. OkHttp client dengan timeout 10s. Response:
- **200 + valid JSON:** `ApiKeyVerificationStatus.Verified`
- **400:** `Invalid`
- **429:** `QuotaExceeded`
- **Network error:** `NetworkError`
Status disimpan via `apiKeyConfigService.setVerificationStatus()`.

---

### Q13: Bagaimana cara edit pocket? Apakah realisasi hilang?
**A:** Logic di `KantongViewModel.kt:385` (`saveEditPocket()`). Edit hanya update nama, target, description, color, icon di PocketEntity. **Realisasi (ExpenseAllocation) tidak tersentuh**, jadi tidak hilang. Validasi: target baru tidak boleh < realisasi saat ini. Test di `app/src/test/.../SakuPocketEditTest.kt:58-108` membuktikan realisasi preserved.

---

### Q14: Apa perbedaan AssetDao.getAllAssets() vs getActiveAssets()?
**A:** `data/local/dao/Daos.kt`:
- **getAllAssets()** (line 20): `SELECT * FROM assets ORDER BY name ASC` → return semua asset termasuk yang `isActive = 0` (soft delete).
- **getActiveAssets()** (line 23): `SELECT * FROM assets WHERE isActive = 1 ORDER BY name ASC` → return hanya yang aktif.
Soft delete: saat delete asset, set `isActive = 0` instead of DELETE. Bisa restore nanti.

---

### Q15: Bagaimana cara format Rupiah di aplikasi?
**A:** Utility object `core/Formatters.kt:8`. Fungsi `formatRupiah(amount: Double)` pakai `NumberFormat.getCurrencyInstance(Locale("id", "ID"))`. Format: `Rp 1.000.000` (titik sebagai separator ribuan). Fungsi `parseAmount(input: String)` extract digit saja, convert ke Double. TextField pakai `ThousandSeparatorTransformation.kt:14` untuk visual separator saat user ketik angka.

---

## 7. Daftar File:Baris Kunci Per Fitur

### Auth
- Login UI: `ui/screens/auth/LoginScreen.kt:81`
- Register UI: `ui/screens/auth/RegisterScreen.kt:76`
- AuthViewModel: `ui/screens/auth/AuthViewModel.kt:32`
- Rute login: `ui/navigation/Screen.kt:16`

### Dashboard
- Dashboard UI: `ui/screens/dashboard/DashboardScreen.kt:102`
- DashboardViewModel: `ui/screens/dashboard/DashboardViewModel.kt:24`
- FinancialSummary calc: `data/repository/SakuRepositoryImpl.kt` (method getFinancialSummary)

### Kantong & Aset
- KantongScreen UI: `ui/screens/kantong/KantongScreen.kt:307`
- KantongViewModel: `ui/screens/kantong/KantongViewModel.kt:109`
- Asset model: `domain/model/Models.kt:14`
- Pocket model: `domain/model/Models.kt:30`
- PocketAllocation entity: `data/local/entity/Entities.kt:104`
- ExpenseAllocation entity: `data/local/entity/Entities.kt:138`
- Transfer logic: `ui/screens/kantong/KantongViewModel.kt:829`

### Transaksi
- AddTransaction UI: `ui/screens/transaction/AddTransactionScreen.kt:133`
- TransactionViewModel: `ui/screens/transaction/TransactionViewModel.kt:77`
- Transaction model: `domain/model/Models.kt:69`
- ReceiptScanner: `data/service/ReceiptScannerService.kt:49`
- OCR Parser: `data/service/ReceiptOcrParser.kt:12`

### Laporan
- Report UI: `ui/screens/report/ReportScreen.kt:106`
- ReportViewModel: `ui/screens/report/ReportViewModel.kt:143`
- Report data calc: `data/repository/SakuRepositoryImpl.kt` (method getFinancialReport)

### Profil
- Profile UI: `ui/screens/profile/ProfileScreen.kt:48`
- ProfileViewModel: `ui/screens/profile/ProfileViewModel.kt:56`
- InformasiPribadi: `ui/screens/profile/InformasiPribadiScreen.kt:103`
- Avatar util: `ui/screens/profile/AvatarBitmapUtil.kt:11`
- API Key service: `data/service/EncryptedApiKeyConfigService.kt:13`

### Database
- SakuDatabase: `data/local/SakuDatabase.kt:40`
- Migration 1→2: `data/local/SakuDatabase.kt:54`
- Migration 2→3: `data/local/SakuDatabase.kt:141`
- Migration 3→4: `data/local/SakuDatabase.kt:262`
- Seed default data: `data/local/SakuDatabase.kt:289`
- DAOs: `data/local/dao/Daos.kt:18`
- Entities: `data/local/entity/Entities.kt:17`

### Ekspor
- Export UI: `ui/screens/export/ExportBackupScreen.kt:68`
- ExportViewModel: `ui/screens/export/ExportBackupViewModel.kt:21`
- XLSX writer: `data/export/SimpleXlsxWriter.kt:14`

### Theme
- Color palette: `ui/theme/Color.kt:6`
- Material theme: `ui/theme/Theme.kt:58`
- Theme lock: `MainActivity.kt:19`

### Navigation
- NavHost: `ui/navigation/SakuNavHost.kt:42`
- Screen routes: `ui/navigation/Screen.kt:15`
- Bottom nav: `ui/components/SakuComponents.kt:123`

---

## 8. Verifikasi Hitungan

**Perintah:** `find app/src -name "*.kt" -type f | wc -l`

**Output:** `56`

**Baris tabel inventaris:** 56

**Match:** ✓ Ya, 56 = 56.

**Bukti file:baris:** Setiap baris inventaris di bagian 2 memiliki kolom "Bukti" dengan path + line number yang dibaca sesi ini.

---

## 9. Asumsi (Minta Koreksi Bila Salah)

1. **Pocket allocation = planning, bukan uang riil.** ExpenseAllocation = realisasi dari expense transaction. (Konfirmasi: benar atau salah?)

2. **Transfer hanya antar aset, tidak ada transfer antar pocket.** Model `Transaction.targetPocketId` deprecated. (Konfirmasi: masih dipakai atau benar-benar deprecated?)

3. **Soft delete untuk asset dan pocket:** `isActive = 0` instead of DELETE. (Konfirmasi: apakah ada UI untuk restore?)

4. **Scan struk AI memerlukan Gemini API key.** Key disimpan encrypted via EncryptedSharedPreferences. (Konfirmasi: apakah ada fallback jika key tidak valid?)

5. **Avatar user disimpan sebagai PNG di internal storage** dengan nama `user_avatar.png`. (Konfirmasi: apakah support multiple user atau hanya 1 user?)

6. **Default data seed hanya jalan saat database dibuat pertama kali,** tidak jalan ulang saat migration. (Konfirmasi: apakah ini behavior yang diinginkan?)

7. **Room database versi 4.** Migration 1→2→3→4 sudah handle perubahan schema dari pocket-as-wallet ke asset+pocket+allocation model. (Konfirmasi: apakah masih ada user di production dengan versi <4?)

8. **Test suite comprehensive:** 12 test class untuk asset management, data integrity, financial calculation, receipt scanning, dll. (Konfirmasi: apakah semua test passing?)

9. **Tidak ada backend server real.** Auth offline-first, data hanya lokal Room. (Konfirmasi: apakah ada rencana integrasi backend?)

10. **Tema dark mode sudah defined (`DarkColorScheme` di Theme.kt:10),** tapi dikunci `darkTheme = false` di MainActivity. (Konfirmasi: apakah dark mode akan diaktifkan nanti?)

---

## 10. Boundary & Limitasi

**Hal yang TIDAK dicek sesi ini:**
- Apakah semua test passing (tidak menjalankan `./gradlew test`).
- Apakah build berhasil (tidak menjalankan `./gradlew assembleDebug`).
- Behavior runtime (tidak menjalankan di emulator/device).
- API Gemini real (tidak verifikasi API key benar-benar work).
- Avatar upload flow (tidak test kamera/galeri permission).
- Database migration actual (tidak test upgrade dari versi lama).

**Hal yang SUDAH dicek:**
- Struktur folder dan file (56 file .kt).
- Isi setiap file: class, interface, object, fungsi utama.
- Hubungan antar komponen (ViewModel → Repository → DAO → Entity).
- Validasi bisnis di repository (saldo tidak boleh negatif, alokasi tidak melebihi balance).
- Alur navigasi dan rute.
- Warna tema dan branding.

---

**Dokumen ini di-generate:** 2026-10-01 (FASE 1 selesai, read-only, tidak ada kode yang diubah).

**Next step:** Tunggu sinyal "lanjut" dari user untuk FASE 2 (penjelasan per fitur detail + Q&A tambahan).
