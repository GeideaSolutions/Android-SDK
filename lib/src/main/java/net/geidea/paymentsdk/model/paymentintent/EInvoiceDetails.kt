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
import net.geidea.paymentsdk.model.common.LocalizableRequest
import java.math.BigDecimal
import java.util.*

@Parcelize
@Serializable
class EInvoiceDetails

    @GeideaSdkInternal
    internal constructor(
        override var language: String?,
        val collectCustomersBillingShippingAddress: Boolean? = null,
        val preAuthorizeAmount: Boolean? = null,
        val subTotalWithoutTax: BigDecimal? = null,
        val subTotalTax: BigDecimal? = null,
        val subTotal: BigDecimal? = null,
        val grandTotal: BigDecimal? = null,
        val extraCharges: BigDecimal? = null,
        val extraChargesType: String? = null,
        val chargeDescription: String? = null,
        val merchantReferenceId: String? = null,
        val invoiceDiscount: BigDecimal? = null,
        val invoiceDiscountType: String? = null,
        val number: String? = null,
        val eInvoiceItems: List<EInvoiceItem>? = null,

        ) : LocalizableRequest, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EInvoiceDetails

        if (language != other.language) return false
        if (collectCustomersBillingShippingAddress != other.collectCustomersBillingShippingAddress) return false
        if (preAuthorizeAmount != other.preAuthorizeAmount) return false
        if (subTotalWithoutTax != other.subTotalWithoutTax) return false
        if (subTotalTax != other.subTotalTax) return false
        if (subTotal != other.subTotal) return false
        if (grandTotal != other.grandTotal) return false
        if (extraCharges != other.extraCharges) return false
        if (extraChargesType != other.extraChargesType) return false
        if (chargeDescription != other.chargeDescription) return false
        if (merchantReferenceId != other.merchantReferenceId) return false
        if (invoiceDiscount != other.invoiceDiscount) return false
        if (invoiceDiscountType != other.invoiceDiscountType) return false
        if (number != other.number) return false
        if (eInvoiceItems != other.eInvoiceItems) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                language,
                collectCustomersBillingShippingAddress,
                preAuthorizeAmount,
                subTotalWithoutTax,
                subTotalTax,
                subTotal,
                grandTotal,
                extraCharges,
                extraChargesType,
                chargeDescription,
                merchantReferenceId,
                invoiceDiscount,
                invoiceDiscountType,
                number,
                eInvoiceItems,
        )
    }

    // GENERATED
    override fun toString(): String {
        return "EInvoiceDetails(language=$language, collectCustomersBillingShippingAddress=$collectCustomersBillingShippingAddress, preAuthorizeAmount=$preAuthorizeAmount, subTotalWithoutTax=$subTotalWithoutTax, subTotalTax=$subTotalTax, subTotal=$subTotal, grandTotal=$grandTotal, extraCharges=$extraCharges, extraChargesType=$extraChargesType, chargeDescription=$chargeDescription, invoiceDiscount=$invoiceDiscount, merchantReferenceId=$merchantReferenceId, invoiceDiscountType=$invoiceDiscountType, number=$number, eInvoiceItems=$eInvoiceItems)"
    }

    /**
     * Builder for [EInvoiceDetails]
     */
    class Builder {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var language: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var collectCustomersBillingShippingAddress: Boolean? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var preAuthorizeAmount: Boolean? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var subTotal: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var subTotalWithoutTax: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var subTotalTax: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var grandTotal: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var extraCharges: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var extraChargesType: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var chargeDescription: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var merchantReferenceId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var invoiceDiscount: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var invoiceDiscountType: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var number: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var eInvoiceItems: List<EInvoiceItem>? = null

        fun setLanguage(language: String?): Builder = apply { this.language = language }
        fun setCollectCustomersBillingShippingAddress(collectCustomersBillingShippingAddress: Boolean?): Builder = apply { this.collectCustomersBillingShippingAddress = collectCustomersBillingShippingAddress }
        fun setPreAuthorizeAmount(preAuthorizeAmount: Boolean?): Builder = apply { this.preAuthorizeAmount = preAuthorizeAmount }
        fun setSubTotalWithoutTax(subtotalWithoutTax: BigDecimal?): Builder = apply { this.subTotalWithoutTax = subtotalWithoutTax }
        fun setSubTotalTax(subTotalTax: BigDecimal?): Builder = apply { this.subTotalTax = subTotalTax }
        fun setSubTotal(subTotal: BigDecimal?): Builder = apply { this.subTotal = subTotal }
        fun setGrandTotal(grandTotal: BigDecimal?): Builder = apply { this.grandTotal = grandTotal }
        fun setExtraCharges(extraCharges: BigDecimal?): Builder = apply { this.extraCharges = extraCharges }
        fun setExtraChargesType(extraChargesType: String?): Builder = apply { this.extraChargesType = extraChargesType }
        fun setChargeDescription(chargeDescription: String?): Builder = apply { this.chargeDescription = chargeDescription }
        fun setMerchantReferenceId(merchantReferenceId: String?): Builder = apply { this.merchantReferenceId = merchantReferenceId }
        fun setInvoiceDiscount(invoiceDiscount: BigDecimal?): Builder = apply { this.invoiceDiscount = invoiceDiscount }
        fun setInvoiceDiscountType(invoiceDiscountType: String?): Builder = apply { this.invoiceDiscountType = invoiceDiscountType }
        fun setNumber(number: String?): Builder = apply { this.number = number }
        fun setEInvoiceItems(eInvoiceItems: List<EInvoiceItem>?): Builder = apply { this.eInvoiceItems = eInvoiceItems }

        fun build(): EInvoiceDetails {
            return EInvoiceDetails(
                    language = this.language,
                    collectCustomersBillingShippingAddress = this.collectCustomersBillingShippingAddress,
                    preAuthorizeAmount = this.preAuthorizeAmount,
                    subTotalWithoutTax = this.subTotalWithoutTax,
                    subTotal = this.subTotal,
                    grandTotal = this.grandTotal,
                    extraCharges = this.extraCharges,
                    extraChargesType = this.extraChargesType,
                    chargeDescription = this.chargeDescription,
                    merchantReferenceId = merchantReferenceId,
                    invoiceDiscount = this.invoiceDiscount,
                    invoiceDiscountType = this.invoiceDiscountType,
                    number = number,
                    eInvoiceItems = this.eInvoiceItems,
            )
        }
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): EInvoiceDetails = decodeFromJson(json)
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun EInvoiceDetails(initializer: EInvoiceDetails.Builder.() -> Unit): EInvoiceDetails {
    return EInvoiceDetails.Builder().apply(initializer).build()
}