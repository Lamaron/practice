package ci.nsu.mobile.main.ui.main.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ci.nsu.mobile.main.data.local.entity.DepositCalculation
import ci.nsu.mobile.main.ui.main.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DepositsTab(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isDepositsLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.deposits.isEmpty()) {
            Text(
                text = "Нет сохранённых расчётов",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.deposits) { deposit ->
                    DepositCard(
                        deposit = deposit,
                        onDelete = { viewModel.deleteCalculation(deposit) }
                    )
                }
            }
        }
    }
}

@Composable
fun DepositCard(
    deposit: DepositCalculation,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Вклад ${"%.2f".format(deposit.initialAmount)} ₽",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Срок: ${deposit.periodMonths} мес.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Ставка: ${deposit.interestRate}%",
                style = MaterialTheme.typography.bodyMedium
            )
            if (deposit.monthlyTopUp != null) {
                Text(
                    text = "Пополнение: ${deposit.monthlyTopUp} ₽/мес.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Итоговая сумма: ${"%.2f".format(deposit.finalAmount)} ₽",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Начисленные проценты: ${"%.2f".format(deposit.interestEarned)} ₽",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = "Дата: ${dateFormat.format(Date(deposit.calculationDate))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}