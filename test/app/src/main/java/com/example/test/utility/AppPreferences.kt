package com.example.test.utility

import android.content.Context


fun isFirstRun(context: Context): Boolean {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return prefs.getBoolean("first_run", true)
}

fun setFirstRunComplete(context: Context) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    prefs.edit().putBoolean("first_run", false).apply()
}
