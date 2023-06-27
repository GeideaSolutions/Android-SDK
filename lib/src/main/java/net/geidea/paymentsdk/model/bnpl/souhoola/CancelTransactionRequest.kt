package net.geidea.paymentsdk.model.bnpl.souhoola

import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.LocalizableRequest
import java.util.*

@Serializable
class CancelTransactionRequest constructor(
        override var language: String? = null,
        val customerIdentifier: String,
        val customerPin: String,
        val souhoolaTransactionId: String,
) : LocalizableRequest {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CancelTransactionRequest

        if (language != other.language) return false
        if (customerIdentifier != other.customerIdentifier) return false
        if (customerPin != other.customerPin) return false
        if (souhoolaTransactionId != other.souhoolaTransactionId) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(language, customerIdentifier, customerPin, souhoolaTransactionId)
    }

    // GENERATED
    override fun toString(): String {
        return "CancelTransactionRequest(language=$language, customerIdentifier='$customerIdentifier', customerPin='$customerPin', souhoolaTransactionId='$souhoolaTransactionId')"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): CancelTransactionRequest = decodeFromJson(json)
    }
}