package com.example.kursova_mobile.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun formatLastUpdate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault())
        return "Оновлено: ${sdf.format(Date(timestamp))}"
    }
}