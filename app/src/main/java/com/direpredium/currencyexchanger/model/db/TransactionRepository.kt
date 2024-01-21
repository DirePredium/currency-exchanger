package com.direpredium.currencyexchanger.model.db

import com.direpredium.currencyexchanger.model.db.entity.Transaction
import com.direpredium.currencyexchanger.model.db.entity.Wallet

class TransactionRepository(private val database: CurrencyExchangerDatabase) {
    suspend fun getAllTransaction(): List<Transaction> = database.transactionDao().getAll()
    suspend fun insertAll(vararg transactions: Transaction) = database.transactionDao().insertAll(*transactions)
}