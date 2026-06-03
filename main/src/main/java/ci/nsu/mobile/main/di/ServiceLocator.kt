package ci.nsu.mobile.main.di

import android.content.Context
import ci.nsu.mobile.main.data.local.AppDatabase
import ci.nsu.mobile.main.data.local.DepositDao
import ci.nsu.mobile.main.data.local.DepositRepository  // Используем существующий
import ci.nsu.mobile.main.data.remote.repository.AuthRepository
import ci.nsu.mobile.main.data.token.TokenManager
import ci.nsu.mobile.main.viewmodel.LoginViewModel
import ci.nsu.mobile.main.viewmodel.RegisterViewModel
import ci.nsu.mobile.main.viewmodel.CalculationsViewModel
import ci.nsu.mobile.main.viewmodel.DepositViewModel
import ci.nsu.mobile.main.viewmodel.UsersViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ServiceLocator(private val context: Context) {

    private val _currentUserId = MutableStateFlow<Long?>(null)
    val currentUserId: StateFlow<Long?> = _currentUserId

    fun setCurrentUserId(userId: Long?) {
        _currentUserId.value = userId
        userId?.let {
            context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                .edit()
                .putLong("current_user_id", it)
                .apply()
        }
    }

    fun loadStoredUserId(): Long? {
        return context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .getLong("current_user_id", -1L)
            .takeIf { it != -1L }
    }

    private val tokenManager: TokenManager by lazy {
        TokenManager.init(context)
        TokenManager
    }

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    private val depositDao: DepositDao by lazy {
        database.depositDao()
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository()
    }

    // Используем существующий DepositRepository
    private val depositRepository: DepositRepository by lazy {
        DepositRepository(depositDao)
    }

    // Фабрики ViewModels
    fun provideLoginViewModel(): LoginViewModel {
        return LoginViewModel()
    }

    fun provideRegisterViewModel(): RegisterViewModel {
        return RegisterViewModel()
    }

    fun provideDepositViewModel(): DepositViewModel {
        return DepositViewModel(depositRepository, this)  // Используем depositRepository
    }

    fun provideCalculationsViewModel(): CalculationsViewModel {
        return CalculationsViewModel(depositRepository, this)  // Используем depositRepository
    }

    fun provideUsersViewModel(): UsersViewModel {
        return UsersViewModel(authRepository)
    }
}