package net.geidea.paymentsdk.model.meezaqr

import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import net.geidea.paymentsdk.model.common.GeideaResponse

@Parcelize
@Serializable
data class MeezaQrImageResponse(
        override val responseCode: String? = null,
        override val responseMessage: String? = null,
        override val detailedResponseCode: String? = null,
        override val detailedResponseMessage: String? = null,
        override val language: String? = null,

        /**
         * The payment intent ID created for this particular QR payment.
         */
        val paymentIntentId: String? = null,
        /**
         * The QR code image represented as Base64 string.
         */
        val image: String? = null,

        /**
         * MIME type of the QR [image] (normally 'image/png').
         */
        val type: String? = null,

        /**
         * The Meeza QR payment message encoded in the QR [image]. Used as an alternative to
         * QR code scanning.
         *
         * @see MeezaPaymentRequest
         */
        val message: String? = null,

) : GeideaJsonObject, GeideaResponse {

    override val isSuccess: Boolean
        get() = image != null && type != null && message != null

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    companion object {
        @JvmStatic
        fun fromJson(json: String): MeezaQrImageResponse = Json.decodeFromString(json)
    }
}