package com.direpredium.currencyexchanger.presentor

import com.direpredium.currencyexchanger.model.db.entity.Transaction

interface CommissionStrategy {
    fun calculateCommission(transaction: Transaction): String
}

class FreeTransactionStrategy : CommissionStrategy {
    override fun calculateCommission(transaction: Transaction): String {
        return "0.0"
    }
}

class StandartConversionStrategy : CommissionStrategy {
    override fun calculateCommission(transaction: Transaction): String {
        return "0.007"
    }
}