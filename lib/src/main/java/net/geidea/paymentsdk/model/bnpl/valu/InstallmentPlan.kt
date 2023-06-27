@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.bnpl.valu

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.bnpl.BnplPlan
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import java.math.BigDecimal
import java.util.*

@Parcelize
@Serializable
class InstallmentPlan(
        override val tenorMonth: Int,
        override val installmentAmount: BigDecimal = BigDecimal.ZERO,
        val adminFees: BigDecimal = BigDecimal.ZERO,
        val downPayment: BigDecimal = BigDecimal.ZERO,
) : BnplPlan, GeideaJsonObject, Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InstallmentPlan

        if (tenorMonth != other.tenorMonth) return false
        if (installmentAmount != other.installmentAmount) return false
        if (adminFees != other.adminFees) return false
        if (downPayment != other.downPayment) return false

        return true
    }

    override fun hashCode(): Int = Objects.hash(
            tenorMonth,
            installmentAmount,
            adminFees,
            downPayment,
    )

    override fun toString(): String {
        return "InstallmentPlan(tenorMonth=$tenorMonth, installmentAmount=$installmentAmount, adminFees=$adminFees, downPayment=$downPayment)"
    }

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    companion object {
        @JvmStatic
        fun fromJson(json: String): InstallmentPlan = decodeFromJson(json)
    }
}