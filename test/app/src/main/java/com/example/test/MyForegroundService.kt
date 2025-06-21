package com.example.test

import android.app.*
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.test.domain.usecase.InsertCellInfoUseCase
import com.example.test.util.CellInfoCollector
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
import com.example.test.boot.*
import com.example.test.data.remote.MobileDataApi
import com.example.test.data.remote.dto.MobileDataRequest
import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MyForegroundService : Service() {

    @Inject
    lateinit var insertCellInfoUseCase: InsertCellInfoUseCase
    @Inject
    lateinit var mobileDataApi: MobileDataApi

    private lateinit var wakeLock: PowerManager.WakeLock
    private val handler = Handler(Looper.getMainLooper())

    private val runnable = object : Runnable {
        override fun run() {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val entity = CellInfoCollector.collect(applicationContext)
                    insertCellInfoUseCase(entity)
                    val sharedPrefs = applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val accessToken = sharedPrefs.getString("access_token", null)

                    if (accessToken != null) {
                        val formatted = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                        val timestampStr = formatted.format(Date(entity.timestamp))

                        val request = MobileDataRequest(
                            network_type = entity.networkType ?: "Unknown",
                            timestamp = timestampStr,
                            latitude = entity.latitude,
                            longitude = entity.longitude,
                            plmn_id = entity.plmnId?.toIntOrNull(),
                            lac = entity.lac,
                            rac = entity.rac,
                            tac = entity.tac,
                            cell_id = entity.cellId,
                            band = entity.band?.toString(),
                            arfcn = entity.arfcn,
                            rsrp = entity.rsrp,
                            rsrq = entity.rsrq,
                            rssi = entity.rssi?.toDouble(),
                            rscp = entity.rscp,
                            ec_no = entity.ecNo,
                            rx_lev = entity.rxLev
                        )

                        try {
                            val response = mobileDataApi.sendMobileData("Bearer $accessToken", request)
                            if (response.isSuccessful) {
                                Log.d("CELL_SERVICE", "✅ Data sent to server successfully.")
                            } else {
                                Log.e("CELL_SERVICE", "❌ Failed to send data: ${response.code()}")
                            }
                        } catch (e: Exception) {
                            Log.e("CELL_SERVICE", "❌ Exception while sending data: ${e.message}", e)
                        }
                    }

                    Log.d("CELL_SERVICE", "✅ Data collected and saved.")
                } catch (e: Exception) {
                    Log.e("CELL_SERVICE", "❌ Error collecting/saving data: ${e.message}", e)
                }
            }
            handler.postDelayed(this, 30_000)
        }
    }

    override fun onCreate() {
        super.onCreate()

        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "MyApp::MyWakeLockTag"
        )
        wakeLock.acquire(10 * 60 * 1000L) // 10 دقیقه


        createNotificationChannel()

        val notification: Notification = NotificationCompat.Builder(this, "service_channel")
            .setContentTitle("📡 سرویس در حال اجرا")
            .setContentText("در حال ذخیره‌سازی اطلاعات شبکه و موقعیت...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handler.post(runnable)
        return START_STICKY
    }

    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        wakeLock.release()

        val alarmIntent = Intent(applicationContext, RestartReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, alarmIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + 60 * 1000, // 1 دقیقه بعد
                    pendingIntent
                )
            } else {
                Log.w("CELL_SERVICE", "⏰ App cannot schedule exact alarms on this device.")
            }
        } catch (e: SecurityException) {
            Log.e("CELL_SERVICE", "⚠️ No permission to schedule exact alarms: ${e.message}")
        }

        super.onDestroy()
    }


    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "service_channel",
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "سرویس فعال برای جمع‌آوری داده شبکه"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
