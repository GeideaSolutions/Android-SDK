package net.geidea.paymentsdk.model.meezaqr

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import net.geidea.paymentsdk.model.common.GeideaResponse
import java.util.*

@Parcelize
@Serializable
class MeezaPaymentResponse private constructor(
        override val responseCode: String? = null,
        override var responseMessage: String? = null,      // This is made a var as a workaround
        override val detailedResponseCode: String? = null,
        override val detailedResponseMessage: String? = null,
        override val language: String? = null,
        val responseDescription: String? = null,
        val receiverName: String? = null,
        val receiverAddress: String? = null,
) : GeideaResponse, GeideaJsonObject, Parcelable {

    init {
        if (!isSuccess && responseDescription != null && responseMessage == null) {
            // Copy Meeza message as our responseMessage as this response is the only one
            // which receives the message in a different field other than our common 'responseMessage'
            responseMessage = responseDescription
        }
    }

    override val isSuccess: Boolean
        get() = responseCode == RESPONSE_CODE_SUCCESS

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MeezaPaymentResponse

        if (responseCode != other.responseCode) return false
        if (responseMessage != other.responseMessage) return false
        if (detailedResponseCode != other.detailedResponseCode) return false
        if (detailedResponseMessage != other.detailedResponseMessage) return false
        if (responseDescription != other.responseDescription) return false
        if (receiverName != other.receiverName) return false
        if (receiverAddress != other.receiverAddress) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                responseCode,
                responseMessage,
                detailedResponseCode,
                detailedResponseMessage,
                language,
                responseDescription,
                receiverName,
                receiverAddress,
        )
    }

    // GENERATED
    override fun toString(): String {
        return "MeezaPaymentResponse(responseCode=$responseCode, responseMessage=$responseMessage, detailedResponseCode=$detailedResponseCode, detailedResponseMessage=$detailedResponseMessage, language=$language, responseDescription=$responseDescription, receiverName=$receiverName, receiverAddress=$receiverAddress)"
    }

    companion object {
        private const val RESPONSE_CODE_SUCCESS = "00000"

        @JvmStatic
        fun fromJson(json: String): MeezaPaymentResponse = decodeFromJson(json)
    }
}