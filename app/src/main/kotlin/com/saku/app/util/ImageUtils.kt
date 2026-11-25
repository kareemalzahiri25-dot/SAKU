package com.saku.app.util

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
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
