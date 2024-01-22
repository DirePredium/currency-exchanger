package com.direpredium.currencyexchanger.model.db

import com.direpredium.currencyexchanger.model.db.entity.Wallet

class WalletRepositoryImpl(private val database: CurrencyExchangerDatabase) : WalletRepository {
    override suspend fun getAllWallets(): List<Wallet> = database.walletDao().getAll()
    override suspend fun insertAll(vararg wallets: Wallet) {
        database.walletDao().insertAll(*wallets)
    }
    override suspend fun getByCurrency(currency: String) = database.walletDao().getByCurrency(currency)
    override suspend fun updateWallets(vararg wallets: Wallet) = database.walletDao().updateWallets(*wallets)
    override suspend fun initWallet(currency : String, cash : String) : Boolean {
        if(database.walletDao().getByCurrency(currency) == null) {
            database.walletDao().insertAll(Wallet(currency, cash))
            return true
        }
        return false
    }
}