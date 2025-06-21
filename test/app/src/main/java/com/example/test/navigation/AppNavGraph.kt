package com.example.test.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.test.presentation.screen.*
import com.example.test.presentation.viewmodel.LoginViewModel
import com.example.test.ui.screen.*

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: LoginViewModel,
    selectedSim: String,
    onSimSelected: (String) -> Unit,
    onClearSim: () -> Unit,
    onExit: () -> Unit
) {
    val context = LocalContext.current
    val isLoggedIn = viewModel.isPreviouslyLoggedIn(context)
    val startDestination = if (isLoggedIn) "main" else "login"

    NavHost(navController = navController, startDestination = startDestination) {

        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onNavigateToSignUp = { navController.navigate("signup") },
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("signup") {
            SignUpScreen(
                viewModel = viewModel,
                onSignUpSuccess = {
                    navController.navigate("main") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainScreen(
                selectedSim = selectedSim,
                onSimSelected = onSimSelected,
                onClearSim = onClearSim,
                onNavigateTo = { destination ->
                    when (destination) {
                        "cell_info" -> navController.navigate("cell_info")
                        "change_password" -> navController.navigate("change_password")
                        "about_us" -> navController.navigate("about_us")
                        else -> navController.navigate(destination)
                    }
                },
                onExit = {
                    onExit()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                    (context as? Activity)?.finish()
                }
            )
        }

        composable("ping_screen")     { PingTestScreen(navController) }
        composable("dns_screen")      { DNSTestScreen(navController) }
        composable("web_screen")      { WebTestScreen(navController) }
        composable("upload_screen")   { UploadTestScreen(navController) }
        composable("download_screen") { DownloadTestScreen(navController) }
        composable("sms_screen")      { SMSTestScreen(navController) }
        composable("cell_info")       { CellInfoScreen(navController) }
        composable("change_password") { ChangePasswordScreen(navController) }
        composable("about_us")        { AboutUsScreen(navController) }
    }
}
