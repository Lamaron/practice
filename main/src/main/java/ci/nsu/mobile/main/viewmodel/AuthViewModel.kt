package ci.nsu.mobile.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ci.nsu.mobile.main.data.remote.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val userId: Long? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(login: String, password: String, onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val result = authRepository.login(login, password)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = AuthUiState(
                        isSuccess = true,
                        isLoggedIn = true,
                        userId = user.id?.toLong()
                    )
                    onSuccess(user.id?.toLong() ?: -1L)
                },
                onFailure = { exception ->
                    _uiState.value = AuthUiState(error = exception.message)
                }
            )
        }
    }

    fun register(
        firstName: String,
        lastName: String,
        middleName: String,
        birthDate: String,
        gender: String,
        groupId: Int,
        login: String,
        password: String,
        email: String,
        phoneNumber: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val result = authRepository.register(
                firstName, lastName, middleName, birthDate,
                gender, groupId, login, password, email, phoneNumber
            )
            result.fold(
                onSuccess = {
                    _uiState.value = AuthUiState(isSuccess = true)
                    onSuccess()
                },
                onFailure = { exception ->
                    _uiState.value = AuthUiState(error = exception.message)
                }
            )
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = AuthUiState(isLoggedIn = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}