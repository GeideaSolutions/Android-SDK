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
import java.math.BigDecimal
import java.util.*

@Parcelize
@Serializable
class EInvoiceItem

    @GeideaSdkInternal
    internal constructor(
            val eInvoiceItemId: String? = null,
            val description: String? = null,
            val price: BigDecimal? = null,
            val priceWithDiscount: BigDecimal? = null,
            val priceTax: BigDecimal? = null,
            val priceTotal: BigDecimal? = null,
            val quantity: Int? = null,
            val sku: String? = null,
            val itemDiscount: BigDecimal? = null,
            val itemDiscountType: String? = null,
            val tax: BigDecimal? = null,
            val taxType: String? = null,
            val totalWithoutTax: BigDecimal? = null,
            val totalTax: BigDecimal? = null,
            val total: BigDecimal? = null,
    ) : GeideaJsonObject, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EInvoiceItem

        if (eInvoiceItemId != other.eInvoiceItemId) return false
        if (description != other.description) return false
        if (price != other.price) return false
        if (priceWithDiscount != other.priceWithDiscount) return false
        if (priceTax != other.priceTax) return false
        if (priceTotal != other.priceTotal) return false
        if (quantity != other.quantity) return false
        if (sku != other.sku) return false
        if (itemDiscount != other.itemDiscount) return false
        if (itemDiscountType != other.itemDiscountType) return false
        if (tax != other.tax) return false
        if (taxType != other.taxType) return false
        if (totalWithoutTax != other.totalWithoutTax) return false
        if (totalTax != other.totalTax) return false
        if (total != other.total) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                eInvoiceItemId,
                description,
                price,
                priceWithDiscount,
                priceTax,
                priceTotal,
                quantity,
                sku,
                itemDiscount,
                itemDiscountType,
                tax,
                taxType,
                totalWithoutTax,
                totalTax,
                total,
        )
    }

    // GENERATED
    override fun toString(): String {
        return "EInvoiceItem(eInvoiceItemId=$eInvoiceItemId, description=$description, price=$price, priceWithDiscount=$priceWithDiscount, priceTax=$priceTax, priceTotal=$priceTotal, quantity=$quantity, sku=$sku, itemDiscount=$itemDiscount, itemDiscountType=$itemDiscountType, tax=$tax, taxType=$taxType, totalWithoutTax=$totalWithoutTax, totalTax=$totalTax, total=$total)"
    }

    /**
     * Builder for [EInvoiceItem]
     */
    class Builder() {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var eInvoiceItemId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var description: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var price: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var priceWithDiscount: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var priceTax: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var priceTotal: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var quantity: Int? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var sku: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var itemDiscount: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var itemDiscountType: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var tax: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var taxType: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var totalWithoutTax: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var totalTax: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var total: BigDecimal? = null

        fun setEInvoiceItemId(eInvoiceItemId: String?): Builder = apply { this.eInvoiceItemId = eInvoiceItemId }
        fun setDescription(description: String?): Builder = apply { this.description = description }
        fun setPrice(price: BigDecimal?): Builder = apply { this.price = price }
        fun setPriceWithDiscount(priceWithDiscount: BigDecimal?): Builder = apply { this.priceWithDiscount = priceWithDiscount }
        fun setPriceTax(priceTax: BigDecimal?): Builder = apply { this.priceTax = priceTax }
        fun setPriceTotal(priceTotal: BigDecimal?): Builder = apply { this.priceTotal = priceTotal }
        fun setQuantity(quantity: Int?): Builder = apply { this.quantity = quantity }
        fun setSku(sku: String?): Builder = apply { this.sku = sku }
        fun setItemDiscount(itemDiscount: BigDecimal?): Builder = apply { this.itemDiscount = itemDiscount }
        fun setItemDiscountType(itemDiscountType: String?): Builder = apply { this.itemDiscountType = itemDiscountType }
        fun setTax(tax: BigDecimal?): Builder = apply { this.tax = tax }
        fun setTaxType(taxType: String?): Builder = apply { this.taxType = taxType }
        fun setTotalWithoutTax(totalWithoutTax: BigDecimal?): Builder = apply { this.totalWithoutTax = totalWithoutTax }
        fun setTotalTax(totalTax: BigDecimal?): Builder = apply { this.totalTax = totalTax }
        fun setTotal(total: BigDecimal?): Builder = apply { this.total = total }

        fun build(): EInvoiceItem {
            return EInvoiceItem(
                    eInvoiceItemId = this.eInvoiceItemId,
                    description = this.description,
                    price = this.price,
                    priceWithDiscount = this.priceWithDiscount,
                    priceTax = this.priceTax,
                    priceTotal = this.priceTotal,
                    quantity = this.quantity,
                    sku = this.sku,
                    itemDiscount = this.itemDiscount,
                    itemDiscountType = this.itemDiscountType,
                    tax = this.tax,
                    taxType = this.taxType,
                    totalWithoutTax = this.totalWithoutTax,
                    totalTax = this.totalTax,
                    total = this.total,
            )
        }
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): EInvoiceItem = decodeFromJson(json)
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun EInvoiceItem(initializer: EInvoiceItem.Builder.() -> Unit): EInvoiceItem {
    return EInvoiceItem.Builder().apply(initializer).build()
}