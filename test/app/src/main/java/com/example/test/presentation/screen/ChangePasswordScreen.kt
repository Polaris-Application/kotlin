package com.example.test.presentation.screen

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.test.presentation.viewmodel.LoginViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import android.app.Activity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    navController: NavController? = null
) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = hiltViewModel()  // دریافت ViewModel از Hilt
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    // تابعی برای انجام تغییر رمز عبور
    val onPasswordChanged = {
        Toast.makeText(context, "رمز عبور با موفقیت تغییر کرد", Toast.LENGTH_SHORT).show()
        if (navController != null) {
            navController.popBackStack()  // برگشت به صفحه قبلی با استفاده از NavController
        } else {
            (context as? Activity)?.finish()  // در صورتی که navController نباشد، از finish برای بستن Activity استفاده می‌کنیم
        }
    }

    // Activity برای پایان دادن به صفحه در صورت لزوم
    val activity = context as? Activity

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("   تغییر رمز عبور", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (navController != null) {
                            navController.popBackStack()
                        } else {
                            activity?.finish()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "رمز عبور قبلی و جدید رو وارد کن",
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = oldPass,
                onValueChange = { oldPass = it },
                label = { Text("رمز قبلی") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newPass,
                onValueChange = { newPass = it },
                label = { Text("رمز جدید") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                if (!viewModel.verifyPassword(context, oldPass)) {
                    error = "رمز قبلی اشتباهه"
                } else if (newPass.length < 4) {
                    error = "رمز جدید باید حداقل ۴ کاراکتر باشه"
                } else {
                    viewModel.updatePassword(context, newPass)
                    onPasswordChanged()  // فراخوانی تابع تغییر رمز
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("تغییر رمز عبور 🔒")
            }
        }
    }
}
