package com.direpredium.currencyexchanger.model.network

import android.util.Log
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

class CurrencyModel {
    private var executor: ScheduledExecutorService? = null
    private var currencyRESTApi: CurrencyRESTApi? = null
    private var latestExchangeRate: ExchangeRate? = null
    private val exchangeRateListeners = mutableListOf<(ExchangeRate) -> Unit>()
    private val exchangeFailRateListeners = mutableListOf<(CustomException) -> Unit>()

    init {
        try {
            currencyRESTApi = RetrofitCurrencyRESTApi()
        } catch(ex: IllegalArgumentException) {
            //onFailure()
            ex.printStackTrace()
        }
    }

    fun startUpdating() {
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

    fun stopUpdating() {
        executor?.shutdown()
        executor = null
    }

    private fun onFailure(ex : CustomException) {
        notifyFailExchangeRateListeners(ex)
    }

    private fun onResponse(exchangeRate: ExchangeRate) {
        latestExchangeRate = exchangeRate
        notifyExchangeRateListeners(exchangeRate)
    }

    fun addFailExchangeRateListener(listener: (ex: CustomException) -> Unit) {
        exchangeFailRateListeners.add(listener)
    }

    private fun notifyFailExchangeRateListeners(ex : CustomException) {
        exchangeFailRateListeners.forEach { it.invoke(ex) }
    }

    fun clearFailExchangeRateListener() {
        exchangeFailRateListeners.clear()
    }

    fun addExchangeRateListener(listener: (ExchangeRate) -> Unit) {
        exchangeRateListeners.add(listener)
        latestExchangeRate?.let { listener.invoke(it) }
    }

    private fun notifyExchangeRateListeners(exchangeRate: ExchangeRate) {
        exchangeRateListeners.forEach { it.invoke(exchangeRate) }
    }

    fun clearExchangeRateListener() {
        exchangeRateListeners.clear()
    }

    fun getLatestExchangeRate(): ExchangeRate? {
        return latestExchangeRate
    }

}