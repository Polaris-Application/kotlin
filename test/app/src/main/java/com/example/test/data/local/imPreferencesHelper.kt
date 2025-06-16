package com.example.test.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SimPreferencesHelper @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getSelectedSim(): String = prefs.getString("selected_sim", "SIM1") ?: "SIM1"

    fun setSelectedSim(sim: String) {
        prefs.edit().putString("selected_sim", sim).apply()
    }

    fun clearSim() {
        prefs.edit().remove("selected_sim").apply()
    }
}
