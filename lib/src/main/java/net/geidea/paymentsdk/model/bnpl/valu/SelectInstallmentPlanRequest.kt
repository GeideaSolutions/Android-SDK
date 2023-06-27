@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.bnpl.valu

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
    val totalAmount: BigDecimal,
    val currency: String? = null,
    val restrictPaymentMethods: Boolean = false,
    val paymentMethods: Set<String>? = null,
    val orderId: String? = null,
    val merchantReferenceId: String? = null,
    val callbackUrl: String? = null,
    val billingAddress: Address? = null,
    val shippingAddress: Address? = null,
    val customerEmail: String? = null,
    val source: String?,   // Intentionally without default b/c serialization omits the defaults
    val platform: Platform? = null,
    val statementDescriptor: StatementDescriptor? = null,
    val custom: String? = null,

    val customerIdentifier: String,
    val bnplOrderId: String,
    val downPayment: BigDecimal? = null,
    val giftCardAmount: BigDecimal? = null,
    val campaignAmount: BigDecimal? = null,
    val adminFees: BigDecimal? = null,
    val tenure: Int,

    val cashOnDelivery: Boolean? = null,

    ) : LocalizableRequest {

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SelectInstallmentPlanRequest

        if (language != other.language) return false
        if (totalAmount != other.totalAmount) return false
        if (currency != other.currency) return false
        if (restrictPaymentMethods != other.restrictPaymentMethods) return false
        if (paymentMethods != other.paymentMethods) return false
        if (orderId != other.orderId) return false
        if (merchantReferenceId != other.merchantReferenceId) return false
        if (callbackUrl != other.callbackUrl) return false
        if (billingAddress != other.billingAddress) return false
        if (shippingAddress != other.shippingAddress) return false
        if (customerEmail != other.customerEmail) return false
        if (source != other.source) return false
        if (platform != other.platform) return false
        if (statementDescriptor != other.statementDescriptor) return false
        if (custom != other.custom) return false

        if (customerIdentifier != other.customerIdentifier) return false
        if (bnplOrderId != other.bnplOrderId) return false
        if (downPayment != other.downPayment) return false
        if (giftCardAmount != other.giftCardAmount) return false
        if (campaignAmount != other.campaignAmount) return false
        if (adminFees != other.adminFees) return false
        if (tenure != other.tenure) return false

        if (cashOnDelivery != other.cashOnDelivery) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                language,
                totalAmount,
                currency,
                restrictPaymentMethods,
                paymentMethods,
                orderId,
                merchantReferenceId,
                callbackUrl,
                billingAddress,
                shippingAddress,
                customerEmail,
                source,
                platform,
                statementDescriptor,
                custom,

                customerIdentifier,
                bnplOrderId,
                downPayment,
                giftCardAmount,
                campaignAmount,
                tenure,

                cashOnDelivery,
        )
    }

    // GENERATED
    override fun toString(): String {
        return """SelectInstallmentPlanRequest(
            language=$language,
            totalAmount=$totalAmount,
            currency=$currency,
            restrictPaymentMethods=$restrictPaymentMethods,
            paymentMethods=$paymentMethods,
            orderId=$orderId,
            merchantReferenceId=$merchantReferenceId,
            callbackUrl=$callbackUrl,
            billingAddress=$billingAddress,
            shippingAddress=$shippingAddress,
            customerEmail=$customerEmail,
            source=$source,
            platform=$platform,
            statementDescriptor=$statementDescriptor,
            custom=$custom,
            
            customIdentifier=$customerIdentifier,
            bnplOrderId=$bnplOrderId,
            downPayment=$downPayment,
            giftCardAmount=$giftCardAmount,
            campaignAmount=$campaignAmount,
            tenure=$tenure,
            cashOnDelivery=$cashOnDelivery,
            )""".replace("\n", "").trimIndent()
    }

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    /**
     * Builder for [SelectInstallmentPlanRequest]
     */
    class Builder() {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var language: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var totalAmount: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var currency: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var paymentMethods: Set<String>? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var restrictPaymentMethods: Boolean = false

        @set:JvmSynthetic // Hide 'void' setter from Java
        var orderId: String? = null

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
        var source: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var platform: Platform? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var statementDescriptor: StatementDescriptor? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var custom: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var customerIdentifier: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var bnplOrderId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var downPayment: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var giftCardAmount: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var campaignAmount: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var adminFees: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var tenure: Int? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var cashOnDelivery: Boolean? = null

        fun setLanguage(language: String?): Builder = apply { this.language = language }
        fun setTotalAmount(totalAmount: BigDecimal?): Builder = apply { this.totalAmount = totalAmount }
        fun setCurrency(currency: String?): Builder = apply { this.currency = currency }
        fun setPaymentMethods(paymentMethods: Set<String>?): Builder = apply { this.paymentMethods = paymentMethods }
        fun setRestrictPaymentMethods(restrictPaymentMethods: Boolean): Builder = apply { this.restrictPaymentMethods = restrictPaymentMethods }
        fun setOrderId(orderId: String?): Builder = apply { this.orderId = orderId }
        fun setMerchantReferenceId(merchantReferenceId: String?): Builder = apply { this.merchantReferenceId = merchantReferenceId }
        fun setCallbackUrl(callbackUrl: String?): Builder = apply { this.callbackUrl = callbackUrl }
        fun setBillingAddress(billingAddress: Address?): Builder = apply { this.billingAddress = billingAddress }
        fun setShippingAddress(shippingAddress: Address?): Builder = apply { this.shippingAddress = shippingAddress }
        fun setCustomerEmail(customerEmail: String?): Builder = apply { this.customerEmail = customerEmail }
        fun setSource(source: String?): Builder = apply { this.source = source }
        fun setPlatform(platform: Platform?): Builder = apply { this.platform = platform }
        fun setStatementDescriptor(statementDescriptor: StatementDescriptor?): Builder = apply { this.statementDescriptor = statementDescriptor }
        fun setCustom(custom: String?): Builder = apply { this.custom = custom }

        fun setBnplOrderId(bnplOrderId: String?): Builder = apply { this.bnplOrderId = bnplOrderId }
        fun setDownPayment(downPayment: BigDecimal?): Builder = apply { this.downPayment = downPayment }
        fun setGiftCardAmount(giftCardAmount: BigDecimal?): Builder = apply { this.giftCardAmount = giftCardAmount }
        fun setCampaignAmount(campaignAmount: BigDecimal?): Builder = apply { this.campaignAmount = campaignAmount }
        fun setAdminFees(adminFeeds: BigDecimal?): Builder = apply { this.adminFees = adminFees}
        fun setTenure(tenure: Int?): Builder = apply { this.tenure = tenure }

        fun setCashOnDelivery(cashOnDelivery: Boolean?): Builder = apply { this.cashOnDelivery = cashOnDelivery }

        fun build(): SelectInstallmentPlanRequest {
            return SelectInstallmentPlanRequest(
                    language = language,
                    totalAmount = requireNotNull(totalAmount) { "Missing totalAmount" },
                    currency = requireNotNull(currency) { "Missing currency" },
                    paymentMethods = paymentMethods,
                    restrictPaymentMethods = restrictPaymentMethods,
                    orderId = orderId,
                    merchantReferenceId = merchantReferenceId,
                    callbackUrl = callbackUrl,
                    billingAddress = billingAddress,
                    shippingAddress = shippingAddress,
                    customerEmail = customerEmail,
                    source = source,
                    platform = platform,
                    statementDescriptor = statementDescriptor,
                    customerIdentifier = requireNotNull(customerIdentifier) { "Missing customerIdentifier" },
                    bnplOrderId = requireNotNull(bnplOrderId) { "Missing bnplOrderId" },
                    downPayment = downPayment,
                    giftCardAmount = giftCardAmount,
                    campaignAmount = campaignAmount,
                    adminFees = adminFees,
                    tenure = requireNotNull(tenure) { "Missing tenure" },
                    cashOnDelivery = cashOnDelivery,
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