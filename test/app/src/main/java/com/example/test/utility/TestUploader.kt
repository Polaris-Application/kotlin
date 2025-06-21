
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

class TestUploader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: TestResultApi

) {
    suspend fun uploadResult(name: String, param: String?, result: Double) {
        withContext(Dispatchers.IO) {
            try {
                val token = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    .getString("access_token", null) ?: return@withContext

                val timestamp = SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()
                ).format(Date())

                val request = TestResultRequest(
                    name = name,
                    timestamp = timestamp,
                    test_domain = param,
                    result = result
                )

                val response = api.sendTestResult("Bearer $token", request)
                if (response.isSuccessful) {
                    Log.d("Uploader", "✅ $name result uploaded")
                } else {
                    Log.e("Uploader", "❌ Upload failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Uploader", "❌ Exception: ${e.message}")
            }
        }
    }
}
