package com.direpredium.currencyexchanger.model.network

import com.direpredium.currencyexchanger.model.network.entity.ExchangeRate

interface CurrencyRESTApi {
    fun getExchangeRate(onFailure : () -> Unit, onResponse : (exchangeRate : ExchangeRate) -> Unit)
}