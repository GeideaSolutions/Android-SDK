package net.geidea.paymentsdk.model.order

import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.LocalizableRequest
import java.util.*

@Serializable
class CancelRequest private constructor(
        override var language: String? = null,
        val orderId: String,
        val reason: String? = null,
) : LocalizableRequest {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CancelRequest

        if (language != other.language) return false
        if (orderId != other.orderId) return false
        if (reason != other.reason) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                language,
                orderId,
                reason
        )
    }

    // GENERATED
    override fun toString(): String {
        return "CancelRequest(language='$language', orderId='$orderId', reason='$reason')"
    }

    class Builder {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var language: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var orderId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var reason: String? = null

        fun setLanguage(language: String?): Builder = apply { this.language = language }
        fun setOrderId(orderId: String?): Builder = apply { this.orderId = orderId }
        fun setReason(reason: String?): Builder = apply { this.reason = reason }

        fun build(): CancelRequest {
            return CancelRequest(
                    language = this.language,
                    orderId = requireNotNull(this.orderId) { "Missing orderId" },
                    reason = requireNotNull(this.reason) { "Missing reason" },
            )
        }
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): CancelRequest = decodeFromJson(json)

        const val REASON_CANCELLED_BY_USER = "CancelledByUser"
        const val REASON_TIMED_OUT = "TimedOut"
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun CancelRequest(initializer: CancelRequest.Builder.() -> Unit): CancelRequest {
    return CancelRequest.Builder().apply(initializer).build()
}