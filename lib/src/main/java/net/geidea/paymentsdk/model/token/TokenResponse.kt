package net.geidea.paymentsdk.model.token

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.ExpiryDate
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import net.geidea.paymentsdk.model.common.GeideaResponse
import java.util.*

@Parcelize
@Serializable
class TokenResponse

@GeideaSdkInternal
internal constructor(
    override val responseCode: String? = null,
    override val responseMessage: String? = null,
    override val detailedResponseCode: String? = null,
    override val detailedResponseMessage: String? = null,
    override val language: String? = null,
    var lastFourDigits: String? = null,
    var schema: String? = null,
    var expiryDate: ExpiryDate? = null
) : GeideaJsonObject, GeideaResponse, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TokenResponse

        if (responseCode != other.responseCode) return false
        if (responseMessage != other.responseMessage) return false
        if (detailedResponseCode != other.detailedResponseCode) return false
        if (detailedResponseMessage != other.detailedResponseMessage) return false
        if (language != other.language) return false
        if (lastFourDigits != other.lastFourDigits) return false
        if (schema != other.schema) return false
        if (expiryDate != other.expiryDate) return false

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
            lastFourDigits,
            schema,
            expiryDate
        )
    }

    // GENERATED
    override fun toString(): String {
        return "TokenResponse(responseCode=$responseCode, responseMessage=$responseMessage, detailedResponseCode=$detailedResponseCode, detailedResponseMessage=$detailedResponseMessage, language=$language, lastFourDigits=$lastFourDigits, schema=$schema, expiryDate=$expiryDate)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): TokenResponse = decodeFromJson(json)
    }
}