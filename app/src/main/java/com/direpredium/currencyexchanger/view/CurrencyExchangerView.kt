package com.direpredium.currencyexchanger.view

import com.direpredium.currencyexchanger.model.db.entity.Transaction
import com.direpredium.currencyexchanger.model.db.entity.Wallet

interface CurrencyExchangerView {
    fun showWallets(wallets: List<Wallet>)
    fun showConvertedMoney(convertedMoney: Double)
    fun showConvertDialog(transaction: Transaction)
    fun showNetworkError()
    fun showConvertError(message: String)
    fun updateBaseCurrencies(currencies: List<String>)
    fun updateTargetCurrencies(currencies: List<String>)
    fun updateConversion(): Boolean
}