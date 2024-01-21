package com.direpredium.currencyexchanger.model.network.retrofit

object Common {
    private val BASE_URL = "https://developers.paysera.com/tasks/api/"
    val retrofitService: RetrofitCurrencyService
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitCurrencyService::class.java)
}