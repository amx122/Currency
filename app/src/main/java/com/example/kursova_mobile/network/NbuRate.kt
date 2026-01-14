package com.example.kursova_mobile.network

import com.google.gson.annotations.SerializedName

data class NbuRate(
    @SerializedName("r030") val r030: Int,
    @SerializedName("txt") val name: String,
    @SerializedName("rate") val rate: Double,
    @SerializedName("cc") val code: String,
    @SerializedName("exchangedate") val date: String
)