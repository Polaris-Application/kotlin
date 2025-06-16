package com.example.test.presentation.receiver

interface SmsReceiverCallback {
    fun onSmsSent(sentTime: Long)  // زمان ارسال
    fun onSmsDelivered(sentTime: Long, deliveryTime: Long)  // زمان ارسال و تحویل
}

