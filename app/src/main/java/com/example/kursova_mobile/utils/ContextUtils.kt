package com.example.kursova_mobile.utils

import android.content.Context
import android.content.ContextWrapper
import java.util.Locale

object ContextUtils {
    fun updateLocale(context: Context, languageToLoad: String): ContextWrapper {
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        return ContextWrapper(context.createConfigurationContext(config))
    }
}