package net.geidea.paymentsdk.sampleapp.sample

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.api.gateway.GatewayApi
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.pay.PaymentContract
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.model.*
import net.geidea.paymentsdk.model.common.InitiatingSource
import net.geidea.paymentsdk.model.order.CancelRequest
import net.geidea.paymentsdk.model.order.CaptureRequest
import net.geidea.paymentsdk.model.order.Order
import net.geidea.paymentsdk.model.order.RefundRequest
import net.geidea.paymentsdk.model.pay.PaymentResponse
import net.geidea.paymentsdk.sampleapp.*
import net.geidea.paymentsdk.sampleapp.databinding.ActivitySamplePaymentFormBinding
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*

class SamplePaymentFormActivity : BaseSampleActivity<ActivitySamplePaymentFormBinding>() {

    private var savedInstanceState: Bundle? = null

    private var initialPaymentData: PaymentData? = null

    override fun createBinding(layoutInflater: LayoutInflater): ActivitySamplePaymentFormBinding {
        return ActivitySamplePaymentFormBinding.inflate(layoutInflater)
    }

    private lateinit var paymentLauncher: ActivityResultLauncher<PaymentData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.savedInstanceState = savedInstanceState

        if (savedInstanceState != null) {
            initialPaymentData = savedInstanceState.getParcelable(STATE_INITIAL_PAYMENT_DATA)
        }

        paymentLauncher = registerForActivityResult(PaymentContract(), ::onPaymentResult)

        with(binding) {
            setSupportActionBar(includeAppBar.toolbar)
            title = "PaymentFormView sample"
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(STATE_INITIAL_PAYMENT_DATA, initialPaymentData)
    }

    override fun setupUi(merchantConfig: MerchantConfigurationResponse) = with(binding) {
        paymentFormView.configure(merchantConfig)
        paymentFormView.setOnValidityChangedListener { _, valid ->
            payButton.isEnabled = valid
        }

        // Initialize only on first creation, because PaymentFormView keeps its state across screen rotation.
        val initial = createInitialPaymentData()

        payButton.text = formatPayButtonText(initial.amount, initial.currency)
        payButton.setOnClickListener {
            val finalPaymentData: PaymentData = createFinalPaymentData(initialPaymentData!!)
            paymentLauncher.launch(finalPaymentData)
        }

        if (savedInstanceState == null) {
            this@SamplePaymentFormActivity.initialPaymentData = initial
            with(paymentFormView) {
                cardNumber = initial.paymentMethod?.cardNumber ?: ""
                cardExpiryDate = initial.paymentMethod?.expiryDate?.toDisplayString() ?: ""
                cardSecurityCode = initial.paymentMethod?.cvv ?: ""
                cardHolder = initial.paymentMethod?.cardHolderName ?: ""

                showCustomerEmail = initial.showCustomerEmail
                if (initial.showCustomerEmail) {
                    customerEmail = initial.customerEmail
                }

                showAddress = initial.showAddress

                initial.billingAddress?.let { billingAddress = it }
                initial.shippingAddress?.let { shippingAddress = it }

                if (initial.billingAddress?.countryCode.isNullOrEmpty()) {
                    billingAddressCountryCode = "SAU"
                }

                if (initial.shippingAddress?.countryCode.isNullOrEmpty()) {
                    shippingAddressCountryCode = "SAU"
                }

                val same = billingAddress.equalsIgnoreCase(shippingAddress)
                isSameAddressChecked = same || initial.shippingAddress.isNullOrEmpty()
            }
        }

        // Allow animated appearance/disappearance of shipping address
        paymentFormView.areTransitionsEnabled = true
    }

    /**
     * The payment data defined by you. Later on this data is merged with the data from the
     * payment form to create the final payment data.
     */
    private fun createInitialPaymentData(): PaymentData {
        return PaymentData {
            amount = "123.45".toBigDecimal()
            currency = "SAR"
            callbackUrl = null
            customerEmail = "email@noreply.test"
            showCustomerEmail = true
            showAddress = false
            initiatedBy = InitiatingSource.INTERNET
        }
    }

    /**
     * Merges the payment data originally created by you with the input from customer
     * done inside [net.geidea.paymentsdk.ui.widget.PaymentFormView].
     *
     * @return the final payment data that will be sent to SDK for execution.
     */
    private fun createFinalPaymentData(initialPaymentData: PaymentData): PaymentData = with(binding) {
        return PaymentData {
            // Populate the merchant-related properties...
            paymentOperation = initialPaymentData.paymentOperation
            amount = initialPaymentData.amount
            currency = initialPaymentData.currency
            merchantReferenceId = initialPaymentData.merchantReferenceId
            callbackUrl = initialPaymentData.callbackUrl
            showCustomerEmail = initialPaymentData.showCustomerEmail
            showAddress = initialPaymentData.showAddress
            cardOnFile = initialPaymentData.cardOnFile
            initiatedBy = initialPaymentData.initiatedBy
            agreementId = initialPaymentData.agreementId
            agreementType = initialPaymentData.agreementType
            bundle = initialPaymentData.bundle

            // ...then read and populate customer data from the form

            val card: Card? = paymentFormView.card
            paymentMethod = PaymentMethod {
                cardHolderName = card?.cardHolderName
                cardNumber = card?.cardNumber
                expiryDate = card?.expiryDate
                cvv = card?.cvv
            }

            customerEmail = if (paymentFormView.showCustomerEmail) {
                paymentFormView.customerEmail
            } else {
                initialPaymentData.customerEmail
            }

            billingAddress = if (paymentFormView.showAddress) {
                paymentFormView.billingAddress
            } else {
                initialPaymentData.billingAddress
            }

            shippingAddress = if (paymentFormView.showAddress) {
                if (paymentFormView.isSameAddressChecked) {
                    paymentFormView.billingAddress
                } else {
                    paymentFormView.shippingAddress
                }
            } else {
                initialPaymentData.shippingAddress
            }
        }
    }

    private fun capture(order: Order) {
        lifecycleScope.launch(Dispatchers.Main) {
            val request = CaptureRequest {
                orderId = order.orderId
                callbackUrl = order.callbackUrl
            }
            onPaymentResult(GatewayApi.captureOrder(request))
        }
    }

    private fun refund(order: Order) {
        lifecycleScope.launch(Dispatchers.Main) {
            val request = RefundRequest {
                orderId = order.orderId
                callbackUrl = order.callbackUrl
            }
            onPaymentResult(GatewayApi.refundOrder(request))
        }
    }

    private fun cancel(order: Order) {
        lifecycleScope.launch(Dispatchers.Main) {
            val request = CancelRequest {
                orderId = order.orderId
                reason = "CancelledByUser"
            }
            when (val result = GatewayApi.cancelOrder(request)) {
                is GeideaResult.Success<PaymentResponse> -> showObjectAsJson(result.data)
                is GeideaResult.Error -> showObjectAsJson(result)
                is GeideaResult.Cancelled -> showObjectAsJson(result)
            }

        }
    }

    private fun onPaymentResult(paymentResult: GeideaResult<Order>) = with(binding) {
        when (paymentResult) {
            is GeideaResult.Success<Order> -> {
                val order: Order = paymentResult.data as Order
                showOrder(order, ::capture, ::refund, ::cancel)
                val tokenId = order.tokenId
                if (tokenId != null) {
                    lifecycleScope.launch {
                        CardRepository.saveCard(CardItem(tokenId, order.paymentMethod!!))
                        snack("Tokenized card saved.")
                    }
                }
            }
            is GeideaResult.Error -> {
                showErrorResult(paymentResult.toJson(pretty = true))
            }
            is GeideaResult.Cancelled -> {
                // The payment flow was intentionally cancelled by the user
                snack("Payment cancelled by the user")
            }
        }
    }

    private fun formatPayButtonText(amount: BigDecimal, currency: String): CharSequence {
        val currencyFormat = DecimalFormat("#,###.## ${currency.toUpperCase(Locale.getDefault())}")
        val amountAndCurrency: String = currencyFormat.format(amount)
        return "Pay $amountAndCurrency"
    }

    companion object {
        const val STATE_INITIAL_PAYMENT_DATA = "paymentData"
    }
}