package ci.nsu.mobile.main.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ci.nsu.mobile.main.di.ServiceLocator
import ci.nsu.mobile.main.ui.auth.LoginScreen
import ci.nsu.mobile.main.ui.main.MainScreenWithBottomNav
import ci.nsu.mobile.main.ui.newcalculation.DepositDetailScreen
import ci.nsu.mobile.main.ui.auth.RegisterScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    serviceLocator: ServiceLocator
) {
    val loginViewModel = serviceLocator.provideLoginViewModel()
    val registerViewModel = serviceLocator.provideRegisterViewModel()
    val depositViewModel = serviceLocator.provideDepositViewModel()

    LaunchedEffect(Unit) {
        val storedUserId = serviceLocator.loadStoredUserId()
        if (storedUserId != null) {
            serviceLocator.setCurrentUserId(storedUserId)
            navController.navigate("main") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { userId ->
                    serviceLocator.setCurrentUserId(userId)
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                viewModel = loginViewModel
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = registerViewModel
            )
        }

        composable("main") {
            MainScreenWithBottomNav(
                navController = navController,
                serviceLocator = serviceLocator,
                onLogout = {
                    serviceLocator.setCurrentUserId(null)
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("calculation_detail") {
            DepositDetailScreen(
                navController = navController,
                viewModel = depositViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}