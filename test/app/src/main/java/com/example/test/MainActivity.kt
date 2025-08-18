package com.example.test

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.test.ui.theme.TestTheme
import com.example.test.presentation.viewmodel.MainViewModel
import com.example.test.navigation.AppNavGraph
import com.example.test.presentation.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleBatteryOptimization()

        startForegroundService(Intent(this, MyForegroundService::class.java))
        startForegroundService(Intent(this, TestSchedulerService::class.java))

        setContent {
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }

            TestTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    viewModel = loginViewModel,
                    mainViewModel = viewModel,
                    selectedSim = viewModel.selectedSim.value,
                    onSimSelected = { viewModel.selectSim(it) },
                    onClearSim = { viewModel.clearSim() },
                    onExit = { finishAffinity() },
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { isDarkTheme = !isDarkTheme }
                )
            }
        }

    }

    private fun handleBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                try {
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = Uri.parse("package:$packageName")
                    }
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
