# PRODUCT REQUIREMENTS DOCUMENT (PRD)

## SAKU

**STATUS: DRAFT SEMENTARA**

| | |
| --- | --- |
| **Nama Produk** | SAKU (Sistem Alokasi Keuangan & Pemindai Struk) |
| **Versi Dokumen** | v0.1 |
| **Disusun oleh** | Kelompok Project (Pengembang) |
| **Untuk** | Tim Dosen & Evaluator Mata Kuliah Mobile Computing |
| **Tanggal** | 21 November 2026 |
| **Dokumen Terkait** | Notulen Diskusi Perancangan Aplikasi SAKU |

---

# 1. Ringkasan Produk (Overview)

Mencatat pengeluaran harian secara konsisten sering kali menjadi kendala bagi banyak orang akibat proses pencatatan manual yang lambat, berulang, dan mudah terlupakan. Ketergantungan pada bukti transaksi fisik berupa struk kertas juga memperparah masalah ini, mengingat struk belanja sangat rentan hilang, kotor, atau terbuang sebelum sempat direkap. Di samping itu, ketiadaan sistem alokasi pos anggaran yang terstruktur membuat pengguna kesulitan mendisiplinkan pengeluaran berdasarkan kategori kebutuhan pokok, tabungan, dan dana darurat. Tanpa adanya visibilitas sisa anggaran secara langsung (*real-time*), pengguna sangat rentan mengalami pengeluaran berlebih (*overspending*) tanpa disadari.

Aplikasi **SAKU** hadir sebagai solusi aplikasi mobile berbasis Android yang mengusung prinsip kerja *offline-first* dengan kemudahan pencatatan otomatis berbasis kamera (*Optical Character Recognition / OCR*). Melalui SAKU, pengguna dapat memindai bukti struk belanja fisik secara instan untuk mengisi detail transaksi tanpa perlu mengetik manual. SAKU juga menerapkan sistem pos anggaran pintar ("Kantong") untuk memisahkan alokasi dana ke berbagai pos spesifik, memberikan indikator peringatan saat mendekati batas anggaran, serta menyajikan dashboard visual berisi grafik laporan keuangan bulanan yang informatif untuk mengevaluasi kesehatan finansial pengguna.

# 2. Tujuan & Sasaran (Goals)

- Mengurangi beban dan waktu pencatatan transaksi pengeluaran harian pengguna melalui otomatisasi pemindai struk fisik.
- Memusatkan riwayat pengeluaran dan bukti transaksi ke dalam format digital yang aman dan rapi di perangkat Android.
- Mencegah risiko pengeluaran berlebih (*overspending*) dengan memfasilitasi pembagian pos dana (*budgeting*) serta kontrol sisa anggaran secara *real-time*.
- Memberikan peringatan dini kepada pengguna ketika penggunaan dana di suatu kantong telah mendekati atau melebihi limit yang ditentukan.
- Menyediakan transparansi arus kas bulanan melalui grafik dan visualisasi laporan yang mudah dipahami.
- Menjamin privasi dan aksesibilitas aplikasi dalam berbagai kondisi jaringan melalui arsitektur penyimpanan lokal (*offline-first*).

# 3. Pengguna & Peran (Users & Roles)

- **Pengguna (User) :** Aktor tunggal pemilik perangkat Android yang menggunakan aplikasi SAKU untuk membuat akun lokal, mengonfigurasi pos kantong anggaran, memindai struk belanja, mencatat transaksi manual/otomatis, mengelola batas pengeluaran, serta meninjau grafik analisis keuangan bulanan.

# 4. Ruang Lingkup (Scope)

## 4.1 Termasuk (MVP)

- Modul Antarmuka Autentikasi Lokal (Splash Screen, Sign Up, Login, dan Profil Pengguna berbasis penyimpanan lokal).
- Modul Profil & Pengaturan Sistem (Manajemen akun lokal, konfigurasi default OCR ML Kit/Cloud, kompresi foto struk, keamanan PIN/biometrik, dan backup basis data Room SQLite).
- Modul Pemindai Struk Otomatis Dual-Engine (Akses kamera smartphone dengan pilihan mode: On-Device Offline ML Kit atau Online Google Cloud AI Vision).
- Modul Manajemen Transaksi (Pencatatan manual dengan hero input amount & quick chips, penyesuaian/koreksi hasil scan struk, dan riwayat mutasi transaksi).
- Modul Sistem Alokasi Anggaran & Manajemen Aset (Tab switcher antara "Kantong Anggaran" dan "Aset Saya", alokasi dana, visualisasi breakdown aset, dan peringatan batas kuota).
- Modul Indikator & Peringatan Anggaran (Indikator visual dan notifikasi *budget warning* saat mendekati atau melebihi batas).
- Modul Dashboard & Laporan Bulanan (Ringkasan total saldo, arus kas masuk/keluar, bar chart 6 bulan, skor kesehatan finansial, dan tombol ekspor langsung ke Excel `.xlsx`).

## 4.2 Di Luar Lingkup Awal / Fase Lanjutan

Sinkronisasi awan (*cloud synchronization*) multi-perangkat otomatis, integrasi mutasi bank/e-wallet otomatis via Open Banking API, ekspor ke format PDF cetak, serta fitur kantong bersama (*Shared Pocket/Family Budget*). Rincian fitur ini dirujuk pada Bab 11.

# 5. Asumsi & Batasan (Assumptions & Constraints)

- **Platform Target :** Aplikasi dikembangkan khusus untuk platform Android native dengan batas minimum Android 8.0 (Oreo / API Level 26) ke atas. *(Asumsi teknis pengembang)*.
- **Arsitektur Penyimpanan :** Mengusung prinsip *offline-first* menggunakan basis data relasional lokal (SQLite / Room Database) di penyimpanan internal perangkat pengguna. *(Asumsi teknis pengembang)*.
- **Sistem Autentikasi :** Alur Sign Up dan Login bekerja secara lokal tanpa server pusat di tahap MVP, dengan validasi kredensial tersimpan secara terenkripsi di penyimpanan lokal (*SharedPreferences/EncryptedSharedPreferences*). *(Asumsi teknis pengembang)*.
- **Teknologi Pemindai (OCR) :** Pemindaian teks struk memanfaatkan salah satu dari dua pendekatan: *on-device* OCR (Google ML Kit Text Recognition) atau layanan Google Cloud Vision API tier gratis dengan koneksi internet aktif sebagai opsi kedua.
- **Akurasi Pemindaian :** Akurasi pembacaan karakter pada struk bergantung pada kejernihan foto, kualitas kamera smartphone, pencahayaan, dan integritas fisik struk (tidak kusut atau pudar).
- **Ketergantungan Perangkat Keras :** Membutuhkan izin akses kamera (*Camera Permission*) dan penyimpanan lokal (*Storage Permission*) yang wajib diberikan pengguna pada perangkat Android.

# 6. Kebutuhan Fungsional (Functional Requirements)

## 6.1 Pengguna — Autentikasi & Akun Lokal

| **ID** | **Kebutuhan Fungsional** | **Prioritas** |
| --- | --- | --- |
| **AUTH-1** | Pengguna dapat membuat akun baru (Sign Up) dengan memasukkan nama, username/email, dan kata sandi yang disimpan di database lokal. | **Wajib** |
| **AUTH-2** | Pengguna dapat masuk (Login) ke aplikasi menggunakan username/email dan kata sandi yang telah didaftarkan. | **Wajib** |
| **AUTH-3** | Sistem menyediakan opsi untuk keluar (*Logout*) dan beralih akun lokal pada perangkat yang sama. | **Penting** |
| **AUTH-4** | Pengguna dapat melihat dan mengubah informasi profil pengguna lokal (nama tampilan, handle username, dan foto/monogram avatar). | **Penting** |
| **AUTH-5** | Pengguna dapat mengonfigurasi preferensi sistem pada halaman Profil (pilihan default engine OCR On-Device/Cloud, kompresi foto struk, siklus reset anggaran bulanan, kunci PIN/biometrik, dan pencadangan database Room). | **Wajib** |

## 6.2 Pengguna — Pemindai Struk Otomatis (OCR)

| **ID** | **Kebutuhan Fungsional** | **Prioritas** |
| --- | --- | --- |
| **SCAN-1** | Pengguna dapat membuka antarmuka kamera smartphone langsung dari dalam aplikasi untuk memotret fisik struk belanja. | **Wajib** |
| **SCAN-2** | Sistem memproses gambar struk menggunakan modul OCR untuk mendeteksi tanggal transaksi, nama toko/merchant, dan total nominal belanja. | **Wajib** |
| **SCAN-3** | Sistem memetakan hasil ekstraksi OCR secara otomatis ke formulir pencatatan transaksi baru. | **Wajib** |
| **SCAN-4** | Pengguna dapat meninjau, mengedit, atau melengkapi rincian formulir (seperti memilih kantong anggaran) sebelum data transaksi disimpan. | **Wajib** |
| **SCAN-5** | Sistem menampilkan pesan kesalahan yang ramah pengguna apabila struk tidak terbaca atau gambar terlalu buram. | **Penting** |
| **SCAN-6** | Sistem menyediakan tombol pemilihan mode OCR (Dual Engine): `⚡ Offline (Lokal ML Kit)` untuk pemrosesan instan tanpa internet, dan `☁ Google Cloud AI` untuk akurasi tinggi saat terhubung jaringan. | **Wajib** |

## 6.3 Pengguna — Manajemen Transaksi

| **ID** | **Kebutuhan Fungsional** | **Prioritas** |
| --- | --- | --- |
| **TRX-1** | Pengguna dapat menambahkan catatan transaksi pengeluaran dan pemasukan secara manual tanpa menggunakan pemindai kamera. | **Wajib** |
| **TRX-2** | Pengguna dapat mengisi nominal, tanggal, memilih pos kantong anggaran, serta menambahkan catatan keterangan transaksi. | **Wajib** |
| **TRX-3** | Pengguna dapat melihat daftar seluruh riwayat transaksi yang diurutkan dari yang terbaru. | **Wajib** |
| **TRX-4** | Pengguna dapat memfilter riwayat transaksi berdasarkan rentang tanggal atau kategori pos kantong anggaran. | **Penting** |
| **TRX-5** | Pengguna dapat mengedit kembali data transaksi yang sudah tersimpan atau menghapusnya jika terjadi kesalahan input. | **Wajib** |

## 6.4 Pengguna — Sistem Alokasi Anggaran (Kantong/Saku)

| **ID** | **Kebutuhan Fungsional** | **Prioritas** |
| --- | --- | --- |
| **SAKU-1** | Sistem menyediakan pos alokasi anggaran bawaan (Kebutuhan Pokok, Dana Darurat, Tabungan, dan Keinginan). | **Wajib** |
| **SAKU-2** | Pengguna dapat mengatur batas anggaran maksimal (*budget limit*) untuk setiap pos kantong per periode bulanan. | **Wajib** |
| **SAKU-3** | Sistem memperbarui dan menampilkan sisa anggaran secara *real-time* setiap kali ada transaksi pengeluaran yang dialokasikan ke kantong tersebut. | **Wajib** |
| **SAKU-4** | Sistem menampilkan indikator visual perubahan warna (*progress bar*) saat alokasi anggaran mendekati batas (>=80%). | **Wajib** |
| **SAKU-5** | Sistem memberikan peringatan visual dan notifikasi (*budget alert*) saat pengeluaran pada suatu kantong telah mencapai atau melampaui batas 100% (*overbudget*). | **Wajib** |
| **SAKU-6** | Pengguna dapat menambah, mengubah nama, atau menonaktifkan kantong anggaran kustom sesuai preferensi pribadi. | **Penting** |
| **SAKU-7** | Pengguna dapat mengelola aset kekayaan (Aset Saya) melalui tab switcher terintegrasi di halaman Kantong, lengkap dengan ringkasan total kekayaan bersih dan visualisasi persentase diversifikasi portofolio aset. | **Wajib** |

## 6.5 Pengguna — Dashboard & Laporan Bulanan

| **ID** | **Kebutuhan Fungsional** | **Prioritas** |
| --- | --- | --- |
| **REPT-1** | Sistem menampilkan ringkasan kartu saldo utama, total pengeluaran bulan berjalan, dan total pemasukan pada layar beranda (*Dashboard*). | **Wajib** |
| **REPT-2** | Sistem menyajikan grafik visual bulanan (diagram lingkaran atau grafik batang) yang memperlihatkan proporsi pengeluaran per pos kantong. | **Wajib** |
| **REPT-3** | Pengguna dapat memilih filter bulan dan tahun untuk melihat kembali perbandingan laporan pengeluaran periode sebelumnya. | **Penting** |
| **REPT-4** | Sistem menampilkan indikator status kesehatan keuangan sederhana (misal: "Aman", "Waspada", atau "Overbudget") berdasarkan kepatuhan terhadap batas anggaran. | **Penting** |
| **REPT-5** | Pengguna dapat mengekspor rekapitulasi data laporan keuangan periode bulanan secara instan khusus ke format spreadsheet Excel (`.xlsx`) via tombol aksi utama maupun quick-button di header. | **Wajib** |

# 7. Alur Pengguna Utama (Key User Flows)

## 7.1 Pendaftaran & Pembuatan Profil Lokal (Pengguna Baru)

1. Pengguna membuka aplikasi SAKU dan disambut oleh *Splash Screen* pengantar.
2. Pengguna memilih menu **"Buat Akun"** (*Sign Up*).
3. Pengguna mengisi nama lengkap, username/email, dan kata sandi baru.
4. Pengguna menekan tombol **"Daftar"**.
5. Sistem memvalidasi input, membuat kredensial akun di basis data lokal, dan menginisialisasi 4 pos kantong bawaan.
6. Sistem mengubah status menjadi "Sesi Aktif" dan mengarahkan pengguna langsung ke halaman Beranda (*Dashboard*).

## 7.2 Pencatatan Pengeluaran via Pemindai Struk (Happy Path)

1. Pengguna menekan tombol aksi utama **"Scan Struk"** pada navigasi bawah atau dashboard.
2. Aplikasi meminta izin akses kamera (jika baru pertama kali) dan membuka jendela bidik kamera.
3. Pengguna mengarahkan kamera ke lembar struk belanja fisik lalu menekan tombol bidik (*Capture*).
4. Sistem memproses gambar dengan modul OCR dan mengekstrak tanggal, merchant, serta total nominal belanja.
5. Sistem membuka formulir konfirmasi transaksi dengan kolom tanggal dan nominal yang telah terisi secara otomatis.
6. Pengguna memilih pos kantong anggaran (misal: "Kebutuhan Pokok") dan menambahkan catatan singkat jika diperlukan.
7. Pengguna menekan tombol **"Simpan Transaksi"**.
8. Sistem menyimpan transaksi, memotong kuota anggaran pada kantong terkait secara *real-time*, dan menampilkan pesan sukses.

## 7.3 Pencatatan Pengeluaran Manual

1. Pengguna memilih menu **"Tambah Transaksi Manual"** (+).
2. Pengguna memilih jenis transaksi "Pengeluaran".
3. Pengguna memasukkan nominal uang, memilih tanggal, dan memilih pos kantong anggaran tujuan.
4. Pengguna menekan tombol **"Simpan"**.
5. Sistem memverifikasi saldo anggaran kantong terkait dan mencatat transaksi ke database lokal.
6. Tampilan dashboard dan saldo kantong langsung diperbarui seketika.

## 7.4 Penanganan Peringatan Batas Anggaran (Overbudget Alert)

1. Pengguna memasukkan transaksi pengeluaran (baik melalui scan struk maupun manual) pada pos kantong tertentu (misal: "Keinginan").
2. Sistem mendeteksi bahwa total pengeluaran pada kantong tersebut telah mencapai >=80% dari batas maksimal.
3. Sistem mengubah warna *progress bar* kantong menjadi kuning (Waspada).
4. Jika transaksi berikutnya menyebabkan akumulasi pengeluaran melewati 100% dari batas anggaran, sistem menetapkan status "Overbudget".
5. Sistem menampilkan dialog peringatan visual konfirmasi: *"Perhatian: Transaksi ini melebihi alokasi anggaran kantong Keinginan. Lanjutkan simpan?"*.
6. Jika pengguna memilih **"Lanjutkan"**, transaksi tetap disimpan dengan label penanda "Overbudget" berwarna merah.

## 7.5 Evaluasi Laporan Keuangan Bulanan

1. Pengguna berpindah ke tab navigasi **"Laporan"**.
2. Sistem menyajikan grafik perbandingan pengeluaran per kategori kantong untuk bulan aktif.
3. Pengguna dapat menekan irisan grafik untuk melihat rincian pengeluaran pada kantong yang dipilih.
4. Pengguna dapat mengubah *dropdown* filter bulan untuk meninjau histori arus kas pada bulan-bulan sebelumnya.

# 8. Model Data (High-Level)

| **Entitas** | **Field Utama** | **Keterangan** |
| --- | --- | --- |
| **user_profile** | `id`, `name`, `username`, `password_hash`, `created_at`, `[avatar_url]` | Menyimpan data akun pengguna yang tersimpan secara lokal di perangkat. |
| **pocket** | `id`, `user_id`, `pocket_name`, `budget_limit`, `current_spent`, `icon_name`, `color_code`, `is_default` | Menyimpan master data pos alokasi anggaran (Kebutuhan Pokok, Tabungan, dll). |
| **transaction** | `id`, `pocket_id`, `user_id`, `type`, `amount`, `transaction_date`, `merchant_name`, `notes`, `receipt_image_path`, `is_overbudget`, `created_at` | Menyimpan seluruh catatan transaksi pemasukan dan pengeluaran. |
| **receipt_scan** | `id`, `raw_text`, `detected_amount`, `detected_date`, `scan_confidence`, `created_at` | Entitas sementara penampung hasil pemrosesan OCR dari gambar struk. |
| **[cloud_backup]** | `[backup_id]`, `[user_id]`, `[sync_timestamp]`, `[backup_file_hash]`, `[cloud_provider]` | Entitas penampung status sinkronisasi cadangan data ke server awan (Fase Lanjutan). |

**Catatan:** field dalam [tanda kurung siku] merupakan bagian dari fitur usulan/Fase Lanjutan (Bab 11).

# 9. Kebutuhan Non-Fungsional (Non-Functional Requirements)

- **Ketersediaan & Offline-First :** Seluruh fungsionalitas pencatatan, perhitungan saldo, dan visualisasi grafik wajib dapat beroperasi penuh tanpa membutuhkan koneksi internet.
- **Performa & Waktu Pemrosesan :** Proses pembacaan OCR pada struk belanja ditargetkan menghasilkan ekstraksi data dalam waktu kurang dari 3 detik (pada pemrosesan lokal *on-device*).
- **Pengalaman Pengguna (UX / Usability) :** Antarmuka pengguna (*UI*) dirancang intuitif, modern, dan mematuhi kaidah *Material Design 3* dengan komponen navigasi yang mudah dioperasikan satu tangan pada perangkat smartphone.
- **Keamanan Data Lokal :** Penyimpanan kata sandi akun lokal wajib menggunakan algoritma *hashing* yang aman, serta berkas basis data SQLite/Room diamankan di direktori aplikasi yang terproteksi (*internal app sandbox*).
- **Integritas & Presisi Kalkulasi :** Perhitungan sisa anggaran dan akumulasi pengeluaran wajib memiliki presisi angka matematis bebas dari kesalahan pembulatan (*floating point error*).

# 10. Integrasi Pihak Ketiga

| **Layanan** | **Fungsi** | **Catatan** |
| --- | --- | --- |
| **Android CameraX API** | Mengakses modul perangkat keras kamera smartphone secara konsisten pada berbagai varian vendor Android. | Komponen inti perangkat keras. |
| **Google ML Kit Text Recognition** | Engine pemrosesan OCR *on-device* untuk membaca teks struk tanpa membutuhkan koneksi internet. | Alternatif utama pemrosesan lokal. |
| **Google Cloud Vision API** | Layanan OCR berbasis cloud menggunakan kuota gratis (*freemium tier*) sebagai alternatif ekstraksi teks kompleks jika online. | Alternatif sekunder / *fallback*. |
| **MPAndroidChart Library** | Komponen visualisasi grafik diagram lingkaran (*pie chart*) dan grafik batang (*bar chart*) pada modul laporan. | Pustaka antarmuka open-source. |
| **[Firebase / Cloud Storage]** | Penyimpanan awan untuk mencadangkan database lokal pengguna (Fase Lanjutan). | Fase Lanjutan (Bab 11). |

# 11. Fitur Usulan / Fase Lanjutan

- **Sinkronisasi Awan & Multi-Perangkat (Cloud Sync).** Menyediakan mekanisme cadangan data (*backup & restore*) otomatis berbasis akun Google / Firebase agar data transaksi tidak hilang saat pengguna berganti perangkat smartphone.
- **Ekspor Dokumen Laporan Cetak PDF.** Memungkinkan pengguna mengunduh rekapitulasi laporan dalam format dokumen cetak PDF bergaya visual resmi untuk keperluan arsip fisik (fitur ekspor Excel `.xlsx` telah diintegrasikan langsung pada MVP).
- **Kantong Bersama (Shared Pocket / Family Budget).** Memungkinkan dua atau lebih pengguna berbagi dan mengelola satu pos kantong anggaran yang sama secara kolaboratif (misal: anggaran rumah tangga bersama pasangan).
- **Pengingat Transaksi Harian (Smart Daily Reminder).** Notifikasi berkala pada jam tertentu (misal: pukul 20.00 malam) yang mengingatkan pengguna untuk merekap struk atau pengeluaran hari tersebut jika belum ada catatan yang dimasukkan.

# 12. Keputusan Desain & Pertanyaan Terbuka

- **Keputusan Arsitektur OCR:** Ditetapkan menggunakan pendekatan **Dual-Engine Hybrid**: default *on-device* dengan Google ML Kit Text Recognition untuk kecepatan dan privasi offline, disertai opsi toggle ke Google Cloud AI Vision API saat pengguna menginginkan akurasi ekstra untuk struk beresolusi rendah atau struk belanja panjang.
- **Keputusan Format Ekspor:** Ditetapkan **khusus format Excel (`.xlsx`)** pada rilis MVP agar pengguna dapat langsung mengolah data angka dan formula secara fleksibel di aplikasi spreadsheet. Format PDF dialokasikan untuk fase lanjutan jika dibutuhkan.
- Kebijakan penyimpanan foto struk: apakah foto asli struk akan disimpan permanen di memori internal ponsel sebagai lampiran bukti, atau dihapus otomatis setelah proses ekstraksi data transaksi selesai guna menghemat ruang memori.
- Penentuan nilai batas persentase peringatan awal anggaran (apakah paten di angka 80% atau dapat dikustomisasi secara manual oleh pengguna di menu pengaturan).

# 13. Glosarium

- **SAKU :** Nama produk aplikasi mobile manajemen keuangan pribadi dan pemindai struk.
- **Kantong (Pocket) :** Pos pembagian alokasi anggaran khusus yang memiliki batas pengeluaran (*budget limit*) tersendiri (misal: Kebutuhan Pokok, Dana Darurat, Tabungan, Keinginan).
- **OCR (Optical Character Recognition) :** Teknologi pengenalan karakter optik yang mengubah teks dalam gambar/foto menjadi data teks digital yang dapat dibaca dan diolah oleh sistem.
- **Overspending :** Kondisi di mana total pengeluaran aktual pengguna telah melampaui batas anggaran yang telah dialokasikan.
- **Budget Limit :** Batas nominal pengeluaran maksimal yang diizinkan pada suatu pos kantong dalam kurun waktu satu bulan.
- **Offline-First :** Paradigma pengembangan perangkat lunak di mana aplikasi dirancang untuk berfungsi penuh secara mandiri tanpa memerlukan keterhubungan dengan jaringan internet.
- **Room Database :** Pustaka persistensi data resmi dari Android Jetpack yang menyediakan lapisan abstraksi di atas SQLite untuk penyimpanan lokal.

---

*Dokumen ini merupakan draft sementara dan dapat berubah seiring pembahasan lebih lanjut dengan klien.*
