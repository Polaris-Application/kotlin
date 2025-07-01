package com.example.test.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.test.presentation.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToSignUp: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val loginState = viewModel.loginState

    LaunchedEffect(loginState) {
        loginState?.let { result ->
            result.onSuccess {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            }.onFailure {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "برای ورود لطفاً شماره موبایل و رمز عبوری که با آن‌ها در سایت ثبت‌نام کرده‌اید را وارد کنید",
            textAlign = TextAlign.Right,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
                phoneError = viewModel.getPhoneValidationError(it)
            },
            label = { Text("شماره موبایل") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
            isError = phoneError != null,
            modifier = Modifier.fillMaxWidth()
        )
        phoneError?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = viewModel.getPasswordValidationError(it)
            },
            label = { Text("رمز عبور") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            isError = passwordError != null,
            modifier = Modifier.fillMaxWidth()
        )
        passwordError?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val phoneValid = viewModel.getPhoneValidationError(phoneNumber) == null
                val passValid = viewModel.getPasswordValidationError(password) == null

                if (phoneValid && passValid) {
                    viewModel.loginUser(context, phoneNumber, password)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ورود ▶️")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onNavigateToSignUp,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("اکانت نداری؟ ثبت‌نام کن", color = MaterialTheme.colorScheme.primary)
        }
    }
}
