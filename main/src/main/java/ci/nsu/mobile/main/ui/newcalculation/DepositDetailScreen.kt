package ci.nsu.mobile.main.ui.newcalculation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ci.nsu.mobile.main.viewmodel.DepositViewModel

@Composable
fun DepositDetailScreen(
    navController: NavController,
    viewModel: DepositViewModel,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Детали расчёта",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Divider()

                CalculationResultRow(label = "Стартовый взнос:", value = "${viewModel.initialAmount} ₽")
                CalculationResultRow(label = "Срок вклада:", value = "${viewModel.periodMonths} мес.")
                CalculationResultRow(label = "Ставка:", value = "${viewModel.getAvailableRate()}%")
                CalculationResultRow(label = "Ежемесячное пополнение:", value = "${viewModel.monthlyTopUp} ₽")

                Divider()

                Text(
                    text = "Итоговая сумма:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${"%.2f".format(viewModel.finalAmount)} ₽",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "Чистый доход: ${"%.2f".format(viewModel.interestEarned)} ₽",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Назад")
        }
    }
}