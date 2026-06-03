package ci.nsu.mobile.main.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ci.nsu.mobile.main.data.remote.model.GroupDto
import ci.nsu.mobile.main.data.remote.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val groups: List<GroupDto> = emptyList(),
    val isGroupsLoading: Boolean = false
)

class RegisterViewModel : ViewModel() {
    private val repository = AuthRepository()
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    init {
        loadGroups()
    }

    fun loadGroups() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGroupsLoading = true)
            val result = repository.getGroups()
            result.fold(
                onSuccess = { groups ->
                    _uiState.value = _uiState.value.copy(
                        groups = groups,
                        isGroupsLoading = false
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        error = it.message,
                        isGroupsLoading = false
                    )
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
            _uiState.value = RegisterUiState(isLoading = true)
            val result = repository.register(
                firstName, lastName, middleName, birthDate,
                gender, groupId, login, password, email, phoneNumber
            )
            result.fold(
                onSuccess = {
                    _uiState.value = RegisterUiState(isSuccess = true)
                    onSuccess()
                },
                onFailure = { exception ->
                    _uiState.value = RegisterUiState(error = exception.message)
                }
            )
        }
    }
}