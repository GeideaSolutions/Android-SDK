@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.pay

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.flow.pay.OrderItem
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.Address
import net.geidea.paymentsdk.model.common.LocalizableRequest
import net.geidea.paymentsdk.model.common.Source
import java.math.BigDecimal
import java.util.*

@Serializable
class TokenPaymentRequest private constructor(
    override var language: String? = null,
    val amount: BigDecimal,
    val currency: String,
    val tokenId: String,
    val cvv: String?,
    val orderId: String?,
    val threeDSecureId: String?,
    val source: String?,
    val merchantReferenceId: String?,
    val callbackUrl: String?,
    val billingAddress: Address?,
    val shippingAddress: Address?,
    val customerEmail: String?,
    val paymentOperation: String?,
    val paymentIntentId: String?,
    val initiatedBy: String?,
    val agreementId: String?,
    val items: List<OrderItem>?,
) : LocalizableRequest {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TokenPaymentRequest

        if (language != other.language) return false
        if (amount != other.amount) return false
        if (currency != other.currency) return false
        if (tokenId != other.tokenId) return false
        if (cvv != other.cvv) return false
        if (orderId != other.orderId) return false
        if (threeDSecureId != other.threeDSecureId) return false
        if (source != other.source) return false
        if (merchantReferenceId != other.merchantReferenceId) return false
        if (callbackUrl != callbackUrl) return false
        if (billingAddress != other.billingAddress) return false
        if (shippingAddress != other.shippingAddress) return false
        if (customerEmail != other.customerEmail) return false
        if (paymentOperation != other.paymentOperation) return false
        if (paymentIntentId != other.paymentIntentId) return false
        if (initiatedBy != other.initiatedBy) return false
        if (agreementId != other.agreementId) return false
        if (items != other.items) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                language,
                amount,
                currency,
                tokenId,
                cvv,
                orderId,
                threeDSecureId,
                source,
                merchantReferenceId,
                callbackUrl,
                billingAddress,
                shippingAddress,
                customerEmail,
                paymentOperation,
                paymentIntentId,
                initiatedBy,
                agreementId,
                items,
        )
    }

    // GENERATED
    override fun toString(): String {
        return "TokenPaymentRequest(language='$language', amount=$amount, currency=$currency, tokenId=$tokenId, cvv=<MASKED>, orderId=$orderId, threeDSecureId=$threeDSecureId, source=$source, merchantReferenceId=$merchantReferenceId, callbackUrl=$callbackUrl, billingAddress=$billingAddress, shippingAddress=$shippingAddress, customerEmail=$customerEmail, paymentOperation=$paymentOperation, paymentIntentId=$paymentIntentId, initiatedBy=$initiatedBy, agreementId=$agreementId, items=$items)"
    }

    class Builder {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var language: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var amount: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var currency: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var tokenId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var cvv: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var orderId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var threeDSecureId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var source: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var merchantReferenceId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var callbackUrl: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var billingAddress: Address? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var shippingAddress: Address? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var customerEmail: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var paymentOperation: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var paymentIntentId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var initiatedBy: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var agreementId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var items: List<OrderItem>? = null

        fun setLanguage(language: String?): Builder = apply { this.language = language }
        fun setAmount(amount: BigDecimal?): Builder = apply { this.amount = amount }
        fun setCurrency(currency: String?): Builder = apply { this.currency = currency }
        fun setTokenId(tokenId: String?): Builder = apply { this.tokenId = tokenId }
        fun setCvv(cvv: String?): Builder = apply { this.cvv = cvv }
        fun setOrderId(orderId: String?): Builder = apply { this.orderId = orderId }
        fun setThreeDSecureId(threeDSecureId: String?): Builder = apply { this.threeDSecureId = threeDSecureId }
        fun setSource(source: String?): Builder = apply { this.source = source }
        fun setMerchantReferenceId(merchantReferenceId: String?): Builder = apply { this.merchantReferenceId = merchantReferenceId }
        fun setCallbackUrl(callbackUrl: String?): Builder = apply { this.callbackUrl = callbackUrl }
        fun setBillingAddress(billingAddress: Address?): Builder = apply { this.billingAddress = billingAddress }
        fun setShippingAddress(shippingAddress: Address?): Builder = apply { this.shippingAddress = shippingAddress }
        fun setCustomerEmail(customerEmail: String?): Builder = apply { this.customerEmail = customerEmail }
        fun setPaymentOperation(paymentOperation: String?): Builder = apply { this.paymentOperation = paymentOperation }
        fun setPaymentIntentId(paymentIntentId: String?): Builder = apply { this.paymentIntentId = paymentIntentId }
        fun setInitiatedBy(initiatedBy: String?): Builder = apply { this.initiatedBy = initiatedBy }
        fun setAgreementId(agreementId: String?): Builder = apply { this.agreementId = agreementId }
        fun setItems(items: List<OrderItem>?): Builder = apply { this.items = items }

        fun build(): TokenPaymentRequest {
            return TokenPaymentRequest(
                    language = this.language,
                    amount = requireNotNull(this.amount) { "Missing amount" },
                    currency = requireNotNull(this.currency) { "Missing currency" },
                    tokenId = requireNotNull(this.tokenId) { "Missing tokenId" },
                    cvv = this.cvv,
                    orderId = this.orderId,
                    threeDSecureId = this.threeDSecureId,
                    source = this.source ?: Source.MOBILE_APP,
                    merchantReferenceId = this.merchantReferenceId,
                    callbackUrl = this.callbackUrl,
                    billingAddress = this.billingAddress,
                    shippingAddress = this.shippingAddress,
                    customerEmail = this.customerEmail,
                    paymentOperation = this.paymentOperation,
                    paymentIntentId = this.paymentIntentId,
                    initiatedBy = this.initiatedBy,
                    agreementId = this.agreementId,
                    items = this.items,
            )
        }
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): TokenPaymentRequest = decodeFromJson(json)
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun TokenPaymentRequest(initializer: TokenPaymentRequest.Builder.() -> Unit): TokenPaymentRequest {
    return TokenPaymentRequest.Builder().apply(initializer).build()
}