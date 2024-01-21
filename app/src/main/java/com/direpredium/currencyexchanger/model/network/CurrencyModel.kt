package com.direpredium.currencyexchanger.model.network

import android.util.Log
import com.direpredium.currencyexchanger.model.network.entity.ExchangeRate
import com.direpredium.currencyexchanger.model.network.retrofit.RetrofitCurrencyRESTApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class CurrencyModel {
    private var executor: ScheduledExecutorService? = null
    private val currencyRESTApi: CurrencyRESTApi = RetrofitCurrencyRESTApi()
    private var latestExchangeRate: ExchangeRate? = null
    private val exchangeRateListeners = mutableListOf<(ExchangeRate) -> Unit>()
    private val exchangeFailRateListeners = mutableListOf<() -> Unit>()

    fun startUpdating() {
        executor = Executors.newSingleThreadScheduledExecutor()
        executor?.scheduleAtFixedRate({
            GlobalScope.launch(Dispatchers.IO) {
                currencyRESTApi.getExchangeRate(::onFailure, ::onResponse)
            }
        }, 0, 5, TimeUnit.SECONDS)
    }

    fun stopUpdating() {
        executor?.shutdown()
        executor = null
    }

    private fun onFailure() {
        notifyFailExchangeRateListeners()
    }

    private fun onResponse(exchangeRate: ExchangeRate) {
        latestExchangeRate = exchangeRate
        notifyExchangeRateListeners(exchangeRate)
    }

    fun addFailExchangeRateListener(listener: () -> Unit) {
        exchangeFailRateListeners.add(listener)
    }

    private fun notifyFailExchangeRateListeners() {
        exchangeFailRateListeners.forEach { it.invoke() }
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