@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.flow.pay

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.util.Validations.simpleVerifyEmail
import net.geidea.paymentsdk.internal.util.Validations.validateHttpsUrl
import net.geidea.paymentsdk.internal.util.has2orLessFractionalDigits
import net.geidea.paymentsdk.model.Address
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.model.PaymentMethod
import net.geidea.paymentsdk.model.isNullOrEmpty
import java.math.BigDecimal
import java.util.*

/**
 * Class used as input parameter for the payment flow. Contains order, customer and payment details.
 *
 * Starting a payment flow is done in few simple steps where the final result of the flow is an
 * object of type [GeideaResult]<[Order][net.geidea.paymentsdk.model.order.Order]>:
 *
 * **Step 1)** Register for activity result in which you would receive the payment flow final result
 *
 * ```
 * val paymentLauncher = registerForActivityResult(PaymentContract(), ::handleOrderResponse)
 * ```
 *
 * **Step 2)** Populate a paymentData object with the order, customer and payment method details.
 *
 * ```
 * val paymentData = PaymentData {
 *     amount = amount
 *     currency = currency
 *     paymentMethod = PaymentMethod {
 *         cardHolderName = "John Doe"
 *         cardNumber = "..."
 *         expiryDate = ExpiryDate(12, 25)
 *         cvv = "123"
 *     }
 *     // ...
 * }
 * ```
 *
 * **Step 3)** Start the actual payment flow.
 *
 * ```
 * paymentLauncher.launch(paymentData)
 * ```
 *
 * **Step 4)** Handle the order response in the function (or lambda) from Step 1
 *
 * ```
 * fun handleOrderResult(result: GeideaResult<Order>) {
 *     when (result) {
 *         is GeideaResult.Success<Order> -> {
 *             // Handle successful order
 *         }
 *         is GeideaResult.Error -> {
 *             // Handle error
 *         }
 *         is GeideaResult.Cancelled -> {
 *             // The payment flow was intentionally cancelled by the user
 *             Toast.makeText(this, "Payment cancelled by the user", Toast.LENGTH_LONG).show()
 *         }
 *     }
 * }
 * ```
 *
 * @see PaymentContract
 * @see GeideaResult
 */
@Parcelize
class PaymentData

@GeideaSdkInternal
internal constructor(
    val amount: BigDecimal,
    val currency: String,
    val paymentOptions: PaymentOptions? = null,
    val paymentOperation: String? = null,
    val merchantReferenceId: String? = null,
    val customerEmail: String? = null,
    val showCustomerEmail: Boolean = false,
    val billingAddress: Address? = null,
    val shippingAddress: Address? = null,
    val showAddress: Boolean = false,
    val showReceipt: Boolean? = null,
    val callbackUrl: String? = null,
    val paymentMethod: PaymentMethod? = null,
    val tokenId: String? = null,
    val cardOnFile: Boolean = false,
    val initiatedBy: String? = null,
    val agreementId: String? = null,
    val agreementType: String? = null,
    val paymentIntentId: String? = null,
    val orderItems: List<OrderItem>? = null,
    val bundle: Bundle? = null,
    val paymentType: PaymentType = PaymentType.SDK,
    val returnUrl: String? = null
) : Parcelable {

    @GeideaSdkInternal
    internal fun copy(
            amount: BigDecimal = this.amount,
            currency: String = this.currency,
            paymentOptions: PaymentOptions? = this.paymentOptions,
            paymentOperation: String? = this.paymentOperation,
            merchantReferenceId: String? = this.merchantReferenceId,
            customerEmail: String? = this.customerEmail,
            showCustomerEmail: Boolean = this.showCustomerEmail,
            billingAddress: Address? = this.billingAddress,
            shippingAddress: Address? = this.shippingAddress,
            showAddress: Boolean = this.showAddress,
            showReceipt: Boolean? = this.showReceipt,
            callbackUrl: String? = this.callbackUrl,
            paymentMethod: PaymentMethod? = this.paymentMethod,
            tokenId: String? = this.tokenId,
            cardOnFile: Boolean = this.cardOnFile,
            initiatedBy: String? = this.initiatedBy,
            agreementId: String? = this.agreementId,
            agreementType: String? = this.agreementType,
            paymentIntentId: String? = this.paymentIntentId,
            orderItems: List<OrderItem>? = this.orderItems,
            bundle: Bundle? = this.bundle,
            paymentType: PaymentType = this.paymentType,
            returnUrl: String? = this.returnUrl
    ) = PaymentData(
        amount = amount,
        currency = currency,
        paymentOptions = paymentOptions,
        paymentOperation = paymentOperation,
        merchantReferenceId = merchantReferenceId,
        customerEmail = customerEmail,
        showCustomerEmail = showCustomerEmail,
        billingAddress = billingAddress,
        shippingAddress = shippingAddress,
        showAddress = showAddress,
        showReceipt = showReceipt,
        callbackUrl = callbackUrl,
        paymentMethod = paymentMethod,
        tokenId = tokenId,
        cardOnFile = cardOnFile,
        initiatedBy = initiatedBy,
        agreementId = agreementId,
        agreementType = agreementType,
        paymentIntentId = paymentIntentId,
        orderItems = orderItems,
        bundle = bundle,
        paymentType = paymentType,
        returnUrl = returnUrl
    )

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PaymentData

        if (amount != other.amount) return false
        if (currency != other.currency) return false
        if (paymentOptions != other.paymentOptions) return false
        if (paymentOperation != other.paymentOperation) return false
        if (merchantReferenceId != other.merchantReferenceId) return false
        if (customerEmail != other.customerEmail) return false
        if (showCustomerEmail != other.showCustomerEmail) return false
        if (billingAddress != other.billingAddress) return false
        if (shippingAddress != other.shippingAddress) return false
        if (showAddress != other.showAddress) return false
        if (showReceipt != other.showReceipt) return false
        if (callbackUrl != other.callbackUrl) return false
        if (paymentMethod != other.paymentMethod) return false
        if (tokenId != other.tokenId) return false
        if (cardOnFile != other.cardOnFile) return false
        if (initiatedBy != other.initiatedBy) return false
        if (agreementId != other.agreementId) return false
        if (agreementType != other.agreementType) return false
        if (paymentIntentId != other.paymentIntentId) return false
        if (orderItems != other.orderItems) return false
        if (bundle != other.bundle) return false
        if (paymentType != other.paymentType) return false
        if (returnUrl != other.returnUrl) return false
        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                amount,
                currency,
                paymentOptions,
                paymentOperation,
                merchantReferenceId,
                customerEmail,
                showCustomerEmail,
                billingAddress,
                shippingAddress,
                showAddress,
                showReceipt,
                paymentMethod,
                tokenId,
                cardOnFile,
                initiatedBy,
                agreementId,
                agreementType,
                paymentIntentId,
                orderItems,
                bundle,
                paymentType,
                returnUrl
        )
    }

    // GENERATED
    override fun toString(): String {
        return """PaymentData(
|               amount=$amount,
|               currency=$currency,
|               paymentOptions=$paymentOptions,
|               paymentOperation=$paymentOperation,
|               merchantReferenceId=$merchantReferenceId,
|               customerEmail=$customerEmail,
|               showCustomerEmail=$showCustomerEmail,
|               billingAddress=$billingAddress,
|               shippingAddress=$shippingAddress,
|               showAddress=$showAddress,
|               showReceipt=$showReceipt,
|               callbackUrl=$callbackUrl,
|               paymentMethod=$paymentMethod,
|               tokenId=$tokenId,
|               cardOnFile=$cardOnFile,
|               initiatedBy=$initiatedBy,
|               agreementId=$agreementId,
|               agreementType=$agreementType,
|               paymentIntentId=$paymentIntentId,
|               orderItems=$orderItems,
|               bundle=$bundle,
|               paymentType=$paymentType,
|               returnUrl=$returnUrl,
|               )""".trimMargin()
    }

    /**
     * Convert to Android [Intent] that can be used to start a payment flow.
     */
    fun toIntent(context: Context, block: Intent.() -> Unit = {}): Intent {
        val intent = Intent(context, PaymentActivity::class.java)
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT_DATA, this)
        return intent.apply(block)
    }

    /**
     * Builder for [PaymentData].
     */
    class Builder {
        /**
         * Transaction amount (mandatory). Must be a positive [BigDecimal] number.
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var amount: BigDecimal? = null

        /**
         * Currency of the amount (mandatory). 3-letter ISO 4217 code.
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var currency: String? = null

        /**
         * Custom payment options which the order will be restricted to.
         */
        var paymentOptions: PaymentOptions? = null

        /**
         * Payment operation type (optional). If null or not set then the default value set
         * for your Merchant configuration will be used automatically.
         *
         * @see net.geidea.paymentsdk.model.transaction.PaymentOperation
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var paymentOperation: String? = null

        /**
         * Use this as your unique reference for each transaction (optional).
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var merchantReferenceId: String? = null

        /**
         * Set customer email address (optional).
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var customerEmail: String? = null


        @set:JvmSynthetic // Hide 'void' setter from Java
        var showCustomerEmail: Boolean = false

        /**
         * Billing address (optional).
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var billingAddress: Address? = null

        /**
         * Shipping address (optional).
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var shippingAddress: Address? = null


        @set:JvmSynthetic // Hide 'void' setter from Java
        var showAddress: Boolean = false

        @set:JvmSynthetic // Hide 'void' setter from Java
        var showReceipt: Boolean? = null

        /**
         * Web hook URL which will be called with the order details (optional). Must be HTTPS url.
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var callbackUrl: String? = null

        /**
         * Payment card details (optional).
         *
         * If not set the SDK will display a payment form to
         * input the required card data. If set the SDK will use the
         * provided data and will not display a payment form.
         *
         * [paymentMethod] and [tokenId] are mutually exclusive and should not be supplied both at
         * the same time.
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var paymentMethod: PaymentMethod? = null

        /**
         * Token ID of a previously saved card. This property is an alternative to [paymentMethod].
         *  If set the SDK will use the provided saved card and will not display a payment form.
         * [paymentMethod] and [tokenId] are mutually exclusive and should not be supplied both at
         * the same time.
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var tokenId: String? = null

        /**
         * Allow/disallow tokenization of the payment card (optional). Default is false.
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var cardOnFile: Boolean = false

        /**
         * Denotes the initiator of the transaction (mandatory when [tokenId] is set).
         *
         * @see net.geidea.paymentsdk.model.common.InitiatingSource
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var initiatedBy: String? = null

        /**
         * Agreement ID
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var agreementId: String? = null

        /**
         * Agreement type
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var agreementType: String? = null

        /**
         * paymentIntentId (optional)
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var paymentIntentId: String? = null

        /**
         * Order items (optional). Required for the following payment methods:
         * - Shahry Installments
         * - Souhoola Installments
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var orderItems: List<OrderItem>? = null

        /**
         * Bundle containing parameters not directly related to the payment - e.g. theme,
         * title, etc. (optional)
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var bundle: Bundle? = null

        /**
         * Payment type - SDK or HPP (optional), default would be SDK
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var paymentType: PaymentType = PaymentType.SDK

        /**
         * Return URL which will be called with the order status (Mandatory for [PaymentType.HPP]). Must be HTTPS url.
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var returnUrl: String? = null

        /**
         * Set transaction amount (mandatory). Must be a positive [BigDecimal] number.
         */
        fun setAmount(amount: BigDecimal): Builder = apply { this.amount = amount }

        /**
         * Set currency of the amount (mandatory). Must be a 3-letter ISO 4217 code.
         */
        fun setCurrency(currency: String): Builder = apply { this.currency = currency }

        /**
         * Custom payment options which the order will be restricted to.
         */
        fun setPaymentOptions(paymentOptions: PaymentOptions?): Builder = apply { this.paymentOptions = paymentOptions }

        /**
         * Set payment operation type (optional). If null or not set then the default value set
         * for your Merchant configuration will be used automatically.
         */
        fun setPaymentOperation(paymentOperation: String?): Builder = apply { this.paymentOperation = paymentOperation }

        /**
         * Set your unique reference for each transaction (optional).
         */
        fun setMerchantReferenceId(merchantReferenceId: String?): Builder = apply { this.merchantReferenceId = merchantReferenceId }

        /**
         * Set customer email address (optional).
         */
        fun setCustomerEmail(customerEmail: String?): Builder = apply { this.customerEmail = customerEmail }

        fun setShowCustomerEmail(showCustomerEmail: Boolean): Builder = apply { this.showCustomerEmail = showCustomerEmail }

        /**
         * Set billing address (optional).
         */
        fun setBillingAddress(billingAddress: Address?): Builder = apply { this.billingAddress = billingAddress }

        /**
         * Set shipping address (optional).
         */
        fun setShippingAddress(shippingAddress: Address?): Builder = apply { this.shippingAddress = shippingAddress }

        fun setShowAddress(showAddress: Boolean): Builder = apply { this.showAddress = showAddress }

        /**
         * Define whether after the payment succeeded or failed a screen with receipt data will be
         * shown. If not set then [MerchantConfigurationResponse.isTransactionReceiptEnabled]
         * will be used as a default value.
         */
        fun setShowReceipt(showReceipt: Boolean): Builder = apply { this.showReceipt = showReceipt }

        /**
         * Set web hook URL which will be called with the order details (optional). Must be HTTPS url.
         */
        fun setCallbackUrl(callbackUrl: String?): Builder = apply { this.callbackUrl = callbackUrl }

        /**
         * Set return URL which will be called with the order status (Mandatory for [PaymentType.HPP]}). Must be HTTPS url.
         */
        fun setReturnUrl(returnUrl: String?): Builder = apply { this.returnUrl = returnUrl }

        /**
         * Payment card details (optional).
         *
         * If not set the SDK will display a payment form to
         * input the required card data. If set the SDK will use the
         * provided data and will not display a payment form.
         *
         * @see tokenId
         * @see cardOnFile
         */
        fun setPaymentMethod(paymentMethod: PaymentMethod?): Builder = apply { this.paymentMethod = paymentMethod }

        /**
         * Token ID of a previously saved card (optional).
         * This property is an alternative to [paymentMethod].
         * If set the SDK will use the provided saved card and will not display a payment form.
         * [paymentMethod] and [tokenId] are mutually exclusive and should not be supplied both at
         * the same time.
         *
         * @see paymentMethod
         * @see cardOnFile
         */
        fun setTokenId(tokenId: String?): Builder = apply { this.tokenId = tokenId }

        /**
         * Set flag to allow/disallow tokenization of the payment card (optional).
         */
        fun setCardOnFile(cardOnFile: Boolean): Builder = apply { this.cardOnFile = cardOnFile }

        /**
         * Sets a value that denotes the initiator of the transaction (optional).
         *
         * @see net.geidea.paymentsdk.model.common.InitiatingSource
         */
        fun setInitiatedBy(initiatedBy: String?): Builder = apply { this.initiatedBy = initiatedBy }

        /**
         * Set agreement id.
         */
        fun setAgreementId(agreementId: String?): Builder = apply { this.agreementId = agreementId }

        /**
         * Set agreement type.
         */
        fun setAgreementType(agreementType: String?): Builder = apply { this.agreementType = agreementType }

        /**
         * Set bundle containing parameters not directly related to the payment - e.g. theme,
         * title, etc. (optional).
         */
        fun setBundle(bundle: Bundle?): Builder = apply { this.bundle = bundle }

        fun setPaymentType(paymentType: PaymentType): Builder = apply { this.paymentType = paymentType }

        /**
         * Set paymentIntentId (optional).
         */
        fun setPaymentIntentId(paymentIntentId: String?): Builder = apply { this.paymentIntentId = paymentIntentId }

        /**
         * Set order items (optional). Required for the following payment methods:
         * - Shahry Installments
         * - Souhoola Installments
         */
        fun setOrderItems(orderItems: List<OrderItem>?): Builder = apply { this.orderItems = orderItems }

        /**
         * Constructs a [PaymentData] object.
         *
         * @throws IllegalArgumentException if some of the mandatory properties
         * [amount], [currency] are not set or some of the arguments are invalid. The SDK performs
         * some basic validity check but the full validation is done server-side. If validation
         * fails server-side [GeideaResult.NetworkError] will be returned as a result of the flow.
         */
        fun build(): PaymentData {
            require(paymentMethod == null || tokenId == null) {
                "paymentMethod and tokenId must not be supplied at the same time"
            }
            return PaymentData(
                amount = checkAmount(),
                currency = checkCurrency(),
                paymentOptions = paymentOptions,
                paymentOperation = paymentOperation,
                merchantReferenceId = merchantReferenceId,
                customerEmail = checkCustomerEmail(),
                showCustomerEmail = showCustomerEmail,
                billingAddress = checkBillingAddress(),
                shippingAddress = checkShippingAddress(),
                showAddress = showAddress,
                showReceipt = showReceipt,
                callbackUrl = checkCallbackUrl(),
                paymentMethod = paymentMethod,
                tokenId = tokenId,
                cardOnFile = cardOnFile,
                initiatedBy = initiatedBy,
                agreementId = agreementId,
                agreementType = agreementType,
                paymentIntentId = paymentIntentId,
                orderItems = checkOrderItems(),
                bundle = bundle,
                paymentType = paymentType,
                returnUrl = returnUrl
            )
        }

        private fun checkAmount(): BigDecimal {
            val amount: BigDecimal? = this.amount
            requireNotNull(amount) { "Missing amount" }
            require(amount.signum() > 0) { "Invalid amount: must be positive number" }
            require(amount.has2orLessFractionalDigits()) {
                "Invalid amount: must have at most 2 fractional digits"
            }
            return amount
        }

        private fun checkCurrency(): String {
            val currency: String? = this.currency
            requireNotNull(currency) { "Missing currency" }
            require(currency.length == 3) { "Invalid currency: must be 3 letters (ISO 4217)" }
            return currency
        }

        private fun checkCustomerEmail(): String? {
            return this.customerEmail?.also {
                require(simpleVerifyEmail(it)) { "Invalid email address" }
            }
        }

        private fun checkCallbackUrl(): String? {
            return this.callbackUrl?.also {
                require(it.isNotBlank()) { "Invalid callback URL: must not be empty" }
                require(validateHttpsUrl(it)) { "Invalid callback URL: must be valid https url" }
            }
        }

        private fun checkShippingAddress(): Address? {
            return this.shippingAddress
                    ?.run {
                        countryCode?.run {
                            require(length == 3) { "Invalid shipping country code" }
                        }
                        city?.run {
                            require(length <= MAX_FIELD_LENGTH) { "Invalid shipping address" }
                        }
                        street?.run {
                            require(length <= MAX_FIELD_LENGTH) { "Invalid shipping address" }
                        }
                        postCode?.run {
                            require(length <= MAX_FIELD_LENGTH) { "Invalid shipping address" }
                        }
                        this
                    }
                    ?.takeIf { !it.isNullOrEmpty() }
        }

        private fun checkBillingAddress(): Address? {
            return this.billingAddress
                    ?.run {
                        countryCode?.run {
                            require(length == 3) { "Invalid billing country code" }
                        }
                        city?.run {
                            require(length <= MAX_FIELD_LENGTH) { "Invalid billing address" }
                        }
                        street?.run {
                            require(length <= MAX_FIELD_LENGTH) { "Invalid billing address" }
                        }
                        postCode?.run {
                            require(length <= MAX_FIELD_LENGTH) { "Invalid billing address" }
                        }
                        this
                    }
                    ?.takeIf { !it.isNullOrEmpty() }
        }

        private fun checkOrderItems(): List<OrderItem>? {
            return this.orderItems
                ?.run {
                    require(sumOf { it.price * it.count.toBigDecimal() } == amount) {
                        "Sum of items prices must be equal to the total order amount"
                    }
                    this
                }
                ?.takeIf { it.isNotEmpty() }
        }
    }

    companion object {
        @GeideaSdkInternal
        internal const val MAX_FIELD_LENGTH = 255
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun PaymentData(initializer: PaymentData.Builder.() -> Unit): PaymentData {
    return PaymentData.Builder().apply(initializer).build()
}