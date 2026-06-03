package ci.nsu.mobile.main.data.local

import kotlinx.coroutines.flow.Flow

class DepositRepository(private val depositDao: DepositDao) {

    fun getDepositsByUserId(userId: Long): Flow<List<DepositEntity>> {
        return depositDao.getDepositsByUserId(userId)
    }

    suspend fun insertDeposit(deposit: DepositEntity) {
        depositDao.insertDeposit(deposit)
    }

    suspend fun deleteDeposit(id: Long, userId: Long) {
        depositDao.deleteDepositById(id, userId)
    }

    suspend fun getDepositById(id: Long, userId: Long): DepositEntity? {
        return depositDao.getDepositById(id, userId)
    }

    // Логика расчёта (остаётся без изменений)
    fun calculateDeposit(
        initial: Double,
        months: Int,
        rate: Double,
        monthlyTopUp: Double
    ): Pair<Double, Double> {
        var total = initial
        val monthlyRate = rate / 12 / 100

        for (i in 1..months) {
            total += total * monthlyRate
            total += monthlyTopUp
        }

        val interestEarned = total - initial - (monthlyTopUp * months)
        return Pair(total, interestEarned)
    }
}