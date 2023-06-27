package net.geidea.paymentsdk.model.bnpl.valu

import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import net.geidea.paymentsdk.model.common.GeideaResponse
import java.util.*

@Parcelize
@Serializable
class GenerateOtpResponse
@GeideaSdkInternal
internal constructor(
        override val responseCode: String? = null,
        override val responseMessage: String? = null,
        override val detailedResponseCode: String? = null,
        override val detailedResponseMessage: String? = null,
        override val language: String? = null,
        val bnplOrderId: String? = null,
) : GeideaJsonObject, GeideaResponse {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GenerateOtpResponse

        if (responseCode != other.responseCode) return false
        if (responseMessage != other.responseMessage) return false
        if (detailedResponseCode != other.detailedResponseCode) return false
        if (detailedResponseMessage != other.detailedResponseMessage) return false
        if (language != other.language) return false
        if (bnplOrderId != other.bnplOrderId) return false

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
        )
    }

    override fun toString(): String {
        return "GenerateOtpResponse(responseCode=$responseCode, responseMessage=$responseMessage, detailedResponseCode=$detailedResponseCode, detailedResponseMessage=$detailedResponseMessage, language=$language, bnplOrderId=$bnplOrderId)"
    }


    companion object {
        @JvmStatic
        fun fromJson(json: String): GenerateOtpResponse = decodeFromJson(json)
    }
}