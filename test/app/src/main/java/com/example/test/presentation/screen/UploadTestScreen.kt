package com.example.test.presentation.screen

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.test.data.local.entity.*
import com.example.test.presentation.viewmodel.TestViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadTestScreen(
    navController: NavController? = null
) {
    val viewModel: TestViewModel = hiltViewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    var showAddPanel by remember { mutableStateOf(false) }
    var repeatInterval by remember { mutableStateOf("هر 1 دقیقه") }

    val repeatOptions = listOf("هر 1 دقیقه", "هر 5 دقیقه", "هر 15 دقیقه", "هر 1 ساعت", "تکرار نشود")
    val allTests by viewModel.uploadTests.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Upload تست‌های", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController?.popBackStack() ?: backDispatcher?.onBackPressed()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddPanel = !showAddPanel }) {
                        Icon(Icons.Filled.Add, contentDescription = "افزودن تست", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            if (showAddPanel) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "تناوب اجرا",
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.titleSmall
                        )

                        repeatOptions.forEach { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(option)
                                Spacer(modifier = Modifier.width(4.dp))
                                RadioButton(
                                    selected = repeatInterval == option,
                                    onClick = { repeatInterval = option },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }

                        Text(
                            "برای این تست نیازی به پارامتر ورودی نیست",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    viewModel.addTest(
                                        NetworkTest(type = "upload", param = "", repeatInterval = repeatInterval),
                                        ""
                                    )
                                    showAddPanel = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text("ذخیره تست")
                            }
                        }
                    }
                }
            }

            Divider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                thickness = 1.dp
            )

            Text(
                "تست‌های تعریف‌شده",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.End
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(allTests) { test ->
                    var expanded by remember { mutableStateOf(false) }
                    val results by viewModel.getResultsForTest(test.id, test.type).collectAsState(initial = emptyList())

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (test.isPaused)
                                MaterialTheme.colorScheme.errorContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expanded = !expanded }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = test.repeatInterval, style = MaterialTheme.typography.bodyLarge)
                                    }
                                    IconButton(onClick = {
                                        if (test.isPaused) viewModel.resumeTest(test.id)
                                        else viewModel.pauseTest(test.id)
                                    }) {
                                        Icon(
                                            imageVector = if (test.isPaused) Icons.Filled.PlayArrow else Icons.Filled.Close,
                                            contentDescription = if (test.isPaused) "ادامه تست" else "توقف تست",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(onClick = { viewModel.removeTest(test) }) {
                                        Icon(
                                            Icons.Filled.Delete,
                                            contentDescription = "حذف تست",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                    Icon(
                                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                        contentDescription = "نمایش نتایج",
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }

                            if (expanded) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    results.filterIsInstance<HttpUploadTestEntity>().reversed().forEach { result ->
                                        val formattedTimestamp = SimpleDateFormat(
                                            "yyyy-MM-dd HH:mm:ss",
                                            Locale.getDefault()
                                        ).format(Date(result.timestamp))
                                        Text(
                                            "⏱ $formattedTimestamp\n📤 Upload Rate: ${result.uploadRate} Mbps\n",
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
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
