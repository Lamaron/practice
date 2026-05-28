package ci.nsu.mobile.main.data.user

import android.content.Context
import android.content.SharedPreferences

class UserManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "user_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_LOGIN = "user_login"
        private const val KEY_USER_EMAIL = "user_email"
    }

    var userId: Long
        get() = prefs.getLong(KEY_USER_ID, -1)
        set(value) = prefs.edit().putLong(KEY_USER_ID, value).apply()

    var userLogin: String?
        get() = prefs.getString(KEY_USER_LOGIN, null)
        set(value) = prefs.edit().putString(KEY_USER_LOGIN, value).apply()

    var userEmail: String?
        get() = prefs.getString(KEY_USER_EMAIL, null)
        set(value) = prefs.edit().putString(KEY_USER_EMAIL, value).apply()

    fun clearUserData() {
        prefs.edit()
            .remove(KEY_USER_ID)
            .remove(KEY_USER_LOGIN)
            .remove(KEY_USER_EMAIL)
            .apply()
    }

    fun isLoggedIn(): Boolean = userId != -1L
}