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
class PaymentIntent

@GeideaSdkInternal
internal constructor(
    val paymentIntentId: String? = null,
    val type: String,
    val id: String? = null, // Used as output only
    val amount: BigDecimal? = null,
    val currency: String? = null,
    val customer: CustomerRequest? = null,
    val expiryDate: String? = null,
    val status: String? = null,
    val merchantId: String? = null,
    val merchantPublicKey: String? = null,
    val merchantName: String? = null,
    val orders: List<Order>? = null,
) : GeideaJsonObject, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PaymentIntent

        if (paymentIntentId != other.paymentIntentId) return false
        if (type != other.type) return false
        if (id != other.id) return false
        if (amount != other.amount) return false
        if (currency != other.currency) return false
        if (customer != other.customer) return false
        if (expiryDate != other.expiryDate) return false
        if (status != other.status) return false
        if (merchantId != other.merchantId) return false
        if (merchantPublicKey != other.merchantPublicKey) return false
        if (merchantName != other.merchantName) return false
        if (orders != other.orders) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                paymentIntentId,
                type,
                id,
                amount,
                currency,
                customer,
                expiryDate,
                status,
                merchantId,
                merchantPublicKey,
                merchantName,
                orders,
        )
    }

    // GENERATED
    override fun toString(): String {
        return "PaymentIntent(paymentIntentId=$paymentIntentId, type=$type, id=$id, amount=$amount, currency='$currency', customer=$customer, expiryDate=$expiryDate, status=$status, merchantId=$merchantId, merchantPublicKey=$merchantPublicKey, merchantName=$merchantName, orders=$orders)"
    }

    /**
     * Builder for [PaymentIntent]
     */
    class Builder() {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var paymentIntentId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var type: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var amount: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var currency: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var customer: CustomerRequest? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var expiryDate: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var status: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var merchantId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var merchantPublicKey: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var merchantName: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var orders: List<Order>? = null

        fun setPaymentIntentId(paymentIntentId: String): Builder = apply { this.paymentIntentId = paymentIntentId }
        fun setType(type: String): Builder = apply { this.type = type }
        fun setAmount(amount: BigDecimal?): Builder = apply { this.amount = amount }
        fun setCurrency(currency: String?): Builder = apply { this.currency = currency }
        fun setCustomer(customer: CustomerRequest?): Builder = apply { this.customer = customer }
        fun setExpiryDate(expiryDate: String?): Builder = apply { this.expiryDate = expiryDate }
        fun setStatus(status: String?): Builder = apply { this.status = status }
        fun setMerchantId(merchantId: String?): Builder = apply { this.merchantId = merchantId }
        fun setMerchantPublicKey(merchantPublicKey: String?): Builder = apply { this.merchantPublicKey = merchantPublicKey }
        fun setMerchantName(merchantName: String?): Builder = apply { this.merchantName = merchantName }
        fun setOrders(orders: List<Order>?): Builder = apply { this.orders = orders }

        fun build(): PaymentIntent {
            return PaymentIntent(
                    paymentIntentId = requireNotNull(this.paymentIntentId) { "Missing paymentIntentId" },
                    type = requireNotNull(this.type) { "Missing type" },
                    amount = this.amount,
                    currency = this.currency,
                    customer = this.customer,
                    expiryDate = this.expiryDate,
                    status = this.status,
                    merchantId = this.merchantId,
                    merchantPublicKey = this.merchantPublicKey,
                    merchantName = this.merchantName,
                    orders = this.orders,
            )
        }
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): PaymentIntent = decodeFromJson(json)
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun PaymentIntent(initializer: PaymentIntent.Builder.() -> Unit): PaymentIntent {
    return PaymentIntent.Builder().apply(initializer).build()
}