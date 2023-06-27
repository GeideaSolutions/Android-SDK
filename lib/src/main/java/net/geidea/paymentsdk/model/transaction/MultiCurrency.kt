@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.transaction

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import net.geidea.paymentsdk.model.order.Order
import java.math.BigDecimal
import java.util.*

@Parcelize
@Serializable
class MultiCurrency

/**
 * For SDK internal usage only!
 */
@GeideaSdkInternal
internal constructor(
    val authAmount: BigDecimal?,
    val authCurrency: String?,
    val settleAmount: BigDecimal?,
    val settleCurrency: String?,
    val exchangeRate: BigDecimal?,
    val exchangeFeePercentage: BigDecimal?,
    val exchangeFeeAmount: BigDecimal?,
) : GeideaJsonObject, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MultiCurrency

        if (authAmount != other.authAmount) return false
        if (authCurrency != other.authCurrency) return false
        if (settleAmount != other.settleAmount) return false
        if (settleCurrency != other.settleCurrency) return false
        if (exchangeRate != other.exchangeRate) return false
        if (exchangeFeePercentage != other.exchangeFeePercentage) return false
        if (exchangeFeeAmount != other.exchangeFeeAmount) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(authAmount, authCurrency, settleAmount, settleCurrency, exchangeRate, exchangeFeePercentage, exchangeFeeAmount)
    }

    override fun toString(): String {
        return "MultiCurrency(authAmount=$authAmount, authCurrency=$authCurrency, settleAmount=$settleAmount, settleCurrency=$settleCurrency, exchangeRate=$exchangeRate, exchangeFeePercentage=$exchangeFeePercentage, exchangeFeeAmount=$exchangeFeeAmount)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): Order = decodeFromJson(json)
    }
}