package net.geidea.paymentsdk.flow.pay

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.ui.model.BnplPaymentMethodDescriptor
import net.geidea.paymentsdk.ui.model.PaymentMethodDescriptor

/**
 * A set of payment options representing the payment method items as they will be seen by
 * the customer on the Payment Options screen. Used when:
 *
 * - order is required to be confined to a specific set of payment methods;
 * - to change the default order;
 * - to customize the payment method label texts.
 *
 * Example:
 * ```
 * PaymentOptions {
 *     option("Visa & Mastercard", PaymentMethodDescriptor.Card(
 *         acceptedBrands = setOf(CardBrand.Visa, CardBrand.Mastercard
 *     ),
 *     option(BnplPaymentMethodDescriptor.ValuInstallments)
 *     option(BnplPaymentMethodDescriptor.SouhoolaInstallments)
 * }
 * ```
 * @see PaymentMethodDescriptor
 * @see BnplPaymentMethodDescriptor
 * @see net.geidea.paymentsdk.model.MerchantConfigurationResponse
 */
@Parcelize
public class PaymentOptions
@GeideaSdkInternal
internal constructor(internal val options: Set<PaymentOption>) : Parcelable {
    class Builder {
        private val options = mutableSetOf<PaymentOption>()

        /**
         * Add a payment method option.
         *
         * @param paymentMethod paymentMethod
         * @param label optional text to override the default label text
         */
        @JvmOverloads
        fun option(paymentMethod: PaymentMethodDescriptor, label: CharSequence? = null): Builder =
            apply {
                options.add(
                    PaymentOption(
                        label = label,
                        paymentMethod = paymentMethod
                    )
                )
            }

        /**
         * Build the payment options.
         *
         * @throws IllegalArgumentException when no option was added
         */
        fun build(): PaymentOptions {
            require(options.isNotEmpty()) { "Empty PaymentOptions" }
            return PaymentOptions(options = this.options)
        }
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun PaymentOptions(initializer: PaymentOptions.Builder.() -> Unit): PaymentOptions {
    return PaymentOptions.Builder().apply(initializer).build()
}

@GeideaSdkInternal
@Parcelize
internal data class PaymentOption(
    val label: CharSequence? = null,
    val paymentMethod: PaymentMethodDescriptor,
) : Parcelable