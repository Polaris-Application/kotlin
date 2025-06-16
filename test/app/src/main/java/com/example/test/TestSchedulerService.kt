package com.example.test

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.test.data.local.entity.*
import com.example.test.domain.usecase.AddTestResultUseCase
import com.example.test.domain.usecase.GetAllTestsUseCase
import com.example.test.domain.usecase.GetResultsByTestIdUseCase
import com.example.test.utility.TestExecutors
import com.example.test.domain.usecase.NetworkTestUseCases
import com.example.test.utility.TestExecutors.runSmsTest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

@AndroidEntryPoint
class TestSchedulerService : Service() {

    @Inject lateinit var getAllTestsUseCase: GetAllTestsUseCase
    @Inject lateinit var addTestResultUseCase: AddTestResultUseCase
    @Inject lateinit var networkTestUseCases: NetworkTestUseCases
    @Inject lateinit var getResultsByTestIdUseCase: GetResultsByTestIdUseCase // تزریق GetResultsByTestIdUseCase


    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private val jobs = mutableMapOf<Long, Job>()
    private var lastSmsSentTime: Long = 0L // زمان آخرین ارسال پیامک

    private val testUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "ACTION_UPDATE_TESTS") {
                restartAllScheduledTests()
            }
        }
    }

    override fun onCreate() {
        Log.d("TestService", "Service started")
        super.onCreate()
        createNotificationChannel()
        startForeground(1, buildNotification())

        registerReceiver(
            testUpdateReceiver,
            IntentFilter("ACTION_UPDATE_TESTS"),
            Context.RECEIVER_NOT_EXPORTED
        )

        startScheduling()
    }

    private fun startScheduling() {
        serviceScope.launch {
            getAllTestsUseCase().collect { testList ->

                val currentIds = testList.map { it.id }.toSet()
                val runningIds = jobs.keys.toSet()

                // Cancel jobs for tests that no longer exist
                (runningIds - currentIds).forEach { id ->
                    jobs[id]?.cancel()
                    jobs.remove(id)
                }

                // Start jobs for new tests
                testList.forEach { test ->
                    if (jobs.containsKey(test.id)) return@forEach
                    val interval = parseInterval(test.repeatInterval)
                    if (interval > 0) {
                        val job = launch {
                            while (isActive) {
                                runTestOnce(test.id, test.type, test.param)
                                delay(interval)
                            }
                        }
                        jobs[test.id] = job
                    } else {
                        runTestOnce(test.id, test.type, test.param)
                    }
                }
            }
        }
    }

    private fun restartAllScheduledTests() {
        jobs.values.forEach { it.cancel() }
        jobs.clear()
        startScheduling()
    }

    private suspend fun runTestOnce(testId: Long, type: String, param: String?) {
        Log.d("TestService", "Running test $type with param $param")
        if (shouldSkipTest(testId, type)) {
            Log.d("TestService", "Test $type with ID $testId already executed, skipping.")
            return
        }
        val resultValue = when (type) {
            "ping" -> TestExecutors.runPing(param ?: "").toString()
            "dns" -> TestExecutors.runDns(param ?: "").toString()
            "web" -> TestExecutors.runWebTest(param ?: "").toString()
            "upload" -> TestExecutors.runHttpUploadTest().toString()
            "download" -> TestExecutors.runHttpDownloadTest().toString()
            "sms" -> {
                val smsResult = runSmsTest(applicationContext, param ?: "", testId)
                Log.d("SmsTest", "SMS delivered at: $smsResult")
                // ذخیره نتیجه SMS در دیتابیس
                val timestamp = System.currentTimeMillis()

                val smsTestEntity = SMSTestEntity(
                    testId = testId,
                    timestamp = timestamp,
                    sentTime = smsResult.sentTime,
                    SMSTime = smsResult.deliveryTime - smsResult.sentTime,
                    deliveryTime = smsResult.deliveryTime
                )

                // ذخیره نتیجه تست SMS در دیتابیس
                addTestResultUseCase(smsTestEntity)
                return@runTestOnce // برمی‌گردیم چون SMS به صورت جداگانه ذخیره می‌شود
            }
            else -> "-1"
        }

        // اگر resultValue برابر با "-1" بود یعنی هیچ داده‌ای ذخیره نمی‌شود
        if (resultValue != "-1") {
            val timestamp = System.currentTimeMillis()

            // ذخیره‌سازی نتیجه براساس نوع تست
            when (type) {
                "ping" -> addTestResultUseCase(
                    PingTestEntity(testId = testId, timestamp = timestamp, pingTime = resultValue.toLong())
                )
                "dns" -> addTestResultUseCase(
                    DNSTestEntity(testId = testId, timestamp = timestamp, dnsTime = resultValue.toLong())
                )
                "web" -> addTestResultUseCase(
                    WebTestEntity(testId = testId, timestamp = timestamp, webResponseTime = resultValue.toLong())
                )
                "upload" -> addTestResultUseCase(
                    HttpUploadTestEntity(testId = testId, timestamp = timestamp, uploadRate = resultValue.toDouble())
                )
                "download" -> addTestResultUseCase(
                    HttpDownloadTestEntity(testId = testId, timestamp = timestamp, downloadRate = resultValue.toDouble())
                )
                "sms" -> {
                    // نتیجه SMS در اینجا ذخیره نمی‌شود چون در runSmsTest ذخیره می‌شود
                }
                else -> { /* Handle other cases */ }
            }
        }
    }

    private suspend fun shouldSkipTest(testId: Long, type: String): Boolean {

        val test = networkTestUseCases.getTestById(testId).firstOrNull()
        if (test == null) return true // تستی وجود نداره

        // اذا paused
        if (test.isPaused == true) {
            Log.d("TestService", "Test $type with ID $testId is paused.")
            return true
        }
        if (test != null && test.repeatInterval == "تکرار نشود") {

            val existingResult = networkTestUseCases.getResultsByTestId(testId, type).firstOrNull()

            return existingResult != null && existingResult.isNotEmpty()
        }

        return false
    }

    private suspend fun getResultsByTestId(testId: Long, testType: String): List<Any>? {

        return getResultsByTestIdUseCase(testId, testType).firstOrNull()
    }


    private fun parseInterval(interval: String): Long {
        return when (interval.trim()) {
            "هر 1 دقیقه" -> 60_000L
            "هر 5 دقیقه" -> 5 * 60_000L
            "هر 15 دقیقه" -> 15 * 60_000L
            "هر 1 ساعت" -> 60 * 60_000L
            else -> 0L
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "test_scheduler_channel",
                "Test Scheduler Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, "test_scheduler_channel")
            .setContentTitle("در حال اجرای تست‌ها")
            .setContentText("سرویس تست شبکه فعال است")
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .build()
    }

    override fun onDestroy() {
        unregisterReceiver(testUpdateReceiver)
        jobs.values.forEach { it.cancel() }
        jobs.clear()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null



}

