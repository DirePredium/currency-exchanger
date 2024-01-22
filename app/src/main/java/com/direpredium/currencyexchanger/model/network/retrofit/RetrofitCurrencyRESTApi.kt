package com.direpredium.currencyexchanger.model.network.retrofit

import com.direpredium.currencyexchanger.model.network.CurrencyRESTApi
import com.direpredium.currencyexchanger.model.network.entity.ExchangeRate
import com.direpredium.currencyexchanger.model.network.exception.CustomException
import com.direpredium.currencyexchanger.model.network.exception.RequestException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException

class RetrofitCurrencyRESTApi : CurrencyRESTApi {
    var currencyService: RetrofitCurrencyService = Common.retrofitService

    override fun getExchangeRate(
        onFailure: (ex: CustomException) -> Unit,
        onResponse: (exchangeRate: ExchangeRate) -> Unit
    ) {
        currencyService.getExchangeRate().enqueue(object : Callback<ExchangeRate> {
            override fun onFailure(call: Call<ExchangeRate>, throwable: Throwable) {
                onFailure(
                    RequestException(
                        1001,
                        "Cannot complete request at address ${call.request().url()}",
                        throwable
                    )
                )
            }

            override fun onResponse(call: Call<ExchangeRate>, response: Response<ExchangeRate>) {
                val exchangeRate = response.body()
                if (exchangeRate == null) {
                    onFailure(
                        RequestException(
                            1002,
                            "Cannot parse into object ExchangeRate"
                        )
                    )
                } else {
                    onResponse(exchangeRate)
                }
            }
        })
    }
}