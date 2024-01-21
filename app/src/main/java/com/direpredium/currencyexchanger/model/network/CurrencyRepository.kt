package com.direpredium.currencyexchanger.model.network

import com.direpredium.currencyexchanger.model.network.entity.ExchangeRate

class CurrencyRepository(private val apiService: CurrencyRESTApi) {

    fun getExchangeRate(onFailure : () -> Unit, onResponse : (ExchangeRate) -> Unit) {
        return apiService.getExchangeRate(onFailure, onResponse)
    }

}