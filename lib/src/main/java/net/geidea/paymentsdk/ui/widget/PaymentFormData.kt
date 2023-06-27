package net.geidea.paymentsdk.ui.widget

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.model.Address
import net.geidea.paymentsdk.model.Card
import java.util.*

/**
 * Class holding the current data contents of [PaymentFormView].
 */
@Parcelize
class PaymentFormData

@GeideaSdkInternal
internal constructor(
        val card: Card?,
        val customerEmail: String?,
        val billingAddress: Address?,
        val shippingAddress: Address?,
        val isSameAddress: Boolean,
) : Parcelable {

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PaymentFormData

        if (card != other.card) return false
        if (customerEmail != other.customerEmail) return false
        if (billingAddress != other.billingAddress) return false
        if (shippingAddress != other.shippingAddress) return false
        if (isSameAddress != other.isSameAddress) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                card,
                customerEmail,
                billingAddress,
                shippingAddress,
                isSameAddress,
        )
    }

    // GENERATED
    override fun toString(): String {
        return "PaymentFormData(card=$card, customerEmail=$customerEmail, billingAddress=$billingAddress, shippingAddress=$shippingAddress, isSameAddress=$isSameAddress)"
    }

    /**
     * Builder for [PaymentData].
     */
    class Builder {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var card: Card? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var customerEmail: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var billingAddress: Address? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var shippingAddress: Address? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var isSameAddress: Boolean = false

        /**
         * Set card.
         */
        fun setCard(card: Card?): Builder = apply { this.card = card }

        /**
         * Set customer email.
         */
        fun setCustomerEmail(customerEmail: String?): Builder = apply { this.customerEmail = customerEmail}

        /**
         * Set billing address.
         */
        fun setBillingAddress(billingAddress: Address?): Builder = apply { this.billingAddress = billingAddress}

        /**
         * Set shipping address.
         */
        fun setShippingAddress(shippingAddress: Address?): Builder = apply { this.shippingAddress = shippingAddress}

        /**
         * Set a flag indicating that the billing address should be used as shipping address.
         */
        fun isSameAddress(isSameAddress: Boolean): Builder = apply { this.isSameAddress = isSameAddress}

        fun build(): PaymentFormData {
            return PaymentFormData(
                    card = card,
                    customerEmail = customerEmail,
                    billingAddress = billingAddress,
                    shippingAddress = shippingAddress,
                    isSameAddress = isSameAddress,
            )
        }
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun PaymentFormData(initializer: PaymentFormData.Builder.() -> Unit): PaymentFormData {
    return PaymentFormData.Builder().apply(initializer).build()
}