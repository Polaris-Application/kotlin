package com.example.test


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.test.presentation.screen.LoginScreen
import com.example.test.presentation.viewmodel.LoginViewModel
import com.example.test.ui.theme.TestTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

                val vm: LoginViewModel = viewModel()
                LoginScreen(viewModel = vm)

        }
    }
}
