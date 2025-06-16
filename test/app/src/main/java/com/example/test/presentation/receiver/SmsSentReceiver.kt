package com.example.test.presentation.receiver
//
//import android.app.Activity
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.util.Log
//
//class SmsSentReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent?) {
//        if (resultCode == Activity.RESULT_OK) {
//            val sentTime = System.currentTimeMillis()
//            Log.d("SmsSentReceiver", "SMS sent successfully at $sentTime")
//            // Optional: Save to SharedPreferences, DB, etc.
//        } else {
//            Log.e("SmsSentReceiver", "SMS sending failed with result code $resultCode")
//        }
//    }
//}


import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.example.test.data.local.Dao.SMSTestDao
import com.example.test.data.local.entity.SMSTestEntity
import com.example.test.domain.usecase.AddTestResultUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

// داخل کلاس TestSchedulerService یا هر کجا که از این Receiverها استفاده می‌کنید

import com.example.test.domain.usecase.GetTestByIdUseCase

class SmsSentReceiver() : BroadcastReceiver() {

    private var callback: SmsReceiverCallback? = null

    // سازنده پیش‌فرض برای ایجاد شیء
    constructor(callback: SmsReceiverCallback) : this() {
        this.callback = callback
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val sentTime = System.currentTimeMillis()
        val testId = intent?.getLongExtra("testId", -1L) ?: -1L

        if (resultCode == Activity.RESULT_OK) {
            // ارسال زمان ارسال به callback
            callback?.onSmsSent(sentTime)
            Log.d("SmsSentReceiver", "SMS sent successfully at $sentTime")
        } else {
            Log.e("SmsSentReceiver", "SMS sending failed")
        }
    }
}

