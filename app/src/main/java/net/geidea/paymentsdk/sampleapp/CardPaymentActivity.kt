package net.geidea.paymentsdk.sampleapp

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.GeideaPaymentSdk
import net.geidea.paymentsdk.ServerEnvironment
import net.geidea.paymentsdk.api.gateway.GatewayApi
import net.geidea.paymentsdk.flow.GeideaContract
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.pay.PaymentContract
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.flow.pay.PaymentType
import net.geidea.paymentsdk.model.Address
import net.geidea.paymentsdk.model.ExpiryDate
import net.geidea.paymentsdk.model.PaymentMethod
import net.geidea.paymentsdk.model.common.InitiatingSource
import net.geidea.paymentsdk.model.order.CancelRequest
import net.geidea.paymentsdk.model.order.CaptureRequest
import net.geidea.paymentsdk.model.order.Order
import net.geidea.paymentsdk.model.order.RefundRequest
import net.geidea.paymentsdk.model.pay.PaymentResponse
import net.geidea.paymentsdk.sampleapp.databinding.ActivityCardPaymentBinding
import java.math.BigDecimal


class CardPaymentActivity : AppCompatActivity() {
    private lateinit var cardPaymentBinding: ActivityCardPaymentBinding
    private lateinit var paymentLauncher: ActivityResultLauncher<PaymentData>
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardPaymentBinding = ActivityCardPaymentBinding.inflate(layoutInflater)
        with(cardPaymentBinding) {
            setContentView(root)
            setSupportActionBar(cardPaymentBinding.includeAppBar.toolbar)

            sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(applicationContext)

            paymentLauncher = registerForActivityResult(PaymentContract(), ::onPaymentResult)
            paymentOptionsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                TransitionManager.beginDelayedTransition(root)
                if (checkedId != -1) {
                    updateViews(transition = true)
                }
                when (checkedId) {
                    R.id.bySdkRadioButton -> {
                        updateViews(transition = true)
                    }

                    R.id.byMerchantRadioButton -> {
                        updateViews(transition = true)
                    }

                    R.id.hpp -> {
                        updateViews(transition = true)
                    }
                }
            }

            // set the currency from configuration
            tvCurrency.text = sharedPreferences.getString("currency", "SAR")

            buttonPay.setOnClickListener {
                val allEntries: Map<String, *> = sharedPreferences.all
                for (entry in allEntries) {
                    println("Shared Pref : ${entry.key} value : ${entry.value}")
                }
                val amountString = amountEditText.textOrNull
                if(TextUtils.isEmpty(amountString)){
                    snack("Please enter amount to pay")
                    return@setOnClickListener
                }

                if(!TextUtils.isDigitsOnly(amountString)){
                    snack("Please enter valid amount to pay")
                    return@setOnClickListener
                }
                when (sharedPreferences.getString(MainActivity.PREF_KEY_SERVER_ENVIRONMENT, null)) {

                    ServerEnvironment.EGY_PREPROD.apiBaseUrl -> {
                        GeideaPaymentSdk.serverEnvironment = ServerEnvironment.EGY_PREPROD
                    }

                    ServerEnvironment.EGY_PROD.apiBaseUrl -> {
                        GeideaPaymentSdk.serverEnvironment = ServerEnvironment.EGY_PROD
                    }

                    ServerEnvironment.UAE_PREPROD.apiBaseUrl -> {
                        GeideaPaymentSdk.serverEnvironment = ServerEnvironment.UAE_PREPROD
                    }

                    ServerEnvironment.UAE_PROD.apiBaseUrl -> {
                        GeideaPaymentSdk.serverEnvironment = ServerEnvironment.UAE_PROD
                    }

                    ServerEnvironment.KSA_PREPROD.apiBaseUrl -> {
                        GeideaPaymentSdk.serverEnvironment = ServerEnvironment.KSA_PREPROD
                    }

                    ServerEnvironment.KSA_PROD.apiBaseUrl -> {
                        GeideaPaymentSdk.serverEnvironment = ServerEnvironment.KSA_PROD
                    }
                }

                val paymentData = PaymentData {
                    amount = amountEditText.textOrNull?.let(::BigDecimal)
                    paymentOperation = sharedPreferences.getString("paymentOperation", null)
                    currency = sharedPreferences.getString("currency", null)
                    merchantReferenceId = sharedPreferences.getString("merchantReferenceId", null)
                    callbackUrl = sharedPreferences.getString("callbackUrl", null)
                    returnUrl = sharedPreferences.getString("returnUrl", null)
                    customerEmail = sharedPreferences.getString("customerEmail", null)

                    billingAddress = Address(
                        countryCode = sharedPreferences.getString("countryCode", null),
                        street = sharedPreferences.getString("street", null),
                        city = sharedPreferences.getString("city", null),
                        postCode = sharedPreferences.getString("postCode", null)
                    )

                    shippingAddress = Address(
                        countryCode = sharedPreferences.getString("shippingCountryCode", null),
                        street = sharedPreferences.getString("shippingStreet", null),
                        city = sharedPreferences.getString("shippingCity", null),
                        postCode = sharedPreferences.getString("shippingPostCode", null)
                    )
                    showAddress = sharedPreferences.getBoolean("showAddress", false)
                    showReceipt = sharedPreferences.getBoolean("showReceipt", false)
                    showCustomerEmail = sharedPreferences.getBoolean("showCustomerEmail", false)

                    initiatedBy =
                        sharedPreferences.getString("initiatedBy", InitiatingSource.INTERNET)
                    bundle = bundleOf(
                        GeideaContract.PARAM_THEME to sharedPreferences.getInt("theme", 0)
                    )

                    cardOnFile = false // todo
                    if (byMerchantRadioButton.isChecked) {
                        paymentMethod = PaymentMethod {
                            cardHolderName = cardDetails.cardHolderEditText.textOrNull
                            cardNumber = cardDetails.cardNumberEditText.textOrNull

                            val expMonth: Int? = cardDetails.expiryMonthEditText.textOrNull?.toInt()
                            val expYear: Int? = cardDetails.expiryYearEditText.textOrNull?.toInt()
                            expiryDate = if (expMonth != null && expYear != null) {
                                ExpiryDate(
                                    month = expMonth,
                                    year = expYear
                                )
                            } else {
                                null    // SDK will throw IllegalArgumentException
                            }

                            cvv = cardDetails.cvvEditText.textOrNull
                        }
                    }
                    paymentType = if (hpp.isChecked) PaymentType.HPP else PaymentType.SDK
                }
                paymentLauncher.launch(paymentData)
            }
        }
    }

    private fun updateViews(transition: Boolean = false) {
        if (transition) {
            TransitionManager.beginDelayedTransition(cardPaymentBinding.root)
        }
        with(cardPaymentBinding) {
            val byMerchant =
                paymentOptionsRadioGroup.checkedRadioButtonId == R.id.byMerchantRadioButton
            cardLayout.visibility = if (byMerchant) View.VISIBLE else View.GONE
        }
    }

    private fun onPaymentResult(paymentResult: GeideaResult<Order>) {
        when (paymentResult) {
            is GeideaResult.Success<Order> -> {
                val order: Order = paymentResult.data as Order
                // TODO refund command depends on a configuration flag isRefundEnabled
                showOrder(order, ::capture, ::refund, ::cancel)
                val tokenId = order.tokenId
                if (tokenId != null) {
                    lifecycleScope.launch {
                        CardRepository.saveCard(CardItem(tokenId, order.paymentMethod!!))
                        snack("Tokenized card saved.")
                    }
                }
                snack("Payment success")
            }

            is GeideaResult.Error -> {
                showErrorResult(paymentResult.toJson(pretty = true))
            }

            is GeideaResult.Cancelled -> {
                // The payment flow was intentionally cancelled by the user (or timed out)
                showCancellationMessage(paymentResult)
            }
        }
    }

    private fun showCancellationMessage(
        cancelResult: GeideaResult.Cancelled,
        onPositive: DialogInterface.OnClickListener? = null
    ) {
        val json = cancelResult.toJson(pretty = true)
        AlertDialog.Builder(this)
            .setTitle("Cancelled")
            .setView(monoSpaceTextContainer(json))
            .setPositiveButton(android.R.string.ok, onPositive)
            .show()
    }

    private fun capture(order: Order) {
        GlobalScope.launch(Dispatchers.Main) {
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

    private fun snack(message: CharSequence, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(cardPaymentBinding.root, message, duration).show()
    }
}