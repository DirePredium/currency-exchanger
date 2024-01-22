package com.direpredium.currencyexchanger.presentor

import android.util.Log
import com.direpredium.currencyexchanger.model.db.TransactionRepository
import com.direpredium.currencyexchanger.model.db.WalletRepository
import com.direpredium.currencyexchanger.model.db.entity.Transaction
import com.direpredium.currencyexchanger.model.db.entity.Wallet
import com.direpredium.currencyexchanger.model.db.entity.calculateCommission
import com.direpredium.currencyexchanger.model.network.CurrencyModel
import com.direpredium.currencyexchanger.model.network.entity.ExchangeRate
import com.direpredium.currencyexchanger.model.network.entity.getCurrencies
import com.direpredium.currencyexchanger.model.network.entity.getExchangeRate
import com.direpredium.currencyexchanger.model.network.exception.CustomException
import com.direpredium.currencyexchanger.view.CurrencyExchangerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class CurrencyExchangerPresenterImpl(
    private val view: CurrencyExchangerView,
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository,
    private val currencyModel: CurrencyModel) {
    init {
        initEuroWallet()
    }
    private var isExchangeRateChanged: Boolean = true
    private var currentExchangeRate : ExchangeRate? = null
        set(exchangeRate) {
            if(field == exchangeRate) {
                isExchangeRateChanged = false
                return
            }
            isExchangeRateChanged = true
            field = exchangeRate
        }

    private fun initEuroWallet() {
        CoroutineScope(Dispatchers.IO).launch {
            walletRepository.initWallet("EUR", 1000.0)
        }
    }

    fun loadUpdates(latestExchangeRate: ExchangeRate) {
        currentExchangeRate = currencyModel.getLatestExchangeRate()
        if (isExchangeRateChanged) {
            CoroutineScope(Dispatchers.Main).launch {
                updateBaseCurrencies(latestExchangeRate)
                view.updateTargetCurrencies(latestExchangeRate.getCurrencies())
                view.updateConversion()
            }
        }
    }

    private suspend fun updateBaseCurrencies(exchangeRate: ExchangeRate) {
        val baseCurrencies = withContext(Dispatchers.IO) {
            getFilteredCurrenciesByWallet(
                exchangeRate.getCurrencies(),
                walletRepository.getAllWallets()
            )
        }
        view.updateBaseCurrencies(baseCurrencies)
    }

    private fun getFilteredCurrenciesByWallet(baseCurrencies : List<String>, wallets: List<Wallet>) : List<String> {
        return baseCurrencies.filter { currency ->
            wallets.any { wallet -> wallet.currency == currency }
        }
    }

    fun loadConvertedMoney(money: Double, baseCurrency: String, targetCurrency: String) {
        val exchangeRate = currencyModel.getLatestExchangeRate()
        val convertedMoney = convertMoney(
            baseCurrency,
            money,
            targetCurrency
        )
        if (convertedMoney == null) {
            view.showConvertError("Exchange rates not available.")
            return
        }
        view.showConvertedMoney(convertedMoney)
    }

    private fun convertMoney(
        baseCurrency: String,
        money: Double,
        targetCurrency: String,
    ): Double? {
        val exchangeRate = currentExchangeRate ?: return null
        val rate = exchangeRate.getExchangeRate(baseCurrency, targetCurrency)
        if (rate != null) {
            return convertCounting(money, rate)
        }
        return null
    }

    private fun convertCounting(
        money: Double,
        rate: Double,
        commission: Double = 0.0
    ): Double {
        return roundUp( money * rate * (1 - commission))
    }

    private fun roundUp(num: Double) : Double {
        return String.format(Locale.US, "%.2f", num).toDouble()
    }

    fun startUpdatingRates() {
        currencyModel.startUpdating()
        currencyModel.addExchangeRateListener(::loadUpdates)
        currencyModel.addFailExchangeRateListener(::showNetworkError)
    }

    fun showNetworkError(ex: CustomException) {
        view.showNetworkError(ex)
    }

    fun stopUpdatingRates() {
        currencyModel.stopUpdating()
    }

    fun showConvertDialog(baseCurrency : String, baseMoney : Double, targetCurrency : String) {
        CoroutineScope(Dispatchers.Main).launch {
            convertMoney(baseCurrency,baseMoney, targetCurrency)?.let {
                val transaction = Transaction(baseCurrency, baseMoney, targetCurrency, it, 0.0)
                withContext(Dispatchers.IO) {
                    calculateTransaction(transaction)
                }
                view.showConvertDialog(transaction)
            }
        }
    }

    private suspend fun calculateTransaction(transaction : Transaction) {
        val strategy = when {
            transactionRepository.getAllTransaction().size < 5 -> FreeTransactionStrategy()
            transaction.baseCurrency == "USD" -> FreeTransactionStrategy()
            else -> StandartConversionStrategy()
        }
        transaction.calculateCommission(strategy)
    }

    fun loadWallets() {
        CoroutineScope(Dispatchers.Main).launch {
            val wallets = withContext(Dispatchers.IO) {
                walletRepository.getAllWallets()
            }
            view.showWallets(wallets)
        }
    }

    fun loadTransactions() {
//        CoroutineScope(Dispatchers.Main).launch {
//            val transactions = withContext(Dispatchers.IO) {
//                repository.getAllTransactions()
//            }
//        }
    }

    fun convert(transaction: Transaction) {
        CoroutineScope(Dispatchers.Main).launch {
            val conversionResult = withContext(Dispatchers.IO) {
                convertMoneyAndExecute(transaction)
            }

            if (conversionResult.successful) {
                loadWallets()

                if (conversionResult.isWalletCountChanged) {
                    currentExchangeRate?.let { updateBaseCurrencies(it) }
                }

                view.updateConversion()
            }
        }
    }

    private suspend fun convertMoneyAndExecute(transaction: Transaction): ConversionResult {
        val walletCountBefore = walletRepository.getAllWallets().size
        val successfulConversion = executeConversion(transaction)
        val walletCountAfter = walletRepository.getAllWallets().size
        val isWalletCountChanged = walletCountBefore != walletCountAfter

        return ConversionResult(successfulConversion, isWalletCountChanged)
    }

    private data class ConversionResult(
        val successful: Boolean,
        val isWalletCountChanged: Boolean
    )

    private suspend fun executeConversion(transaction: Transaction): Boolean {
        val baseWallet = walletRepository.getByCurrency(transaction.baseCurrency)
        var targetWallet = walletRepository.getByCurrency(transaction.targetCurrency)

        if (baseWallet == null || baseWallet == targetWallet) {
            return false
        }
        baseWallet.cash -= roundUp(transaction.baseMoney * (1 + transaction.commission))
        if (baseWallet.cash < 0) {
            return false
        }

        if(targetWallet == null) {
            targetWallet = Wallet(transaction.targetCurrency, transaction.targetMoney)
            walletRepository.insertAll(targetWallet)
        }

        targetWallet.cash += transaction.targetMoney
        walletRepository.updateWallets(baseWallet, targetWallet)
        transactionRepository.insertAll(transaction)

        Log.d("MyLog", "Size: ${transactionRepository.getAllTransaction().size}")
        Log.d("MyLog", "Transactions: ")
        transactionRepository.getAllTransaction().forEach {
            Log.d("MyLog", it.toString())
        }
        return true
    }

}