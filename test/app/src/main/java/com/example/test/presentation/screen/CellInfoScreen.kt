package com.example.test.presentation.screen

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.test.presentation.viewmodel.CellInfoViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.text.style.TextAlign
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CellInfoScreen(
    navController: NavController? = null
) {
    val viewModel: CellInfoViewModel = hiltViewModel()
    val cellInfos = viewModel.cellInfoList.collectAsState(initial = emptyList())
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val activity = (context as? Activity)
    val listState = rememberLazyListState()

    LaunchedEffect(cellInfos.value) {
        if (cellInfos.value.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("📡 مشاهده اطلاعات سلول", style = MaterialTheme.typography.titleLarge,textAlign = TextAlign.Center) },
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { viewModel.collectAndSaveCellInfo(context) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("دریافت اطلاعات", color = MaterialTheme.colorScheme.onPrimary)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = {
                        viewModel.clearAllCellInfo()
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("✅ لیست پاک شد")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("پاک‌سازی لیست", color = MaterialTheme.colorScheme.onError)
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(cellInfos.value, key = { it.timestamp }) { cell ->
                    val simColor = when (cell.plmnId?.takeLast(1)) {
                        "1" -> MaterialTheme.colorScheme.primaryContainer
                        "2" -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }

                    val signalColor = when {
                        cell.rsrp != null && cell.rsrp!! > -95 -> MaterialTheme.colorScheme.primary
                        cell.rsrp != null && cell.rsrp!! > -110 -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.error
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .animateItemPlacement(),
                        colors = CardDefaults.cardColors(containerColor = simColor),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            InfoRow("🕓", "زمان", formatTimestamp(cell.timestamp))
                            InfoRow("📍", "مکان", "${cell.latitude}, ${cell.longitude}")
                            InfoRow("🌐", "نوع شبکه", cell.networkType.toString())
                            InfoRow("🗼", "نوع فناوری سلولی", cell.actualTechnology.toString())
                            InfoRow("📱", "PLMN", cell.plmnId.toString())
                            InfoRow("🔄", "LAC/RAC/TAC", "${cell.lac}/${cell.rac}/${cell.tac}")
                            InfoRow("📡", "Cell ID", cell.cellId.toString())
                            InfoRow("📶", "Band", "${cell.band}")
                            InfoRow("📳", "ARFCN", "${cell.arfcn}")
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "", color = Color.Transparent)
                                Row {
                                    Text(
                                        text = "قدرت سیگنال‌ها ",
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.End,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "📊", textAlign = TextAlign.End)
                                }
                            }

                            InfoRowColored("RSRP", cell.rsrp?.toString() ?: "-", signalColor)
                            InfoRowColored("RSRQ", cell.rsrq?.toString() ?: "-", signalColor)
                            InfoRowColored("RSSI", cell.rssi?.toString() ?: "-", signalColor)
                            InfoRowColored("RSCP", cell.rscp?.toString() ?: "-", signalColor)
                            InfoRowColored("Ec/No", cell.ecNo?.toString() ?: "-", signalColor)
                            InfoRowColored("RxLev", cell.rxLev?.toString() ?: "-", signalColor)

                            Divider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: String, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = value,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row {
            Text(
                text = label,
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = icon,
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun InfoRowColored(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = value,
            color = color,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = label,
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
