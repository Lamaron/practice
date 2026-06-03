package ci.nsu.mobile.main.ui.newcalculation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ci.nsu.mobile.main.viewmodel.DepositViewModel

@Composable
fun DepositNavHost(
    navController: NavHostController,
    viewModel: DepositViewModel,
    onSaveComplete: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "step1"
    ) {
        composable("step1") {
            Step1Screen(navController, viewModel)
        }
        composable("step2") {
            Step2Screen(navController, viewModel)
        }
        composable("result") {
            ResultScreen(
                navController = navController,
                viewModel = viewModel,
                onSaveComplete = onSaveComplete
            )
        }
    }
}