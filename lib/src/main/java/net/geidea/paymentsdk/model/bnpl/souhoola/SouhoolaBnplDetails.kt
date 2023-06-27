@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.bnpl.souhoola

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import java.math.BigDecimal
import java.util.*

@Parcelize
@Serializable
class SouhoolaBnplDetails(
    val souhoolaTransactionId: String,
    val totalInvoicePrice: BigDecimal,
    val downPayment: BigDecimal? = null,
    val loanAmount: BigDecimal,
    val netAdminFees: BigDecimal? = null,
    val mainAdminFees: BigDecimal? = null,
    val tenure: Int,
    val annualRate: BigDecimal? = null,
) : GeideaJsonObject, Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SouhoolaBnplDetails

        if (souhoolaTransactionId != other.souhoolaTransactionId) return false
        if (totalInvoicePrice != other.totalInvoicePrice) return false
        if (downPayment != other.downPayment) return false
        if (loanAmount != other.loanAmount) return false
        if (netAdminFees != other.netAdminFees) return false
        if (mainAdminFees != other.mainAdminFees) return false
        if (tenure != other.tenure) return false
        if (annualRate != other.annualRate) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(
            souhoolaTransactionId,
            totalInvoicePrice,
            downPayment,
            loanAmount,
            netAdminFees,
            mainAdminFees,
            tenure,
            annualRate,
        )
    }

    override fun toString(): String {
        return "SouhoolaBnplDetails(souhoolaTransactionId='$souhoolaTransactionId', totalInvoicePrice=$totalInvoicePrice, downPayment=$downPayment, loanAmount=$loanAmount, netAdminFees=$netAdminFees, mainAdminFees=$mainAdminFees, tenure=$tenure, annualRate=$annualRate)"
    }

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    /**
     * Builder for [SouhoolaBnplDetails]
     */
    class Builder {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var souhoolaTransactionId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var totalInvoicePrice: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var downPayment: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var loanAmount: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var netAdminFees: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var mainAdminFees: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var tenure: Int? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var annualRate: BigDecimal? = null

        fun build() = SouhoolaBnplDetails(
            souhoolaTransactionId = requireNotNull(souhoolaTransactionId) { "Missing souhoolaTransactionId" },
            totalInvoicePrice = requireNotNull(totalInvoicePrice) { "Missing totalInvoicePrice" },
            downPayment = downPayment,
            loanAmount = requireNotNull(loanAmount) { "Missing loanAmount" },
            netAdminFees = requireNotNull(netAdminFees) { "Missing netAdminFees" },
            tenure = requireNotNull(tenure) { "Missing tenure" },
            annualRate = annualRate,
        )
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): SouhoolaBnplDetails = decodeFromJson(json)
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun SouhoolaBnplDetails(initializer: SouhoolaBnplDetails.Builder.() -> Unit): SouhoolaBnplDetails {
    return SouhoolaBnplDetails.Builder().apply(initializer).build()
}