package com.direpredium.currencyexchanger.model.network

import com.direpredium.currencyexchanger.config.ApiConfig
import com.direpredium.currencyexchanger.model.network.exception.CustomException
import com.direpredium.currencyexchanger.model.network.exception.NoApiConnectionException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

abstract class CurrencyModel<T, E : Exception> {
    private var latestUpdate: T? = null
    private val tListeners = mutableListOf<(T) -> Unit>()
    private val tFailListeners = mutableListOf<(E) -> Unit>()

    abstract fun startUpdating()

    abstract fun stopUpdating()

    fun addFailListener(listener: (ex: E) -> Unit) {
        tFailListeners.add(listener)
    }

    protected fun notifyFailListeners(ex: E) {
        tFailListeners.forEach { it.invoke(ex) }
    }

    fun clearFailListener() {
        tFailListeners.clear()
    }

    fun addListener(listener: (T) -> Unit) {
        tListeners.add(listener)
        latestUpdate?.let { listener.invoke(it) }
    }

    protected fun notifyListeners(t: T) {
        tListeners.forEach { it.invoke(t) }
    }

    fun clearListener() {
        tListeners.clear()
    }

    fun getLatestUpdate(): T? {
        return latestUpdate
    }

    fun setLatestUpdate(t: T) {
        latestUpdate = t
    }
}
