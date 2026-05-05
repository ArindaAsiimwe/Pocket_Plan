package com.example.pocketplan.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("pocket_plan_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val USER_ID_KEY = "user_id"
    }

    fun saveUserId(userId: String) {
        prefs.edit().putString(USER_ID_KEY, userId).apply()
    }

    fun getUserId(): String? {
        return prefs.getString(USER_ID_KEY, null)
    }

    fun clearSession() {
        prefs.edit().remove(USER_ID_KEY).apply()
    }
}
