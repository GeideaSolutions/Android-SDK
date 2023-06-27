@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.bnpl.souhoola

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.flow.pay.OrderItem
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.bnpl.shahry.ShahryOrderItem
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import java.math.BigDecimal
import java.util.*

@Parcelize
@Serializable
class SouhoolaOrderItem private constructor(
        val merchantItemId: String? = null,
        val name: String,
        val description: String? = null,
        val categories: String? = null,
        val count: Int,
        val price: BigDecimal,
        val currency: String,
) : GeideaJsonObject, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SouhoolaOrderItem

        if (merchantItemId != other.merchantItemId) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (categories != other.categories) return false
        if (count != other.count) return false
        if (price != other.price) return false
        if (currency != other.currency) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                merchantItemId,
                name,
                description,
                categories,
                count,
                price,
                currency,
        )
    }

    // GENERATED
    override fun toString(): String {
        return "SouhoolaOrderItem(merchantItemId=$merchantItemId, name=$name, description=$description, categories=$categories, count=$count, price=$price, currency=$currency)"
    }

    /**
     * Builder for [ShahryOrderItem].
     */
    class Builder() {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var merchantItemId: String? = null

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

        @set:JvmSynthetic // Hide 'void' setter from Java
        var currency: String? = null

        fun setMerchantItemId(merchantItemId: String?): Builder = apply { this.merchantItemId = merchantItemId }
        fun setName(name: String?): Builder = apply { this.name = name }
        fun setDescription(description: String?): Builder = apply { this.description = description }
        fun setCategories(categories: String?): Builder = apply { this.categories = categories }
        fun setCount(count: Int?): Builder = apply { this.count = count }
        fun setPrice(price: BigDecimal?): Builder = apply { this.price = price }
        fun setCurrency(currency: String?): Builder = apply { this.currency = currency }

        fun build(): SouhoolaOrderItem = SouhoolaOrderItem(
                merchantItemId = this.merchantItemId,
                name = requireNotNull(this.name) { "Missing name" },
                description = this.description,
                categories = requireNotNull(this.categories) { "Missing categories" },
                count = requireNotNull(this.count) { "Missing count" },
                price = requireNotNull(this.price) { "Missing price" },
                currency = requireNotNull(this.currency) { "Missing currency" },
        )
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): ShahryOrderItem = decodeFromJson(json)
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun SouhoolaOrderItem(initializer: SouhoolaOrderItem.Builder.() -> Unit): SouhoolaOrderItem {
    return SouhoolaOrderItem.Builder().apply(initializer).build()
}

@GeideaSdkInternal
internal fun List<OrderItem>.toSouhoolaOrderItems(currency: String): List<SouhoolaOrderItem> {
    return map {
        SouhoolaOrderItem {
            merchantItemId = it.merchantItemId
            name = it.name
            description = it.description
            categories = it.categories
            count = it.count
            price = it.price
            this.currency = currency
        }
    }
}