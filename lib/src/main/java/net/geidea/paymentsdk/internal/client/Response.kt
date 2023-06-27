package net.geidea.paymentsdk.internal.client

import net.geidea.paymentsdk.flow.ERR_UNEXPECTED_CLIENT_ERROR_BODY
import net.geidea.paymentsdk.flow.ERR_UNEXPECTED_SERVER_ERROR_BODY
import net.geidea.paymentsdk.model.error.GenericError
import net.geidea.paymentsdk.model.exception.SdkException

internal enum class ResponseType {
    Success,
    Informational,
    Redirection,
    ClientError,
    ServerError
}

internal sealed class Response<R>(val responseType: ResponseType) {
    abstract val statusCode: Int
    abstract val headers: Map<String,List<String>>

    fun unwrapOrThrow(): R {
        return when (this) {
            is Success -> this.data
            is ClientError -> {
                if (this.body is GenericError) {
                    throw HttpException(
                            statusCode = this.statusCode,
                            responseCode = this.body.responseCode,
                            responseMessage = this.body.responseMessage,
                            detailedResponseCode = this.body.detailedResponseCode,
                            detailedResponseMessage = this.body.detailedResponseMessage,
                            language = this.body.language,
                            type = this.body.type,
                            title = this.body.title,
                            status = this.body.status,
                            traceId = this.body.traceId,
                            errors = this.body.errors,
                    )
                } else {
                    throw SdkException(ERR_UNEXPECTED_CLIENT_ERROR_BODY, "statusCode=$statusCode")
                }
            }
            is ServerError -> {
                if (this.body is GenericError) {
                    throw HttpException(
                            statusCode = this.statusCode,
                            responseCode = this.body.responseCode,
                            responseMessage = this.body.responseMessage,
                            detailedResponseCode = this.body.detailedResponseCode,
                            detailedResponseMessage = this.body.detailedResponseMessage,
                            language = this.body.language,
                            type = this.body.type,
                            title = this.body.title,
                            status = this.body.status,
                            traceId = this.body.traceId,
                            errors = this.body.errors,
                    )
                } else {
                    throw SdkException(ERR_UNEXPECTED_SERVER_ERROR_BODY, "statusCode=$statusCode")
                }
            }
            is Informational -> throw UnsupportedOperationException("Informational responses are not supported!")
            is Redirection -> throw UnsupportedOperationException("Redirection responses are not supported!")
        }
    }
}

internal data class Success<R>(
        val data: R,
        override val statusCode: Int = -1,
        override val headers: Map<String, List<String>> = mapOf()
) : Response<R>(ResponseType.Success)

internal data class Informational<R>(
        val statusText: String,
        override val statusCode: Int = -1,
        override val headers: Map<String, List<String>> = mapOf()
) : Response<R>(ResponseType.Informational)

internal data class Redirection<R>(
        override val statusCode: Int = -1,
        override val headers: Map<String, List<String>> = mapOf()
) : Response<R>(ResponseType.Redirection)

internal data class ClientError<R>(
        val body: Any? = null,
        override val statusCode: Int = -1,
        override val headers: Map<String, List<String>> = mapOf()
) : Response<R>(ResponseType.ClientError)

internal data class ServerError<R>(
        val message: String? = null,
        val body: Any? = null,
        override val statusCode: Int = -1,
        override val headers: Map<String, List<String>>
) : Response<R>(ResponseType.ServerError)