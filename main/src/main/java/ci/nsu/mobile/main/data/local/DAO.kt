package ci.nsu.mobile.main.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DepositDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeposit(deposit: DepositEntity)

    // Получить все расчёты пользователя
    @Query("SELECT * FROM deposits WHERE userId = :userId ORDER BY calculationDate DESC")
    fun getDepositsByUserId(userId: Long): Flow<List<DepositEntity>>

    // Получить конкретный расчёт
    @Query("SELECT * FROM deposits WHERE id = :id AND userId = :userId")
    suspend fun getDepositById(id: Long, userId: Long): DepositEntity?

    // Удалить расчёт
    @Query("DELETE FROM deposits WHERE id = :id AND userId = :userId")
    suspend fun deleteDepositById(id: Long, userId: Long)

    // Фильтрация по дате (пример)
    @Query("SELECT * FROM deposits WHERE userId = :userId AND calculationDate BETWEEN :startDate AND :endDate ORDER BY calculationDate DESC")
    fun getDepositsByDateRange(userId: Long, startDate: Long, endDate: Long): Flow<List<DepositEntity>>
}