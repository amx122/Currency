package com.example.kursova_mobile.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

object PrefsManager {
    private const val PREFS_NAME = "currency_prefs"
    private const val KEY_THEME = "theme_mode"
    private const val KEY_BASE_CURRENCY = "base_currency"
    private const val KEY_LANGUAGE = "language_code"
    private const val KEY_PRECISION = "number_precision"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun setDarkMode(context: Context, isDark: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_THEME, isDark).apply()
        val mode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun isDarkMode(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_THEME, false)
    }

    fun setBaseCurrency(context: Context, code: String) {
        getPrefs(context).edit().putString(KEY_BASE_CURRENCY, code).apply()
    }

    fun getBaseCurrency(context: Context): String {
        return getPrefs(context).getString(KEY_BASE_CURRENCY, "UAH") ?: "UAH"
    }

    fun setLanguage(context: Context, langCode: String) {
        getPrefs(context).edit().putString(KEY_LANGUAGE, langCode).apply()
    }

    fun getLanguage(context: Context): String {
        return getPrefs(context).getString(KEY_LANGUAGE, "uk") ?: "uk"
    }

    fun setPrecision(context: Context, precision: Int) {
        getPrefs(context).edit().putInt(KEY_PRECISION, precision).apply()
    }

    fun getPrecision(context: Context): Int {
        return getPrefs(context).getInt(KEY_PRECISION, 2)
    }

    fun getPrecisionFormat(context: Context): String {
        val p = getPrecision(context)
        return "%.${p}f"
    }
}