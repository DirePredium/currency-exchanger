package com.direpredium.currencyexchanger.view

import com.direpredium.currencyexchanger.model.db.entity.Transaction
import com.direpredium.currencyexchanger.model.db.entity.Wallet
import com.direpredium.currencyexchanger.model.network.exception.CustomException
import java.math.BigDecimal

interface CurrencyExchangerView {
    fun showWallets(wallets: List<Wallet>)
    fun showConvertedMoney(convertedMoney: BigDecimal)
    fun showConvertDialog(transaction: Transaction)
    fun showNetworkError(ex: CustomException)
    fun showConvertError(message: String)
    fun updateBaseCurrencies(currencies: List<String>)
    fun updateTargetCurrencies(currencies: List<String>)
    fun updateConversion(): Boolean
}