@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.bnpl.souhoola

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.LocalizableRequest
import java.math.BigDecimal
import java.util.*

@Serializable
class ReviewTransactionRequest private constructor(
        override var language: String? = null,
        val customerIdentifier: String,
        val customerPin: String,
        val totalAmount: BigDecimal,
        val downPayment: BigDecimal,
        val currency: String,
        val tenure: Int,
        val minimumDownPaymentTenure: BigDecimal,
        val promoCode: String? = null,
        val approvedLimit: BigDecimal,
        val outstanding: BigDecimal,
        val availableLimit: BigDecimal,
        val minLoanAmount: BigDecimal,
        val items: List<SouhoolaOrderItem>? = null,
) : LocalizableRequest {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReviewTransactionRequest

        if (language != other.language) return false
        if (customerIdentifier != other.customerIdentifier) return false
        if (customerPin != other.customerPin) return false
        if (totalAmount != other.totalAmount) return false
        if (downPayment != other.downPayment) return false
        if (currency != other.currency) return false
        if (tenure != other.tenure) return false
        if (minimumDownPaymentTenure != other.minimumDownPaymentTenure) return false
        if (promoCode != other.promoCode) return false
        if (outstanding != other.outstanding) return false
        if (availableLimit != other.availableLimit) return false
        if (minLoanAmount != other.minLoanAmount) return false
        if (items != other.items) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(language, customerIdentifier, customerPin, totalAmount, downPayment, currency, tenure, minimumDownPaymentTenure, promoCode, approvedLimit, outstanding, availableLimit, minLoanAmount, items)
    }

    // GENERATED
    override fun toString(): String {
        return "ReviewTransactionRequest(language=$language, customerIdentifier='$customerIdentifier', customerPin='$customerPin', totalAmount=$totalAmount, downPayment=$downPayment, currency='$currency', tenure=$tenure, minimumDownPaymentTenure=$minimumDownPaymentTenure, promoCode='$promoCode', approvedLimit=$approvedLimit, outstanding=$outstanding, availableLimit=$availableLimit, minLoanAmount=$minLoanAmount)"
    }

    /**
     * Builder for [ReviewTransactionRequest]
     */
    class Builder {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var language: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var customerIdentifier: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var customerPin: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var totalAmount: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var downPayment: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var currency: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var tenure: Int? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var minimumDownPaymentTenure: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var promoCode: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var approvedLimit: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var outstanding: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var availableLimit: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var minLoanAmount: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var items: List<SouhoolaOrderItem>? = null

        fun setLanguage(language: String?): Builder = apply { this.language = language }
        fun setCustomerIdentifier(customerIdentifier: String?): Builder = apply { this.customerIdentifier = customerIdentifier }
        fun setCustomerPin(customerPin: String?): Builder = apply { this.customerPin = customerPin }
        fun setTotalAmount(totalAmount: BigDecimal?): Builder = apply { this.totalAmount = totalAmount }
        fun setDownPayment(downPayment: BigDecimal?): Builder = apply { this.downPayment = downPayment }
        fun setCurrency(currency: String?): Builder = apply { this.currency = currency }
        fun setTenure(tenure: Int?): Builder = apply { this.tenure = tenure }
        fun setMinimumDownPaymentTenure(minimumDownPaymentTenure: BigDecimal?): Builder = apply { this.minimumDownPaymentTenure = minimumDownPaymentTenure }
        fun setPromoCode(promoCode: String?): Builder = apply { this.promoCode = promoCode }
        fun setApprovedLimit(approvedLimit: BigDecimal?): Builder = apply { this.approvedLimit = approvedLimit }
        fun setOutstanding(outstanding: BigDecimal?): Builder = apply { this.outstanding = outstanding }
        fun setAvailableLimit(availableLimit: BigDecimal?): Builder = apply { this.availableLimit = availableLimit }
        fun setMinLoanAmount(minLoanAmount: BigDecimal?): Builder = apply { this.minLoanAmount = minLoanAmount }
        fun setItems(items: List<SouhoolaOrderItem>?): Builder = apply { this.items = items }

        fun build(): ReviewTransactionRequest = ReviewTransactionRequest(
            language = this.language,
            customerIdentifier = requireNotNull(this.customerIdentifier) { "Missing customerIdentifier" },
            customerPin = requireNotNull(this.customerPin) { "Missing customerPin" },
            totalAmount = requireNotNull(this.totalAmount) { "Missing totalAmount" },
            downPayment = requireNotNull(this.downPayment) { "Missing downPayment" },
            currency = requireNotNull(this.currency) { "Missing currency" },
            tenure = requireNotNull(this.tenure) { "Missing tenure" },
            minimumDownPaymentTenure = requireNotNull(this.minimumDownPaymentTenure) { "Missing minimumDownPaymentTenure" },
            promoCode = requireNotNull(this.promoCode) { "Missing promoCode" },
            approvedLimit = requireNotNull(this.approvedLimit) { "Missing approvedLimit" },
            outstanding = requireNotNull(this.outstanding) { "Missing outstanding" },
            availableLimit = requireNotNull(this.availableLimit) { "Missing availableLimit" },
            minLoanAmount = requireNotNull(this.minLoanAmount) { "Missing minLoanAmount" },
            items = requireNotNull(this.items) { "Missing items" }
        )
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): ReviewTransactionRequest = decodeFromJson(json)
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun ReviewTransactionRequest(initializer: ReviewTransactionRequest.Builder.() -> Unit): ReviewTransactionRequest {
    return ReviewTransactionRequest.Builder().apply(initializer).build()
}