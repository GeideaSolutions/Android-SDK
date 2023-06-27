@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.bnpl.valu

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.LocalizableRequest
import java.math.BigDecimal
import java.util.*

@Serializable
class InstallmentPlansRequest private constructor(
        override var language: String?,
        val customerIdentifier: String,
        val totalAmount: BigDecimal,
        val currency: String,
        val downPayment: BigDecimal,
        val giftCardAmount: BigDecimal = BigDecimal.ZERO,
        val campaignAmount: BigDecimal = BigDecimal.ZERO,
) : LocalizableRequest {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InstallmentPlansRequest

        if (language != other.language) return false
        if (customerIdentifier != other.customerIdentifier) return false
        if (totalAmount != other.totalAmount) return false
        if (currency != other.currency) return false
        if (downPayment != other.downPayment) return false
        if (giftCardAmount != other.giftCardAmount) return false
        if (campaignAmount != other.campaignAmount) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                language,
                customerIdentifier,
                currency,
                totalAmount,
                downPayment,
                giftCardAmount,
                campaignAmount
        )
    }

    override fun toString(): String {
        return "InstallmentPlansRequest(language=$language, customerIdentifier='$customerIdentifier', totalAmount=$totalAmount, currency='$currency', downPayment=$downPayment, giftCardAmount=$giftCardAmount, campaignAmount=$campaignAmount)"
    }

    /**
     * Builder for [InstallmentPlansRequest]
     */
    class Builder() {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var language: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var customerIdentifier: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var totalAmount: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var currency: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var downPayment: BigDecimal = BigDecimal.ZERO

        @set:JvmSynthetic // Hide 'void' setter from Java
        var giftCardAmount: BigDecimal = BigDecimal.ZERO

        @set:JvmSynthetic // Hide 'void' setter from Java
        var campaignAmount: BigDecimal = BigDecimal.ZERO

        fun setLanguage(language: String?): Builder = apply { this.language = language }
        fun setCustomerIdentifier(customerIdentifier: String?): Builder = apply { this.customerIdentifier = customerIdentifier }
        fun setTotalAmount(totalAmount: BigDecimal): Builder = apply { this.totalAmount = totalAmount }
        fun setCurrency(currency: String?): Builder = apply { this.currency = currency }
        fun setDownPayment(downPayment: BigDecimal): Builder = apply { this.downPayment = downPayment }
        fun setGiftCardAmount(giftCardAmount: BigDecimal): Builder = apply { this.giftCardAmount = giftCardAmount }
        fun setCampaignAmount(campaignAmount: BigDecimal): Builder = apply { this.campaignAmount = campaignAmount }

        fun build(): InstallmentPlansRequest {
            return InstallmentPlansRequest(
                    language = language,
                    customerIdentifier = requireNotNull(this.customerIdentifier) { "Missing customerIdentifier" },
                    totalAmount = requireNotNull(this.totalAmount) { "Missing totalAmount" },
                    currency = requireNotNull(this.currency) { "Missing currency" },
                    downPayment = this.downPayment,
                    giftCardAmount = this.giftCardAmount,
                    campaignAmount = this.campaignAmount
            )
        }
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): InstallmentPlansRequest = decodeFromJson(json)
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun InstallmentPlansRequest(initializer: InstallmentPlansRequest.Builder.() -> Unit): InstallmentPlansRequest {
    return InstallmentPlansRequest.Builder().apply(initializer).build()
}