package com.example.test.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.test.presentation.viewmodel.LoginViewModel

@Composable
fun VerifyPasswordScreen(
    viewModel: LoginViewModel,
    onVerified: () -> Unit
) {
    val context = LocalContext.current
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "برای ادامه، رمز عبوری که قبلاً وارد کرده بودی رو بزن:",
            textAlign = TextAlign.Right,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                error = null
            },
            label = { Text("رمز عبور") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            isError = error != null,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary
            )
        )

        error?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val valid = viewModel.verifyPassword(context, password)
                if (valid) {
                    viewModel.reLoginWithProvidedPassword(
                        context = context,
                        password = password,
                        onSuccess = {
                            Toast.makeText(context, "توکن‌ها به‌روز شدند!", Toast.LENGTH_SHORT).show()
                            onVerified()
                        },
                        onError = { errorMessage ->
                            error = "خطا در به‌روزرسانی توکن: $errorMessage"
                            Toast.makeText(context, "خطا: $errorMessage", Toast.LENGTH_LONG).show()
                        }
                    )
                } else {
                    error = "رمز عبور اشتباه است"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("ادامه")
        }
    }
}
