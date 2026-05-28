package ci.nsu.mobile.main.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import ci.nsu.mobile.main.di.ServiceLocator
import ci.nsu.mobile.main.ui.main.tabs.DepositsTab
import ci.nsu.mobile.main.ui.main.tabs.NewDepositTab
import ci.nsu.mobile.main.ui.main.tabs.UsersTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogout: () -> Unit,
    serviceLocator: ServiceLocator,
    viewModel: MainViewModel = viewModel(
        factory = MainViewModel.Factory(
            serviceLocator.authRepository,
            serviceLocator.depositRepository,
            serviceLocator.userManager
        )
    )
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf("Пользователи", "Мои расчёты", "Новый расчёт")
    val icons = listOf(
        Icons.Default.Person,
        Icons.Default.List,
        Icons.Default.Add
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Расчёт вкладов") },
                actions = {
                    TextButton(onClick = {
                        viewModel.logout()
                        onLogout()
                    }) {
                        Text("Выйти")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = null) },
                        label = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> UsersTab(viewModel = viewModel)
                1 -> DepositsTab(viewModel = viewModel)
                2 -> NewDepositTab(viewModel = viewModel)
            }
        }
    }
}