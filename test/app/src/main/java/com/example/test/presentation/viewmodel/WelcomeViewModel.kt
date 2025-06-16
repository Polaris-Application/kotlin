package com.example.test.presentation.viewmodel

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor() : ViewModel() {

    companion object {
        const val PERMISSION_REQUEST_CODE = 1001
    }

    var currentPermission: String? = null
    var backFromSettings: Boolean = false
    var onPermissionsGranted: (() -> Unit)? = null

    fun requestNextMissingPermission(activity: Activity) {
        val missing = getMissingPermissions(activity)
        if (missing.isEmpty()) {
            onPermissionsGranted?.invoke()
            return
        }

        val next = missing.first()
        currentPermission = next
        ActivityCompat.requestPermissions(activity, arrayOf(next), PERMISSION_REQUEST_CODE)
    }

    fun onRequestPermissionsResult(
        context: Context,
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != PERMISSION_REQUEST_CODE || currentPermission == null) return

        val granted = grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
        if (granted) {
            Toast.makeText(context, "✅ دسترسی داده شد", Toast.LENGTH_SHORT).show()
            requestNextMissingPermission(context as Activity)
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, currentPermission!!)) {
                showSettingsDialog(context, currentPermission!!)
            } else {
                Toast.makeText(context, "❌ دسترسی رد شد", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onResume(context: Context) {
        if (backFromSettings && currentPermission != null) {
            backFromSettings = false
            if (ContextCompat.checkSelfPermission(context, currentPermission!!) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "✅ دسترسی از تنظیمات فعال شد", Toast.LENGTH_SHORT).show()
                requestNextMissingPermission(context as Activity)
            } else {
                Toast.makeText(context, "❌ دسترسی هنوز داده نشده", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getAllRequiredPermissions(): List<String> {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.READ_SMS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= 34) {
            permissions.addAll(
                listOf(
                    Manifest.permission.FOREGROUND_SERVICE_LOCATION,
                    Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC
                )
            )
        }
        return permissions
    }

    fun getMissingPermissions(context: Context): List<String> {
        return getAllRequiredPermissions().filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
    }

    fun allPermissionsGranted(context: Context): Boolean {
        return getMissingPermissions(context).isEmpty()
    }

    fun isFirstRun(context: Context): Boolean {
        return context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .getBoolean("is_first_run", true)
    }

    fun setFirstRunComplete(context: Context) {
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit {
            putBoolean("is_first_run", false)
        }
    }

    private fun showSettingsDialog(context: Context, permission: String) {
        val msg = when (permission) {
            Manifest.permission.ACCESS_FINE_LOCATION -> "برای دریافت دقیق موقعیت مکانی، این دسترسی لازم است."
            Manifest.permission.ACCESS_COARSE_LOCATION -> "برای دریافت موقعیت تقریبی، این دسترسی نیاز است."
            Manifest.permission.ACCESS_BACKGROUND_LOCATION -> "برای دسترسی به موقعیت در پس‌زمینه، این دسترسی باید فعال باشد."
            Manifest.permission.READ_PHONE_STATE -> "برای بررسی وضعیت شبکه، این دسترسی باید فعال باشد."
            Manifest.permission.SEND_SMS -> "برای ارسال پیامک، نیاز به این دسترسی دارید."
            Manifest.permission.READ_SMS -> "برای خواندن پیامک‌ها، این دسترسی الزامی است."
            Manifest.permission.RECEIVE_SMS -> "برای بررسی تحویل پیامک، این دسترسی الزامی است."
            Manifest.permission.ACCESS_NETWORK_STATE -> "برای تشخیص اتصال به اینترنت، این دسترسی ضروری است."
            Manifest.permission.FOREGROUND_SERVICE -> "برای اجرای پایدار سرویس در پس‌زمینه، این دسترسی باید فعال باشد."
            Manifest.permission.FOREGROUND_SERVICE_LOCATION -> "اجرای سرویس مکان‌یابی در پس‌زمینه نیاز به این دسترسی دارد."
            Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC -> "اجرای سرویس همگام‌سازی پس‌زمینه نیاز به این دسترسی دارد."
            else -> "برای ادامه استفاده از اپ، این دسترسی ضروری است."
        }

        AlertDialog.Builder(context)
            .setTitle("دسترسی رد شده")
            .setMessage("$msg\n\nبرای فعال‌سازی دستی به تنظیمات اپ برو و از بخش «Permissions» فعالش کن.")
            .setPositiveButton("رفتن به تنظیمات") { _, _ ->
                backFromSettings = true
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
            .setNegativeButton("لغو", null)
            .show()
    }
}
