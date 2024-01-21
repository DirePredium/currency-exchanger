package com.direpredium.currencyexchanger.model.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["currency"], unique = true)])
data class Wallet (
    @ColumnInfo(name = "currency") val currency: String,
    @ColumnInfo(name = "cash") var cash: Double,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}