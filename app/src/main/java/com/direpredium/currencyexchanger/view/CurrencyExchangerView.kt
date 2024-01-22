package com.direpredium.currencyexchanger.view

import com.direpredium.currencyexchanger.model.db.entity.Transaction
import com.direpredium.currencyexchanger.model.db.entity.Wallet
import com.direpredium.currencyexchanger.model.network.exception.CustomException

interface CurrencyExchangerView {
    fun showWallets(wallets: List<Wallet>)
    fun showConvertedMoney(convertedMoney: Double)
    fun showConvertDialog(transaction: Transaction)
    fun showNetworkError(ex: CustomException)
    fun showConvertError(message: String)
    fun updateBaseCurrencies(currencies: List<String>)
    fun updateTargetCurrencies(currencies: List<String>)
    fun updateConversion(): Boolean
}