package net.geidea.paymentsdk.model.bnpl.valu

import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.LocalizableRequest
import java.util.*

@Serializable
class GenerateOtpRequest constructor(
        override var language: String? = null,
        val customerIdentifier: String,
        val bnplOrderId: String,
) : LocalizableRequest {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GenerateOtpRequest

        if (language != other.language) return false
        if (customerIdentifier != other.customerIdentifier) return false
        if (bnplOrderId != other.bnplOrderId) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(language, customerIdentifier, bnplOrderId)
    }

    override fun toString(): String {
        return "GenerateOtpRequest(language=$language, customerIdentifier='$customerIdentifier', bnplOrderId='$bnplOrderId')"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): GenerateOtpRequest = decodeFromJson(json)
    }
}