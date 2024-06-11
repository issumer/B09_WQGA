package com.example.b09_wqga.util

import android.content.Context
import android.content.SharedPreferences
import com.example.b09_wqga.database.User

class SharedPreferencesHelper(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUser(user: User) {
        val editor = prefs.edit()
        editor.putInt("user_id", user.user_id)
        editor.putString("username", user.username)
        editor.putString("password", user.password)
        editor.putString("name", user.name)
        editor.putInt("points", user.points)
        editor.putString("enterDate", user.enterDate)
        editor.putString("updateDate", user.updateDate)
        editor.apply()
    }

    fun getUser(): User? {
        val user_id = prefs.getInt("user_id", -1)
        if (user_id == -1) return null
        val username = prefs.getString("username", null) ?: return null
        val password = prefs.getString("password", null) ?: return null
        val name = prefs.getString("name", null) ?: return null
        val points = prefs.getInt("points", 0)
        val enterDate = prefs.getString("enterDate", null) ?: return null
        val updateDate = prefs.getString("updateDate", null) ?: return null
        return User(user_id, username, password, name, points, enterDate, updateDate)
    }

    fun clearUser() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}
