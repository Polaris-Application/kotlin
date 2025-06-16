//package com.example.test.presentation.receiver
//
//import android.app.Activity
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.util.Log
//
//class SmsDeliveryReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent?) {
//        if (resultCode == Activity.RESULT_OK) {
//            val deliveryTime = System.currentTimeMillis()
//            Log.d("SmsDeliveryReceiver", "SMS delivered successfully at $deliveryTime")
//            // Optional: Save to SharedPreferences, DB, etc.
//        } else {
//            Log.e("SmsDeliveryReceiver", "SMS delivery failed with result code $resultCode")
//        }
//    }
//}
//
//
//

package com.example.test.presentation.receiver

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


import com.example.test.domain.usecase.GetTestByIdUseCase


class SmsDeliveryReceiver() : BroadcastReceiver() {

    private var callback: SmsReceiverCallback? = null

    // سازنده پیش‌فرض برای ایجاد شیء
    constructor(callback: SmsReceiverCallback) : this() {
        this.callback = callback
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val deliveryTime = System.currentTimeMillis()
        val testId = intent?.getLongExtra("testId", -1L) ?: -1L
        val sentTime = intent?.getLongExtra("sentTime", -1L) ?: -1L

        if (resultCode == Activity.RESULT_OK) {
            // ارسال زمان تحویل و زمان ارسال به callback
            callback?.onSmsDelivered(sentTime, deliveryTime)
            Log.d("SmsDeliveryReceiver", "SMS delivered successfully at $deliveryTime")
        } else {
            Log.e("SmsDeliveryReceiver", "SMS delivery failed")
        }
    }
}

