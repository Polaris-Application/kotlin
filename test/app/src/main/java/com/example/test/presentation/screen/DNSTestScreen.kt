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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material.icons.filled.Close

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DNSTestScreen(
    navController: NavController? = null
) {
    val viewModel: TestViewModel = hiltViewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    var showAddPanel by remember { mutableStateOf(false) }
    var selectedTestType by remember { mutableStateOf("dns") }
    var repeatInterval by remember { mutableStateOf("هر 1 دقیقه") }

    // فیلدهای مربوط به تست‌ها
    var dnsServer by remember { mutableStateOf(TextFieldValue("example.com")) }

    val repeatOptions = listOf("هر 1 دقیقه", "هر 5 دقیقه", "هر 15 دقیقه", "هر 1 ساعت", "تکرار نشود")
    val allTests by viewModel.dnsTests.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(" ِDNS تست های ", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController?.popBackStack() ?: backDispatcher?.onBackPressed()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddPanel = !showAddPanel }) {
                        Icon(Icons.Filled.Add, contentDescription = "افزودن تست")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (showAddPanel) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // کاهش فاصله بین گزینه‌های زمان
                    Text("تناوب اجرا", textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                    repeatOptions.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 0.1.dp) // کاهش padding
                        ) {
                            Text(option)
                            Spacer(modifier = Modifier.width(2.dp))
                            RadioButton(
                                selected = repeatInterval == option,
                                onClick = { repeatInterval = option }
                            )
                        }
                    }

                    // نمایش فیلد‌ها برای هر تست
                    when (selectedTestType) {
                        "dns" -> {
                            OutlinedTextField(
                                value = dnsServer,
                                onValueChange = { dnsServer = it },
                                label = { Text("DNS Server", textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth()) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(onClick = {
                            val isInputValid = dnsServer.text.isNotBlank()
                            if (!isInputValid) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("پارامتر ورودی معتبر نیست")
                                }
                            } else {
                                val param = dnsServer.text
                                viewModel.addTest(
                                    NetworkTest(type = selectedTestType, param = param, repeatInterval = repeatInterval),
                                    param
                                )
                                showAddPanel = false
                            }
                        }) {
                            Text("ذخیره تست")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = Color.Gray, thickness = 1.dp)
            Text("تست‌های تعریف‌شده", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(allTests) { test ->
                    var expanded by remember { mutableStateOf(false) }
                    val results by viewModel.getResultsForTest(test.id, test.type).collectAsState(initial = emptyList())

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expanded = !expanded }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween // همین جا آیکون‌ها رو در یک خط قرار می‌دهیم
                            ) {

                                // در اینجا آیکون حذف و آیکون کشویی در یک Row قرار می‌گیرند
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp), // فاصله بین آیکون‌ها
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
//                                    Column(modifier = Modifier.weight(1f)) {
//                                        Text("Type: ${test.type}")
//                                        Text("Param: ${test.param}")
//                                        Text("Repeat Interval: ${test.repeatInterval}")
//                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${test.param}")
                                        Text("${test.repeatInterval}")
                                    }
                                    IconButton(onClick = {
                                        if (test.isPaused) viewModel.resumeTest(test.id)
                                        else viewModel.pauseTest(test.id)
                                    }) {
                                        Icon(
                                            imageVector = if (test.isPaused) {
                                                Icons.Filled.PlayArrow
                                            } else {
                                                Icons.Filled.Close // <- استفاده از Stop بجای Pause
                                            },
                                            contentDescription = if (test.isPaused) "ادامه تست" else "توقف تست"
                                        )
                                    }
                                    IconButton(onClick = { viewModel.removeTest(test) }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "حذف تست")
                                    }
                                    Icon(
                                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                        contentDescription = "Toggle Expand"
                                    )

                                }
                            }

                            if (expanded) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    // نمایش نتایج برای هر تست خاص
                                    when (test.type) {
                                        "dns" -> {
                                            results.filterIsInstance<DNSTestEntity>().forEach { result ->
                                                val formattedTimestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(result.timestamp))
                                                Text("⏱ ${formattedTimestamp} \n📊 DNS Response Time: ${result.dnsTime} ms\n")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
