package com.example.test.presentation.screen

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.test.R
import com.example.test.presentation.viewmodel.WelcomeViewModel

@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel,
    activity: Activity,
    onPermissionsGranted: () -> Unit
) {
    viewModel.onPermissionsGranted = onPermissionsGranted

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background // ← کرم روشن در لایت، مشکی در دارک
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .height(240.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "\u202Bبه Polaris خوش اومدی 😊",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary // نارنجی سفارشی تو
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "\u202Bتوی اپ ما می‌تونی اطلاعات سلول شبکه رو پایش کنی و چندین تست کاربردی شبکه رو انجام بدی\n:) همین الان شروع کن",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground // متن خاکستری تیره یا روشن بر اساس تم
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.requestNextMissingPermission(activity)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // نارنجی
                    contentColor = MaterialTheme.colorScheme.onPrimary  // سفید یا مشکی
                )
            ) {
                Text(
                    text = "📲 دادن همه‌ی دسترسی‌ها و شروع",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
