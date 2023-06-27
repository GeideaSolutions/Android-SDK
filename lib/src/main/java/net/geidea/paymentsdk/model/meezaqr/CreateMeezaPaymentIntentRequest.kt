@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.meezaqr

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.LocalizableRequest
import net.geidea.paymentsdk.model.paymentintent.CustomerRequest
import java.math.BigDecimal
import java.util.*

@Parcelize
@Serializable
class CreateMeezaPaymentIntentRequest

@GeideaSdkInternal
internal constructor(
    override var language: String? = null,
    val amount: BigDecimal,
    val currency: String,
    val customer: CustomerRequest? = null,
    val expiryDate: String? = null,
    val activationDate: String? = null,
    val merchantPublicKey: String? = null,
    val orderId: String? = null,
    val merchantReferenceId: String? = null,
    val source: String? = null,
    val callbackUrl: String? = null,
) : LocalizableRequest, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    fun copy(
            language: String? = null,
            amount: BigDecimal? = null,
            currency: String? = null,
            customer: CustomerRequest? = null,
            expiryDate: String? = null,
            activationDate: String? = null,
            merchantPublicKey: String? = null,
            orderId: String? = null,
            merchantReferenceId: String? = null,
            source: String? = null,
            callbackUrl: String? = null,
    ) = CreateMeezaPaymentIntentRequest(
            language = language ?: this.language,
            amount = amount ?: this.amount,
            currency = currency ?: this.currency,
            customer = customer ?: this.customer,
            expiryDate = expiryDate ?: this.expiryDate,
            activationDate = activationDate ?: this.activationDate,
            merchantPublicKey = merchantPublicKey ?: this.merchantPublicKey,
            orderId = orderId ?: this.orderId,
            merchantReferenceId = merchantReferenceId ?: this.merchantReferenceId,
            source = source ?: this.source,
            callbackUrl = callbackUrl ?: this.callbackUrl,
    )

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreateMeezaPaymentIntentRequest

        if (language != other.language) return false
        if (amount != other.amount) return false
        if (currency != other.currency) return false
        if (customer != other.customer) return false
        if (expiryDate != other.expiryDate) return false
        if (activationDate != other.activationDate) return false
        if (merchantPublicKey != other.merchantPublicKey) return false
        if (orderId != other.orderId) return false
        if (merchantReferenceId != other.merchantReferenceId) return false
        if (source != other.source) return false
        if (callbackUrl != other.callbackUrl) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                language,
                amount,
                currency,
                customer,
                expiryDate,
                activationDate,
                merchantPublicKey,
                orderId,
                merchantReferenceId,
                source,
                callbackUrl,
        )
    }

    // GENERATED
    override fun toString(): String {
        return "CreateMeezaPaymentIntentRequest(language=$language, amount=$amount, currency='$currency', customer=$customer, expiryDate=$expiryDate, activationDate=$activationDate, merchantPublicKey=$merchantPublicKey, orderId=$orderId, merchantReferenceId=$merchantReferenceId, source=$source, callbackUrl=$callbackUrl)"
    }

    /**
     * Builder for [CreateMeezaPaymentIntentRequest]
     */
    class Builder {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var language: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var amount: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var currency: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var customer: CustomerRequest? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var expiryDate: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var activationDate: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var merchantPublicKey: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var orderId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var merchantReferenceId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var source: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var callbackUrl: String? = null

        fun setLanguage(language: String?): Builder = apply { this.language = language }
        fun setAmount(amount: BigDecimal?): Builder = apply { this.amount = amount }
        fun setCurrency(currency: String?): Builder = apply { this.currency = currency }
        fun setCustomer(customer: CustomerRequest?): Builder = apply { this.customer = customer }
        fun setExpiryDate(expiryDate: String?): Builder = apply { this.expiryDate = expiryDate }
        fun setActivationDate(activationDate: String?): Builder = apply { this.activationDate = activationDate }
        fun setMerchantPublicKey(merchantPublicKey: String?) = apply { this.merchantPublicKey = merchantPublicKey }
        fun setOrderId(orderId: String?) = apply { this.orderId = orderId }
        fun setMerchantReferenceId(merchantReferenceId: String?) = apply { this.merchantReferenceId = merchantReferenceId }
        fun setSource(source: String?) = apply { this.source = source }
        fun setCallbackUrl(callbackUrl: String?) = apply { this.callbackUrl = callbackUrl }

        fun build(): CreateMeezaPaymentIntentRequest {
            return CreateMeezaPaymentIntentRequest(
                    language = this.language,
                    amount = requireNotNull(this.amount) { "Missing amount" },
                    currency = requireNotNull(this.currency) { "Missing currency" },
                    customer = this.customer,
                    expiryDate = this.expiryDate,
                    activationDate = this.activationDate,
                    merchantPublicKey = this.merchantPublicKey,
                    orderId = this.orderId,
                    merchantReferenceId = this.merchantReferenceId,
                    source = this.source,
                    callbackUrl = this.callbackUrl,
            )
        }
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): CreateMeezaPaymentIntentRequest = decodeFromJson(json)
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun CreateMeezaPaymentIntentRequest(initializer: CreateMeezaPaymentIntentRequest.Builder.() -> Unit): CreateMeezaPaymentIntentRequest {
    return CreateMeezaPaymentIntentRequest.Builder().apply(initializer).build()
}