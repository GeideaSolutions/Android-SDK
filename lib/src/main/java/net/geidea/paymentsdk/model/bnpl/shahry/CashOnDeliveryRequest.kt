package net.geidea.paymentsdk.model.bnpl.shahry

import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.LocalizableRequest
import java.util.*

@Serializable
class CashOnDeliveryRequest(
        override var language: String? = null,
        val orderId: String? = null,
) : LocalizableRequest {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CashOnDeliveryRequest

        if (language != other.language) return false
        if (orderId != other.orderId) return false

        return true
    }

    override fun hashCode(): Int = Objects.hash(language, orderId)

    override fun toString(): String {
        return "CashOnDeliveryRequest(language=$language, orderId=$orderId)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): CashOnDeliveryRequest = decodeFromJson(json)
    }
}