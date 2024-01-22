package com.direpredium.currencyexchanger.model.network.exception

class RequestException(
    code: Int,
    devMessage: String,
    cause: Throwable? = null
) :
    CustomException(
        "Code: $code. Unable to make a request to the server",
        devMessage,
        cause
    )

class NoApiConnectionException(
    code: Int,
    devMessage: String,
    cause: Throwable? = null
) :
    CustomException(
        "Code: $code. No connection to API",
        devMessage,
        cause
    )