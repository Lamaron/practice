package ci.nsu.mobile.main.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ci.nsu.mobile.main.ViewModel.DepositViewModel

@Composable
fun ResultScreen(navController: NavController, viewModel: DepositViewModel) {
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
                .padding(bottom = 24.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Результаты расчёта",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text("Стартовый взнос: ${viewModel.initialAmount}")
                Text("Срок: ${viewModel.periodMonths} мес.")
                Text("Итоговая сумма: ${"%.2f".format(viewModel.finalAmount)}")
                Text("Доход: ${"%.2f".format(viewModel.interestEarned)}")
            }
        }

        Button(
            onClick = {
                viewModel.saveCalculation()
                navController.navigate("main") {
                    popUpTo("main") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(0.7f) // Кнопка на 70% ширины экрана
        ) {
            Text("Сохранить и в начало")
        }
    }
}