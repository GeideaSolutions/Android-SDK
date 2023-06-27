package net.geidea.paymentsdk.model.bnpl.shahry

import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import net.geidea.paymentsdk.model.common.GeideaResponse
import net.geidea.paymentsdk.model.order.Order
import java.util.*

@Parcelize
@Serializable
class ConfirmResponse(
    override val responseCode: String? = null,
    override val responseMessage: String? = null,
    override val detailedResponseCode: String? = null,
    override val detailedResponseMessage: String? = null,
    override val language: String? = null,
    val order: Order? = null,
) : GeideaJsonObject, GeideaResponse {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ConfirmResponse

        if (responseCode != other.responseCode) return false
        if (responseMessage != other.responseMessage) return false
        if (detailedResponseCode != other.detailedResponseCode) return false
        if (detailedResponseMessage != other.detailedResponseMessage) return false
        if (language != other.language) return false
        if (order != other.order) return false

        return true
    }

    override fun hashCode(): Int = Objects.hash(
        responseCode,
        responseMessage,
        detailedResponseCode,
        detailedResponseMessage,
        language,
        order
    )

    override fun toString(): String {
        return "ConfirmResponse(responseCode=$responseCode, responseMessage=$responseMessage, detailedResponseCode=$detailedResponseCode, detailedResponseMessage=$detailedResponseMessage, language=$language, order=$order)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): ConfirmResponse = decodeFromJson(json)
    }
}