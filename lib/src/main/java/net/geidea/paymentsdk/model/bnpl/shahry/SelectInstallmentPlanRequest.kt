@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.bnpl.shahry

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.Address
import net.geidea.paymentsdk.model.common.LocalizableRequest
import net.geidea.paymentsdk.model.transaction.Platform
import net.geidea.paymentsdk.model.transaction.StatementDescriptor
import java.math.BigDecimal
import java.util.*

@Serializable
class SelectInstallmentPlanRequest private constructor(
    override var language: String? = null,
    val orderId: String?,
    val customerIdentifier: String,
    val totalAmount: BigDecimal,
    val currency: String,
    val merchantReferenceId: String? = null,
    val callbackUrl: String? = null,
    val billingAddress: Address? = null,
    val shippingAddress: Address? = null,
    val customerEmail: String? = null,
    val returnUrl: String? = null,
    val custom: String? = null,
    val platform: Platform? = null,
    val statementDescriptor: StatementDescriptor? = null,
    val restrictPaymentMethods: Boolean? = null,
    val paymentMethods: Set<String>? = null,
    val paymentOperation: String? = null,
    val paymentBrand: String? = null,
    val description: String? = null,
    val source: String? = null, // = Source.MOBILE_APP,
    val items: List<ShahryOrderItem>? = null,
) : LocalizableRequest {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SelectInstallmentPlanRequest

        if (language != other.language) return false
        if (orderId != other.orderId) return false
        if (customerIdentifier != other.customerIdentifier) return false
        if (totalAmount != other.totalAmount) return false
        if (currency != other.currency) return false
        if (merchantReferenceId != other.merchantReferenceId) return false
        if (callbackUrl != other.callbackUrl) return false
        if (billingAddress != other.billingAddress) return false
        if (shippingAddress != other.shippingAddress) return false
        if (customerEmail != other.customerEmail) return false
        if (returnUrl != other.returnUrl) return false
        if (custom != other.custom) return false
        if (platform != other.platform) return false
        if (statementDescriptor != other.statementDescriptor) return false
        if (restrictPaymentMethods != other.restrictPaymentMethods) return false
        if (paymentMethods != other.paymentMethods) return false
        if (paymentOperation != other.paymentOperation) return false
        if (paymentBrand != other.paymentBrand) return false
        if (description != other.description) return false
        if (source != other.source) return false
        if (items != other.items) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                language,
                orderId,
                customerIdentifier,
                totalAmount,
                currency,
                merchantReferenceId,
                callbackUrl,
                billingAddress,
                shippingAddress,
                customerEmail,
                returnUrl,
                custom,
                platform,
                statementDescriptor,
                restrictPaymentMethods,
                paymentMethods,
                paymentOperation,
                paymentBrand,
                description,
                source,
                items,
        )
    }

    // GENERATED
    override fun toString(): String {
        return "SelectInstallmentPlanRequest(language=$language, orderId=$orderId, customerIdentifier='$customerIdentifier', totalAmount=$totalAmount, currency='$currency', merchantReferenceId=$merchantReferenceId, callbackUrl=$callbackUrl, billingAddress=$billingAddress, shippingAddress=$shippingAddress, customerEmail=$customerEmail, returnUrl=$returnUrl, custom=$custom, platform=$platform, statementDescriptor=$statementDescriptor, restrictPaymentMethods=$restrictPaymentMethods, paymentMethods=$paymentMethods, paymentOperation=$paymentOperation, paymentBrand=$paymentBrand, description=$description, source=$source, items=$items)"
    }

    /**
     * Builder for [SelectInstallmentPlanRequest]
     */
    class Builder() {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var language: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var orderId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var customerIdentifier: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var totalAmount: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var currency: String? = null

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
        var custom: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var platform: Platform? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var statementDescriptor: StatementDescriptor? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var restrictPaymentMethods: Boolean? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var paymentMethods: Set<String>? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var paymentOperation: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var paymentBrand: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var description: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var source: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var items: List<ShahryOrderItem>? = null

        fun setLanguage(language: String?): Builder = apply { this.language = language }
        fun setOrderId(orderId: String?): Builder = apply { this.orderId = orderId }
        fun setCustomerIdentifier(customerIdentifier: String?): Builder = apply { this.customerIdentifier = customerIdentifier }
        fun setTotalAmount(totalAmount: BigDecimal): Builder = apply { this.totalAmount = totalAmount }
        fun setCurrency(currency: String?): Builder = apply { this.currency = currency }
        fun setMerchantReferenceId(merchantReferenceId: String?): Builder = apply { this.merchantReferenceId = merchantReferenceId }
        fun setCallbackUrl(callbackUrl: String?): Builder = apply { this.callbackUrl = callbackUrl }
        fun setBillingAddress(billingAddress: Address?): Builder = apply { this.billingAddress = billingAddress }
        fun setShippingAddress(shippingAddress: Address?): Builder = apply { this.shippingAddress = shippingAddress }
        fun setCustomerEmail(customerEmail: String?): Builder = apply { this.customerEmail = customerEmail }
        fun setReturnUrl(returnUrl: String?): Builder = apply { this.returnUrl = returnUrl }
        fun setCustom(custom: String?): Builder = apply { this.custom = custom }
        fun setPlatform(platform: Platform?): Builder = apply { this.platform = platform }
        fun setRestrictPaymentMethods(restrictPaymentMethods: Boolean?): Builder = apply { this.restrictPaymentMethods = restrictPaymentMethods }
        fun setPaymentMethods(paymentMethods: Set<String>?): Builder = apply { this.paymentMethods = paymentMethods }
        fun setPaymentOperation(paymentOperation: String?): Builder = apply { this.paymentOperation = paymentOperation }
        fun setPaymentBrand(paymentBrand: String?): Builder = apply { this.paymentBrand = paymentBrand }
        fun setDescription(description: String?): Builder = apply { this.description = description }
        fun setSource(source: String?): Builder = apply { this.source = source }
        fun setItems(items: List<ShahryOrderItem>?): Builder = apply { this.items = items }

        fun build(): SelectInstallmentPlanRequest {
            return SelectInstallmentPlanRequest(
                    language = language,
                    orderId = orderId,
                    customerIdentifier = requireNotNull(this.customerIdentifier) { "Missing customerIdentifier" },
                    totalAmount = requireNotNull(this.totalAmount) { "Missing totalAmount" },
                    currency = requireNotNull(this.currency) { "Missing currency" },
                    merchantReferenceId = merchantReferenceId,
                    callbackUrl = callbackUrl,
                    billingAddress = billingAddress,
                    shippingAddress = shippingAddress,
                    customerEmail = customerEmail,
                    returnUrl = returnUrl,
                    custom = custom,
                    platform = platform,
                    statementDescriptor = statementDescriptor,
                    restrictPaymentMethods = restrictPaymentMethods,
                    paymentMethods = paymentMethods,
                    paymentOperation = paymentOperation,
                    paymentBrand = paymentBrand,
                    description = description,
                    source = source,
                    items = items,
            )
        }
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): SelectInstallmentPlanRequest = decodeFromJson(json)
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun SelectInstallmentPlanRequest(initializer: SelectInstallmentPlanRequest.Builder.() -> Unit): SelectInstallmentPlanRequest {
    return SelectInstallmentPlanRequest.Builder().apply(initializer).build()
}