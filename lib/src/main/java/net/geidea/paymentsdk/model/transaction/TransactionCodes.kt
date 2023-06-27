package net.geidea.paymentsdk.model.transaction

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import java.util.*

@Parcelize
@Serializable
class TransactionCodes

internal constructor(
        val responseCode: String? = null,
        val responseMessage: String? = null,
        val detailedResponseCode: String? = null,
        val detailedResponseMessage: String? = null,
        val acquirerCode: String? = null,
        val acquirerMessage: String? = null,
) : GeideaJsonObject, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TransactionCodes

        if (responseCode != other.responseCode) return false
        if (responseMessage != other.responseMessage) return false
        if (detailedResponseCode != other.detailedResponseCode) return false
        if (detailedResponseMessage != other.detailedResponseMessage) return false
        if (acquirerCode != other.acquirerCode) return false
        if (acquirerMessage != other.acquirerMessage) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                responseCode,
                responseMessage,
                detailedResponseCode,
                detailedResponseMessage,
                acquirerCode,
                acquirerMessage,
        )
    }

    // GENERATED
    override fun toString(): String {
        return "TransactionCodes(responseCode=$responseCode, responseMessage=$responseMessage, detailedResponseCode=$detailedResponseCode, detailedResponseMessage=$detailedResponseMessage, acquirerCode=$acquirerCode, acquirerMessage=$acquirerMessage)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): TransactionCodes = decodeFromJson(json)
    }
}
