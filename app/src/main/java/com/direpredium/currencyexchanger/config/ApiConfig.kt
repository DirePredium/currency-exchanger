package com.direpredium.currencyexchanger.config

import android.content.Context
import com.direpredium.currencyexchanger.R
import java.util.Properties

object ApiConfig {
    lateinit var baseUrl: String
    var timeout: Long = 0

    fun init(context: Context) {
        val properties = Properties()
        val inputStream = context.resources.openRawResource(R.raw.api_config)

        try {
            properties.load(inputStream)
            baseUrl = properties.getProperty("BASE_URL")
            timeout = properties.getProperty("TIMEOUT").toLong()
        } finally {
            inputStream.close()
        }
    }
}