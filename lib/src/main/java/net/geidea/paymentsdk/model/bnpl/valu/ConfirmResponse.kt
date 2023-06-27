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
class ConfirmResponse

@GeideaSdkInternal
internal constructor(
        override val responseCode: String? = null,
        override val responseMessage: String? = null,
        override val detailedResponseCode: String? = null,
        override val detailedResponseMessage: String? = null,
        override val language: String? = null,
        val orderId: String? = null,
        val bnplOrderId: String? = null,
        val providerTransactionId: String? = null,
        val loanNumber: String? = null,
        val financedAmount: BigDecimal? = null,
        val downPayment: BigDecimal? = null,
        val giftCardAmount: BigDecimal? = null,
        val campaignAmount: BigDecimal? = null,
        val tenure: Int? = null,
        val installmentAmount: BigDecimal? = null,
        val firstInstallmentDate: String? = null,
        val lastInstallmentDate: String? = null,
        val adminFees: BigDecimal? = null,
        val interestTotalAmount: BigDecimal? = null,
) : GeideaJsonObject, GeideaResponse {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ConfirmResponse

        if (responseCode != other.responseCode) return false
        if (responseMessage != other.responseMessage) return false
        if (detailedResponseCode != other.detailedResponseCode) return false
        if (detailedResponseMessage != other.detailedResponseMessage) return false
        if (language != other.language) return false
        if (orderId != other.orderId) return false
        if (bnplOrderId != other.bnplOrderId) return false
        if (providerTransactionId != other.providerTransactionId) return false
        if (loanNumber != other.loanNumber) return false
        if (financedAmount != other.financedAmount) return false
        if (downPayment != other.downPayment) return false
        if (giftCardAmount != other.giftCardAmount) return false
        if (campaignAmount != other.giftCardAmount) return false
        if (tenure != other.tenure) return false
        if (installmentAmount != other.installmentAmount) return false
        if (firstInstallmentDate != other.firstInstallmentDate) return false
        if (lastInstallmentDate != other.lastInstallmentDate) return false
        if (adminFees != other.adminFees) return false
        if (interestTotalAmount != other.interestTotalAmount) return false

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
                orderId,
                bnplOrderId,
                providerTransactionId,
                loanNumber,
                financedAmount,
                downPayment,
                giftCardAmount,
                campaignAmount,
                tenure,
                installmentAmount,
                firstInstallmentDate,
                lastInstallmentDate,
                adminFees,
                interestTotalAmount
        )
    }

    // GENERATED
    override fun toString(): String {
        return "ConfirmResponse(responseCode=$responseCode, responseMessage=$responseMessage, detailedResponseCode=$detailedResponseCode, detailedResponseMessage=$detailedResponseMessage, language=$language, orderId=$orderId, bnplOrderId=$bnplOrderId, providerTransactionId=$providerTransactionId, loanNumber=$loanNumber, financedAmount=$financedAmount, downPayment=$downPayment, giftCardAmount=$giftCardAmount, campaignAmount=$campaignAmount, tenure=$tenure, installmentAmount=$installmentAmount, firstInstallmentDate=$firstInstallmentDate, lastInstallmentDate=$lastInstallmentDate, adminFees=$adminFees, interestTotalAmount=$interestTotalAmount)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): ConfirmResponse = decodeFromJson(json)
    }
}