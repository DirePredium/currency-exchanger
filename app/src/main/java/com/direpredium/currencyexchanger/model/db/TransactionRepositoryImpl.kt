package com.direpredium.currencyexchanger.model.db

import com.direpredium.currencyexchanger.model.db.entity.Transaction

class TransactionRepositoryImpl(private val database: CurrencyExchangerDatabase) : TransactionRepository {
    override suspend fun getAllTransaction(): List<Transaction> = database.transactionDao().getAll()
    override suspend fun insertAll(vararg transactions: Transaction) = database.transactionDao().insertAll(*transactions)
}