package com.direpredium.currencyexchanger.model.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.direpredium.currencyexchanger.presentor.CommissionStrategy
import java.math.BigDecimal

@Entity
data class Transaction (
    @ColumnInfo(name = "base_currency") val baseCurrency: String,
    @ColumnInfo(name = "base_money") val baseMoney: String,
    @ColumnInfo(name = "target_currency") val targetCurrency: String,
    @ColumnInfo(name = "target_money") val targetMoney: String,
    @ColumnInfo(name = "commission") var commission: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

fun Transaction.calculateCommission(strategy: CommissionStrategy) {
    this.commission = strategy.calculateCommission(this)
}