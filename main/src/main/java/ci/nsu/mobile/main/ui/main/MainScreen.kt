package ci.nsu.mobile.main.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ci.nsu.mobile.main.di.ServiceLocator
import ci.nsu.mobile.main.ui.calculations.CalculationsScreen
import ci.nsu.mobile.main.ui.newcalculation.DepositNavHost
import ci.nsu.mobile.main.ui.users.UsersScreen
import ci.nsu.mobile.main.viewmodel.CalculationsViewModel
import ci.nsu.mobile.main.viewmodel.DepositViewModel
import ci.nsu.mobile.main.viewmodel.UsersViewModel

sealed class BottomNavItem(val route: String, val icon: @Composable () -> Unit, val label: String) {
    object Users : BottomNavItem("users", { Icon(Icons.Default.People, contentDescription = null) }, "Пользователи")
    object Calculations : BottomNavItem("calculations", { Icon(Icons.Default.History, contentDescription = null) }, "Мои расчёты")
    object NewCalculation : BottomNavItem("new_calculation", { Icon(Icons.Default.Calculate, contentDescription = null) }, "Новый расчёт")
}

@Composable
fun MainScreenWithBottomNav(
    navController: NavHostController,  // Изменен тип
    serviceLocator: ServiceLocator,
    onLogout: () -> Unit
) {
    var selectedItem by remember { mutableStateOf(0) }
    val bottomNavItems = listOf(
        BottomNavItem.Users,
        BottomNavItem.Calculations,
        BottomNavItem.NewCalculation
    )

    val usersViewModel = remember { serviceLocator.provideUsersViewModel() }
    val calculationsViewModel = remember { serviceLocator.provideCalculationsViewModel() }
    val depositViewModel = remember { serviceLocator.provideDepositViewModel() }

    // Создаем отдельный NavController для вкладки "Новый расчёт"
    val depositNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        icon = { item.icon() },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->
        when (selectedItem) {
            0 -> UsersScreen(
                navController = navController,
                viewModel = usersViewModel
            )
            1 -> CalculationsScreen(
                navController = navController,
                viewModel = calculationsViewModel,
                onCalculationClick = { calculation ->
                    depositViewModel.loadFromHistory(calculation)
                    navController.navigate("calculation_detail")
                }
            )
            2 -> DepositNavHost(
                navController = depositNavController,
                viewModel = depositViewModel,
                onSaveComplete = {
                    selectedItem = 1
                }
            )
        }
    }
}