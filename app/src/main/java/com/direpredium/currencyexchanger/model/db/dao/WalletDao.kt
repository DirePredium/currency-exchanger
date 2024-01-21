package com.direpredium.currencyexchanger.model.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.direpredium.currencyexchanger.model.db.entity.Wallet

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallet WHERE currency = :currency")
    suspend fun getByCurrency(currency: String): Wallet?

    @Query("SELECT * FROM wallet")
    suspend fun getAll(): List<Wallet>

    @Query("SELECT * FROM wallet WHERE id IN (:walletIds)")
    suspend fun loadAllByIds(walletIds: IntArray): List<Wallet>

    @Insert
    suspend fun insertAll(vararg wallets: Wallet)

    @Delete
    suspend fun delete(wallet: Wallet)

    @Update
    suspend fun updateWallet(wallet: Wallet)

    @Update
    suspend fun updateWallets(vararg wallets: Wallet)

    @Query("UPDATE Wallet SET cash = :cash WHERE currency = :currency")
    suspend fun updateWalletByCurrency(currency: String, cash: Double)
}