@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.auth.v1

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.Address
import net.geidea.paymentsdk.model.PaymentMethod
import net.geidea.paymentsdk.model.common.LocalizableRequest
import java.math.BigDecimal
import java.util.*

@Serializable
class AuthenticationRequest private constructor(
    override var language: String? = null,
    val amount: BigDecimal,
    val currency: String? = null,
    val paymentMethod: PaymentMethod,
    val paymentMethods: Set<String>? = null,
    val restrictPaymentMethods: Boolean? = false,
    val orderId: String? = null,
    val paymentOperation: String? = null,
    val merchantReferenceId: String? = null,
    val callbackUrl: String? = null,
    val billingAddress: Address? = null,
    val shippingAddress: Address? = null,
    val customerEmail: String? = null,
    val returnUrl: String? = null,
    val cardOnFile: Boolean = false,
    val initiatedBy: String? = null,
    val agreementId: String? = null,
    val agreementType: String? = null,
    val source: String?,   // Intentionally without default b/c serialization omits the defaults
    val paymentIntentId: String? = null,
) : LocalizableRequest {

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AuthenticationRequest

        if (language != other.language) return false
        if (amount != other.amount) return false
        if (currency != other.currency) return false
        if (paymentMethod != other.paymentMethod) return false
        if (paymentMethods != other.paymentMethods) return false
        if (restrictPaymentMethods != other.restrictPaymentMethods) return false
        if (orderId != other.orderId) return false
        if (paymentOperation != other.paymentOperation) return false
        if (merchantReferenceId != other.merchantReferenceId) return false
        if (callbackUrl != other.callbackUrl) return false
        if (billingAddress != other.billingAddress) return false
        if (shippingAddress != other.shippingAddress) return false
        if (customerEmail != other.customerEmail) return false
        if (returnUrl != other.returnUrl) return false
        if (cardOnFile != other.cardOnFile) return false
        if (initiatedBy != other.initiatedBy) return false
        if (agreementId != other.agreementId) return false
        if (agreementType != other.agreementType) return false
        if (source != other.source) return false
        if (paymentIntentId != other.paymentIntentId) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                language,
                amount,
                currency,
                paymentMethod,
                paymentMethods,
                restrictPaymentMethods,
                orderId,
                paymentOperation,
                merchantReferenceId,
                callbackUrl,
                billingAddress,
                shippingAddress,
                customerEmail,
                returnUrl,
                cardOnFile,
                initiatedBy,
                agreementId,
                agreementType,
                source,
                paymentIntentId,
        )
    }

    // GENERATED
    override fun toString(): String {
        return """AuthenticationRequest(
            language=$language,
            amount=$amount,
            currency=$currency,
            paymentMethod=$paymentMethod,
            paymentMethods=$paymentMethods,
            restrictPaymentMethods=$restrictPaymentMethods,
            orderId=$orderId,
            paymentOperation=$paymentOperation,
            merchantReferenceId=$merchantReferenceId,
            callbackUrl=$callbackUrl,
            billingAddress=$billingAddress,
            shippingAddress=$shippingAddress,
            customerEmail=$customerEmail,
            returnUrl=$returnUrl,
            cardOnFile=$cardOnFile,
            initiatedBy=$initiatedBy,
            agreementId=$agreementId,
            agreementType=$agreementType,
            source=$source, 
            paymentIntentId=$paymentIntentId,
            )""".replace("\n", "").trimIndent()
    }

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    /**
     * Builder for [AuthenticationRequest]
     */
    class Builder() {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var language: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var amount: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var currency: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var paymentMethod: PaymentMethod? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var paymentMethods: Set<String>? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var restrictPaymentMethods: Boolean? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var orderId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var paymentOperation: String? = null

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
        var returnUrl: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var cardOnFile: Boolean = false

        @set:JvmSynthetic // Hide 'void' setter from Java
        var initiatedBy: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var agreementId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var agreementType: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var source: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var paymentIntentId: String? = null

        fun setLanguage(language: String?): Builder = apply { this.language = language }
        fun setAmount(amount: BigDecimal?): Builder = apply { this.amount = amount }
        fun setCurrency(currency: String?): Builder = apply { this.currency = currency }
        fun setPaymentMethod(paymentMethod: PaymentMethod?): Builder = apply { this.paymentMethod = paymentMethod }
        fun setPaymentMethods(paymentMethods: Set<String>?): Builder = apply { this.paymentMethods = paymentMethods }
        fun setRestrictPaymentMethods(restrictPaymentMethods: Boolean) = apply { this.restrictPaymentMethods = restrictPaymentMethods }
        fun setOrderId(orderId: String?): Builder = apply { this.orderId = orderId }
        fun setPaymentOperation(paymentOperation: String?): Builder = apply { this.paymentOperation = paymentOperation }
        fun setMerchantReferenceId(merchantReferenceId: String?): Builder = apply { this.merchantReferenceId = merchantReferenceId }
        fun setCallbackUrl(callbackUrl: String?): Builder = apply { this.callbackUrl = callbackUrl }
        fun setBillingAddress(billingAddress: Address?): Builder = apply { this.billingAddress = billingAddress }
        fun setShippingAddress(shippingAddress: Address?): Builder = apply { this.shippingAddress = shippingAddress }
        fun setCustomerEmail(customerEmail: String?): Builder = apply { this.customerEmail = customerEmail }
        fun setReturnUrl(returnUrl: String?): Builder = apply { this.returnUrl = returnUrl }
        fun setCardOnFile(cardOnFile: Boolean): Builder = apply { this.cardOnFile = cardOnFile }
        fun setInitiatedBy(initiatedBy: String?): Builder = apply { this.initiatedBy = initiatedBy }
        fun setAgreementId(agreementId: String?): Builder = apply { this.agreementId = agreementId }
        fun setAgreementType(agreementType: String?): Builder = apply { this.agreementType = agreementType }
        fun setSource(source: String?): Builder = apply { this.source = source }
        fun setPaymentIntentId(paymentIntentId: String?): Builder = apply { this.paymentIntentId = paymentIntentId }

        fun build(): AuthenticationRequest {
            return AuthenticationRequest(
                    language = language,
                    amount = requireNotNull(amount) { "Missing amount" },
                    currency = requireNotNull(currency) { "Missing currency" },
                    paymentMethod = requireNotNull(paymentMethod) { "Missing paymentMethod" },
                    paymentMethods = paymentMethods,
                    restrictPaymentMethods = restrictPaymentMethods,
                    orderId = orderId,
                    paymentOperation = paymentOperation,
                    merchantReferenceId = merchantReferenceId,
                    callbackUrl = callbackUrl,
                    billingAddress = billingAddress,
                    shippingAddress = shippingAddress,
                    customerEmail = customerEmail,
                    returnUrl = returnUrl,
                    cardOnFile = cardOnFile,
                    initiatedBy = initiatedBy,
                    agreementId = agreementId,
                    agreementType = agreementType,
                    source = source,
                    paymentIntentId = paymentIntentId,
            )
        }
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): AuthenticationRequest = decodeFromJson(json)
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun AuthenticationRequest(initializer: AuthenticationRequest.Builder.() -> Unit): AuthenticationRequest {
    return AuthenticationRequest.Builder().apply(initializer).build()
}