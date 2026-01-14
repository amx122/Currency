package com.example.kursova_mobile.utils

object CurrencyUtils {
    fun getFlagEmoji(currencyCode: String): String {
        if (currencyCode == "EUR") return "🇪🇺"
        if (currencyCode.length != 3) return "🌐"

        val countryCode = currencyCode.dropLast(1) // USD -> US
        val firstChar = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
        val secondChar = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6
        return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
    }
}