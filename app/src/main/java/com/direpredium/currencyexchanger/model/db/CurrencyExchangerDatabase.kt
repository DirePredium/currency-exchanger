package com.direpredium.currencyexchanger.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.direpredium.currencyexchanger.model.db.dao.TransactionDao
import com.direpredium.currencyexchanger.model.db.dao.WalletDao
import com.direpredium.currencyexchanger.model.db.entity.Transaction
import com.direpredium.currencyexchanger.model.db.entity.Wallet

@Database(entities = [Wallet::class, Transaction::class], version = 1)
abstract class CurrencyExchangerDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao
    abstract fun transactionDao(): TransactionDao
}