package com.direpredium.currencyexchanger.model.network.retrofit

import com.direpredium.currencyexchanger.config.ApiConfig

object Common {
    private val BASE_URL = ApiConfig.baseUrl
    val retrofitService: RetrofitCurrencyService
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitCurrencyService::class.java)
}