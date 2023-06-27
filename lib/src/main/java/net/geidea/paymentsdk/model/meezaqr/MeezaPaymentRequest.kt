package net.geidea.paymentsdk.model.meezaqr

import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.LocalizableRequest
import java.util.*

@Serializable
class MeezaPaymentRequest private constructor(
        override var language: String? = null,
        val merchantPublicKey: String? = null,
        val qrCodeMessage: String? = null,
        val receiverId: String? = null,
) : LocalizableRequest {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    fun copy(
            language: String? = null,
            merchantPublicKey: String? = null,
            qrCodeMessage: String? = null,
            receiverId: String? = null,
    ) = MeezaPaymentRequest(
            language = language ?: this.language,
            merchantPublicKey = merchantPublicKey ?: this.merchantPublicKey,
            qrCodeMessage = qrCodeMessage ?: this.qrCodeMessage,
            receiverId = receiverId ?: this.receiverId,
    )

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MeezaPaymentRequest

        if (language != other.language) return false
        if (merchantPublicKey != other.merchantPublicKey) return false
        if (qrCodeMessage != other.qrCodeMessage) return false
        if (receiverId != other.receiverId) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                language,
                merchantPublicKey,
                qrCodeMessage,
                receiverId,
        )
    }

    // GENERATED
    override fun toString(): String {
        return "MeezaPaymentRequest(language='$language', merchantPublicKey='$merchantPublicKey', qrCodeMessage='$qrCodeMessage', receiverId='$receiverId')"
    }

    /**
     * Builder for [MeezaPaymentRequest]
     */
    class Builder {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var language: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var merchantPublicKey: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var qrCodeMessage: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var receiverId: String? = null

        fun setLanguage(language: String?): Builder = apply { this.language = language }
        fun setMerchantPublicKey(merchantPublicKey: String?): Builder = apply { this.merchantPublicKey = merchantPublicKey }
        fun setQrCodeMessage(qrCodeMessage: String?): Builder = apply { this.qrCodeMessage = qrCodeMessage }
        fun setReceiverId(receiverId: String?): Builder = apply { this.receiverId = receiverId }

        fun build(): MeezaPaymentRequest {
            return MeezaPaymentRequest(
                    language = this.language,
                    merchantPublicKey = this.merchantPublicKey,
                    qrCodeMessage = this.qrCodeMessage,
                    receiverId = this.receiverId,
            )
        }
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): MeezaPaymentRequest = decodeFromJson(json)
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun MeezaPaymentRequest(initializer: MeezaPaymentRequest.Builder.() -> Unit): MeezaPaymentRequest {
    return MeezaPaymentRequest.Builder().apply(initializer).build()
}