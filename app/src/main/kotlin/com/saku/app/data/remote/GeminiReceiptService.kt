package com.saku.app.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.saku.app.data.CategoryType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

data class GeminiReceiptResult(
    val merchant: String,
    val amount: Long,
    val date: String,
    val notes: String,
    val suggestedPocket: String?,
    val categoryType: CategoryType
)

sealed interface GeminiResult<out T> {
    data class Success<T>(val data: T) : GeminiResult<T>
    data class Error(val message: String) : GeminiResult<Nothing>
}

/**
 * Klien REST ringan untuk memverifikasi API Key Gemini dan memindai foto struk belanja
 * menggunakan Gemini Vision (gemini-2.5-flash) tanpa dependensi SDK tambahan.
 */
object GeminiReceiptService {

    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
    private const val MODEL_NAME = "gemini-2.5-flash"
    private const val CONNECT_TIMEOUT_MS = 15_000
    private const val READ_TIMEOUT_MS = 30_000

    suspend fun verifyApiKey(apiKey: String, isEn: Boolean): GeminiResult<Unit> =
        withContext(Dispatchers.IO) {
            val trimmedKey = apiKey.trim()
            if (trimmedKey.isBlank()) {
                return@withContext GeminiResult.Error(
                    if (isEn) "API Key cannot be empty." else "API Key tidak boleh kosong."
                )
            }

            var connection: HttpURLConnection? = null
            try {
                val url = URL("$BASE_URL/models?pageSize=1")
                connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = CONNECT_TIMEOUT_MS
                    readTimeout = READ_TIMEOUT_MS
                    setRequestProperty("x-goog-api-key", trimmedKey)
                }

                val statusCode = connection.responseCode
                if (statusCode in 200..299) {
                    GeminiResult.Success(Unit)
                } else {
                    val errorBody = readStream(connection.errorStream)
                    GeminiResult.Error(parseApiError(statusCode, errorBody, isEn))
                }
            } catch (e: Exception) {
                GeminiResult.Error(
                    if (isEn) "Network error: ${e.localizedMessage ?: "Unable to reach Gemini API"}"
                    else "Gagal terhubung ke server: ${e.localizedMessage ?: "Periksa koneksi internet"}"
                )
            } finally {
                connection?.disconnect()
            }
        }

    suspend fun scanReceipt(
        context: Context,
        imageUri: Uri,
        apiKey: String,
        availablePockets: List<String>,
        isEn: Boolean
    ): GeminiResult<GeminiReceiptResult> = withContext(Dispatchers.IO) {
        val trimmedKey = apiKey.trim()
        if (trimmedKey.isBlank()) {
            return@withContext GeminiResult.Error(
                if (isEn) "Gemini API Key is not set in Profile."
                else "Gemini API Key belum diatur di halaman Profil."
            )
        }

        val base64Image = encodeUriToBase64Jpeg(context, imageUri)
            ?: return@withContext GeminiResult.Error(
                if (isEn) "Failed to read receipt image." else "Gagal membaca gambar struk."
            )

        var connection: HttpURLConnection? = null
        try {
            val url = URL("$BASE_URL/models/$MODEL_NAME:generateContent")
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                setRequestProperty("x-goog-api-key", trimmedKey)
            }

            val requestBody = buildScanRequestJson(base64Image, availablePockets, isEn)
            connection.outputStream.use { out ->
                out.write(requestBody.toString().toByteArray(Charsets.UTF_8))
            }

            val statusCode = connection.responseCode
            if (statusCode !in 200..299) {
                val errorBody = readStream(connection.errorStream)
                return@withContext GeminiResult.Error(parseApiError(statusCode, errorBody, isEn))
            }

            val responseBody = readStream(connection.inputStream)
            val parsed = parseScanResponse(responseBody, availablePockets, isEn)
            if (parsed != null) {
                GeminiResult.Success(parsed)
            } else {
                GeminiResult.Error(
                    if (isEn) "Could not extract receipt details from this image."
                    else "Struk tidak dapat dibaca dari gambar ini."
                )
            }
        } catch (e: Exception) {
            GeminiResult.Error(
                if (isEn) "Scan failed: ${e.localizedMessage ?: "Network error"}"
                else "Pemindaian gagal: ${e.localizedMessage ?: "Gangguan jaringan"}"
            )
        } finally {
            connection?.disconnect()
        }
    }

    private fun buildScanRequestJson(
        base64Image: String,
        availablePockets: List<String>,
        isEn: Boolean
    ): JSONObject {
        val pocketsList = availablePockets.joinToString(", ")
        val languageHint = if (isEn) "English" else "Indonesian"
        val prompt = """
            Analyze this shopping receipt image and extract transaction details into JSON.
            Return ONLY a JSON object with these exact keys:
            - "merchant": string (store/merchant name, e.g. "Superindo", "Alfamart")
            - "amount": integer (grand total amount in IDR without dots, commas, or currency symbols; 0 if unreadable)
            - "date": string (transaction date & time if visible, e.g. "21 Nov 2026, 14:30", or "" if not visible)
            - "notes": string (brief summary of main purchased items in $languageHint, max 80 chars)
            - "category": string, MUST be one of: "FOOD", "TRANSPORT", "ENTERTAINMENT", "INCOME", "GENERAL"
            - "pocket": string, pick the best matching pocket from: [$pocketsList]
        """.trimIndent()

        val inlineData = JSONObject()
            .put("mime_type", "image/jpeg")
            .put("data", base64Image)

        val parts = JSONArray()
            .put(JSONObject().put("text", prompt))
            .put(JSONObject().put("inline_data", inlineData))

        val contents = JSONArray()
            .put(JSONObject().put("parts", parts))

        val generationConfig = JSONObject()
            .put("responseMimeType", "application/json")
            .put("temperature", 0.1)

        return JSONObject()
            .put("contents", contents)
            .put("generationConfig", generationConfig)
    }

    private fun parseScanResponse(
        responseBody: String,
        availablePockets: List<String>,
        isEn: Boolean
    ): GeminiReceiptResult? {
        return try {
            val root = JSONObject(responseBody)
            val candidates = root.optJSONArray("candidates") ?: return null
            if (candidates.length() == 0) return null

            val parts = candidates.getJSONObject(0)
                .optJSONObject("content")
                ?.optJSONArray("parts")
                ?: return null
            if (parts.length() == 0) return null

            val rawText = parts.getJSONObject(0).optString("text", "").trim()
            if (rawText.isBlank()) return null

            val cleanedJson = rawText
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val json = JSONObject(cleanedJson)
            val merchant = json.optString("merchant", "").trim()
                .ifBlank { if (isEn) "Scanned Receipt" else "Struk Belanja" }
            val amount = json.optLong("amount", 0L).coerceAtLeast(0L)
            val date = json.optString("date", "").trim()
                .ifBlank { if (isEn) "Today" else "Hari ini" }
            val notes = json.optString("notes", "").trim()
            val categoryStr = json.optString("category", "GENERAL").uppercase()
            val categoryType = runCatching { CategoryType.valueOf(categoryStr) }
                .getOrDefault(CategoryType.GENERAL)
            val pocketRaw = json.optString("pocket", "").trim()
            val matchedPocket = availablePockets.firstOrNull {
                it.equals(pocketRaw, ignoreCase = true)
            }

            GeminiReceiptResult(
                merchant = merchant,
                amount = amount,
                date = date,
                notes = notes,
                suggestedPocket = matchedPocket,
                categoryType = categoryType
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun encodeUriToBase64Jpeg(context: Context, uri: Uri, maxDimension: Int = 1280): String? {
        return try {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, bounds)
            }

            var sampleSize = 1
            val maxSide = maxOf(bounds.outWidth, bounds.outHeight)
            if (maxSide > maxDimension) {
                while (maxSide / sampleSize > maxDimension) {
                    sampleSize *= 2
                }
            }

            val decodeOpts = BitmapFactory.Options().apply { inSampleSize = sampleSize }
            val bitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, decodeOpts)
            } ?: return null

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos)
            bitmap.recycle()
            Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun readStream(stream: java.io.InputStream?): String {
        if (stream == null) return ""
        return stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
    }

    private fun parseApiError(statusCode: Int, errorBody: String, isEn: Boolean): String {
        val apiMessage = runCatching {
            JSONObject(errorBody).optJSONObject("error")?.optString("message").orEmpty()
        }.getOrDefault("")

        return when (statusCode) {
            400, 401, 403 -> if (isEn) {
                "Invalid or unauthorized Gemini API Key ($statusCode)."
            } else {
                "API Key Gemini tidak valid atau ditolak ($statusCode)."
            }
            429 -> if (isEn) {
                "Gemini API quota exceeded (429). Try again later."
            } else {
                "Kuota Gemini API habis (429). Coba beberapa saat lagi."
            }
            else -> if (apiMessage.isNotBlank()) {
                "Gemini API ($statusCode): $apiMessage"
            } else {
                if (isEn) "Gemini API error ($statusCode)." else "Error Gemini API ($statusCode)."
            }
        }
    }
}
