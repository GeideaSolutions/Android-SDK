@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.pay

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.PaymentMethod
import net.geidea.paymentsdk.model.common.LocalizableRequest
import java.util.Objects

@Serializable
class PaymentRequest private constructor(
    override var language: String? = null,
    val sessionId: String? = null,
    val orderId: String,
    val threeDSecureId: String? = null,
    val paymentMethod: PaymentMethod,
    val source: String?
) : LocalizableRequest {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PaymentRequest

        if (language != other.language) return false
        if (sessionId != other.sessionId) return false
        if (paymentMethod != other.paymentMethod) return false
        if (threeDSecureId != other.threeDSecureId) return false
        if (orderId != other.orderId) return false
        return source == other.source
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(language, sessionId, paymentMethod, threeDSecureId, orderId, source)
    }

    // GENERATED
    override fun toString(): String {
        return "PaymentRequest(language='$language', sessionId=$sessionId, paymentMethod=$paymentMethod, threeDSecureId=$threeDSecureId, orderId='$orderId', source=$source)"
    }

    class Builder {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var language: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var sessionId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var paymentMethod: PaymentMethod? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var threeDSecureId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var orderId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var source: String? = null

        fun setLanguage(language: String?): Builder = apply { this.language = language }
        fun setSessionId(sessionId: String?): Builder = apply { this.sessionId = sessionId }
        fun setPaymentMethod(paymentMethod: PaymentMethod?): Builder =
            apply { this.paymentMethod = paymentMethod }

        fun setThreeDsSecureId(threeDSecureId: String?): Builder =
            apply { this.threeDSecureId = threeDSecureId }

        fun setOrderId(orderId: String?): Builder = apply { this.orderId = orderId }
        fun setSource(source: String?): Builder = apply { this.source = source }

        fun build(): PaymentRequest {
            return PaymentRequest(
                language = this.language,
                sessionId = requireNotNull(this.sessionId) { "Missing required parameters" },
                paymentMethod = requireNotNull(this.paymentMethod) { "Missing paymentMethod" },
                threeDSecureId = this.threeDSecureId,
                orderId = requireNotNull(this.orderId) { "Missing orderId" },
                source = this.source,
            )
        }
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): PaymentRequest = decodeFromJson(json)
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun PaymentRequest(initializer: PaymentRequest.Builder.() -> Unit): PaymentRequest {
    return PaymentRequest.Builder().apply(initializer).build()
}