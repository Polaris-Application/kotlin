@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.test.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.test.utility.UploadPolicy
import com.example.test.presentation.viewmodel.MainViewModel // دقت کن این باید MainViewModel باشه
import com.example.test.utility.UploadSettingsHelper

@Composable
fun MainScreen(
    loginviewModel: LoginViewModel,
    mainViewModel: MainViewModel,
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
    val uploadHelper = remember { UploadSettingsHelper(context) }
    val lastCellTime = remember { uploadHelper.getLastCellUploadTime() }
    val lastTestTime = remember { uploadHelper.getLastTestUploadTime() }
    var lastCellUploadTime by remember { mutableStateOf(mainViewModel.uploadSettings.getLastCellUploadTime()) }
    var lastTestUploadTime by remember { mutableStateOf(mainViewModel.uploadSettings.getLastTestUploadTime()) }
    LaunchedEffect(Unit) {
        while (true) {
            lastCellUploadTime = mainViewModel.uploadSettings.getLastCellUploadTime()
            lastTestUploadTime = mainViewModel.uploadSettings.getLastTestUploadTime()
            kotlinx.coroutines.delay(60_000) // هر 60 ثانیه
        }
    }
    val snackbarHostState = remember { SnackbarHostState() }


    fun formatTime(timestamp: Long): String {
        return if (timestamp == 0L) "ارسالی تاکنون انجام نشده"
        else java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date(timestamp))
    }


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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },

        ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
            UploadSettingsCard(
                title = "ارسال اطلاعات سلول چه زمانی انجام شود؟",
                policy = mainViewModel.cellUploadPolicy.value,
                intervalMinutes = mainViewModel.cellUploadInterval.value,
                onPolicyChange = { mainViewModel.setCellUploadPolicy(it) },
                onIntervalChange = { mainViewModel.setCellUploadInterval(it) }
            )
            val formattedCellTime = if (lastCellUploadTime == 0L)
                "⏱ هنوز ارسال نشده"
            else
                formatTime(lastCellUploadTime)

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "📡 آخرین ارسال اطلاعات سلول: $formattedCellTime",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            if (mainViewModel.cellUploadPolicy.value == UploadPolicy.MANUAL) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        mainViewModel.SendCellDataNow(context)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("📡 اطلاعات سلول ارسال شد")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text("📤 ارسال اطلاعات سلول", color = MaterialTheme.colorScheme.onTertiary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            UploadSettingsCard(
                title = "ارسال نتایج تست‌ها چه زمانی انجام شود؟",
                policy = mainViewModel.testUploadPolicy.value,
                intervalMinutes = mainViewModel.testUploadInterval.value,
                onPolicyChange = { mainViewModel.setTestUploadPolicy(it) },
                onIntervalChange = { mainViewModel.setTestUploadInterval(it) }
            )

            val formattedTestTime = if (lastTestUploadTime == 0L)
                "⏱ هنوز ارسال نشده"
            else
                formatTime(lastTestUploadTime)

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "🧪 آخرین ارسال نتایج تست: $formattedTestTime",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            if (mainViewModel.testUploadPolicy.value == UploadPolicy.MANUAL) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        mainViewModel.sendAllUnsentTestResults(context)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("📤 تست‌ها ارسال شدند")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("📤 ارسال نتایج تست‌ها", color = MaterialTheme.colorScheme.onSecondary)
                }
            }



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

@Composable
fun UploadSettingsCard(
    title: String,
    policy: UploadPolicy,
    intervalMinutes: Int,
    onPolicyChange: (UploadPolicy) -> Unit,
    onIntervalChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("فقط ارسال دستی", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(
                        selected = policy == UploadPolicy.MANUAL,
                        onClick = { onPolicyChange(UploadPolicy.MANUAL) }
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ارسال خودکار هر $intervalMinutes دقیقه", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(
                        selected = policy == UploadPolicy.INTERVAL,
                        onClick = { onPolicyChange(UploadPolicy.INTERVAL) }
                    )
                }

                if (policy == UploadPolicy.INTERVAL) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 40.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Slider(
                            value = intervalMinutes.toFloat(),
                            onValueChange = { onIntervalChange(it.toInt()) },
                            valueRange = 1f..240f,
                            steps = 239,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "بازه بین ۱ تا ۲۴۰ دقیقه",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ارسال هنگام دسترسی به اینترنت", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(
                        selected = policy == UploadPolicy.WHEN_AVAILABLE,
                        onClick = { onPolicyChange(UploadPolicy.WHEN_AVAILABLE) }
                    )
                }
            }
        }
    }
}
