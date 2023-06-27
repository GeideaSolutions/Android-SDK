package net.geidea.paymentsdk.model.order

import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import net.geidea.paymentsdk.model.common.GeideaResponse

@Parcelize
@Serializable
data class OrderSearchResponse(
    override val responseCode: String? = null,
    override val responseMessage: String? = null,
    override val detailedResponseCode: String? = null,
    override val detailedResponseMessage: String? = null,
    override val language: String? = null,
    val orders: List<Order>? = null,
    val totalCount: Int? = null,
) : GeideaJsonObject, GeideaResponse {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    companion object {
        @JvmStatic
        fun fromJson(json: String): OrderSearchResponse = Json.decodeFromString(json)
    }
}