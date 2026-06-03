package ci.nsu.mobile.main.data.remote.repository

import ci.nsu.mobile.main.data.remote.api.ApiService
import ci.nsu.mobile.main.data.remote.model.GroupDto
import ci.nsu.mobile.main.data.remote.model.LoginRequest
import ci.nsu.mobile.main.data.remote.model.PersonDto
import ci.nsu.mobile.main.data.remote.model.RegisterRequest
import ci.nsu.mobile.main.data.remote.model.UserDto
import ci.nsu.mobile.main.data.token.TokenManager
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {

    suspend fun login(login: String, password: String): Result<UserDto> {
        return withContext(Dispatchers.IO) {
            try {
                val loginRequest = LoginRequest(login, password)
                val response = ApiService.login(loginRequest)
                TokenManager.token = response.token

                // After login, fetch user data
                val users = ApiService.getUsers()
                val currentUser = users.find { it.login == login }

                if (currentUser != null) {
                    Result.success(currentUser)
                } else {
                    Result.failure(Exception("User not found"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun register(
        firstName: String,
        lastName: String,
        middleName: String,
        birthDate: String,
        gender: String,
        groupId: Int,
        login: String,
        password: String,
        email: String,
        phoneNumber: String
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val person = PersonDto(
                    firstName = firstName,
                    lastName = lastName,
                    middleName = middleName,
                    birthDate = birthDate,
                    gender = gender,
                    groupId = groupId
                )

                val registerRequest = RegisterRequest(
                    login = login,
                    password = password,
                    email = email,
                    phoneNumber = phoneNumber,
                    roleId = 1,
                    authAllowed = true,
                    person = person
                )

                val response: HttpResponse = ApiService.register(registerRequest)
                if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
                    Result.success(Unit)
                } else {
                    val errorBody = response.bodyAsText()
                    Result.failure(Exception("Registration failed: $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getUsers(): Result<List<UserDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val users = ApiService.getUsers()
                Result.success(users)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getGroups(): Result<List<GroupDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val groups = ApiService.getGroups()
                Result.success(groups)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getCurrentUserId(): Long? {
        return withContext(Dispatchers.IO) {
            try {
                val users = getUsers()
                users.getOrNull()?.firstOrNull()?.id?.toLong()
            } catch (e: Exception) {
                null
            }
        }
    }

    fun logout() {
        TokenManager.clearToken()
    }
}