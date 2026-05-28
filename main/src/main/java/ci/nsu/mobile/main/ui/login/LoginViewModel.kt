package ci.nsu.mobile.main.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ci.nsu.mobile.main.data.repository.AuthRepository
import ci.nsu.mobile.main.data.token.TokenManager
import ci.nsu.mobile.main.data.user.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userManager: UserManager? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(login: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)

            val result = authRepository.login(login, password)
            result.fold(
                onSuccess = { user ->
                    // Сохраняем данные пользователя
                    userManager?.let {
                        user.id?.let { id -> it.userId = id.toLong() }
                        it.userLogin = user.login
                        it.userEmail = user.email
                    }

                    _uiState.value = LoginUiState(isSuccess = true)
                },
                onFailure = { e ->
                    _uiState.value = LoginUiState(error = e.message)
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}