package net.geidea.paymentsdk.model.paymentintent

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject

@Parcelize
@Serializable
data class EInvoiceSentLink(
        val sentDate: String? = null,
        val channel: String? = null,
        val recipient: String? = null,
) : GeideaJsonObject, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    companion object {
        @JvmStatic
        fun fromJson(json: String): EInvoiceSentLink = Json.decodeFromString(json)
    }
}