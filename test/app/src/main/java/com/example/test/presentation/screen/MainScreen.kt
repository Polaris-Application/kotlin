@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.test.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
    selectedSim: String,
    onSimSelected: (String) -> Unit,
    onClearSim: () -> Unit,
    onNavigateTo: (String) -> Unit,
    onExit: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("پنل اصلی", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right)
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "منو", modifier = Modifier.padding(end = 8.dp))
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            menuItem("مشاهده اطلاعات سلول", "📶") {
                                menuExpanded = false
                                onNavigateTo("cell_info")
                            }
                            menuItem("تست Ping", "📡") {
                                menuExpanded = false
                                onNavigateTo("ping_screen")
                            }
                            menuItem("تست DNS", "🌐") {
                                menuExpanded = false
                                onNavigateTo("dns_screen")
                            }
                            menuItem("تست Upload", "📤") {
                                menuExpanded = false
                                onNavigateTo("upload_screen")
                            }
                            menuItem("تست Download", "📥") {
                                menuExpanded = false
                                onNavigateTo("download_screen")
                            }
                            menuItem("تست SMS", "✉️") {
                                menuExpanded = false
                                onNavigateTo("sms_screen")
                            }
                            menuItem("تست Web", "🌍") {
                                menuExpanded = false
                                onNavigateTo("web_screen")
                            }
//                            menuItem("تغییر رمز عبور", "🔑") {
//                                menuExpanded = false
//                                onNavigateTo("change_password")
//                            }

                            menuItem("درباره ما", "👤") {
                                menuExpanded = false
                                onNavigateTo("about_us")
                            }
                            menuItem("خروج از اپلیکیشن", "🚪") {
                                menuExpanded = false
                                onExit()
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "کدوم سیم‌کارت رو می‌خوای پایش کنیم؟",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedSim == "SIM1",
                        onClick = { onSimSelected("SIM1") }
                    )
                    Text("سیم‌کارت ۱")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedSim == "SIM2",
                        onClick = { onSimSelected("SIM2") }
                    )
                    Text("سیم‌کارت ۲")
                }
            }

            Text(
                text = "$selectedSim: سیم کارت انتخاب شده",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
//
//            Button(onClick = onClearSim) {
//                Text("♻️ بازنشانی سیم‌کارت")
//            }
//

        }
    }
}

@Composable
fun menuItem(text: String, emoji: String, onClick: () -> Unit) {
    DropdownMenuItem(
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "$text ", textAlign = TextAlign.Right)
                Text(text = emoji)
            }
        },
        onClick = onClick
    )
}
