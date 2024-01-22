package com.direpredium.currencyexchanger.model.db

import com.direpredium.currencyexchanger.model.db.entity.Wallet

interface WalletRepository {
    suspend fun getAllWallets(): List<Wallet>
    suspend fun insertAll(vararg wallets: Wallet)
    suspend fun getByCurrency(currency: String) : Wallet?
    suspend fun updateWallets(vararg wallets: Wallet)
    suspend fun initWallet(currency : String, cash : String) : Boolean
}