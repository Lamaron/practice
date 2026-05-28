package ci.nsu.mobile.main.ui.main.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ci.nsu.mobile.main.ui.main.MainViewModel

@Composable
fun UsersTab(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isUsersLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.error != null) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ошибка: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.loadUsers() }) {
                    Text("Повторить")
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.users) { user ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = user.person?.let {
                                    "${it.lastName} ${it.firstName} ${it.middleName ?: ""}"
                                } ?: user.login,
                                style = MaterialTheme.typography.titleMedium
                            )
                            if (user.email != null) {
                                Text(
                                    text = "Email: ${user.email}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            if (user.phoneNumber != null) {
                                Text(
                                    text = "Телефон: ${user.phoneNumber}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}