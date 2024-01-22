package com.direpredium.currencyexchanger.model.db

import com.direpredium.currencyexchanger.model.db.entity.Transaction

interface TransactionRepository {
    suspend fun getAllTransaction(): List<Transaction>
    suspend fun insertAll(vararg transactions: Transaction)
}