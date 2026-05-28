package ci.nsu.mobile.main.data.repository

import ci.nsu.mobile.main.data.local.DepositDao
import ci.nsu.mobile.main.data.local.entity.DepositCalculation
import ci.nsu.mobile.main.data.user.UserManager
import kotlinx.coroutines.flow.Flow

class DepositRepository(
    private val depositDao: DepositDao,
    private val userManager: UserManager
) {

    fun getCurrentUserCalculations(): Flow<List<DepositCalculation>> {
        return depositDao.getCalculationsByUserId(userManager.userId)
    }

    suspend fun saveCalculation(calculation: DepositCalculation): Long {
        return depositDao.insertCalculation(calculation)
    }

    suspend fun deleteCalculation(calculation: DepositCalculation) {
        depositDao.deleteCalculation(calculation)
    }

    suspend fun deleteCalculationById(id: Long) {
        depositDao.deleteCalculationById(id)
    }

    suspend fun getCalculationById(id: Long): DepositCalculation? {
        return depositDao.getCalculationById(id)
    }

    fun calculateDeposit(
        initialAmount: Double,
        periodMonths: Int,
        interestRate: Double,
        monthlyTopUp: Double? = null
    ): Pair<Double, Double> {
        var totalAmount = initialAmount
        val monthlyRate = interestRate / 100.0 / 12.0

        for (month in 1..periodMonths) {
            // Начисление процентов
            totalAmount += totalAmount * monthlyRate

            // Добавление ежемесячного пополнения
            monthlyTopUp?.let {
                totalAmount += it
            }
        }

        val interestEarned = totalAmount - initialAmount - (monthlyTopUp ?: 0.0) * periodMonths

        return Pair(totalAmount, interestEarned)
    }
}