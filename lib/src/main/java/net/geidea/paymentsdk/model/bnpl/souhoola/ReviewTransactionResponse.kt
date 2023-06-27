@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.bnpl.souhoola

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
class ReviewTransactionResponse

@GeideaSdkInternal
internal constructor(
    override val responseCode: String? = null,
    override val responseMessage: String? = null,
    override val detailedResponseCode: String? = null,
    override val detailedResponseMessage: String? = null,
    override val language: String? = null,
    val souhoolaTransactionId: String? = null,
    val installments: List<Installment>? = null,
    val totalInvoicePrice: BigDecimal? = null,
    val loanAmount: BigDecimal? = null,
    val downPayment: BigDecimal? = null,
    val administrativeFees: String? = null,
    val netAdministrativeFees: BigDecimal? = null,
    val mainAdministrativeFees: BigDecimal? = null,
    val annualRate: BigDecimal? = null,
    val cartCount: Int? = null,
    val promoCode: String? = null,
    val merchantName: String? = null,
) : GeideaJsonObject, GeideaResponse {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReviewTransactionResponse

        if (responseCode != other.responseCode) return false
        if (responseMessage != other.responseMessage) return false
        if (detailedResponseCode != other.detailedResponseCode) return false
        if (detailedResponseMessage != other.detailedResponseMessage) return false
        if (language != other.language) return false
        if (souhoolaTransactionId != other.souhoolaTransactionId) return false
        if (installments != other.installments) return false
        if (totalInvoicePrice != other.totalInvoicePrice) return false
        if (loanAmount != other.loanAmount) return false
        if (downPayment != other.downPayment) return false
        if (administrativeFees != other.administrativeFees) return false
        if (netAdministrativeFees != other.netAdministrativeFees) return false
        if (mainAdministrativeFees != other.mainAdministrativeFees) return false
        if (annualRate != other.annualRate) return false
        if (cartCount != other.cartCount) return false
        if (promoCode != other.promoCode) return false
        if (merchantName != other.merchantName) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(
            responseCode,
            responseMessage,
            detailedResponseCode,
            detailedResponseMessage,
            language,
            souhoolaTransactionId,
            installments,
            totalInvoicePrice,
            loanAmount,
            downPayment,
            administrativeFees,
            netAdministrativeFees,
            mainAdministrativeFees,
            annualRate,
            cartCount,
            promoCode,
            merchantName,
        )
    }

    override fun toString(): String {
        return "ReviewTransactionResponse(responseCode=$responseCode, responseMessage=$responseMessage, detailedResponseCode=$detailedResponseCode, detailedResponseMessage=$detailedResponseMessage, language=$language, souhoolaTransactionId=$souhoolaTransactionId, installments=$installments, totalInvoicePrice=$totalInvoicePrice, loanAmount=$loanAmount, downPayment=$downPayment, administrativeFees=$administrativeFees, netAdministrativeFees=$netAdministrativeFees, mainAdministrativeFees=$mainAdministrativeFees, annualRate=$annualRate, cartCount=$cartCount, promoCode=$promoCode, merchantName=$merchantName)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): ReviewTransactionResponse = decodeFromJson(json)
    }
}