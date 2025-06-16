package com.example.test.presentation.screen

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.test.presentation.viewmodel.LoginViewModel
import com.example.test.MainActivity

@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    val context = LocalContext.current
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "شماره و رمز عبوری که با اون در سایت ما ثبت‌نام کردی (یا می‌خوای بکنی) رو در فیلدهای زیر وارد کن",
            textAlign = androidx.compose.ui.text.style.TextAlign.Right,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
                phoneError = viewModel.getPhoneValidationError(it)
            },
            label = { Text("شماره موبایل") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Phone
            ),
            isError = phoneError != null,
            modifier = Modifier.fillMaxWidth()
        )

        phoneError?.let {
            Spacer(modifier = Modifier.height(4.dp))
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
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            isError = passwordError != null,
            modifier = Modifier.fillMaxWidth()
        )

        passwordError?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // هشدار ⚠️
        Text(
            " برای دیدن نتایج باید با همین شماره و رمز عبور توی سایت لاگین کنی",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Right,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val phoneValid = viewModel.getPhoneValidationError(phoneNumber) == null
                val passValid = viewModel.getPasswordValidationError(password) == null

                if (phoneValid && passValid) {
                    viewModel.saveLoginData(context, phoneNumber, password)
                    context.startActivity(Intent(context, MainActivity::class.java))
                    (context as? android.app.Activity)?.finish()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ورود ▶️")
        }
    }
}
