package com.direpredium.currencyexchanger.model.network

import com.direpredium.currencyexchanger.model.network.entity.ExchangeRate
import com.direpredium.currencyexchanger.model.network.exception.CustomException

interface CurrencyRESTApi {
    fun getExchangeRate(onFailure : (ex: CustomException) -> Unit, onResponse : (exchangeRate: ExchangeRate) -> Unit)
}