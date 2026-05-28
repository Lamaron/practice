package ci.nsu.mobile.main.data.local

import androidx.room.*
import ci.nsu.mobile.main.data.local.entity.DepositCalculation
import kotlinx.coroutines.flow.Flow

@Dao
interface DepositDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculation(calculation: DepositCalculation): Long

    @Update
    suspend fun updateCalculation(calculation: DepositCalculation)

    @Delete
    suspend fun deleteCalculation(calculation: DepositCalculation)

    @Query("SELECT * FROM deposit_calculations WHERE userId = :userId ORDER BY calculationDate DESC")
    fun getCalculationsByUserId(userId: Long): Flow<List<DepositCalculation>>

    @Query("SELECT * FROM deposit_calculations WHERE id = :id")
    suspend fun getCalculationById(id: Long): DepositCalculation?

    @Query("DELETE FROM deposit_calculations WHERE id = :id")
    suspend fun deleteCalculationById(id: Long)

    @Query("DELETE FROM deposit_calculations WHERE userId = :userId")
    suspend fun deleteAllCalculationsByUserId(userId: Long)
}