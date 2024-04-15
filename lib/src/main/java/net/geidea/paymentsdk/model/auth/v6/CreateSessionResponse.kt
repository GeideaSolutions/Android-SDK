package net.geidea.paymentsdk.model.auth.v6

import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import net.geidea.paymentsdk.model.common.GeideaResponse
import net.geidea.paymentsdk.model.session.Session

@Parcelize
@Serializable
class CreateSessionResponse
@GeideaSdkInternal
internal constructor(
    override val responseCode: String? = null,
    override val responseMessage: String? = null,
    override val detailedResponseCode: String? = null,
    override val detailedResponseMessage: String? = null,
    override val language: String? = null,
    val session: Session?

) : GeideaJsonObject, GeideaResponse {
    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

}