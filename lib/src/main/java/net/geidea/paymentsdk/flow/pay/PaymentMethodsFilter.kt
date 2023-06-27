package net.geidea.paymentsdk.flow.pay

import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.internal.ui.fragment.options.*
import net.geidea.paymentsdk.internal.util.*
import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.ui.model.BnplPaymentMethodDescriptor
import net.geidea.paymentsdk.ui.model.PaymentMethodDescriptor
import java.math.BigDecimal

@GeideaSdkInternal
internal class PaymentMethodsFilter(
    private val merchantConfiguration: MerchantConfigurationResponse,
    private val paymentData: PaymentData,
    private val isBnplDownPaymentMode: Boolean = false,
) {
    /**
     * Remove options with payment methods and card brands that are disabled in
     * in [Merchant configuration][merchantConfiguration].
     */
    val filteredPaymentOptions: Set<PaymentOption>

    /**
     * All card brands acceptable for the current payment after filtered. Brands not configured or
     * not listed in as payment options are filtered out. If there is a payment options contain
     * [PaymentMethodDescriptor.Card] with empty [PaymentMethodDescriptor.Card.acceptedBrands] list
     * then all configured brands will be accepted for the current payment.
     */
    val acceptedCardBrands: Set<CardBrand>

    /**
     * Card brands enabled statically in the [Merchant configuration][merchantConfiguration].
     */
    private val configuredCardBrands get(): Set<CardBrand> = merchantConfiguration.paymentMethods
        ?.map(CardBrand::fromBrandName)
        ?.filter { it !is CardBrand.Unknown }
        .orEmpty()
        .toSet()

    private val hasMerchantProvidedPaymentMethod: Boolean =
        paymentData.paymentMethod != null || paymentData.tokenId != null

    init {
        require(merchantConfiguration.isSuccess)

        val requestedOptions = paymentData.paymentOptions?.options ?: createDefaultPaymentOptions()

        filteredPaymentOptions = filterPaymentOptions(requestedOptions)

        acceptedCardBrands = filteredPaymentOptions
            .map(PaymentOption::paymentMethod)
            .filterIsInstance<PaymentMethodDescriptor.Card>()
            .flatMap {
                it.acceptedBrands.takeIf(Set<CardBrand>::isNotEmpty) ?: configuredCardBrands
            }
            .filter { it !is CardBrand.Unknown }
            .intersect(configuredCardBrands)
            .toSet()
    }

    private fun filterPaymentOptions(options: Set<PaymentOption>): Set<PaymentOption> {
        return options
            .mapNotNull { option ->
                when (option.paymentMethod) {
                    is PaymentMethodDescriptor.Card -> {
                        val withFilteredBrands = filterAcceptableCardBrands(option.paymentMethod.acceptedBrands)
                        if (withFilteredBrands.isNotEmpty()) {
                            option.copy(paymentMethod = PaymentMethodDescriptor.Card(withFilteredBrands))
                        } else {
                            // All brands in this option are unacceptable, filter it out
                            null
                        }
                    }
                    else -> option
                }
            }
            .filter { option ->
                when (option.paymentMethod) {
                    is PaymentMethodDescriptor.MeezaQr -> shouldAllowMeezaQr()
                    is BnplPaymentMethodDescriptor -> shouldAllowBnplMethod(option.paymentMethod)
                    else -> true
                }
            }
            .toSet()
    }

    /**
     * Create default [payment options][PaymentOptions] from the [merchantConfiguration] with
     * default labels, ordering and grouping. Used when no [PaymentData.paymentOptions] is provided.
     */
    internal fun createDefaultPaymentOptions(): Set<PaymentOption> {
        val options = mutableSetOf<PaymentOption>()

        val merchantPaymentMethodIds: Set<String> = merchantConfiguration.paymentMethods.orEmpty()
        val configuredBrands: Set<CardBrand> = merchantPaymentMethodIds
            .map(CardBrand::fromBrandName)
            .toSet()

        if (configuredBrands.isNotEmpty()) {
            options.add(
                PaymentOption(
                    paymentMethod = PaymentMethodDescriptor.Card(acceptedBrands = configuredBrands)
                )
            )
        }

        if (merchantConfiguration.isMeezaQrEnabled == true) {
            options.add(PaymentOption(paymentMethod = PaymentMethodDescriptor.MeezaQr))
        }

        if (isEnabledInConfig(BnplPaymentMethodDescriptor.ValuInstallments)) {
            options.add(PaymentOption(paymentMethod = BnplPaymentMethodDescriptor.ValuInstallments))
        }

        if (isEnabledInConfig(BnplPaymentMethodDescriptor.ShahryInstallments)) {
            options.add(PaymentOption(paymentMethod = BnplPaymentMethodDescriptor.ShahryInstallments))
        }

        if (isEnabledInConfig(BnplPaymentMethodDescriptor.SouhoolaInstallments)) {
            options.add(PaymentOption(paymentMethod = BnplPaymentMethodDescriptor.SouhoolaInstallments))
        }

        return options
    }

    fun getPaymentMethodsAlternativeTo(paymentMethod: PaymentMethodDescriptor): Set<PaymentMethodDescriptor> {
        return filteredPaymentOptions
            .map(PaymentOption::paymentMethod)
            .filter { it.name != paymentMethod.name }
            .toSet()
    }

    internal fun shouldAllowMeezaQr(): Boolean {
        val enabledInConfig = merchantConfiguration.isMeezaQrEnabled == true
        val requestedForThisPayment = paymentData.paymentOptions?.options
            ?.any { it.paymentMethod == PaymentMethodDescriptor.MeezaQr } ?: false

        return enabledInConfig &&
                (paymentData.paymentOptions == null || requestedForThisPayment || isBnplDownPaymentMode)
                && !hasMerchantProvidedPaymentMethod
    }

    internal fun shouldAllowBnplMethod(bnplMethod: BnplPaymentMethodDescriptor): Boolean {
        val requestedForThisPayment = paymentData.paymentOptions?.options
            ?.any { it.paymentMethod == bnplMethod } ?: false
        return isEnabledInConfig(bnplMethod)
                && !isBnplDownPaymentMode
                && (paymentData.paymentOptions == null || requestedForThisPayment)
                && !hasMerchantProvidedPaymentMethod
    }

    private fun isEnabledInConfig(bnplMethod: BnplPaymentMethodDescriptor): Boolean {
        return when (bnplMethod) {
            is BnplPaymentMethodDescriptor.ValuInstallments -> merchantConfiguration.isValuBnplEnabled == true
            is BnplPaymentMethodDescriptor.ShahryInstallments -> merchantConfiguration.isShahryCnpBnplEnabled == true
            is BnplPaymentMethodDescriptor.SouhoolaInstallments -> merchantConfiguration.isSouhoolaCnpBnplEnabled == true
        }
    }

    private fun filterAcceptableCardBrands(brands: Set<CardBrand>): Set<CardBrand> {
        // Brands dynamically set in [PaymentData.paymentOptions]
        val requestedBrands = brands.takeIf(Set<CardBrand>::isNotEmpty) ?: configuredCardBrands

        return if (requestedBrands.isNotEmpty()) {
            requestedBrands.intersect(configuredCardBrands)
        } else {
            // When empty brand set is request, we assume all configured brands
            configuredCardBrands
        }
    }

    /**
     * Note: The following method has been introduced for "consistency with the HPP".
     *
     * Performs a set of very restrictive checks. Punishes the user with an error simply
     * because merchant dynamically requests a method that might be just temporarily disabled in
     * config. Without this method being called the rest of the filtering logic in this class just
     * silently filters out any methods disabled in config. Thus blocking of payments is avoided
     * when an administrator disables a payment method for some reason, even by mistake.
     *
     * The filtering logic of this class works correct even without this method.
     */
    fun checkValidity(): NativeText? {
        // Check if a requested method is disabled in config
        if (paymentData.paymentOptions?.options?.map { it.paymentMethod }.orEmpty().any { method ->
            when (method) {
                is PaymentMethodDescriptor.Card -> {
                    val requestedBrands = method.acceptedBrands.takeIf { it.isNotEmpty() } ?: configuredCardBrands
                    !configuredCardBrands.containsAll(requestedBrands)
                }
                is PaymentMethodDescriptor.MeezaQr -> {
                    merchantConfiguration.isMeezaQrEnabled != true
                }
                is BnplPaymentMethodDescriptor -> {
                    !isEnabledInConfig(method)
                }
            }
        }) {
            return resourceText(R.string.gd_invalid_payment_options)
        }

        // Check if there is a BNPL method but no method to pay the down payment with
        val methods = (paymentData.paymentOptions?.options ?: createDefaultPaymentOptions())
            .map(PaymentOption::paymentMethod)
        if (methods.any { it is BnplPaymentMethodDescriptor }
            && methods.none { paymentMethod ->
                // Is card or qr and enabled
                (paymentMethod is PaymentMethodDescriptor.Card && paymentMethod.acceptedBrands.any(configuredCardBrands::contains))
                        || (paymentMethod is PaymentMethodDescriptor.MeezaQr && merchantConfiguration.isMeezaQrEnabled == true)
            }
        ) {
            return resourceText(R.string.gd_no_down_payment_methods)
        }

        // No error
        return null
    }
    /**
     * Check order amounts, currency and other prerequisites of a given [paymentMethod].
     * When the required conditions are not met the payment flow with this concrete payment method
     * will not be allowed to proceed.
     *
     * @param paymentMethod the given payment method
     * @return null for success and non-null for the respective error
     */
    fun checkRequirements(paymentMethod: PaymentMethodDescriptor): NativeText? {
        val errorMessage: NativeText? =
            if (paymentMethod.isEgyptOnly && paymentData.currency != "EGP") {
                // BNPL payment methods in Egypt are currently restricted to EGP!
                resourceText(R.string.gd_invalid_currency)
            } else if (paymentMethod in setOf(
                    BnplPaymentMethodDescriptor.ShahryInstallments,
                    BnplPaymentMethodDescriptor.SouhoolaInstallments
                )
                && paymentData.orderItems.isNullOrEmpty()
            ) {
                resourceText(R.string.gd_bnpl_missing_order_items)
            } else {
                when (paymentMethod) {
                    is BnplPaymentMethodDescriptor.ValuInstallments -> minAmountErrorText(
                        paymentMethodName = resourceText(BnplPaymentMethodDescriptor.ValuInstallments.text),
                        amount = paymentData.amount,
                        minAmount = merchantConfiguration.valUMinimumAmount,
                        currency = paymentData.currency,
                    )
                    is BnplPaymentMethodDescriptor.SouhoolaInstallments -> minAmountErrorText(
                        paymentMethodName = resourceText(BnplPaymentMethodDescriptor.SouhoolaInstallments.text),
                        amount = paymentData.amount,
                        minAmount = merchantConfiguration.souhoolaMinimumAmount,
                        currency = paymentData.currency,
                    )
                    else -> null    // OK
                }
            }

        return errorMessage
    }

    private val PaymentMethodDescriptor.isEgyptOnly: Boolean
        get() = this is BnplPaymentMethodDescriptor
                || this is PaymentMethodDescriptor.MeezaQr

    private fun minAmountErrorText(
        paymentMethodName: NativeText,
        amount: BigDecimal,
        minAmount: BigDecimal?,
        currency: String
    ): NativeText? =
        if (amount < minAmount.orZero())
            templateText(
                R.string.gd_min_amount_required_s_s,
                paymentMethodName,
                formatAmount(minAmount.orZero(), currency)
            )
        else
            null
}