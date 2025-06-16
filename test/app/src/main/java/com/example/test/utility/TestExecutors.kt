package com.example.test.utility

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast

import com.example.test.presentation.receiver.SmsDeliveryReceiver
import com.example.test.presentation.receiver.SmsSentReceiver
import com.example.test.presentation.receiver.SmsReceiverCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URL
import kotlin.coroutines.resume
import kotlin.system.measureTimeMillis

import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError
import android.telephony.SubscriptionManager
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object TestExecutors {

    suspend fun runPing(address: String): Long = withContext(Dispatchers.IO) {
        try {
            val process = ProcessBuilder("ping", "-c", "4", address)
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader().use { it.readText() }
            process.waitFor()

            val match = Regex("avg = ([\\d.]+)").find(output)
                ?: Regex("= [\\d.]+/([\\d.]+)/").find(output)

            match?.groupValues?.get(1)?.toDoubleOrNull()?.toLong() ?: -1L
        } catch (e: Exception) {
            -1L
        }
    }

    suspend fun runDns(domain: String): Long = withContext(Dispatchers.IO) {
        try {
            val cleanDomain = domain.replace("https://", "")
                .replace("http://", "")
                .replace("/", "")
            measureTimeMillis {
                InetAddress.getByName(cleanDomain)
            }
        } catch (e: Exception) {
            -1L
        }
    }

    suspend fun runWebTest(urlStr: String): Long = withContext(Dispatchers.IO) {
        try {
            val url = URL(urlStr)
            val start = System.nanoTime()

            (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 10000
                readTimeout = 10000
                inputStream.buffered().readBytes()
                disconnect()
            }

            val end = System.nanoTime()
            (end - start) / 1_000_000
        } catch (e: Exception) {
            -1L
        }
    }


    suspend fun runHttpDownloadTest(): Double = suspendCancellableCoroutine { continuation ->

        val downloadUrl = "https://ipv4.download.thinkbroadband.com/1MB.zip" // Download test URL
        val speedTestSocket = SpeedTestSocket()

        var finalTransferRate: Double = -1.0
        var isResumed = false // Prevent multiple continuation.resume calls

        val listener = object : ISpeedTestListener {
            override fun onCompletion(report: SpeedTestReport) {
                finalTransferRate =( report.transferRateBit.toDouble()) / (1_024 * 1_024)  // Convert to Mbps
                Log.d("DownloadTest", "[COMPLETED] Rate in Mbps: $finalTransferRate")
                checkAndResume()
            }

            override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                Log.e("DownloadTest", "Error during download: $errorMessage")
                finalTransferRate = -1.0 // Return negative value in case of error
                checkAndResume()
            }

            override fun onProgress(percent: Float, report: SpeedTestReport) {
                val transferRate = report.transferRateBit // Speed in bits per second
                Log.d("DownloadTest", "[PROGRESS] Progress: $percent%, Rate in bit/s: $transferRate")
            }

            private fun checkAndResume() {
                if (!isResumed) {
                    isResumed = true
                    continuation.resume(finalTransferRate)
                }
            }
        }

        speedTestSocket.addSpeedTestListener(listener)
        speedTestSocket.startDownload(downloadUrl)

        continuation.invokeOnCancellation {
            speedTestSocket.closeSocket() // Stop the download test on cancellation
        }
    }


    suspend fun runHttpUploadTest(): Double = withContext(Dispatchers.IO) {
        val uploadUrl = URL("http://speedtest.rd.ks.cox.net:8080/speedtest/upload.php")
        val fileData = ByteArray(1 * 1024 * 1024) { 1 } // داده 1 مگابایتی

        try {
            val connection = uploadUrl.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/octet-stream")
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val startTime = System.currentTimeMillis()

            connection.outputStream.use { output ->
                output.write(fileData)
                output.flush()
            }

            val responseCode = connection.responseCode
            val endTime = System.currentTimeMillis()

            if (responseCode == 200) {
                val durationSeconds = (endTime - startTime) / 1000.0
                val speedMbps = ((fileData.size * 8) / 1_000_000.0) / durationSeconds
                Log.d("UploadTest", "Upload completed in $durationSeconds seconds, speed: $speedMbps Mbps")
                return@withContext speedMbps
            } else {
                Log.e("UploadTest", "Server responded with code: $responseCode")
                return@withContext -1.0
            }

        } catch (e: Exception) {
            Log.e("UploadTest", "Upload failed: ${e.message}")
            return@withContext -1.0
        }
    }

//    suspend fun runHttpUploadTest(): Double = suspendCancellableCoroutine { continuation ->
//
//        val uploadUrl = "https://ipv4.download.thinkbroadband.com/1MB.zip" // Upload test URL
//        val fileData = ByteArray(1 * 1024 * 1024) { 1 } // Example data to upload (1MB)
//
//        val speedTestSocket = SpeedTestSocket()
//
//        var finalTransferRate: Double = -1.0
//        var isResumed = false // Prevent multiple continuation.resume calls
//
//        val listener = object : ISpeedTestListener {
//            override fun onCompletion(report: SpeedTestReport) {
//                finalTransferRate = (report.transferRateBit.toDouble()) / (1_024 * 1_024) // Convert to Mbps
//                Log.d("UploadTest", "[COMPLETED] Rate in Mbps: $finalTransferRate")
//                checkAndResume()
//            }
//
//            override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
//                Log.e("UploadTest", "Error during upload: $errorMessage")
//                finalTransferRate = -1.0 // Return negative value in case of error
//                checkAndResume()
//            }
//
//            override fun onProgress(percent: Float, report: SpeedTestReport) {
//                val transferRate = report.transferRateBit // Speed in bits per second
//                Log.d("UploadTest", "[PROGRESS] Progress: $percent%, Rate in bit/s: $transferRate")
//            }
//
//            private fun checkAndResume() {
//                if (!isResumed) {
//                    isResumed = true
//                    continuation.resume(finalTransferRate)
//                }
//            }
//        }
//
//        speedTestSocket.addSpeedTestListener(listener)
//        speedTestSocket.startUpload(uploadUrl, fileData.size)  // Start the upload with the size of the file data
//
//        continuation.invokeOnCancellation {
//            speedTestSocket.closeSocket() // Stop the upload test on cancellation
//        }
//    }




    suspend fun runSmsTest(
        context: Context,
        phoneNumber: String,
        testId: Long
    ): SmsTestResult = suspendCancellableCoroutine { continuation ->

        // بررسی مجوزهای لازم
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "مجوز ارسال پیامک وجود ندارد!", Toast.LENGTH_LONG).show()
            continuation.resume(SmsTestResult(-1, -1)) // خطا در دسترسی به پیامک
            return@suspendCancellableCoroutine
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "مجوز دسترسی به اطلاعات سیم‌کارت وجود ندارد!", Toast.LENGTH_LONG).show()
            continuation.resume(SmsTestResult(-1, -1)) // خطا در دسترسی به سیم‌کارت
            return@suspendCancellableCoroutine
        }

        // چک کردن مجوز موقعیت مکانی در صورت نیاز
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "مجوز موقعیت مکانی وجود ندارد!", Toast.LENGTH_LONG).show()
            continuation.resume(SmsTestResult(-1, -1)) // خطا در دسترسی به موقعیت مکانی
            return@suspendCancellableCoroutine
        }

        val simPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val selectedSim = simPref.getString("selected_sim", "SIM1")

        val subManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val subscriptionList = subManager.activeSubscriptionInfoList ?: emptyList()

        val subId = when (selectedSim) {
            "SIM2" -> subscriptionList.getOrNull(1)?.subscriptionId
            else -> subscriptionList.getOrNull(0)?.subscriptionId
        } ?: run {
            Toast.makeText(context, "خطا در دریافت اطلاعات سیم‌کارت!", Toast.LENGTH_LONG).show()
            Log.e("CellInfoCollector", "❌ Failed to resolve subscription ID")
            continuation.resume(SmsTestResult(-1, -1))
            return@suspendCancellableCoroutine
        }

        // اگر نسخه اندروید 12 یا بالاتر بود از createForSubscriptionId استفاده می‌کنیم
        val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(SmsManager::class.java)?.createForSubscriptionId(subId)
        } else {
            SmsManager.getDefault() // برای نسخه‌های قدیمی‌تر از getSmsManagerForSubscriptionId
        }

        val message = "Test message from Polaris"
        var sentTime1: Long? = null
        var deliveryTime1: Long? = null
        var isResumed = false

        val callback = object : SmsReceiverCallback {
            override fun onSmsSent(sentTime: Long) {
                sentTime1 = sentTime
                checkAndResume()
            }

            override fun onSmsDelivered(sentTime: Long, deliveryTime: Long) {
                deliveryTime1 = deliveryTime
                checkAndResume()
            }

            private fun checkAndResume() {
                if (sentTime1 != null && deliveryTime1 != null && !isResumed) {
                    isResumed = true
                    continuation.resume(SmsTestResult(sentTime1!!, deliveryTime1!!))
                }
            }
        }

        val smsSentReceiver = SmsSentReceiver(callback)
        val smsDeliveredReceiver = SmsDeliveryReceiver(callback)

        context.registerReceiver(smsSentReceiver, IntentFilter("SMS_SENT"), Context.RECEIVER_EXPORTED)
        context.registerReceiver(smsDeliveredReceiver, IntentFilter("SMS_DELIVERED"), Context.RECEIVER_EXPORTED)

        val sentIntent = PendingIntent.getBroadcast(
            context, 0, Intent("SMS_SENT").putExtra("testId", testId), PendingIntent.FLAG_IMMUTABLE
        )
        val deliveredIntent = PendingIntent.getBroadcast(
            context, 0, Intent("SMS_DELIVERED").putExtra("testId", testId), PendingIntent.FLAG_IMMUTABLE
        )

        smsManager?.sendTextMessage(phoneNumber, null, message, sentIntent, deliveredIntent)
    }




}

data class SmsTestResult(
    val sentTime: Long,
    val deliveryTime: Long
)

