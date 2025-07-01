@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.test.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.test.presentation.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    loginviewModel: LoginViewModel,
    selectedSim: String,
    onSimSelected: (String) -> Unit,
    onClearSim: () -> Unit,
    onNavigateTo: (String) -> Unit,
    onExit: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showMenuSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "پنل اصلی",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Right,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    Card(
                        shape = RoundedCornerShape(50),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
                    ) {
                        IconToggleButton(
                            checked = isDarkTheme,
                            onCheckedChange = { onToggleTheme() }
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                contentDescription = "تغییر تم",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showMenuSheet = true
                        coroutineScope.launch { sheetState.show() }
                    }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "منو",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "کدوم سیم‌کارت رو می‌خوای پایش کنیم؟",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedSim == "SIM1",
                        onClick = { onSimSelected("SIM1") },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text("سیم‌کارت ۱", color = MaterialTheme.colorScheme.onBackground)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedSim == "SIM2",
                        onClick = { onSimSelected("SIM2") },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text("سیم‌کارت ۲", color = MaterialTheme.colorScheme.onBackground)
                }
            }

            Text(
                text = "$selectedSim: سیم کارت انتخاب شده",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    // ✅ ModalBottomSheet به جای DropdownMenu
    if (showMenuSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showMenuSheet = false
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End
            ) {
                menuItem("مشاهده اطلاعات سلول", "📶") {
                    showMenuSheet = false; onNavigateTo("cell_info")
                }
                menuItem("Ping تست", "📡") {
                    showMenuSheet = false; onNavigateTo("ping_screen")
                }
                menuItem("DNS تست", "🌐") {
                    showMenuSheet = false; onNavigateTo("dns_screen")
                }
                menuItem("Upload تست", "📤") {
                    showMenuSheet = false; onNavigateTo("upload_screen")
                }
                menuItem("Download تست", "📥") {
                    showMenuSheet = false; onNavigateTo("download_screen")
                }
                menuItem("SMS تست", "✉️") {
                    showMenuSheet = false; onNavigateTo("sms_screen")
                }
                menuItem("Web تست", "🌍") {
                    showMenuSheet = false; onNavigateTo("web_screen")
                }
                menuItem("درباره ما", "👤") {
                    showMenuSheet = false; onNavigateTo("about_us")
                }
                menuItem("خروج از حساب کاربری", "🚪") {
                    showMenuSheet = false
                    loginviewModel.logout(context) // ← context باید فرستاده بشه
                    onNavigateTo("login_screen")
                }
            }
        }
    }
}

@Composable
fun menuItem(text: String, emoji: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(text = "$text ", textAlign = TextAlign.Right)
            Text(text = emoji)
        }
    }
}
