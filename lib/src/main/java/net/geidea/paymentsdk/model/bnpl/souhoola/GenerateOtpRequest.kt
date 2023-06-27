package net.geidea.paymentsdk.model.bnpl.souhoola

import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.LocalizableRequest
import java.util.*

@Serializable
class GenerateOtpRequest internal constructor(
        override var language: String? = null,
        val customerIdentifier: String,
        val customerPin: String,
        val orderId: String,
        val souhoolaTransactionId: String,
) : LocalizableRequest {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GenerateOtpRequest

        if (language != other.language) return false
        if (customerIdentifier != other.customerIdentifier) return false
        if (customerPin != other.customerPin) return false
        if (orderId != other.orderId) return false
        if (souhoolaTransactionId != other.souhoolaTransactionId) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(language, customerIdentifier, customerPin, orderId, souhoolaTransactionId)
    }

    override fun toString(): String {
        return "GenerateOtpRequest(language=$language, customerIdentifier='$customerIdentifier', customerPin='$customerPin', orderId='$orderId', souhoolaTransactionId='$souhoolaTransactionId')"
    }

    class Builder {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var language: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var customerIdentifier: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var customerPin: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var orderId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var souhoolaTransactionId: String? = null

        fun setLanguage(language: String?): Builder = apply { this.language = language }
        fun setCustomerIdentifier(customerIdentifier: String?): Builder = apply { this.customerIdentifier = customerIdentifier }
        fun setCustomerPin(customerPin: String?): Builder = apply { this.customerPin = customerPin }
        fun setOrderId(orderId: String?): Builder = apply { this.orderId = orderId }
        fun setSouhoolaTransactionId(souhoolaTransactionId: String?): Builder = apply { this.souhoolaTransactionId = souhoolaTransactionId }

        fun build() = GenerateOtpRequest(
            language = this.language,
            customerIdentifier = requireNotNull(this.customerIdentifier) { "Missing customerIdentifier" },
            customerPin = requireNotNull(this.customerPin) { "Missing customerPin" },
            orderId = requireNotNull(this.orderId) { "Missing orderId" },
            souhoolaTransactionId = requireNotNull(this.souhoolaTransactionId) { "Missing souhoolaTransactionId" },
        )
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): GenerateOtpRequest = decodeFromJson(json)
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun GenerateOtpRequest(initializer: GenerateOtpRequest.Builder.() -> Unit): GenerateOtpRequest {
    return GenerateOtpRequest.Builder().apply(initializer).build()
}