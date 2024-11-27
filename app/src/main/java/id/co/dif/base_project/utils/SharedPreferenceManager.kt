package id.co.dif.base_project.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPreferenceManager {
    private const val PREF_NAME = "MyPrefs"

    // Keys for data to be stored
    private const val KEY_EMAIL = "email"
    private const val KEY_PASSWORD = "password"
    private const val KEY_REMEMBER_ME = "rememberMe"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveLoginCredentials(context: Context, email: String, password: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_PASSWORD, password)
        editor.apply()
    }

    fun getSavedEmail(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_EMAIL, null)
    }

    fun getSavedPassword(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_PASSWORD, null)
    }

    fun setRememberMe(context: Context, isChecked: Boolean) {
        val editor = getSharedPreferences(context).edit()
        editor.putBoolean(KEY_REMEMBER_ME, isChecked)
        editor.apply()
    }

    fun getRememberMe(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_REMEMBER_ME, false)
    }
}
