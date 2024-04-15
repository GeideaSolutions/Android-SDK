@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.session


import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import java.math.BigDecimal

@Parcelize
@Serializable
class Session
@GeideaSdkInternal
internal constructor(
    val id: String?,
    val amount: BigDecimal?,
    val currency: String?,
    val callbackUrl: String?,
    val returnUrl: String?,
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'")
    val expiryDate: String?,
    val status: String?,
    val merchantId: String?,
    val merchantPublicKey: String?,
    val language: String?,
    val merchantReferenceId: String?,
    val paymentIntentId: String?,
    val paymentOperation: String?,
    val cardOnFile: Boolean?,
    val cofAgreement: CofAgreement?,
    val initiatedBy: String?,
    val tokenId: String?,
    val customer: String?,
    val platform: Platform?,
    val paymentOptions: String?,
    val recurrence: String?,
    val order: String?,
    val items: String?,
    val appearance: Appearance?,
    val metadata: Metadata?,
    val paymentMethod: PaymentMethod?,
    val subscription: String?
) : GeideaJsonObject, Parcelable {
    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)
}


@Parcelize
@Serializable
class CofAgreement
internal constructor(
    val id: String?,
    val type: String?
) : GeideaJsonObject, Parcelable {
    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

}

@Parcelize
@Serializable
class Platform
internal constructor(
    val name: String?,
    val version: String?,
    val pluginVersion: String?,
    val partnerId: String?
) : GeideaJsonObject, Parcelable {
    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

}

@Parcelize
@Serializable
class Appearance
internal constructor(
    val merchant: Merchant?,
    val showEmail: Boolean?,
    val showAddress: Boolean?,
    val showPhone: Boolean?,
    val receiptPage: Boolean?,
    val styles: Styles?,
    val uiMode: String?
) : GeideaJsonObject, Parcelable {
    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

}

@Parcelize
@Serializable
class Merchant
internal constructor(
    val name: String?,
    val logoUrl: String?
) : GeideaJsonObject, Parcelable {
    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

}

@Parcelize
@Serializable
class Styles
internal constructor(
    val headerColor: String?,
    val hppProfile: String?,
    val hideGeideaLogo: Boolean? = false
) : GeideaJsonObject, Parcelable {
    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

}

@Parcelize
@Serializable
class Metadata
internal constructor(
    val custom: String?
) : GeideaJsonObject, Parcelable {
    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

}

@Parcelize
@Serializable
class PaymentMethod
internal constructor(
    val cardNumber: String?,
    val cardholderName: String?,
    val cvv: String?,
    val expiryDate: ExpiryDate?
) : GeideaJsonObject, Parcelable {
    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

}

@Parcelize
@Serializable
class ExpiryDate
internal constructor(
    val month: String?,
    val year: String?
) : GeideaJsonObject, Parcelable {
    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

}
