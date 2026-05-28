package ci.nsu.mobile.main.di

import android.content.Context
import ci.nsu.mobile.main.data.api.ApiService
import ci.nsu.mobile.main.data.local.AppDatabase
import ci.nsu.mobile.main.data.local.DepositDao
import ci.nsu.mobile.main.data.repository.AuthRepository
import ci.nsu.mobile.main.data.repository.DepositRepository
import ci.nsu.mobile.main.data.token.TokenManager
import ci.nsu.mobile.main.data.user.UserManager

class ServiceLocator(private val context: Context) {

    // База данных
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    // DAO для депозитов
    val depositDao: DepositDao by lazy {
        database.depositDao()
    }

    // Репозитории
    val authRepository: AuthRepository by lazy {
        AuthRepository()
    }

    val depositRepository: DepositRepository by lazy {
        DepositRepository(depositDao, userManager)
    }

    // Менеджеры
    val userManager: UserManager by lazy {
        UserManager(context)
    }

    companion object {
        @Volatile
        private var instance: ServiceLocator? = null

        fun getInstance(context: Context): ServiceLocator {
            return instance ?: synchronized(this) {
                instance ?: ServiceLocator(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
}