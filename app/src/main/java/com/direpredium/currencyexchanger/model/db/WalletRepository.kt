package com.direpredium.currencyexchanger.model.db

import androidx.room.Insert
import com.direpredium.currencyexchanger.model.db.entity.Wallet

class WalletRepository(private val database: CurrencyExchangerDatabase) {
    suspend fun getAllWallets(): List<Wallet> = database.walletDao().getAll()
    suspend fun insertAll(vararg wallets: Wallet) {
        database.walletDao().insertAll(*wallets)
    }
    suspend fun getByCurrency(currency: String) = database.walletDao().getByCurrency(currency)
    suspend fun updateWallets(vararg wallets: Wallet) = database.walletDao().updateWallets(*wallets)
    suspend fun initWallet(currency : String, cash : String) : Boolean {
        if(database.walletDao().getByCurrency(currency) == null) {
            database.walletDao().insertAll(Wallet(currency, cash))
            return true
        }
        return false
    }
}