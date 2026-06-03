package ci.nsu.mobile.main.ui.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import ci.nsu.mobile.main.data.remote.model.UserDto
import ci.nsu.mobile.main.viewmodel.UsersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    navController: NavController,
    viewModel: UsersViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedUser by remember { mutableStateOf<UserDto?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Пользователи") }
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
                        Button(onClick = { viewModel.loadUsers() }) {
                            Text("Повторить")
                        }
                    }
                }
                uiState.users.isEmpty() -> {
                    Text(
                        text = "Нет пользователей",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.users) { user ->
                            UserCard(
                                user = user,
                                onClick = { selectedUser = user }
                            )
                        }
                    }
                }
            }

            // Диалог с деталями пользователя
            selectedUser?.let { user ->
                UserDetailDialog(
                    user = user,
                    onDismiss = { selectedUser = null }
                )
            }
        }
    }
}

@Composable
fun UserCard(user: UserDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = user.person?.let {
                    "${it.lastName} ${it.firstName} ${it.middleName ?: ""}"
                } ?: user.login,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Логин: ${user.login}",
                style = MaterialTheme.typography.bodyMedium
            )
            user.email?.let {
                Text(
                    text = "Email: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun UserDetailDialog(user: UserDto, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Детали пользователя") },
        text = {
            Column {
                Text(
                    text = user.person?.let {
                        "${it.lastName} ${it.firstName} ${it.middleName ?: ""}"
                    } ?: user.login,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Логин: ${user.login}")
                user.email?.let { Text(text = "Email: $it") }
                user.phoneNumber?.let { Text(text = "Телефон: $it") }
                user.person?.let { person ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Дата рождения: ${person.birthDate}")
                    Text(text = "Пол: ${if (person.gender == "male") "Мужской" else "Женский"}")
                    Text(text = "Группа ID: ${person.groupId}")
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Закрыть")
            }
        }
    )
}

@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit
) {
    TODO("Not yet implemented")
}