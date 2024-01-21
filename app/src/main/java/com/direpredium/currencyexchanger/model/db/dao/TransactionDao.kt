package com.direpredium.currencyexchanger.model.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.direpredium.currencyexchanger.model.db.entity.Transaction

@Dao
interface TransactionDao {
    @Query("SELECT * FROM `transaction`")
    suspend fun getAll(): List<Transaction>

    @Query("SELECT * FROM `transaction` WHERE id IN (:transactionIds)")
    suspend fun loadAllByIds(transactionIds: IntArray): List<Transaction>

    @Query("SELECT * FROM `transaction` WHERE base_currency LIKE :baseCurrency LIMIT 1")
    suspend fun findByCurrency(baseCurrency: String, ): Transaction

    @Insert
    suspend fun insertAll(vararg transactions: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)
}