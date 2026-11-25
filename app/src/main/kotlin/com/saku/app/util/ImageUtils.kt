package com.saku.app.util

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ImageUtils {

    /**
     * Menyalin foto dari URI yang dipilih dari Photo Picker / Galeri
     * ke direktori berkas internal aplikasi agar permanen dan tidak kedaluwarsa.
     */
    fun saveProfileAvatarFromUri(context: Context, uri: Uri): String? {
        return try {
            val avatarDir = File(context.filesDir, "avatars").apply {
                if (!exists()) mkdirs()
            }

            // Bersihkan avatar lama agar hemat ruang penyimpanan
            avatarDir.listFiles()?.forEach { file ->
                if (file.isFile && file.name.startsWith("avatar_")) {
                    file.delete()
                }
            }

            val destinationFile = File(avatarDir, "avatar_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            destinationFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Menghapus berkas foto profil jika pengguna ingin kembali ke monogram inisial.
     */
    fun deleteProfileAvatar(path: String?) {
        if (!path.isNullOrBlank()) {
            try {
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Membuat file sementara di cacheDir dan mengembalikan content:// URI melalui FileProvider
     * untuk digunakan oleh aplikasi kamera bawaan (ActivityResultContracts.TakePicture).
     */
    fun createTempReceiptUri(context: Context): Uri {
        val receiptDir = File(context.cacheDir, "receipts").apply {
            if (!exists()) mkdirs()
        }
        val photoFile = File(receiptDir, "receipt_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
    }

    /**
     * Memuat ImageBitmap dari content:// URI dengan downsampling agar aman dari OutOfMemoryError
     * saat menampilkan foto beresolusi tinggi dari kamera bawaan atau galeri.
     */
    @Composable
    fun rememberUriBitmap(uri: Uri?, maxDimension: Int = 1024): ImageBitmap? {
        val context = LocalContext.current
        return remember(uri) {
            if (uri == null) return@remember null
            try {
                val boundsOptions = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream, null, boundsOptions)
                }

                var sampleSize = 1
                val maxSide = maxOf(boundsOptions.outWidth, boundsOptions.outHeight)
                if (maxSide > maxDimension) {
                    while (maxSide / sampleSize > maxDimension) {
                        sampleSize *= 2
                    }
                }

                val decodeOptions = BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                }
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream, null, decodeOptions)?.asImageBitmap()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Helper Composable untuk memuat ImageBitmap dari path lokal secara reaktif & efisien.
     */
    @Composable
    fun rememberProfileBitmap(path: String?): ImageBitmap? {
        return remember(path) {
            if (!path.isNullOrBlank()) {
                val file = File(path)
                if (file.exists() && file.length() > 0) {
                    try {
                        BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                } else null
            } else null
        }
    }
}
