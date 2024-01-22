package com.direpredium.currencyexchanger

import android.app.Application
import com.direpredium.currencyexchanger.config.ApiConfig

class CurrencyExchangerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ApiConfig.init(this)
    }
}