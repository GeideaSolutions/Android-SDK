@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.flow.pay

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.bnpl.shahry.ShahryOrderItem
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import java.math.BigDecimal
import java.util.*

@Parcelize
@Serializable
class OrderItem private constructor(
    val orderItemId: String? = null,
    val merchantItemId: String? = null,
    val bnplProviderItemId: String? = null,
    val name: String,
    val description: String? = null,
    val categories: String? = null,
    val count: Int,
    val price: BigDecimal,
    val status: String? = null,
    val installmentAmount: BigDecimal? = null,
    var currency: String? = null,   // Internal use, automatically set by SDK for card/token pay requests
) : GeideaJsonObject, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OrderItem

        if (orderItemId != other.orderItemId) return false
        if (merchantItemId != other.merchantItemId) return false
        if (bnplProviderItemId != other.bnplProviderItemId) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (categories != other.categories) return false
        if (count != other.count) return false
        if (price != other.price) return false
        if (status != other.status) return false
        if (installmentAmount != other.installmentAmount) return false
        if (currency != other.currency) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
            orderItemId,
            merchantItemId,
            bnplProviderItemId,
            name,
            description,
            categories,
            count,
            price,
            status,
            installmentAmount,
            currency,
        )
    }

    // GENERATED
    override fun toString(): String {
        return "OrderItem(orderItemId=$orderItemId, merchantItemId=$merchantItemId, bnplProviderItemId=$bnplProviderItemId, name=$name, description=$description, categories=$categories, count=$count, price=$price, status=$status, installmentAmount=$installmentAmount, currency=$currency)"
    }

    /**
     * Builder for [ShahryOrderItem].
     */
    class Builder() {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var orderItemId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var merchantItemId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var bnplProviderItemId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var name: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var description: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var categories: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var count: Int? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var price: BigDecimal? = null

        /**
         * @see net.geidea.paymentsdk.model.order.OrderStatus
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var status: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var installmentAmount: BigDecimal? = null

        fun setOrderItemId(orderItemId: String?): Builder = apply { this.orderItemId = orderItemId }
        fun setMerchantItemId(merchantItemId: String?): Builder = apply { this.merchantItemId = merchantItemId }
        fun setBnplProviderItemId(bnplProviderItemId: String?): Builder = apply { this.bnplProviderItemId = bnplProviderItemId }
        fun setName(name: String?): Builder = apply { this.name = name }
        fun setDescription(description: String?): Builder = apply { this.description = description }
        fun setCategories(categories: String?): Builder = apply { this.categories = categories }
        fun setCount(count: Int?): Builder = apply { this.count = count }
        fun setPrice(price: BigDecimal?): Builder = apply { this.price = price }

        /**
         * @see net.geidea.paymentsdk.model.order.OrderStatus
         */
        fun setStatus(status: String?): Builder = apply { this.status = status }
        fun setInstallmentAmount(installmentAmount: BigDecimal?): Builder = apply { this.installmentAmount = installmentAmount }

        fun build(): OrderItem = OrderItem(
            orderItemId = this.orderItemId,
            merchantItemId = this.merchantItemId,
            bnplProviderItemId = this.bnplProviderItemId,
            name = requireNotNull(this.name) { "Missing name" },
            description = this.description,
            categories = requireNotNull(this.categories) { "Missing categories" },
            count = requireNotNull(this.count) { "Missing count" },
            price = requireNotNull(this.price) { "Missing price" },
            status = this.status,
            installmentAmount = this.installmentAmount,
        )
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): OrderItem = decodeFromJson(json)
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun OrderItem(initializer: OrderItem.Builder.() -> Unit): OrderItem {
    return OrderItem.Builder().apply(initializer).build()
}