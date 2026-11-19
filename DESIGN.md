# SAKU Design System (DESIGN.md)

Panduan resmi identitas visual, token desain, tipografi, dan konvensi antarmuka (UI/UX) untuk aplikasi **Saku**. Dokumen ini menjadi acuan tunggal agar setiap pengembangan layar, komponen, dan fitur baru konsisten, ergonomis, dan aksesibel.

---

## 1. Filosofi & Karakter Brand

| Elemen | Karakteristik |
| :--- | :--- |
| **Identitas** | Saku — *"Catat. Atur. Tumbuh."* |
| **Mood Desain** | *Warm Organic Financial Elegance* — tenang, terpercaya, tidak mengintimidasi, hangat (nuansa kertas/krem dan hijau hutan gelap). |
| **Target Pengguna** | Masyarakat Indonesia (pengelolaan keuangan pribadi, keluarga, dan UMKM sederhana). |
| **Prinsip Utama** | 1. **Asset-Centric**: Saldo riil hanya ada di Aset (rekening/dompet), bukan di Kantong.<br>2. **High Legibility**: Angka nominal uang selalu terbaca jelas (tabular, pemisah ribuan).<br>3. **Contrast-Safe**: Teks pada tombol gelap wajib putih pekat (`Color.White`), hindari abu-abu samar.<br>4. **Indonesian Microcopy**: Utamakan istilah baku bahasa Indonesia (misal: "Masuk", bukan "Login"). |

---

## 2. Palet Warna & Token Desain

Semua warna didefinisikan secara modular di [`app/src/main/java/com/example/ui/theme/Color.kt`](file:///c:/Saku/app/src/main/java/com/example/ui/theme/Color.kt). Selalu gunakan token konstanta ini, **jangan pernah menulis warna hardcode hex langsung di Composable**.

### 2.1 Primitive Colors (Warna Dasar)

#### A. Hijau Brand (Primary Forest)
| Nama Token | Hex Code | Preview | Peruntukan Utama |
| :--- | :--- | :---: | :--- |
| `SakuDarkGreen` | `#133E35` | 🟩 | Primary brand, top app bar, tombol utama, teks judul display |
| `SakuDarkGreenDeep` | `#0C2922` | ⬛ | Dark accent, header kontras tinggi, status bar |
| `SakuMediumGreen` | `#1B5347` | 🟩 | Primary container, state hover/active |
| `SakuForestGreen` | `#266E5E` | 🟩 | Aksen sekunder, grafik keuangan |
| `SakuSageAccent` | `#4C7E6A` | 🟩 | Secondary element, indikator tab aktif, progress bar |
| `SakuLightGreen` | `#E3EFE9` | 🩵 | Background badge/chip positif, container sekunder |
| `SakuLightGreenTint` | `#EDF6F2` | 🩵 | Surface tint lembut, zebra striping tabel |

#### B. Krem & Latar Belakang (Warm Paper & Surfaces)
| Nama Token | Hex Code | Preview | Peruntukan Utama |
| :--- | :--- | :---: | :--- |
| `SakuCreamBackground` | `#FAF7EE` | ⬜ | Latar belakang utama aplikasi (Scaffold background) |
| `SakuCreamSurface` | `#FFFDF8` | ⬜ | Latar kartu (Card), modal sheet, pop-up surface |
| `SakuCreamSurfaceVariant`| `#F1ECE0` | 🟨 | Latar kolom input (TextField), container chip nonaktif |
| `SakuCreamBorder` | `#E4DDCD` | 🟨 | Garis tepi kartu (Card border 1dp), pembatas (Divider) |

#### C. Emas & Aksen (Gold Accents)
| Nama Token | Hex Code | Preview | Peruntukan Utama |
| :--- | :--- | :---: | :--- |
| `SakuGoldAccent` | `#C58E2E` | 🟧 | Floating Action Button (+), highlight pencapaian target tabungan |
| `SakuGoldLight` | `#F7EED8` | 🟨 | Background container badge emas, progress milestone |
| `SakuGoldDark` | `#8F641B` | 🟫 | Teks kontras di atas container emas |

#### D. Tipografi & Kontras Teks
| Nama Token | Hex Code | Preview | Peruntukan Utama |
| :--- | :--- | :---: | :--- |
| `SakuTextPrimary` | `#182622` | ⬛ | Teks konten utama, nominal uang, judul kartu |
| `SakuTextSecondary` | `#5A6C66` | 🔘 | Teks keterangan, subtitle, label tanggal/waktu |
| `SakuTextMuted` | `#8A9A93` | 🔘 | Placeholder input, ikon nonaktif, teks helper |
| `Color.White` | `#FFFFFF` | ⬜ | **Wajib** untuk teks/ikon di atas tombol `SakuDarkGreen` |

#### E. Status Finansial (Income & Expense)
| Nama Token | Hex Code | Preview | Peruntukan Utama |
| :--- | :--- | :---: | :--- |
| `SakuIncomeGreen` | `#178A4C` | 🟩 | Transaksi pemasukan, badge surplus, delta positif |
| `SakuIncomeGreenBg` | `#E6F5EC` | 🩵 | Background badge pemasukan |
| `SakuExpenseRed` | `#D32F2F` | 🟥 | Transaksi pengeluaran, peringatan overbudget, error |
| `SakuExpenseRedBg` | `#FBEAEA` | 🩷 | Background badge pengeluaran, container error |

#### F. Dark Mode (Mode Gelap)
| Nama Token | Hex Code | Peruntukan Utama |
| :--- | :--- | :--- |
| `SakuDarkBackground` | `#0F1714` | Latar belakang Scaffold dark theme |
| `SakuDarkSurface` | `#16231E` | Latar kartu dan panel dark theme |
| `SakuDarkSurfaceVariant`| `#1D2E28` | Kolom input dark theme |
| `SakuDarkBorder` | `#293F37` | Border kartu dark theme |
| `SakuDarkTextPrimary` | `#EDF2EF` | Teks utama dark theme |
| `SakuDarkTextSecondary` | `#A5B5AE` | Teks pendukung dark theme |

---

## 3. Sistem Tipografi (Typography Scale)

Konfigurasi resmi berada di [`app/src/main/java/com/example/ui/theme/Type.kt`](file:///c:/Saku/app/src/main/java/com/example/ui/theme/Type.kt) berbasis Material 3:

```
displayLarge    (32sp / ExtraBold)  → Total saldo bersih, angka hero dashboard
headlineLarge   (28sp / Bold)       → Judul layar auth, onboarding hero
headlineMedium  (24sp / Bold)       → Judul layar utama (Dashboard, Kantong)
headlineSmall   (20sp / Bold)       → Sub-header section besar
titleLarge      (18sp / Bold)       → Top Bar title, judul kartu aset/kantong
titleMedium     (16sp / SemiBold)   → Tombol utama, item transaksi title, nominal kartu
titleSmall      (14sp / Bold)       → Sub-judul section ringkas, chip terpilih
bodyLarge       (15sp / Normal)     → Teks paragraf, deskripsi form
bodyMedium      (14sp / Normal)     → Teks default form, catatan transaksi
bodySmall       (12sp / Normal)     → Keterangan tanggal, status sekunder
labelLarge      (14sp / Bold)       → Action chip, tab header
labelMedium     (12sp / SemiBold)   → Badge kategori, label metric ringkasan
labelSmall      (11sp / Medium)     → Timestamp riwayat, tooltip microcopy
```

### Aturan Penulisan Angka & Uang
1. **Pemisah Ribuan Baku**: Selalu gunakan format Rupiah: `Rp 150.000` via helper `Formatters.formatRupiah(amount)`.
2. **Ellipsis Proteksi**: Nominal pada kartu ringkasan atau list wajib dipagari dengan:
   ```kotlin
   maxLines = 1,
   overflow = TextOverflow.Ellipsis
   ```
   Tujuannya agar saldo ratusan juta/miliar tidak merusak layout (*line-wrapping break*).

---

## 4. Spacing, Corner Radius, & Elevation

### 4.1 Spacing Scale (Kelipatan 4dp / 8dp)
- `4.dp`  → Spacing micro (jarak ikon dengan teks kecil, padding badge dalam).
- `8.dp`  → Spacing antar elemen terkait erat (jarak icon dan teks tombol, gap antar chip).
- `12.dp` → Spacing antar kolom form atau list item rapat.
- `16.dp` → Padding standar kartu, horizontal margin layar ponsel.
- `20.dp` → Vertical spacing antar grup elemen dalam satu section.
- `24.dp` → Spacing antar section besar (misal: Kartu Ringkasan ke Daftar Transaksi).
- `32.dp` → Margin atas form login atau hero illustration.

### 4.2 Corner Radius (Bentuk Sudut Komponen)
| Ukuran | Nilai dp | Digunakan Pada |
| :--- | :--- | :--- |
| **Small** | `8.dp` | Chip filter kecil, indikator progres, thumbnail ikon |
| **Medium** | `12.dp` | Kolom input (`OutlinedTextField`), tombol sekunder/primer |
| **Large** | `16.dp` | Kartu transaksi, kartu metrik, dialog modal |
| **Extra Large** | `24.dp` - `28.dp` | Top corner Bottom Sheet modal, lengkungan bar navigasi bawah |
| **Pill / Circle** | `CircleShape` / `999.dp` | FAB (+), avatar profil, pill tab switcher, badge status |

### 4.3 Elevasi & Border
- Desain Saku mengutamakan **garis pembatas halus** (*stroke border*) daripada bayangan tebal.
- Kartu default: Background `SakuCreamSurface` + Border `BorderStroke(1.dp, SakuCreamBorder)` + Elevation `0.dp` s.d `2.dp`.
- Floating Bottom Bar & FAB: Elevation `12.dp` - `16.dp` untuk efek melayang di atas konten scrollable.

---

## 5. Komponen Standar (Design Patterns)

### 5.1 Tombol (Buttons)

#### Tombol Utama (Primary Action)
- **Container**: `SakuDarkGreen`
- **Teks**: `Color.White` (Wajib! Jangan gunakan warna teks default agar terbaca jelas)
- **Shape**: `RoundedCornerShape(12.dp)`
- **Tinggi**: Minimum `48.dp` (target sentuh ramah jempol)
```kotlin
Button(
    onClick = { /* action */ },
    colors = ButtonDefaults.buttonColors(
        containerColor = SakuDarkGreen,
        contentColor = Color.White
    ),
    shape = RoundedCornerShape(12.dp),
    modifier = Modifier.fillMaxWidth().height(50.dp)
) {
    Text("Simpan Transaksi", style = MaterialTheme.typography.titleMedium, color = Color.White)
}
```

#### Tombol Sekunder / Outlined
- **Container**: `Color.Transparent`
- **Border**: `BorderStroke(1.dp, SakuDarkGreen)`
- **Content Color**: `SakuDarkGreen`
```kotlin
OutlinedButton(
    onClick = { /* action */ },
    border = BorderStroke(1.dp, SakuDarkGreen),
    colors = ButtonDefaults.outlinedButtonColors(contentColor = SakuDarkGreen),
    shape = RoundedCornerShape(12.dp)
) {
    Text("Uji Koneksi", color = SakuDarkGreen)
}
```

#### Tombol Destruktif (Hapus / Keluar)
- **Container**: `SakuExpenseRed`
- **Teks**: `Color.White`

---

### 5.2 Kartu (Cards)
Gunakan token kartu seragam di seluruh layar:
```kotlin
Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SakuCreamSurface),
    border = BorderStroke(1.dp, SakuCreamBorder),
    modifier = Modifier.fillMaxWidth()
) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Konten kartu
    }
}
```

---

### 5.3 Input Fields (Formulir)
- Gunakan `OutlinedTextField` dengan label melayang atau label terpisah di atasnya.
- Container background: `SakuCreamSurface` atau `SakuCreamSurfaceVariant`.
- Focus border: `SakuDarkGreen`. Unfocused border: `SakuCreamBorder`.

---

### 5.4 Grid & Layout Responsif
- **Perhatian Khusus**: Jika layar memiliki parent `verticalScroll(rememberScrollState())`, **dilarang menggunakan `LazyVerticalGrid`** karena akan memicu crash ukuran tak hingga (*infinite height constraint crash*).
- **Gunakan Pattern Adaptif Baris/Kolom**:
  Gunakan `BoxWithConstraints` dengan perhitungan kolom dinamis:
  ```kotlin
  val columns = (maxWidth / 160.dp).toInt().coerceAtLeast(2)
  ```
- Kartu dalam baris seimbang: gunakan `weight(1f)` hanya bila lebar antar kolom seragam; jika berisi teks dinamis panjang, gunakan `wrapContentHeight()`.

---

## 6. Kamus Kata & Konvensi UX (Indonesian-First)

Untuk menjaga konsistensi nada bahasa (*tone of voice*), gunakan padanan kata berikut:

| ❌ Hindari (Inggris / Slang) | ✅ Gunakan (Bahasa Indonesia Baku) |
| :--- | :--- |
| Login | **Masuk** |
| Register / Sign Up | **Daftar Akun** |
| Password | **Kata Sandi** |
| Forgot Password | **Lupa Kata Sandi** |
| Logout | **Keluar Akun** |
| Save | **Simpan** |
| Cancel | **Batal** |
| Delete | **Hapus** |
| Current Balance | **Saldo Saat Ini** (khusus Aset) |
| Target Amount | **Target Anggaran** (khusus Kantong) |
| Realization / Progress | **Realisasi** |
| Remaining Budget | **Sisa Anggaran** |
| Income | **Pemasukan** |
| Expense | **Pengeluaran** |
| Transfer | **Transfer Antar Aset** |
| Export / Backup | **Cadangkan & Unduh** |

---

## 7. Aturan Wajib (Golden Rules for Developers)

1. **Aturan Kontras Tombol Gelap:** Setiap kali membuat tombol atau badge dengan warna latar belakang gelap (`SakuDarkGreen`, `SakuDarkGreenDeep`, `SakuExpenseRed`), warna teks dan ikon **HARUS secara eksplisit** disetel ke `Color.White`.
2. **Larangan Saldo pada Kantong:** Kantong (*Pocket*) tidak memiliki saldo (`balance`). Kantong hanya memiliki target (`targetAmount`) dan realisasi pengeluaran (`realization`). Saldo riil hanya berada pada Aset.
3. **Penyimpanan Kode Warna:** Jangan menambahkan warna baru langsung di Composable. Jika diperlukan varian warna baru, daftarkan terlebih dahulu di `Color.kt` sebagai token semantik.
4. **Navigasi 5 Tab Tetap:** Bottom navigation bar memiliki lekukan tengah (*curved notch*) dengan tombol FAB tambah transaksi di posisi tengah. Selalu jaga agar navbar tidak menutupi item paling bawah dari scrollable content (tambahkan `Spacer(modifier = Modifier.height(96.dp))` di ujung bawah list).
