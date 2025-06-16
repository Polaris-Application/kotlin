package com.example.test.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.test.presentation.screen.AboutUsScreen
import com.example.test.presentation.screen.CellInfoScreen
import com.example.test.presentation.screen.ChangePasswordScreen
import com.example.test.presentation.screen.DNSTestScreen
import com.example.test.presentation.screen.DownloadTestScreen
import com.example.test.presentation.screen.PingTestScreen
import com.example.test.presentation.screen.SMSTestScreen
import com.example.test.presentation.screen.UploadTestScreen
import com.example.test.presentation.screen.WebTestScreen
import com.example.test.ui.screen.*

@Composable
fun AppNavGraph(
    navController: NavHostController,
    selectedSim: String,
    onSimSelected: (String) -> Unit,
    onClearSim: () -> Unit,
    onExit: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(
                selectedSim = selectedSim,
                onSimSelected = onSimSelected,
                onClearSim = onClearSim,
                onNavigateTo = { destination ->
                    when (destination) {
                        "cell_info" -> {
                             navController.navigate("cell_info")
                        }
                        "change_password" -> {
                            navController.navigate("change_password")
                        }
                        "about_us" -> {
                            navController.navigate("about_us")
                        }
                        else -> {
                            navController.navigate(destination)
                        }
                    }
                },
                onExit = onExit
            )
        }

        composable("ping_screen")     { PingTestScreen(navController)
            }
        composable("dns_screen")      { DNSTestScreen(navController)
            }
        composable("web_screen")      { WebTestScreen(navController)
            }
        composable("upload_screen")   { UploadTestScreen(navController)
            }
        composable("download_screen") { DownloadTestScreen(navController) }
        composable("sms_screen")      { SMSTestScreen(navController)
            }

        composable("cell_info") { CellInfoScreen(navController) }
        composable("change_password") { ChangePasswordScreen(navController) }
        composable("about_us") { AboutUsScreen(navController) }
    }
}
