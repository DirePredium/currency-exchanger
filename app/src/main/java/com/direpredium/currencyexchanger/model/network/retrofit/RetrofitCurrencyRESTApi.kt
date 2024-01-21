package com.direpredium.currencyexchanger.model.network.retrofit

import com.direpredium.currencyexchanger.model.network.CurrencyRESTApi
import com.direpredium.currencyexchanger.model.network.entity.ExchangeRate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RetrofitCurrencyRESTApi : CurrencyRESTApi {
    var currencyService: RetrofitCurrencyService = Common.retrofitService

    override fun getExchangeRate(onFailure : () -> Unit, onResponse : (exchangeRate : ExchangeRate) -> Unit) {
        currencyService.getExchangeRate().enqueue(object : Callback<ExchangeRate> {
            override fun onFailure(call: Call<ExchangeRate>, throwable: Throwable) {
                onFailure()
            }

            override fun onResponse(call: Call<ExchangeRate>, response: Response<ExchangeRate>) {
                val exchangeRate = response.body()
                if (exchangeRate == null) {
                    onFailure()
                }else {
                    onResponse(exchangeRate)
                }
            }
        })
    }
}