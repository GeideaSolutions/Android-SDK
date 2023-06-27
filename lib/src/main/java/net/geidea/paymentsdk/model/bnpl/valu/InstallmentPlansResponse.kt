@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.bnpl.valu

import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import net.geidea.paymentsdk.model.common.GeideaResponse
import java.math.BigDecimal
import java.util.*

@Parcelize
@Serializable
class InstallmentPlansResponse

@GeideaSdkInternal
internal constructor(
        override val responseCode: String? = null,
        override val responseMessage: String? = null,
        override val detailedResponseCode: String? = null,
        override val detailedResponseMessage: String? = null,
        override val language: String? = null,
        val currency: String,
        val minimumDownPayment: BigDecimal? = null,
        val totalAmount: BigDecimal = BigDecimal.ZERO,
        val financedAmount: BigDecimal = BigDecimal.ZERO,
        val giftCardAmount: BigDecimal = BigDecimal.ZERO,
        val campaignAmount: BigDecimal = BigDecimal.ZERO,
        val bnplOrderId: String? = null,
        val installmentPlans: List<InstallmentPlan> = emptyList()
) : GeideaJsonObject, GeideaResponse {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InstallmentPlansResponse

        if (responseCode != other.responseCode) return false
        if (responseMessage != other.responseMessage) return false
        if (detailedResponseCode != other.detailedResponseCode) return false
        if (detailedResponseMessage != other.detailedResponseMessage) return false
        if (language != other.language) return false
        if (currency != other.currency) return false
        if (minimumDownPayment != other.minimumDownPayment) return false
        if (totalAmount != other.totalAmount) return false
        if (financedAmount != other.financedAmount) return false
        if (giftCardAmount != other.giftCardAmount) return false
        if (campaignAmount != other.campaignAmount) return false
        if (bnplOrderId != other.bnplOrderId) return false
        if (installmentPlans != other.installmentPlans) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                responseCode,
                responseMessage,
                detailedResponseCode,
                detailedResponseMessage,
                language,
                bnplOrderId,
                currency,
                minimumDownPayment,
                totalAmount,
                financedAmount,
                giftCardAmount,
                campaignAmount,
                installmentPlans,
        )
    }

    override fun toString(): String {
        return "InstallmentPlansResponse(responseCode=$responseCode, responseMessage=$responseMessage, detailedResponseCode=$detailedResponseCode, detailedResponseMessage=$detailedResponseMessage, language=$language, currency=$currency, minimumDownPayment=$minimumDownPayment, totalAmount=$totalAmount, financedAmount=$financedAmount, giftCardAmount=$giftCardAmount, campaignAmount=$campaignAmount, bnplOrderId=$bnplOrderId, installmentPlans=$installmentPlans)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): InstallmentPlansResponse = decodeFromJson(json)
    }
}