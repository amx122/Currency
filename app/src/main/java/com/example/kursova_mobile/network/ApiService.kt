package com.example.kursova_mobile.network

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("NBUStatService/v1/statdirectory/exchange?json")
    suspend fun getNbuRates(): List<NbuRate>

    @GET("NBUStatService/v1/statdirectory/exchange?json")
    suspend fun getRateByDate(
        @Query("valcode") currencyCode: String,
        @Query("date") date: String
    ): List<NbuRate>
}