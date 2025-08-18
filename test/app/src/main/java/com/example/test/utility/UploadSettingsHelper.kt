package com.example.test.utility

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

enum class UploadPolicy {
    MANUAL,
    INTERVAL,
    WHEN_AVAILABLE
}

class UploadSettingsHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("upload_prefs", Context.MODE_PRIVATE)

    fun getCellUploadPolicy(): UploadPolicy {
        val value = prefs.getString("cell_policy", UploadPolicy.INTERVAL.name)!!
        return UploadPolicy.valueOf(value)
    }

    fun setCellUploadPolicy(policy: UploadPolicy) {
        prefs.edit().putString("cell_policy", policy.name).apply()
    }

    fun getCellInterval(): Int = prefs.getInt("cell_interval", 15)

    fun setCellInterval(mins: Int) {
        prefs.edit().putInt("cell_interval", mins).apply()
    }

    fun getTestUploadPolicy(): UploadPolicy {
        val value = prefs.getString("test_policy", UploadPolicy.INTERVAL.name)!!
        return UploadPolicy.valueOf(value)
    }

    fun setTestUploadPolicy(policy: UploadPolicy) {
        prefs.edit().putString("test_policy", policy.name).apply()
    }

    fun getTestInterval(): Int = prefs.getInt("test_interval", 15)

    fun setTestInterval(mins: Int) {
        prefs.edit().putInt("test_interval", mins).apply()
    }

    fun getLastCellUploadTime(): Long {
        return prefs.getLong("last_cell_upload_time", 0L)
    }

    fun setLastCellUploadTime(timeMillis: Long) {
        prefs.edit().putLong("last_cell_upload_time", timeMillis).apply()
    }

    fun getLastTestUploadTime(): Long {
        return prefs.getLong("last_test_upload_time", 0L)
    }

    fun setLastTestUploadTime(timeMillis: Long) {
        prefs.edit().putLong("last_test_upload_time", timeMillis).apply()
    }

}
