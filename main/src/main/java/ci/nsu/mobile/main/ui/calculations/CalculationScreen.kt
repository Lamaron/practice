package ci.nsu.mobile.main.ui.calculations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ci.nsu.mobile.main.data.local.DepositEntity
import ci.nsu.mobile.main.viewmodel.CalculationsViewModel
import ci.nsu.mobile.main.viewmodel.FilterType
import ci.nsu.mobile.main.viewmodel.SortOrder
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculationsScreen(
    navController: NavController,
    viewModel: CalculationsViewModel,
    onCalculationClick: (DepositEntity) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilterMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<DepositEntity?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadCalculations()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои расчёты") },
                actions = {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Фильтр")
                    }
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.Default.Sort, contentDescription = "Сортировка")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ошибка: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadCalculations() }) {
                            Text("Повторить")
                        }
                    }
                }
                uiState.calculations.isEmpty() -> {
                    Text(
                        text = "Нет сохранённых расчётов",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.calculations) { calculation ->
                            CalculationCard(
                                calculation = calculation,
                                onItemClick = { onCalculationClick(calculation) },
                                onDeleteClick = { itemToDelete = calculation }
                            )
                        }
                    }
                }
            }

            // Фильтр меню
            DropdownMenu(
                expanded = showFilterMenu,
                onDismissRequest = { showFilterMenu = false }
            ) {
                FilterType.values().forEach { filter ->
                    DropdownMenuItem(
                        text = { Text(filter.name) },
                        onClick = {
                            viewModel.setFilterType(filter)
                            showFilterMenu = false
                        }
                    )
                }
            }

            // Сортировка меню
            DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = false }
            ) {
                SortOrder.values().forEach { sort ->
                    DropdownMenuItem(
                        text = { Text(sort.name) },
                        onClick = {
                            viewModel.setSortOrder(sort)
                            showSortMenu = false
                        }
                    )
                }
            }

            // Диалог подтверждения удаления
            itemToDelete?.let { calculation ->
                AlertDialog(
                    onDismissRequest = { itemToDelete = null },
                    title = { Text("Удалить расчёт") },
                    text = { Text("Вы уверены, что хотите удалить этот расчёт?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.deleteCalculation(calculation)
                                itemToDelete = null
                            }
                        ) {
                            Text("Удалить")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { itemToDelete = null }) {
                            Text("Отмена")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CalculationCard(
    calculation: DepositEntity,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    val dateString = sdf.format(Date(calculation.calculationDate))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Сумма: ${"%.2f".format(calculation.finalAmount)} ₽",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Взнос: ${calculation.initialAmount} ₽, Срок: ${calculation.periodMonths} мес.",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Доход: ${"%.2f".format(calculation.interestEarned)} ₽",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить")
            }
        }
    }
}