package com.direpredium.currencyexchanger.model.network.entity

import android.util.ArrayMap

data class ExchangeRate (
    var base: String? = null,
    var date: String? = null,
    var rates: ArrayMap<String, Double>? = null,
)

fun ExchangeRate.getExchangeRate(baseCurrency: String, targetCurrency: String): Double? {
    val baseRate = rates?.get(baseCurrency)
    val targetRate = rates?.get(targetCurrency)

    return baseRate?.let { targetRate?.div(it) }
}

fun ExchangeRate.getCurrencies(): List<String> {
    return rates.orEmpty().keys.toList()
}