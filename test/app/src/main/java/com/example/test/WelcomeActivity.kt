package com.example.test

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.test.presentation.screen.WelcomeScreen
import com.example.test.presentation.viewmodel.WelcomeViewModel
import com.example.test.presentation.viewmodel.LoginViewModel
import com.example.test.ui.theme.TestTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeActivity : ComponentActivity() {

    private val viewModel: WelcomeViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels() // ✅ این درسته

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!viewModel.isFirstRun(this) && viewModel.allPermissionsGranted(this)) {
            handleLoginRouting()
            return
        }

        setContent {
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }

            TestTheme(darkTheme = isDarkTheme) {
                WelcomeScreen(
                    viewModel = viewModel,
                    activity = this
                ) {
                    handleLoginRouting()
                }
            }
        }
    }

    private fun handleLoginRouting() {
        viewModel.setFirstRunComplete(this)

        if (loginViewModel.isPreviouslyLoggedIn(this)) {
            goToVerifyPassword()
        } else {
            goToLogin()
        }
    }

    private fun goToLogin() {
        startActivity(Intent(this, MainActivity::class.java))
        //startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun goToVerifyPassword() {
        startActivity(Intent(this, VerifyPasswordActivity::class.java))
        finish()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume(this)
    }

    @Deprecated("Used for backward compatibility with Android 9–10")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        viewModel.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }
}
