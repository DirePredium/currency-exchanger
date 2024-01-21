package com.direpredium.currencyexchanger.model.network.retrofit

import com.direpredium.currencyexchanger.model.network.entity.ExchangeRate
import retrofit2.Call
import retrofit2.http.GET

interface RetrofitCurrencyService {
    @GET("currency-exchange-rates")
    fun getExchangeRate(): Call<ExchangeRate>
}