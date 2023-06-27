package net.geidea.paymentsdk.model.bnpl.valu

import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.LocalizableRequest
import java.util.*

@Serializable
class VerifyCustomerRequest constructor(
        override var language: String? = null,
        val customerIdentifier: String
) : LocalizableRequest {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VerifyCustomerRequest

        if (language != other.language) return false
        if (customerIdentifier != other.customerIdentifier) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(language, customerIdentifier)
    }

    // GENERATED
    override fun toString(): String {
        return "VerifyCustomerRequest(language=$language, customerIdentifier='$customerIdentifier')"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): VerifyCustomerRequest = decodeFromJson(json)
    }
}