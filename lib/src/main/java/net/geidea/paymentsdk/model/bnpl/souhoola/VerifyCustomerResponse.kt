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
class VerifyCustomerResponse

@GeideaSdkInternal
internal constructor(
        override val responseCode: String? = null,
        override val responseMessage: String? = null,
        override val detailedResponseCode: String? = null,
        override val detailedResponseMessage: String? = null,
        override val language: String? = null,
        val approvedLimit: BigDecimal? = null,
        val outstanding: BigDecimal? = null,
        val availableLimit: BigDecimal? = null,
        val minLoanAmount: BigDecimal? = null,
) : GeideaJsonObject, GeideaResponse {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VerifyCustomerResponse

        if (responseCode != other.responseCode) return false
        if (responseMessage != other.responseMessage) return false
        if (detailedResponseCode != other.detailedResponseCode) return false
        if (detailedResponseMessage != other.detailedResponseMessage) return false
        if (language != other.language) return false
        if (approvedLimit != other.approvedLimit) return false
        if (outstanding != other.outstanding) return false
        if (availableLimit != other.availableLimit) return false
        if (minLoanAmount != other.minLoanAmount) return false

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
                approvedLimit,
                outstanding,
                availableLimit,
                minLoanAmount,
        )
    }

    // GENERATED
    override fun toString(): String {
        return "VerifyCustomerResponse(responseCode=$responseCode, responseMessage=$responseMessage, detailedResponseCode=$detailedResponseCode, detailedResponseMessage=$detailedResponseMessage, language=$language, approvedLimit=$approvedLimit, outstanding=$outstanding, availableLimit=$availableLimit, minLoanAmount=$minLoanAmount)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): VerifyCustomerResponse = decodeFromJson(json)
    }
}