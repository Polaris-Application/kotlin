package com.example.test.utility

import android.content.Context
import android.util.Log
import com.example.test.data.remote.TestResultApi
import com.example.test.data.remote.dto.TestResultRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import com.example.test.domain.usecase.*

class TestUploader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: TestResultApi
) {

    private suspend fun uploadResult(
        name: String,
        param: String?,
        result: Double,
        timestampMillis: Long? = null
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                .getString("access_token", null) ?: return@withContext false

            val timestamp = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()
            ).format(Date(timestampMillis ?: System.currentTimeMillis()))

            val request = TestResultRequest(
                name = name,
                timestamp = timestamp,
                test_domain = param,
                result = result
            )

            val response = api.sendTestResult("Bearer $token", request)
            if (response.isSuccessful) {
                Log.d("Uploader", "✅ $name result uploaded")
                true
            } else {
                Log.e("Uploader", "❌ Upload failed: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("Uploader", "❌ Exception: ${e.message}")
            false
        }
    }

    suspend fun sendUnsentTestResultsNow(
        context: Context,
        uploader: TestUploader,
        getUnsentPingTests: GetUnsentPingTestsUseCase,
        markPingTestsAsUploaded: MarkPingTestsAsUploadedUseCase,
        getUnsentDnsTests: GetUnsentDnsTestsUseCase,
        markDnsTestsAsUploaded: MarkDnsTestsAsUploadedUseCase,
        getUnsentWebTests: GetUnsentWebTestsUseCase,
        markWebTestsAsUploaded: MarkWebTestsAsUploadedUseCase,
        getUnsentUploadTests: GetUnsentUploadTestsUseCase,
        markUploadTestsAsUploaded: MarkUploadTestsAsUploadedUseCase,
        getUnsentDownloadTests: GetUnsentDownloadTestsUseCase,
        markDownloadTestsAsUploaded: MarkDownloadTestsAsUploadedUseCase,
        getUnsentSmsTests: GetUnsentSmsTestsUseCase,
        markSmsTestsAsUploaded: MarkSmsTestsAsUploadedUseCase,
        uploadSettings: UploadSettingsHelper
    ) = withContext(Dispatchers.IO) {
        val token = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .getString("access_token", null)

        if (token == null) {
            Log.w("MANUAL_TEST_UPLOAD", "🔐 No access token found")
            return@withContext
        }

        suspend fun <T> process(
            name: String,
            items: List<T>,
            extract: (T) -> Pair<Double, Long>,
            getId: (T) -> Long,
            markUploaded: suspend (List<Long>) -> Unit
        ) {
            val successIds = mutableListOf<Long>()
            for (item in items) {
                val (value, timestamp) = extract(item)
                val ok = uploader.uploadResult(name, null, value, timestamp)
                if (ok) successIds.add(getId(item))
            }
            if (successIds.isNotEmpty()) {
                markUploaded(successIds)
            }
        }

        val pingTests = getUnsentPingTests().filter { it.pingTime != null }
        process("ping", pingTests, { it.pingTime!!.toDouble() to it.timestamp }, { it.id.toLong() }, markPingTestsAsUploaded::invoke)

        val dnsTests = getUnsentDnsTests()
        process("dns", dnsTests, { it.dnsTime.toDouble() to it.timestamp }, { it.id.toLong() }, markDnsTestsAsUploaded::invoke)

        val webTests = getUnsentWebTests()
        process("web", webTests, { it.webResponseTime.toDouble() to it.timestamp }, { it.id.toLong() }, markWebTestsAsUploaded::invoke)

        val uploadTests = getUnsentUploadTests()
        process("upload", uploadTests, { it.uploadRate to it.timestamp }, { it.id.toLong() }, markUploadTestsAsUploaded::invoke)

        val downloadTests = getUnsentDownloadTests()
        process("download", downloadTests, { it.downloadRate to it.timestamp }, { it.id.toLong() }, markDownloadTestsAsUploaded::invoke)

        val smsTests = getUnsentSmsTests().filter { it.SMSTime != null }
        process("sms", smsTests, { it.SMSTime!!.toDouble() to it.timestamp }, { it.id }, markSmsTestsAsUploaded::invoke)

        // به‌روزرسانی آخرین زمان ارسال اگر چیزی واقعاً ارسال شد
        if (
            pingTests.isNotEmpty() ||
            dnsTests.isNotEmpty() ||
            webTests.isNotEmpty() ||
            uploadTests.isNotEmpty() ||
            downloadTests.isNotEmpty() ||
            smsTests.isNotEmpty()
        ) {
            uploadSettings.setLastTestUploadTime(System.currentTimeMillis())
            Log.d("MANUAL_TEST_UPLOAD", "✅ Some test results uploaded successfully")
        } else {
            Log.d("MANUAL_TEST_UPLOAD", "ℹ️ No test results to upload")
        }
    }
}
