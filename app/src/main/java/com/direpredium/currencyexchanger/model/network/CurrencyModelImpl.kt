package com.direpredium.currencyexchanger.model.network

import com.direpredium.currencyexchanger.config.ApiConfig
import com.direpredium.currencyexchanger.model.network.entity.ExchangeRate
import com.direpredium.currencyexchanger.model.network.exception.CustomException
import com.direpredium.currencyexchanger.model.network.exception.NoApiConnectionException
import com.direpredium.currencyexchanger.model.network.retrofit.RetrofitCurrencyRESTApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class CurrencyModelImpl: CurrencyModel<ExchangeRate, CustomException>() {
    private var executor: ScheduledExecutorService? = null
    private var currencyRESTApi: CurrencyRESTApi? = null

    init {
        try {
            currencyRESTApi = RetrofitCurrencyRESTApi()
        } catch(ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
    }

    override fun startUpdating() {
        executor = Executors.newSingleThreadScheduledExecutor()
        executor?.scheduleAtFixedRate({
            GlobalScope.launch(Dispatchers.IO) {
                val currencyRESTApiTemp = currencyRESTApi
                if(currencyRESTApiTemp == null) {
                    withContext(Dispatchers.Main) {
                        onFailure(NoApiConnectionException(1003, "Unable to connect to API. Failed to create object 'currencyRESTApi'"))
                    }
                    return@launch
                }
                currencyRESTApiTemp.getExchangeRate(::onFailure, ::onResponse)
            }
        }, 0, ApiConfig.timeout, TimeUnit.MILLISECONDS)
    }

    override fun stopUpdating() {
        executor?.shutdown()
        executor = null
    }

    private fun onFailure(ex : CustomException) {
        notifyFailListeners(ex)
    }

    private fun onResponse(exchangeRate: ExchangeRate) {
        setLatestUpdate(exchangeRate)
        notifyListeners(exchangeRate)
    }

}