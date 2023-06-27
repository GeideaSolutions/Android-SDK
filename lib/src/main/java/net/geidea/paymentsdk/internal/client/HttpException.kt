package net.geidea.paymentsdk.internal.client

import java.io.IOException

internal open class HttpException(
        open val statusCode: Int,
        val responseCode: String? = null,
        val responseMessage: String? = null,
        val detailedResponseCode: String? = null,
        val detailedResponseMessage: String? = null,
        val language: String? = null,
        val type: String? = null,
        val title: String? = null,
        val status: Int? = null,
        val traceId: String? = null,
        val errors: Map<String, List<String>>? = emptyMap()
) : IOException("HTTP$statusCode ${responseCode.orEmpty()}.${detailedResponseCode.orEmpty()} ${detailedResponseMessage ?: responseMessage.orEmpty()}")