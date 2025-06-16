package com.example.test.presentation.screen

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.test.data.local.entity.*
import com.example.test.presentation.viewmodel.TestViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkTestScreen(
    navController: NavController? = null
) {
//    val viewModel: TestViewModel = hiltViewModel()
//    val snackbarHostState = remember { SnackbarHostState() }
//    val coroutineScope = rememberCoroutineScope()
//    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
//
//    var showAddPanel by remember { mutableStateOf(false) }
//    var selectedTestType by remember { mutableStateOf("ping") }
//    var repeatInterval by remember { mutableStateOf("هر 1 دقیقه") }
//
//    // فیلدهای مربوط به تست‌ها
//    var pingAddress by remember { mutableStateOf(TextFieldValue("8.8.8.8")) }
//    var dnsServer by remember { mutableStateOf(TextFieldValue("8.8.8.8")) }
//    var webUrl by remember { mutableStateOf(TextFieldValue("https://example.com")) }
//    var smsNumber by remember { mutableStateOf(TextFieldValue("09123456789")) }
//
//    val repeatOptions = listOf("هر 1 دقیقه", "هر 5 دقیقه", "هر 15 دقیقه", "هر 1 ساعت", " تکرار نشود")
//    val allTests by viewModel.allTests.collectAsState()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text("لیست تست‌ها", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
//                },
//                navigationIcon = {
//                    IconButton(onClick = {
//                        navController?.popBackStack() ?: backDispatcher?.onBackPressed()
//                    }) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "بازگشت",
//                            tint = MaterialTheme.colorScheme.primary
//                        )
//                    }
//                },
//                actions = {
//                    IconButton(onClick = { showAddPanel = !showAddPanel }) {
//                        Icon(Icons.Filled.Add, contentDescription = "افزودن تست")
//                    }
//                }
//            )
//        },
//        snackbarHost = { SnackbarHost(snackbarHostState) }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .fillMaxSize()
//                .padding(16.dp)
//        ) {
//            if (showAddPanel) {
//                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                    Text("نوع تست را انتخاب کنید", textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
//                    var expanded by remember { mutableStateOf(false) }
//                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
//                        Button(onClick = { expanded = true }) {
//                            Text(selectedTestType.uppercase() + " ▼")
//                        }
//                        DropdownMenu(
//                            expanded = expanded,
//                            onDismissRequest = { expanded = false }
//                        ) {
//                            listOf("ping", "dns", "web", "upload", "download", "sms").forEach { type ->
//                                DropdownMenuItem(
//                                    text = { Text(type.uppercase()) },
//                                    onClick = {
//                                        selectedTestType = type
//                                        expanded = false
//                                    }
//                                )
//                            }
//                        }
//                    }
//
//                    Text("تناوب اجرا", textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
//                    repeatOptions.forEach { option ->
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.End,
//                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
//                        ) {
//                            Text(option)
//                            Spacer(modifier = Modifier.width(2.dp))
//                            RadioButton(
//                                selected = repeatInterval == option,
//                                onClick = { repeatInterval = option }
//                            )
//                        }
//                    }
//
//                    // نمایش فیلد‌ها برای هر تست
//                    when (selectedTestType) {
//                        "ping" -> {
//                            OutlinedTextField(
//                                value = pingAddress,
//                                onValueChange = { pingAddress = it },
//                                label = { Text("آدرس Ping", textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth()) },
//                                modifier = Modifier.fillMaxWidth()
//                            )
//                        }
//                        "dns" -> {
//                            OutlinedTextField(
//                                value = dnsServer,
//                                onValueChange = { dnsServer = it },
//                                label = { Text("DNS Server", textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth()) },
//                                modifier = Modifier.fillMaxWidth()
//                            )
//                        }
//                        "web" -> {
//                            OutlinedTextField(
//                                value = webUrl,
//                                onValueChange = { webUrl = it },
//                                label = { Text("Web URL", textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth()) },
//                                modifier = Modifier.fillMaxWidth()
//                            )
//                        }
//                        "upload", "download" -> {
//                            // تست‌های upload و download نیازی به ورودی ندارند
//                            Text("برای این تست نیازی به پارامتر ورودی نیست", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
//                        }
//                        "sms" -> {
//                            OutlinedTextField(
//                                value = smsNumber,
//                                onValueChange = { smsNumber = it },
//                                label = { Text("SMS شماره", textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth()) },
//                                modifier = Modifier.fillMaxWidth()
//                            )
//                        }
//                    }
//
//                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
//                        Button(onClick = {
//                            val isInputValid = when (selectedTestType) {
//                                "ping" -> pingAddress.text.isNotBlank()
//                                "dns" -> dnsServer.text.isNotBlank()
//                                "web" -> webUrl.text.isNotBlank()
//                                "sms" -> smsNumber.text.isNotBlank()
//                                else -> true // برای upload و download نیازی به پارامتر نیست
//                            }
//
//                            if (!isInputValid) {
//                                coroutineScope.launch {
//                                    snackbarHostState.showSnackbar("پارامتر ورودی معتبر نیست")
//                                }
//                            } else {
//                                val param = when (selectedTestType) {
//                                    "ping" -> pingAddress.text
//                                    "dns" -> dnsServer.text
//                                    "web" -> webUrl.text
//                                    "sms" -> smsNumber.text
//                                    else -> ""  // برای upload و download نیازی به پارامتر نیست
//                                }
//                                viewModel.addTest(
//                                    NetworkTest(type = selectedTestType, param = param, repeatInterval = repeatInterval),
//                                    param
//                                )
//                                showAddPanel = false
//                            }
//                        }) {
//                            Text("ذخیره تست")
//                        }
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            HorizontalDivider(color = Color.Gray, thickness = 1.dp)
//            Text("تست‌های تعریف‌شده", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
//
//            LazyColumn(modifier = Modifier.fillMaxSize()) {
//                items(allTests) { test ->
//                    var expanded by remember { mutableStateOf(false) }
//                    val results by viewModel.getResultsForTest(test.id, test.type).collectAsState(initial = emptyList())
//
//                    Card(
//                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
//                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
//                        elevation = CardDefaults.cardElevation(4.dp)
//                    ) {
//                        Column {
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .clickable { expanded = !expanded }
//                                    .padding(16.dp),
//                                horizontalArrangement = Arrangement.SpaceBetween
//                            ) {
//                                Text("${test.type} - ${test.param} - ${test.repeatInterval}", modifier = Modifier.weight(1f))
//                                IconButton(onClick = { viewModel.removeTest(test) }) {
//                                    Icon(Icons.Filled.Delete, contentDescription = "حذف تست")
//                                }
//                                Icon(
//                                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
//                                    contentDescription = "Toggle Expand"
//                                )
//                            }
//
//                            if (expanded) {
//                                Column(modifier = Modifier.padding(12.dp)) {
//                                    // نمایش نتایج برای هر تست خاص
//                                    when (test.type) {
//                                        "ping" -> {
//                                            results.filterIsInstance<PingTestEntity>().forEach { result ->
//                                                Text("⏱ ${result.timestamp}  📊 Ping Time: ${result.pingTime}")
//                                            }
//                                        }
//                                        "dns" -> {
//                                            results.filterIsInstance<DNSTestEntity>().forEach { result ->
//                                                Text("⏱ ${result.timestamp}  📊 DNS Response Time: ${result.dnsTime}")
//                                            }
//                                        }
//                                        "web" -> {
//                                            results.filterIsInstance<WebTestEntity>().forEach { result ->
//                                                Text("⏱ ${result.timestamp}  📊 Web Response Time: ${result.webResponseTime}")
//                                            }
//                                        }
//                                        "upload" -> {
//                                            results.filterIsInstance<HttpUploadTestEntity>().forEach { result ->
//                                                Text("⏱ ${result.timestamp}  📊 Upload Rate: ${result.uploadRate}")
//                                            }
//                                        }
//                                        "download" -> {
//                                            results.filterIsInstance<HttpDownloadTestEntity>().forEach { result ->
//                                                Text("⏱ ${result.timestamp}  📊 Download Rate: ${result.downloadRate}")
//                                            }
//                                        }
//                                        "sms" -> {
//                                            results.filterIsInstance<SMSTestEntity>().forEach { result ->
//                                                Text("⏱ ${result.timestamp}  📊 SMS Time: ${result.SMSTime}")
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
}
