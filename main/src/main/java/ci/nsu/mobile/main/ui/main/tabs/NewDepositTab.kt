package ci.nsu.mobile.main.ui.main.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ci.nsu.mobile.main.ui.main.MainViewModel

@Composable
fun NewDepositTab(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    var initialAmount by remember { mutableStateOf("") }
    var periodMonths by remember { mutableStateOf("") }
    var interestRate by remember { mutableStateOf("") }
    var monthlyTopUp by remember { mutableStateOf("") }
    var isCalculated by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Новый расчёт вклада",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = initialAmount,
            onValueChange = {
                initialAmount = it
                isCalculated = false
            },
            label = { Text("Начальная сумма (₽)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = periodMonths,
            onValueChange = {
                periodMonths = it
                isCalculated = false
            },
            label = { Text("Срок (месяцев)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = interestRate,
            onValueChange = {
                interestRate = it
                isCalculated = false
            },
            label = { Text("Процентная ставка (%)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = monthlyTopUp,
            onValueChange = {
                monthlyTopUp = it
                isCalculated = false
            },
            label = { Text("Ежемесячное пополнение (₽, опционально)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                val amount = initialAmount.toDoubleOrNull()
                val months = periodMonths.toIntOrNull()
                val rate = interestRate.toDoubleOrNull()
                val topUp = monthlyTopUp.toDoubleOrNull()

                if (amount != null && months != null && rate != null) {
                    viewModel.calculateDeposit(amount, months, rate, topUp)
                    isCalculated = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isCalculating &&
                    initialAmount.isNotBlank() &&
                    periodMonths.isNotBlank() &&
                    interestRate.isNotBlank()
        ) {
            if (uiState.isCalculating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Рассчитать")
            }
        }

        if (isCalculated && uiState.calculatedFinalAmount != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Результаты расчёта",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Итоговая сумма: ${"%.2f".format(uiState.calculatedFinalAmount)} ₽",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Начисленные проценты: ${"%.2f".format(uiState.calculatedInterest)} ₽",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (uiState.calculationSaved) {
                        Text(
                            text = "Расчёт сохранён!",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Button(
                            onClick = {
                                val amount = initialAmount.toDoubleOrNull() ?: 0.0
                                val months = periodMonths.toIntOrNull() ?: 0
                                val rate = interestRate.toDoubleOrNull() ?: 0.0
                                val topUp = monthlyTopUp.toDoubleOrNull()
                                val finalAmount = uiState.calculatedFinalAmount ?: 0.0
                                val interest = uiState.calculatedInterest ?: 0.0

                                viewModel.saveCalculation(
                                    initialAmount = amount,
                                    periodMonths = months,
                                    interestRate = rate,
                                    monthlyTopUp = topUp,
                                    finalAmount = finalAmount,
                                    interestEarned = interest
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isSavingCalculation
                        ) {
                            if (uiState.isSavingCalculation) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Сохранить расчёт")
                            }
                        }
                    }
                }
            }
        }
    }
}