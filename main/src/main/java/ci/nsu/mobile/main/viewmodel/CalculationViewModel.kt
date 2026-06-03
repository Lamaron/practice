package ci.nsu.mobile.main.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ci.nsu.mobile.main.data.local.DepositEntity
import ci.nsu.mobile.main.data.local.DepositRepository
import ci.nsu.mobile.main.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class CalculationsUiState(
    val isLoading: Boolean = false,
    val calculations: List<DepositEntity> = emptyList(),
    val error: String? = null,
    val filterType: FilterType = FilterType.ALL,
    val sortOrder: SortOrder = SortOrder.DATE_DESC
)

enum class FilterType { ALL, WEEK, MONTH, YEAR }
enum class SortOrder { DATE_DESC, DATE_ASC, AMOUNT_DESC, AMOUNT_ASC }

class CalculationsViewModel(
    private val repository: DepositRepository,  // DepositRepository
    private val serviceLocator: ServiceLocator
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculationsUiState())
    val uiState: StateFlow<CalculationsUiState> = _uiState.asStateFlow()

    var selectedCalculation by mutableStateOf<DepositEntity?>(null)
        private set

    fun loadCalculations() {
        val userId = serviceLocator.currentUserId.value ?: serviceLocator.loadStoredUserId()
        if (userId == null) {
            _uiState.value = _uiState.value.copy(error = "Пользователь не авторизован")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                repository.getDepositsByUserId(userId).collect { deposits ->
                    val filtered = filterCalculations(deposits, _uiState.value.filterType)
                    val sorted = sortCalculations(filtered, _uiState.value.sortOrder)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        calculations = sorted,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Ошибка загрузки расчётов"
                )
            }
        }
    }

    fun deleteCalculation(calculation: DepositEntity) {
        val userId = serviceLocator.currentUserId.value ?: return
        viewModelScope.launch {
            repository.deleteDeposit(calculation.id, userId)
            loadCalculations()
        }
    }

    fun selectCalculation(calculation: DepositEntity) {
        selectedCalculation = calculation
    }

    fun clearSelectedCalculation() {
        selectedCalculation = null
    }

    fun setFilterType(filterType: FilterType) {
        _uiState.value = _uiState.value.copy(filterType = filterType)
        loadCalculations()
    }

    fun setSortOrder(sortOrder: SortOrder) {
        _uiState.value = _uiState.value.copy(sortOrder = sortOrder)
        loadCalculations()
    }

    private fun filterCalculations(calculations: List<DepositEntity>, filterType: FilterType): List<DepositEntity> {
        val now = System.currentTimeMillis()
        return when (filterType) {
            FilterType.ALL -> calculations
            FilterType.WEEK -> calculations.filter { now - it.calculationDate <= 7 * 24 * 60 * 60 * 1000L }
            FilterType.MONTH -> calculations.filter { now - it.calculationDate <= 30 * 24 * 60 * 60 * 1000L }
            FilterType.YEAR -> calculations.filter { now - it.calculationDate <= 365 * 24 * 60 * 60 * 1000L }
        }
    }

    private fun sortCalculations(calculations: List<DepositEntity>, sortOrder: SortOrder): List<DepositEntity> {
        return when (sortOrder) {
            SortOrder.DATE_DESC -> calculations.sortedByDescending { it.calculationDate }
            SortOrder.DATE_ASC -> calculations.sortedBy { it.calculationDate }
            SortOrder.AMOUNT_DESC -> calculations.sortedByDescending { it.finalAmount }
            SortOrder.AMOUNT_ASC -> calculations.sortedBy { it.finalAmount }
        }
    }
}