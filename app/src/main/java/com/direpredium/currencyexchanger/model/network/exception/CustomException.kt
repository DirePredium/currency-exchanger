package com.direpredium.currencyexchanger.model.network.exception

open class CustomException(
    val userMessage: String,
    devMessage: String,
    cause: Throwable? = null
) : Exception(devMessage, cause)