package com.example.test.data.remote.dto


data class TestResultRequest(
    val name: String,             // نوع تست مثل dns, ping
    val timestamp: String,        // زمان اجرا
    val test_domain: String?,     // یا param، بستگی به نوع تست داره
    val result: Double            // نتیجه تست
)
