package net.geidea.paymentsdk.flow

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.internal.client.HttpException
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.internal.util.Logger.loge
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import net.geidea.paymentsdk.model.common.GeideaResponse

/**
 * Parcelable final output of Geidea network operation(s) or UI flow.
 *
 * @param R type of the desired successful result
 */
sealed class GeideaResult<out R : Parcelable> : Parcelable {

    /**
     * Success result containing the output [data]
     */
    @Parcelize
    data class Success<R : Parcelable>(val data: R) : GeideaResult<R>()

    /**
     * Indicates that the UI flow is interrupted -  normally by the user via Back button.
     */
    @Serializable
    @Parcelize
    data class Cancelled(
            override val responseCode: String? = null,
            override val responseMessage: String? = null,
            override val detailedResponseCode: String? = null,
            override val detailedResponseMessage: String? = null,
            override val language: String? = null,
            val orderId: String? = null,
    ) : GeideaResult<Nothing>(), GeideaResponse, GeideaJsonObject {

        override fun toJson(pretty: Boolean): String = encodeToJson(pretty)
    }

    /**
     * Base class of any errors which might occur during a UI flow that SDK cannot handle. In those
     * cases the SDK has interrupted the flow.
     */
    abstract class Error : GeideaResult<Nothing>(), GeideaJsonObject

    @Serializable
    @Parcelize
    data class InvalidInputError(
            val errorMessage: String
    ) : Error() {
        override fun toJson(pretty: Boolean): String = encodeToJson(pretty)
    }

    /**
     * Network error.
     */
    @Serializable
    @Parcelize
    data class NetworkError(
            /**
             * If [orderId] is not null then an order was created but something else in the
             * flow has failed. That means the Merchant could attempt again to request payment
             * for this [orderId].
             */
            val orderId: String? = null,
            val httpStatusCode: Int? = null,
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
    ) : Error() {

        constructor(orderId: String? = null, response: GeideaResponse? = null) : this(
                orderId = orderId,
                responseCode = response?.responseCode,
                responseMessage = response?.responseMessage,
                detailedResponseCode = response?.detailedResponseCode,
                detailedResponseMessage = response?.detailedResponseMessage,
                language = response?.language
        )

        internal constructor(
            orderId: String? = null, responseMessage: String? = null) : this(
                orderId = orderId,
                responseCode = null,
                responseMessage = responseMessage,
        )

        override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

        fun hasCode(responseCode: String, detailedResponseCode: String): Boolean {
            return this.responseCode == responseCode && this.detailedResponseCode == detailedResponseCode
        }
    }

    /**
     * Error indicating that an unexpected condition has occurred in the SDK. Usually signals
     * improper SDK integration or SDK internal bug. For more info contact Support and provide them
     * with the [errorCode] and [errorMessage].
     */
    @Serializable
    @Parcelize
    data class SdkError(
            val errorCode: String? = null,
            val errorMessage: String? = null
    ) : Error() {
        constructor(throwable: Throwable)
                : this(errorMessage = throwable.message ?: throwable.javaClass.toString())

        override fun toJson(pretty: Boolean): String = encodeToJson(pretty)
    }
}

// TODO optional orderId is necessary to be included
internal fun <R : Parcelable> Throwable.toGeideaResult(): GeideaResult<R> {
    return when (this) {
        is HttpException -> GeideaResult.NetworkError(
            httpStatusCode = statusCode,
            responseCode = responseCode,
            responseMessage = responseMessage,
            detailedResponseCode = detailedResponseCode,
            detailedResponseMessage = detailedResponseMessage,
            language = language,
            type = type,
            title = title,
            status = status,
            traceId = traceId,
            errors = errors,
        )
        else -> GeideaResult.SdkError(throwable = this)
    }
}

internal suspend fun <T : GeideaResponse> responseAsResult(block: suspend () -> T): GeideaResult<T> {
    return responseAsResult(::asIs, block)
}

internal suspend fun <T : GeideaResponse, R : Parcelable> responseAsResult(
        outputTransform: (T) -> R,
        block: suspend () -> T
): GeideaResult<R> {
    return try {
        val response = block()
        if (response.isSuccess) {
            GeideaResult.Success(outputTransform(response))
        } else {
            GeideaResult.NetworkError(response = response)
        }
    } catch (t: Throwable) {
        loge(t.stackTraceToString())
        // TODO treat cancellation exceptions as Cancelled result?
        t.toGeideaResult()
    }
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun <T> asIs(input: T): T = input