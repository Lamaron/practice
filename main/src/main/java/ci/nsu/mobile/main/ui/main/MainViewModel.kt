package ci.nsu.mobile.main.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ci.nsu.mobile.main.data.local.entity.DepositCalculation
import ci.nsu.mobile.main.data.model.UserDto
import ci.nsu.mobile.main.data.repository.AuthRepository
import ci.nsu.mobile.main.data.repository.DepositRepository
import ci.nsu.mobile.main.data.token.TokenManager
import ci.nsu.mobile.main.data.user.UserManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MainUiState(
    val users: List<UserDto> = emptyList(),
    val deposits: List<DepositCalculation> = emptyList(),
    val isUsersLoading: Boolean = false,
    val isDepositsLoading: Boolean = false,
    val error: String? = null,
    val calculatedFinalAmount: Double? = null,
    val calculatedInterest: Double? = null,
    val isCalculating: Boolean = false,
    val isSavingCalculation: Boolean = false,
    val calculationSaved: Boolean = false
)

class MainViewModel(
    private val authRepository: AuthRepository,
    private val depositRepository: DepositRepository,
    private val userManager: UserManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState

    init {
        loadUsers()
        loadDeposits()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUsersLoading = true)

            val result = authRepository.getUsers()
            result.fold(
                onSuccess = { users ->
                    _uiState.value = _uiState.value.copy(
                        users = users,
                        isUsersLoading = false
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        error = e.message,
                        isUsersLoading = false
                    )
                }
            )
        }
    }

    fun loadDeposits() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDepositsLoading = true)

            depositRepository.getCurrentUserCalculations()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        error = e.message,
                        isDepositsLoading = false
                    )
                }
                .collect { deposits ->
                    _uiState.value = _uiState.value.copy(
                        deposits = deposits,
                        isDepositsLoading = false
                    )
                }
        }
    }

    fun calculateDeposit(
        initialAmount: Double,
        periodMonths: Int,
        interestRate: Double,
        monthlyTopUp: Double?
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCalculating = true)

            val (finalAmount, interestEarned) = depositRepository.calculateDeposit(
                initialAmount = initialAmount,
                periodMonths = periodMonths,
                interestRate = interestRate,
                monthlyTopUp = monthlyTopUp
            )

            _uiState.value = _uiState.value.copy(
                calculatedFinalAmount = finalAmount,
                calculatedInterest = interestEarned,
                isCalculating = false
            )
        }
    }

    fun saveCalculation(
        initialAmount: Double,
        periodMonths: Int,
        interestRate: Double,
        monthlyTopUp: Double?,
        finalAmount: Double,
        interestEarned: Double
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSavingCalculation = true)

            val calculation = DepositCalculation(
                userId = userManager.userId,
                initialAmount = initialAmount,
                periodMonths = periodMonths,
                interestRate = interestRate,
                monthlyTopUp = monthlyTopUp,
                finalAmount = finalAmount,
                interestEarned = interestEarned
            )

            depositRepository.saveCalculation(calculation)

            _uiState.value = _uiState.value.copy(
                isSavingCalculation = false,
                calculationSaved = true
            )
        }
    }

    fun deleteCalculation(calculation: DepositCalculation) {
        viewModelScope.launch {
            depositRepository.deleteCalculation(calculation)
        }
    }

    fun deleteCalculationById(id: Long) {
        viewModelScope.launch {
            depositRepository.deleteCalculationById(id)
        }
    }

    fun clearCalculationResult() {
        _uiState.value = _uiState.value.copy(
            calculatedFinalAmount = null,
            calculatedInterest = null,
            calculationSaved = false
        )
    }

    fun logout() {
        TokenManager.clearToken()
        userManager.clearUserData()
    }

    class Factory(
        private val authRepository: AuthRepository,
        private val depositRepository: DepositRepository,
        private val userManager: UserManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(authRepository, depositRepository, userManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}