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
class ConfirmRequest private constructor(
        override var language: String? = null,
        val customerIdentifier: String,
        val orderId: String? = null,
        val bnplOrderId: String,
        val otp: String,
        val totalAmount: BigDecimal,
        val currency: String,
        val adminFees: BigDecimal? = null,
        val downPayment: BigDecimal? = null,
        val giftCardAmount: BigDecimal? = null,
        val campaignAmount: BigDecimal? = null,
        val tenure: Int,
) : LocalizableRequest {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ConfirmRequest

        if (language != other.language) return false
        if (customerIdentifier != other.customerIdentifier) return false
        if (orderId != other.orderId) return false
        if (bnplOrderId != other.bnplOrderId) return false
        if (otp != other.otp) return false
        if (totalAmount != other.totalAmount) return false
        if (currency != other.currency) return false
        if (adminFees != other.adminFees) return false
        if (downPayment != other.downPayment) return false
        if (giftCardAmount != other.giftCardAmount) return false
        if (campaignAmount != other.giftCardAmount) return false
        if (tenure != other.tenure) return false

        return true
    }

    // GENERATED
    override fun toString(): String {
        return "ConfirmRequest(language='$language', customerIdentifier='$customerIdentifier', orderId='$orderId', bnplOrderId='$bnplOrderId', otp='$otp', totalAmount=$totalAmount, currency='$currency', adminFees='$adminFees', downPayment=$downPayment, giftCardAmount=$giftCardAmount, campaignAmount=$campaignAmount, tenure=$tenure)"
    }

    override fun hashCode(): Int {
        return Objects.hash(
                language,
                customerIdentifier,
                orderId,
                bnplOrderId,
                otp,
                totalAmount,
                currency,
                adminFees,
                downPayment,
                giftCardAmount,
                campaignAmount,
                tenure,
        )
    }

    class Builder {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var language: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var customerIdentifier: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var orderId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var bnplOrderId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var otp: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var totalAmount: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var currency: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var adminFees: BigDecimal = BigDecimal.ZERO

        @set:JvmSynthetic // Hide 'void' setter from Java
        var downPayment: BigDecimal = BigDecimal.ZERO

        @set:JvmSynthetic // Hide 'void' setter from Java
        var giftCardAmount: BigDecimal = BigDecimal.ZERO

        @set:JvmSynthetic // Hide 'void' setter from Java
        var campaignAmount: BigDecimal = BigDecimal.ZERO

        @set:JvmSynthetic // Hide 'void' setter from Java
        var tenure: Int? = null

        fun setLanguage(language: String?): Builder = apply { this.language = language }
        fun setCustomerIdentifier(customerIdentifier: String?): Builder = apply { this.customerIdentifier = customerIdentifier }
        fun setOrderId(orderId: String?): Builder = apply { this.orderId = orderId }
        fun setBnplOrderId(bnplOrderId: String?): Builder = apply { this.bnplOrderId = bnplOrderId }
        fun setOtp(otp: String?): Builder = apply { this.otp = otp }
        fun setTotalAmount(totalAmount: BigDecimal): Builder = apply { this.totalAmount = totalAmount }
        fun setCurrency(currency: String?): Builder = apply { this.currency = currency }
        fun setAdminFees(adminFees: BigDecimal): Builder = apply { this.adminFees = adminFees }
        fun setDownPayment(downPayment: BigDecimal): Builder = apply { this.downPayment = downPayment }
        fun setGiftCardAmount(giftCardAmount: BigDecimal): Builder = apply { this.giftCardAmount = giftCardAmount }
        fun setCampaignAmount(campaignAmount: BigDecimal): Builder = apply { this.campaignAmount = campaignAmount }
        fun setTenure(tenure: Int): Builder = apply { this.tenure = tenure }

        fun build(): ConfirmRequest {
            return ConfirmRequest(
                    language = language,
                    customerIdentifier = requireNotNull(customerIdentifier) { "Missing customerIdentifier" },
                    orderId = orderId,
                    bnplOrderId = requireNotNull(bnplOrderId) { "Missing bnplOrderId" },
                    otp = requireNotNull(otp) { "Missing otp" },
                    totalAmount = requireNotNull(totalAmount) { "Missing totalAmount" },
                    currency = requireNotNull(currency) { "Missing currency" },
                    adminFees = adminFees,
                    downPayment = downPayment,
                    giftCardAmount = giftCardAmount,
                    campaignAmount = campaignAmount,
                    tenure = requireNotNull(tenure) { "Missing tenure" },
            )
        }
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): ConfirmRequest = decodeFromJson(json)
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun PurchaseRequest(initializer: ConfirmRequest.Builder.() -> Unit): ConfirmRequest {
    return ConfirmRequest.Builder().apply(initializer).build()
}