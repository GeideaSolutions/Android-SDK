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
class Installment(
    val kstNo: Int? = null,
    val kstAmt: BigDecimal? = null,
    val kstInt: BigDecimal? = null,
    val kstBal: BigDecimal? = null,
    val kstDate: String? = null,
    val oldDebtBal: BigDecimal? = null,
    val newDebtBal: BigDecimal? = null,
    val debtNo: String? = null,
) : GeideaJsonObject, Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Installment

        if (kstNo != other.kstNo) return false
        if (kstAmt != other.kstAmt) return false
        if (kstInt != other.kstInt) return false
        if (kstBal != other.kstBal) return false
        if (kstDate != other.kstDate) return false
        if (oldDebtBal != other.oldDebtBal) return false
        if (newDebtBal != other.newDebtBal) return false
        if (debtNo != other.debtNo) return false

        return true
    }

    override fun hashCode(): Int = Objects.hash(
        kstNo,
        kstAmt,
        kstInt,
        kstBal,
        kstDate,
        oldDebtBal,
        newDebtBal,
        debtNo,
    )

    override fun toString(): String {
        return "Installment(kstNo=$kstNo, kstAmt=$kstAmt, kstInt=$kstInt, kstBal=$kstBal, kstDate=$kstDate, oldDebtBal=$oldDebtBal, newDebtBal=$newDebtBal, debtNo=$debtNo)"
    }

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    companion object {
        @JvmStatic
        fun fromJson(json: String): Installment = decodeFromJson(json)
    }
}