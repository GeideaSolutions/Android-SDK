@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.auth.v6

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.LocalizableRequest
import java.math.BigDecimal
import java.util.Objects

@Serializable
class CreateSessionRequest private constructor(
    override var language: String? = null,
    val amount: BigDecimal,
    val currency: String? = null,
    val timestamp: String,
    val callbackUrl: String? = null,
    val returnUrl: String? = null,
    val merchantReferenceId: String,
    val paymentIntentId: String? = null,
    val paymentOperation: String? = null,
    val cardOnFile: Boolean = false,
    val appearanceRequest: AppearanceRequest? = null,
    val signature: String
) : LocalizableRequest {
    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreateSessionRequest

        if (language != other.language) return false
        if (amount != other.amount) return false
        if (currency != other.currency) return false
        if (timestamp != other.timestamp) return false
        if (callbackUrl != other.callbackUrl) return false
        if (returnUrl != other.returnUrl) return false
        if (merchantReferenceId != other.merchantReferenceId) return false
        if (paymentIntentId != other.paymentIntentId) return false
        if (paymentOperation != other.paymentOperation) return false
        if (cardOnFile != other.cardOnFile) return false
        if (appearanceRequest != other.appearanceRequest) return false
        return signature == other.signature
    }

    override fun hashCode(): Int {
        return Objects.hash(
            language,
            amount,
            currency,
            timestamp,
            callbackUrl,
            returnUrl,
            merchantReferenceId,
            paymentIntentId,
            paymentOperation,
            cardOnFile,
            appearanceRequest,
            signature
        )
    }

    override fun toString(): String {
        return "CreateSessionRequest(" +
                "language=$language, " +
                "amount=$amount, currency=$currency, " +
                "timestamp=$timestamp, callbackUrl=$callbackUrl, " +
                "returnUrl=$returnUrl, " +
                "merchantReferenceId=$merchantReferenceId, " +
                "paymentIntentId=$paymentIntentId, " +
                "paymentOperation=$paymentOperation, " +
                "cardOnFile=$cardOnFile, " +
                "appearanceRequest=$appearanceRequest, " +
                "signature=$signature)".replace("\n", "").trimIndent()
    }

    class Builder() {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var language: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var amount: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var currency: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var timestamp: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var callbackUrl: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var returnUrl: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var merchantReferenceId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var paymentIntentId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var paymentOperation: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var cardOnFile: Boolean = false

        @set:JvmSynthetic // Hide 'void' setter from Java
        var appearanceRequest: AppearanceRequest? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var signature: String? = null

        fun setLanguage(language: String?): Builder = apply { this.language = language }

        fun setAmount(amount: BigDecimal?): Builder = apply { this.amount = amount }

        fun setCurrency(currency: String?): Builder = apply { this.currency = currency }

        fun setTimestamp(timestamp: String?): Builder = apply { this.timestamp = timestamp }

        fun setCallbackUrl(callbackUrl: String?): Builder = apply { this.callbackUrl = callbackUrl }

        fun setReturnUrl(returnUrl: String?): Builder = apply { this.returnUrl = returnUrl }

        fun setMerchantReferenceId(merchantReferenceId: String?): Builder =
            apply { this.merchantReferenceId = merchantReferenceId }

        fun setPaymentIntentId(paymentIntentId: String?): Builder =
            apply { this.paymentIntentId = paymentIntentId }

        fun setPaymentOperation(paymentOperation: String?): Builder =
            apply { this.paymentOperation = paymentOperation }

        fun setCardOnFile(cardOnFile: Boolean): Builder = apply { this.cardOnFile = cardOnFile }

        fun setAppearanceRequest(appearanceRequest: AppearanceRequest?): Builder =
            apply { this.appearanceRequest = appearanceRequest }

        fun setSignature(signature: String?): Builder = apply { this.signature = signature }

        fun build(): CreateSessionRequest {
            return CreateSessionRequest(
                language = this.language,
                amount = requireNotNull(this.amount) { "Amount must not be null" },
                currency = requireNotNull(currency) { "Missing currency" },
                timestamp = requireNotNull(this.timestamp) { "Missing timestamp" },
                callbackUrl = this.callbackUrl,
                returnUrl = this.returnUrl,
                merchantReferenceId = requireNotNull(this.merchantReferenceId) { "Missing Merchant Reference Id" },
                paymentIntentId = this.paymentIntentId,
                paymentOperation = this.paymentOperation,
                cardOnFile = this.cardOnFile,
                appearanceRequest = this.appearanceRequest,
                signature = requireNotNull(this.signature) { "Required missing attributes" }
            )
        }

    }
}

/**
 * Kotlin builder function.
 */
@JvmSynthetic
fun CreateSessionRequest(initializer: CreateSessionRequest.Builder.() -> Unit): CreateSessionRequest {
    return CreateSessionRequest.Builder().apply(initializer).build()
}

