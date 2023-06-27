package net.geidea.paymentsdk.model.order

import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.LocalizableRequest
import java.util.*

@Serializable
class RefundRequest private constructor(
        override var language: String? = null,
        val orderId: String,
        val callbackUrl: String? = null,
) : LocalizableRequest {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RefundRequest

        if (language != other.language) return false
        if (orderId != other.orderId) return false
        if (callbackUrl != other.callbackUrl) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                language,
                orderId,
                callbackUrl,
        )
    }

    // GENERATED
    override fun toString(): String {
        return "RefundRequest(language='$language', orderId='$orderId', callbackUrl='$callbackUrl')"
    }

    class Builder {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var language: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var orderId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var callbackUrl: String? = null

        fun setLanguage(language: String?): Builder = apply { this.language = language }
        fun setOrderId(orderId: String?): Builder = apply { this.orderId = orderId }
        fun setCallbackUrl(callbackUrl: String?): Builder = apply { this.callbackUrl = callbackUrl }

        fun build(): RefundRequest {
            return RefundRequest(
                    language = this.language,
                    orderId = requireNotNull(this.orderId) { "Missing orderId" },
                    callbackUrl = this.callbackUrl
            )
        }
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): RefundRequest = decodeFromJson(json)
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun RefundRequest(initializer: RefundRequest.Builder.() -> Unit): RefundRequest {
    return RefundRequest.Builder().apply(initializer).build()
}