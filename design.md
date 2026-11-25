---
name: SAKU — Sistem Alokasi Keuangan & Pemindai Struk
description: Dokumentasi Design System & UI/UX Audit untuk SAKU Mobile Fintech App berdasarkan 16 Frame FigCraft dan implementasi Jetpack Compose di Android Studio.
version: 1.0.0
source: visual-audit
mode: both
default-mode: light
style-direction: Organic Fintech + Material Design 3 + Bento Card Grid
mood: trustworthy, calm, sophisticated
comparable-to: Jago, Wise, Linear, Monzo
screen-type: Mobile Application Suite (16 Frames)
target-platform: Android (Jetpack Compose + Navigation 3 + Material 3) & Web Preview (Tailwind v4)

# ============================================
# DESIGN TOKENS
# ============================================

colors:
  # Brand
  primary: "#114232"             # Deep Forest Green — main CTA, hero card bg, active bottom nav
  primary-hover: "#0c3024"       # 1 step deeper forest green
  primary-subtle: "#eaf3ef"      # ~10% tint — active chip, selected state, verified badge bg
  secondary: "#10b981"           # Vibrant Emerald — positive growth indicator, scan laser, scanner active
  secondary-hover: "#059669"
  
  # Canvas
  background: "#f7f5ee"          # Light Warm Cream Canvas — calm, non-sterile paper feel
  surface: "#ffffff"             # Pure White Card / Panel — high contrast vs warm canvas
  surface-2: "#f3f0e6"           # Nested surface — input container, segment track, soft chip bg
  surface-hover: "#fbfaf7"       # List row hover
  
  # Text
  text-primary: "#142921"        # Dark Forest Charcoal — 14.08:1 contrast on #f7f5ee (WCAG AAA)
  text-secondary: "#52605b"      # Muted Slate Green — 5.2:1 contrast on #f7f5ee (WCAG AA)
  text-muted: "#9ca3af"          # Placeholder, disabled label, inactive tab
  text-on-primary: "#ffffff"     # White text on primary Deep Forest (11.29:1, WCAG AAA)
  
  # Lines & Borders
  border: "#eae7dc"              # Hairline Warm Sand Border (1px)
  border-strong: "#d1cdbf"       # Active input border, card dividers
  
  # Semantic
  success: "#10b981"             # Pemasukan / Saldo bertambah / OCR Aktif
  success-subtle: "#e6f7f0"
  warning: "#f59e0b"             # Waspada kuota kantong (>85%) / Pending
  warning-subtle: "#fef3c7"
  error: "#dc2626"               # Pengeluaran debit / Overbudget / Hapus aksi
  error-subtle: "#fff5f5"
  info: "#3b82f6"                # Rekening Bank / Tabungan / Cloud AI
  info-subtle: "#eff6ff"

colors-dark:
  primary: "#10b981"             # Vibrant Emerald CTA in dark mode
  primary-hover: "#34d399"
  primary-subtle: "#064e3b"
  secondary: "#114232"
  background: "#0c1310"          # AMOLED Dark Forest Canvas
  surface: "#14231d"             # Dark card surface
  surface-2: "#1d3229"           # Nested dark card
  surface-hover: "#233d32"
  text-primary: "#f3f5f4"        # Crisp light text
  text-secondary: "#9cb1a7"      # Soft sage secondary
  text-muted: "#5b7569"          # Inactive icon/label
  text-on-primary: "#062217"     # Dark text on emerald CTA
  border: "#203a2f"
  border-strong: "#2e5243"
  success: "#34d399"
  success-subtle: "#064e3b"
  warning: "#fbbf24"
  warning-subtle: "#451a03"
  error: "#f87171"
  error-subtle: "#450a0a"
  info: "#60a5fa"
  info-subtle: "#172554"

typography:
  font-family-display: "'Inter', system-ui, -apple-system, BlinkMacSystemFont, sans-serif"
  font-family-body: "'Inter', system-ui, -apple-system, BlinkMacSystemFont, sans-serif"
  font-family-mono: "'JetBrains Mono', ui-monospace, SFMono-Regular, Menlo, monospace"
  
  display:
    size: "1.625rem"            # 26px — Big KPI Balances
    weight: 700
    line-height: 1.15
    letter-spacing: "-0.025em"
  h1:
    size: "1.5rem"              # 24px — Screen Titles ("Kantong & Aset", "Laporan")
    weight: 700
    line-height: 1.2
    letter-spacing: "-0.02em"
  h2:
    size: "1.375rem"            # 22px — Section Headers, Auth Headlines
    weight: 700
    line-height: 1.25
    letter-spacing: "-0.015em"
  h3:
    size: "1.125rem"            # 18px — Card Headlines, Sub-section Titles
    weight: 600
    line-height: 1.3
  h4:
    size: "1.0625rem"           # 17px — Modal / Dialog Headers
    weight: 600
    line-height: 1.35
  body-lg:
    size: "1rem"                # 16px — Standard input text, lead paragraphs
    weight: 400
    line-height: 1.5
  body:
    size: "0.875rem"            # 14px — Standard UI text, list item labels
    weight: 400
    line-height: 1.5
  body-sm:
    size: "0.8125rem"           # 13px — Secondary metadata, timestamps, helper text
    weight: 400
    line-height: 1.45
  label:
    size: "0.75rem"             # 12px — Field labels, uppercase trackers, chip text
    weight: 500
    line-height: 1.4
    letter-spacing: "0.01em"
  caption:
    size: "0.6875rem"           # 11px — Bottom bar captions, badge tags
    weight: 500
    line-height: 1.35
  numeric:
    feature: "tabular-nums"
    weight: 600

spacing:
  base: 4                       # 4px base grid with 8px rhythm
  scale:
    "0": "0"
    "0.5": "0.125rem"          # 2px
    "1": "0.25rem"             # 4px
    "1.5": "0.375rem"          # 6px
    "2": "0.5rem"              # 8px
    "3": "0.75rem"             # 12px
    "4": "1rem"                # 16px
    "5": "1.25rem"             # 20px
    "6": "1.5rem"              # 24px
    "7": "1.75rem"             # 28px
    "8": "2rem"                # 32px
    "10": "2.5rem"             # 40px
    "12": "3rem"               # 48px
    "16": "4rem"               # 64px
    "20": "5rem"               # 80px
    "24": "6rem"               # 96px

radius:
  none: "0"
  xs: "0.375rem"               # 6px
  sm: "0.5rem"                 # 8px
  md: "0.875rem"               # 14px — buttons, chips, form inputs
  lg: "1.125rem"               # 18px — card containers, inner panels
  xl: "1.25rem"                # 20px — main content cards, modal dialogs
  "2xl": "1.75rem"             # 28px — hero balance card, floating containers
  full: "9999px"               # pill badges, circular action buttons, FAB

shadow:
  none: "none"
  xs: "0 1px 2px rgba(17, 66, 50, 0.04)"
  sm: "0 2px 6px rgba(17, 66, 50, 0.05)"
  md: "0 4px 12px rgba(17, 66, 50, 0.07)"
  lg: "0 10px 24px rgba(17, 66, 50, 0.10)"
  xl: "0 20px 40px rgba(17, 66, 50, 0.14)"
  focus: "0 0 0 3px rgba(17, 66, 50, 0.20)"

motion:
  duration-fast: "150ms"
  duration-base: "220ms"
  duration-slow: "320ms"
  easing-standard: "cubic-bezier(0.4, 0, 0.2, 1)"
  easing-emphasized: "cubic-bezier(0.2, 0, 0, 1)"

layout:
  max-content-width: "480px"   # Standard mobile width max constraint
  topbar-height: "56px"
  bottom-nav-height: "68px"
  page-padding-x: "1rem"       # 16px horizontal margins
  page-padding-y: "1.25rem"    # 20px vertical margins
  card-padding: "1.25rem"      # 20px padding inside white cards
  section-gap: "1.25rem"       # 20px vertical spacing between cards

---

# 📖 OVERVIEW & FILOSOFI DESAIN SAKU
SAKU ("Sistem Alokasi Keuangan & Pemindai Struk") mengusung filosofi **Organic Fintech & Mindful Budgeting**. Berbeda dari aplikasi perbankan korporat yang kaku dan dingin, SAKU menggabungkan palet hijau hutan (*Deep Forest* `#114232`), latar belakang krim hangat (*Warm Cream Canvas* `#f7f5ee`), dan aksen zamrud (*Vibrant Emerald* `#10b981`) untuk menciptakan pengalaman finansial yang menenangkan, menghilangkan kecemasan uang (*financial anxiety*), dan mengedukasi disiplin alokasi pos anggaran.

---

# 📱 16 FRAME FIGCRAFT & IMPLEMENTASI KODE ANDROID

| No | Frame ID & Nama Desain FigCraft | Komponen & Fitur Kunci | Berkas Implementasi Android Studio |
|---|---|---|---|
| 1 | **Frame 133: Beranda (Dashboard)** | Hero Balance Card (visibilitas saldo, pemasukan/pengeluaran), Quick Actions, Kantong Saya (progress bar), Riwayat Transaksi, Brand Info Dialog. | [MainScreen.kt](file:///d:/PART2/app/src/main/kotlin/com/saku/app/ui/main/MainScreen.kt) |
| 2 | **Frame 380: Page Kantong** | Segmented tab slider `[Kantong Anggaran \| Aset Saya]`, filter chip kategori, kartu anggaran dengan remaining days & target progress. | [PocketsAndAssetsScreen.kt](file:///d:/PART2/app/src/main/kotlin/com/saku/app/ui/pockets/PocketsAndAssetsScreen.kt) |
| 3 | **Frame 469: Page Aset** | Net worth hero card, multi-segment asset allocation progress bar (Tunai, Investasi, Emas, Properti), list kelas aset & yield bulanan. | [PocketsAndAssetsScreen.kt](file:///d:/PART2/app/src/main/kotlin/com/saku/app/ui/pockets/PocketsAndAssetsScreen.kt) |
| 4 | **Frame 1823: Page Buat Kantong** | Nominal target card (+100rb, +500rb, +1jt chips), nama kantong, category picker, color dot picker (6 warna), icon picker (8 ikon), switch alert 80%, siklus reset. | [CreatePocketScreen.kt](file:///d:/PART2/app/src/main/kotlin/com/saku/app/ui/pockets/CreatePocketScreen.kt) |
| 5 | **Frame 1888: Page Tambah Aset** | Valuasi aset card, 2x2 grid kelas aset, nama platform/akun, target yield tahunan chips, switch likuiditas aset, submit button. | [AddAssetScreen.kt](file:///d:/PART2/app/src/main/kotlin/com/saku/app/ui/assets/AddAssetScreen.kt) |
| 6 | **Frame 729: Scan Struk (OCR)** | Mode switcher `[Scan Struk OCR \| Input Manual]`, OCR engine switch `[Offline ML Kit \| Gemini AI]`, kamera viewfinder dengan scanning laser hijau animasi, kartu hasil OCR struk. | [AddTransactionScreen.kt](file:///d:/PART2/app/src/main/kotlin/com/saku/app/ui/transaction/AddTransactionScreen.kt) |
| 7 | **Frame 808: Input Manual** | Toggle Pengeluaran vs Pemasukan, nominal picker (+10rb, +50rb, +100rb chips), pemilihan kantong alokasi, tanggal & waktu picker, textarea catatan transaksi. | [AddTransactionScreen.kt](file:///d:/PART2/app/src/main/kotlin/com/saku/app/ui/transaction/AddTransactionScreen.kt) |
| 8 | **Frame 952: Page Laporan** | Periode selector `[Mingguan \| Bulanan \| Tahunan]`, bar chart tren pengeluaran bulanan (Jun-Nov) dengan highlight bulan aktif, donut breakdown kategori, list riwayat. | [ReportsScreen.kt](file:///d:/PART2/app/src/main/kotlin/com/saku/app/ui/reports/ReportsScreen.kt) |
| 9 | **Frame 2008: Detail Transaksi** | Hero receipt card, tabel rincian transaksi (status, waktu, kategori, kantong, metode, catatan), kartu lampiran foto struk dengan verified badge, tombol edit & hapus. | [TransactionDetailScreen.kt](file:///d:/PART2/app/src/main/kotlin/com/saku/app/ui/transaction/TransactionDetailScreen.kt) |
| 10 | **Frame 1311: Page Profil** | Monogram avatar `DP`, stats strip finansial (3 kolom), toggle Biometrik & Notifikasi Anggaran, navigation menu rows, pill tema & bahasa, tombol keluar akun. | [ProfileScreen.kt](file:///d:/PART2/app/src/main/kotlin/com/saku/app/ui/profile/ProfileScreen.kt) |
| 11 | **Frame 1436: Edit Profil & API** | Avatar dengan camera badge & "Ubah Foto Profil", form data pribadi (Nama, Email terverifikasi), form Gemini API Key (show/hide, Cek Koneksi, Tempel, Hapus, status live). | [EditProfileAndApiScreen.kt](file:///d:/PART2/app/src/main/kotlin/com/saku/app/ui/profile/EditProfileAndApiScreen.kt) |
| 12 | **Frame 1525: Page Login** | Logo badge resmi SAKU, input email & password, remember me checkbox, "Lupa sandi?" link, tombol masuk, tombol sidik jari, tombol Google sign-in, footer daftar. | [AuthScreens.kt](file:///d:/PART2/app/src/main/kotlin/com/saku/app/ui/auth/AuthScreens.kt) (`LoginScreen`) |
| 13 | **Frame 1707: Daftar Akun (Register)** | Intro "Mulai Langkah Finansialmu", input nama, email, buat sandi, indikator kriteria sandi 8 karakter, konfirmasi sandi, checkbox syarat & ketentuan, tombol submit. | [AuthScreens.kt](file:///d:/PART2/app/src/main/kotlin/com/saku/app/ui/auth/AuthScreens.kt) (`RegisterScreen`) |
| 14 | **Frame 1706: Lupa Sandi** | Lock badge zamrud, input email terdaftar, info callout OTP (5 menit), tombol kirim kode OTP, link kembali ke login. | [AuthScreens.kt](file:///d:/PART2/app/src/main/kotlin/com/saku/app/ui/auth/AuthScreens.kt) (`ForgotPasswordScreen`) |
| 15 | **Frame 2156: Verifikasi OTP** | Key badge zamrud, 6 digit input boxes individual (`[4][8][2][9][1][0]`), countdown kirim ulang (00:45), input sandi baru & konfirmasi, tombol simpan sandi & masuk. | [AuthScreens.kt](file:///d:/PART2/app/src/main/kotlin/com/saku/app/ui/auth/AuthScreens.kt) (`OtpVerificationScreen`) |
| 16 | **Brand System & Iconography** | Logo master 512x512, launcher adaptive mipmap (mdpi sampai xxxhdpi), palet warna semantik, typography scaling. | [saku_app_icon.png](file:///d:/PART2/app/src/main/res/drawable/saku_app_icon.png) & [Color.kt](file:///d:/PART2/app/src/main/kotlin/com/saku/app/ui/theme/Color.kt) |

---

# 🚫 ATURAN DESAIN YANG TIDAK BOLEH DILANGGAR (STRICT ZERO AI-SLOP)
1. **DILARANG** menggunakan background putih salju murni `#ffffff` untuk seluruh body/canvas (wajib gunakan canvas warm cream `#f7f5ee`).
2. **DILARANG** menggunakan emoji sebagai ikon navigasi struktural atau tombol (wajib SVG monoline presisi seperti Material Icons / Lucide).
3. **DILARANG** menggunakan bayangan abu-abu hitam pekat (wajib soft tinted shadow kelipatan `#114232`).
4. **DILARANG** mematikan focus outline atau menghilangkan status interaktif hover/active pada tombol form.
5. **DILARANG** menggunakan font display dekoratif atau serif pada data finansial; wajib konsisten menggunakan sans-serif geometris (`Inter`) dengan `tabular-nums`.

# ✅ STANDAR KUALITAS YANG WAJIB DIPENUHI
1. Gunakan `tabular-nums` pada seluruh teks yang menampilkan nominal mata uang (`Rp ...`), rasio persentase (`%`), dan tanggal.
2. Sediakan hit area minimal 44x44px (iOS/web touch) atau 48x48dp (Android) pada seluruh kontrol navigasi, tombol back, filter chip, dan icon button.
3. Format nominal pengeluaran selalu diawali tanda minus merah (`-Rp ...`) dan pemasukan selalu diawali tanda plus hijau (`+Rp ...`).
4. Seluruh card wajib memiliki border hairline 1px `#eae7dc` untuk batas kontras yang terdefinisi di layar OLED maupun LCD.

---

# 🎨 SISTEM IKONOGRAFI KONSISTEN (SAKU UNIFIED ICONOGRAPHY SYSTEM)

Untuk menjaga konsistensi visual 100% di seluruh aplikasi SAKU, seluruh tombol, navigasi, dan indikator grafis wajib menggunakan **satu tipe keluarga ikon terpadu**: **SAKU Handcrafted Monoline System** (berbasis standar vektor geometris 24dp yang setara dengan *Lucide Icons / Material Symbols Rounded* dengan stroke 1.75dp – 2.0dp, round cap, dan round join).

Semua aset ikon dipusatkan secara deklaratif di `com.saku.app.ui.theme.SakuIcons` dan menggunakan file Android Vector Drawable (`res/drawable/ic_saku_...xml`).

### 📐 Aturan & Spesifikasi Teknis Desain Ikon:
- **Family / Style:** `Outlined Monoline Vector` (setara dengan *Lucide Icons* / *Material Symbols Rounded*).
- **Stroke Uniformity:** Ketebalan visual konsisten `1.75dp` untuk ikon umum, dan `2.0dp` untuk tanda plus (+) FAB & tanda centang (✓).
- **Bounding Box Canvas:** `24×24dp` standar (`viewportWidth="24"`, `viewportHeight="24"`).
- **Line Caps & Joins:** `strokeLineCap="round"` dan `strokeLineJoin="round"` (seluruh sudut dan ujung garis membulat lembut, anti-kaku).
- **Strict Anti-AI-Slop Bans:**
  - ❌ Dilarang menggunakan emoji untuk tombol, navigasi, tab, maupun kategori pos anggaran.
  - ❌ Dilarang mencampur ikon bergaya *Filled / Solid* dengan *Outlined* di layar yang sama.
  - ❌ Dilarang menggunakan ikon dengan ketebalan garis yang berbeda-beda (*mixed stroke weights*).
- **Palet Warna Ikon (Color Tints):**
  - **Primer Aktif / Struktural:** `SakuDarkGreen` (`#114232`)
  - **Aksen / Positif / Sukses:** `SakuEmerald` (`#10B981`)
  - **Netral / Sekunder:** `SakuTextSecondary` (`#52605B`)
  - **Muted / Inaktif:** `SakuTextMuted` (`#9CA3AF`)
  - **Peringatan / Warning:** `SakuAmber` (`#F59E0B`)
  - **Pengeluaran / Destruktif:** `SakuExpenseRed` (`#DC2626`)
  - **Kontras Tinggi pada Button Primer:** `White` (`#FFFFFF`)

---

### 📂 KATALOG IKON RESMI & PEMETAAN TOMBOL APLIKASI (SAKU OFFICIAL ICON REPOSITORY)

Berikut adalah katalog lengkap ikon satu keluarga resmi beserta gambaran bentuk geometris dan letak tombolnya di aplikasi SAKU:

#### 1. Bilah Navigasi Bawah & Aksi Global (Navigation & Structure)
| Referensi `SakuIcons` | Berkas Drawable | Gambaran Bentuk Geometris | Padanan Lucide | Tombol & Penempatan di Aplikasi |
|---|---|---|---|---|
| `SakuIcons.Home` | `ic_saku_nav_home.xml` | Atap segitiga simetris (`M3,10.5 L12,3.5 L21,10.5`), dinding membulat, dan pintu melengkung di tengah. | `Home` | Menu utama "Beranda" (bilah navigasi bawah) |
| `SakuIcons.Pockets` | `ic_saku_nav_pockets.xml` | Saku baju origami / dompet berkancing dengan lipatan diagonal atas menyerupai amplop pos dana. | `Wallet` / `Folder` | Menu "Kantong & Aset" (bilah navigasi bawah) |
| `SakuIcons.Add` | `ic_saku_action_add.xml` | Palang tambah (+) simetris tebal 2.0dp dengan ujung garis membulat penuh (*round cap*). | `Plus` | Floating Action Button (FAB) tengah bilah bawah |
| `SakuIcons.Reports` | `ic_saku_nav_reports.xml` | Sumbu dasar horizontal, 3 batang vertikal berjenjang naik, dan panah tren pertumbuhan ke kanan atas. | `BarChart3` | Menu "Laporan" (bilah navigasi bawah) |
| `SakuIcons.Profile` | `ic_saku_nav_profile.xml` | Lingkaran kepala proporsional (`circle r=4`) dan kurva pundak melengkung halus di bawahnya. | `User` | Menu "Profil & Akun" (bilah navigasi bawah) |
| `SakuIcons.Back` | `ic_saku_nav_back.xml` | Garis horizontal dengan kepala panah sudut 45° menghadap ke kiri (`M19,12 L5,12`). | `ArrowLeft` | Tombol kembali di seluruh sub-halaman aplikasi |
| `SakuIcons.Close` | `ic_saku_action_close.xml` | Silang diagonal simetris (✕) dengan ujung membulat (`M6,6 L18,18` & `M18,6 L6,18`). | `X` | Tombol tutup modal dialog, scanner, & bottom sheet |
| `SakuIcons.ChevronRight` | `ic_saku_chevron_right.xml` | Siku lancip membulat menghadap ke kanan (›) (`M9,6 L15,12 L9,18`). | `ChevronRight` | Navigasi menu baris profil & drill-down |
| `SakuIcons.ChevronLeft` | `ic_saku_chevron_left.xml` | Siku lancip membulat menghadap ke kiri (‹) (`M15,6 L9,12 L15,18`). | `ChevronLeft` | Navigasi pemilihan periode bulan di Laporan |
| `SakuIcons.LinkExt` | `ic_saku_nav_link_ext.xml` | Anak panah miring serong kanan atas (↗) dengan garis poros diagonal (`M7,17 L17,7`). | `ArrowUpRight` | Tombol tautan cepat "Semua Riwayat" & "Atur Kantong" |

#### 2. Kategori Anggaran & Transaksi (Budget Pockets & Categories)
| Referensi `SakuIcons` | Berkas Drawable | Gambaran Bentuk Geometris | Padanan Lucide | Kategori Alokasi Anggaran |
|---|---|---|---|---|
| `SakuIcons.Groceries` | `ic_saku_cat_groceries.xml` | Kantong belanja kertas supermarket (*paper bag*) dengan pegangan melengkung setengah lingkaran di atas. | `ShoppingBag` | Pos Kebutuhan Pokok, belanja pasar & supermarket |
| `SakuIcons.Food` | `ic_saku_cat_food.xml` | Cangkir kopi/mangkuk bertelinga dengan 2 liukan uap panas di atasnya dan piring tatakan bawah. | `Utensils` / `Coffee` | Pos Makan & Minum, kuliner, restoran, kafe |
| `SakuIcons.Transport` | `ic_saku_cat_transport.xml` | Mobil sedan tampak depan dengan kaca aerodinamis miring dan 2 roda lingkaran simetris. | `Car` | Pos Bensin, tol, parkir, ojek online, & kendaraan |
| `SakuIcons.Entertainment` | `ic_saku_cat_entertainment.xml` | Kontroler game konsol (*gamepad*) dengan tombol D-pad silang di kiri dan tombol aksi di kanan. | `Gamepad2` | Pos Hiburan, hobi, streaming, bioskop, liburan |
| `SakuIcons.Bills` | `ic_saku_cat_bills.xml` | Kertas struk bergigi zig-zag di bagian bawah dengan 3 garis teks horizontal monoline. | `Receipt` | Pos Tagihan bulanan, listrik PLN, air PDAM, WiFi |
| `SakuIcons.Savings` | `ic_saku_cat_savings.xml` | Celengan babi (*piggy bank*) siluet garis dengan lubang koin horizontal di punggung atas. | `PiggyBank` | Pos Tabungan berjangka & dana cadangan |
| `SakuIcons.Health` | `ic_saku_cat_health.xml` | Perisai pelindung dengan palang tanda tambah medis (+) di titik tengahnya. | `ShieldAlert` | Pos Dana Darurat, obat, apotek, medis, asuransi |
| `SakuIcons.Gift` | `ic_saku_cat_gift.xml` | Kotak kado persegi dengan ikatan pita melingkar dan simpul pita di atas tutup kotak. | `Gift` | Pos Sedekah, zakat, infaq, hadiah, & amal |
| `SakuIcons.Education` | `ic_saku_cat_education.xml` | Topi wisuda akademis belah ketupat dengan tali rumbai menjuntai di samping. | `GraduationCap` | Pos Pendidikan, uang kuliah, kursus, & buku |
| `SakuIcons.Income` | `ic_saku_cat_income.xml` | Lembaran uang kertas dengan anak panah mengarah ke bawah di tengahnya (*cash inflow*). | `ArrowDownToLine` | Kategori Transaksi Pemasukan (Gaji, Bonus, Dividen) |

#### 3. Manajemen Portofolio & Kelas Aset (Assets & Wealth)
| Referensi `SakuIcons` | Berkas Drawable | Gambaran Bentuk Geometris | Padanan Lucide | Kelas Aset Finansial |
|---|---|---|---|---|
| `SakuIcons.AssetBank` | `ic_saku_asset_bank.xml` | Gedung bank pilar klasik Yunani/Romawi dengan atap pedimen segitiga dan lantai bertingkat. | `Landmark` | Tabungan bank, rekening koran, & giro |
| `SakuIcons.AssetCash` | `ic_saku_asset_cash.xml` | Dua lembar uang kertas bersusun dengan lingkaran nominal di tengah. | `Banknote` | Kas tunai fisik (*cash on hand*) & dompet digital |
| `SakuIcons.AssetInvest` | `ic_saku_asset_invest.xml` | Garis sumbu dasar dengan grafik fluktuasi yang menanjak tajam ke arah kanan atas (*uptrend*). | `TrendingUp` | Portofolio saham, reksadana, & obligasi |
| `SakuIcons.AssetGold` | `ic_saku_asset_gold.xml` | Batangan emas trapezoid dengan efek bevel berkilau di permukaannya. | `Coins` / `Gem` | Tabungan emas fisik & logam mulia Antam |

#### 4. Pemindai Struk & AI Intelligence (OCR & Camera)
| Referensi `SakuIcons` | Berkas Drawable | Gambaran Bentuk Geometris | Padanan Lucide | Tombol Fitur Pemindai Struk |
|---|---|---|---|---|
| `SakuIcons.ScanOcr` | `ic_saku_scan_ocr.xml` | 4 sudut pembidik kamera (*viewfinder*), miniatur struk belanja, dan garis laser pemindai hijau zamrud. | `ScanLine` | Tombol utama fitur pemindai struk otomatis |
| `SakuIcons.Camera` | `ic_saku_scan_camera.xml` | Kamera saku monoline dengan lensa lingkaran tengah dan tombol shutter di atas kanan. | `Camera` | Ambil foto nota belanja / ganti foto profil |
| `SakuIcons.Flash` | `ic_saku_flash.xml` | Kilatan petir zig-zag bergaris tepi tanpa isian hitam padat. | `Zap` | Tombol lampu senter pada jendela kamera |
| `SakuIcons.AiGemini` | `ic_saku_ai_gemini.xml` | Kilauan empat bintang (*sparkles*) yang melambangkan kecerdasan buatan. | `Sparkles` | Switch mode mesin AI (Google Gemini OCR) |

#### 5. Keamanan, Autentikasi & Akun (Security & Auth)
| Referensi `SakuIcons` | Berkas Drawable | Gambaran Bentuk Geometris | Padanan Lucide | Fungsi Keamanan & Kredensial |
|---|---|---|---|---|
| `SakuIcons.Lock` | `ic_saku_security_lock.xml` | Gembok klasik dengan gantungan lengkung atas dan lubang kunci vertikal di badan gembok. | `Lock` | Indikator kata sandi & layar lupa sandi |
| `SakuIcons.Key` | `ic_saku_security_key.xml` | Kunci fisik monoline dengan lubang lingkaran atas dan 2 gerigi vertikal di batang kunci. | `KeyRound` | Konfigurasi Gemini API Key & verifikasi OTP |
| `SakuIcons.Fingerprint`| `ic_saku_fingerprint.xml` | 4 alur kontur kurva sidik jari (*fingerprint ridges*) konsentris melengkung. | `Fingerprint` | Autentikasi biometrik Face/Fingerprint |
| `SakuIcons.Eye` | `ic_saku_eye.xml` | Bentuk kontur kelopak mata terbuka dengan pupil melingkar di titik tengah. | `Eye` | Tombol tampilkan kata sandi / intip saldo |
| `SakuIcons.EyeOff` | `ic_saku_eye_off.xml` | Bentuk kelopak mata dengan garis diagonal melintang dari kiri atas ke kanan bawah. | `EyeOff` | Tombol sembunyikan kata sandi / tutup saldo |
| `SakuIcons.Logout` | `ic_saku_logout.xml` | Kusen pintu terbuka sebelah kiri dengan panah horizontal menembus keluar ke kanan. | `LogOut` | Tombol "Keluar dari Akun" di halaman profil |
| `SakuIcons.ShieldCheck`| `ic_saku_shield_check.xml`| Perisai dengan tanda centang (✓) di bagian tengahnya. | `ShieldCheck` | Badge keamanan data lokal & verifikasi struk |

#### 6. Aksi Formulir, Utilitas & Sistem (Actions & Utilities)
| Referensi `SakuIcons` | Berkas Drawable | Gambaran Bentuk Geometris | Padanan Lucide | Fungsi Interaksi |
|---|---|---|---|---|
| `SakuIcons.Edit` | `ic_saku_action_edit.xml` | Pena pensil miring bersudut 45° dengan mata pena runcing dan garis alas datar. | `Pencil` | Tombol edit transaksi & ubah profil |
| `SakuIcons.Delete` | `ic_saku_action_delete.xml` | Tempat sampah monoline dengan tutup sedikit terbuka dan 2 garis vertikal di tabung sampah. | `Trash2` | Tombol hapus transaksi, hapus kantong, kosongkan API |
| `SakuIcons.Paste` | `ic_saku_action_paste.xml` | Papan jalan penjepit dokumen (*clipboard*) dengan lembar kertas di atasnya. | `Clipboard` | Tombol tempel cepat API Key dari papan klip |
| `SakuIcons.Excel` | `ic_saku_action_excel.xml` | Berkas lembar dokumen dengan sudut kanan terlipat dan kisi-kisi tabel baris/kolom spreadsheet. | `FileSpreadsheet` | Tombol ekspor laporan bulanan ke format `.xlsx` |
| `SakuIcons.Check` | `ic_saku_check.xml` | Tanda centang tunggal (✓) tebal 2.0dp dengan sudut lekuk tumpul membulat. | `Check` | Ceklis persetujuan form & tombol konfirmasi simpan |
| `SakuIcons.Share` | `ic_saku_action_share.xml` | 3 simpul lingkaran kecil yang dihubungkan oleh 2 garis sudut (*share nodes*). | `Share2` | Tombol bagikan rincian nota transaksi |
| `SakuIcons.Notification`| `ic_saku_notification.xml`| Lonceng monoline dengan pendulum bulat kecil di bagian bawah. | `Bell` | Switch notifikasi pengingat batas anggaran |

