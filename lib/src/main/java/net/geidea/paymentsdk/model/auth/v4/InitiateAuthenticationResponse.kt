package net.geidea.paymentsdk.model.auth.v4

import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import net.geidea.paymentsdk.model.common.GeideaResponse
import java.util.*

@Parcelize
@Serializable
class InitiateAuthenticationResponse

@GeideaSdkInternal
internal constructor(
        override val responseCode: String? = null,
        override val responseMessage: String? = null,
        override val detailedResponseCode: String? = null,
        override val detailedResponseMessage: String? = null,
        override val language: String? = null,
        val orderId: String? = null,
        val threeDSecureId: String? = null,
        val redirectHtml: String? = null,
        val gatewayDecision: String? = null,
) : GeideaJsonObject, GeideaResponse {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InitiateAuthenticationResponse

        if (responseCode != other.responseCode) return false
        if (responseMessage != other.responseMessage) return false
        if (detailedResponseCode != other.detailedResponseCode) return false
        if (detailedResponseMessage != other.detailedResponseMessage) return false
        if (language != other.language) return false
        if (orderId != other.orderId) return false
        if (threeDSecureId != other.threeDSecureId) return false
        if (redirectHtml != other.redirectHtml) return false
        if (gatewayDecision != other.gatewayDecision) return false

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
                orderId,
                threeDSecureId,
                redirectHtml,
                gatewayDecision
        )
    }

    override fun toString(): String {
        return "InitiateAuthenticationResponse(responseCode=$responseCode, responseMessage=$responseMessage, detailedResponseCode=$detailedResponseCode, detailedResponseMessage=$detailedResponseMessage, language=$language, orderId=$orderId, threeDSecureId=$threeDSecureId, redirectHtml=$redirectHtml, gatewayDecision=$gatewayDecision)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): InitiateAuthenticationResponse = decodeFromJson(json)
    }
}