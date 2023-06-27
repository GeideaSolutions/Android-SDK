package net.geidea.paymentsdk.model.exception

class SdkException(
        val errorCode: String,
        message: String? = null
) : Exception(message)