@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.paymentintent

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
class EInvoicePaymentIntent

@GeideaSdkInternal
internal constructor(
    val paymentIntentId: String,
    val amount: BigDecimal? = null,
    val currency: String? = null,
    val customer: CustomerResponse? = null,
    val merchantId: String? = null,
    val merchantPublicKey: String? = null,
    val expiryDate: String? = null,
    val activationDate: String? = null,
    val orders: List<Order>? = emptyList(),
    val status: String? = null,
    val createdDate: String? = null,
    val createdBy: String? = null,
    val updatedDate: String? = null,
    val updatedBy: String? = null,
    val link: String? = null,
    val type: String? = null,
    val eInvoiceDetails: EInvoiceDetails? = null,
    val eInvoiceSentLinks: List<EInvoiceSentLink>? = null
) : GeideaJsonObject, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EInvoicePaymentIntent

        if (paymentIntentId != other.paymentIntentId) return false
        if (amount != other.amount) return false
        if (currency != other.currency) return false
        if (customer != other.customer) return false
        if (merchantId != other.merchantId) return false
        if (merchantPublicKey != other.merchantPublicKey) return false
        if (expiryDate != other.expiryDate) return false
        if (activationDate != other.expiryDate) return false
        if (orders != other.orders) return false
        if (status != other.status) return false
        if (createdDate != other.createdDate) return false
        if (createdBy != other.createdBy) return false
        if (updatedDate != other.updatedDate) return false
        if (updatedBy != other.updatedBy) return false
        if (link != other.link) return false
        if (type != other.type) return false
        if (eInvoiceDetails != other.eInvoiceDetails) return false
        if (eInvoiceSentLinks != other.eInvoiceSentLinks) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                paymentIntentId,
                amount,
                currency,
                customer,
                merchantId,
                merchantPublicKey,
                expiryDate,
                activationDate,
                orders,
                status,
                createdDate,
                createdBy,
                updatedDate,
                updatedBy,
                link,
                type,
                eInvoiceDetails,
                eInvoiceSentLinks,
        )
    }

    override fun toString(): String {
        return "EInvoicePaymentIntent(paymentIntentId='$paymentIntentId', amount=$amount, currency='$currency', customer=$customer, merchantId='$merchantId', merchantPublicKey='$merchantPublicKey', expiryDate=$expiryDate, activationDate=$activationDate, orders=$orders, status=$status, createdDate=$createdDate, createdBy=$createdBy, updatedDate=$updatedDate, updatedBy=$updatedBy, link=$link, type=$type, eInvoiceDetails=$eInvoiceDetails, eInvoiceSentLinks=$eInvoiceSentLinks)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): EInvoicePaymentIntent = decodeFromJson(json)
    }
}