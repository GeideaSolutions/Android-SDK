package net.geidea.paymentsdk.sampleapp.sample.paymentintents

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.api.gateway.GatewayApi
import net.geidea.paymentsdk.api.paymentintent.PaymentIntentApi
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.model.*
import net.geidea.paymentsdk.model.meezaqr.CreateMeezaPaymentIntentRequest
import net.geidea.paymentsdk.model.meezaqr.MeezaPaymentRequest
import net.geidea.paymentsdk.model.meezaqr.MeezaPaymentResponse
import net.geidea.paymentsdk.model.meezaqr.MeezaQrImageResponse
import net.geidea.paymentsdk.model.paymentintent.PaymentIntent
import net.geidea.paymentsdk.model.paymentintent.PaymentIntentResponse
import net.geidea.paymentsdk.model.paymentintent.PaymentIntentStatus
import net.geidea.paymentsdk.sampleapp.databinding.ActivitySampleMeezaBinding
import net.geidea.paymentsdk.sampleapp.databinding.DialogMeezaPhoneBinding
import net.geidea.paymentsdk.sampleapp.hideKeyboard
import net.geidea.paymentsdk.sampleapp.sample.BaseSampleActivity
import net.geidea.paymentsdk.sampleapp.showErrorResult
import net.geidea.paymentsdk.sampleapp.showObjectAsJson
import net.geidea.paymentsdk.sampleapp.textOrNull
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.receiver.MeezaMobileNumberOrDigitalIdValidator
import net.geidea.paymentsdk.ui.widget.TextInputErrorListener
import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * A sample activity demonstrating a custom Meeza QR payment screen. The UI/UX conforms to the
 * following Meeza UX guidelines described in the 'Meeza Digital - UX Guide v1.0' document:
 * 1) The QR code image view must be large enough to be well visible;
 * 2) Your [merchant name][MerchantConfigurationResponse.merchantName] must be well visible;
 * 3) The [Meeza Digital logo][net.geidea.paymentsdk.R.id.gd_ic_meeza_logo] must be well visible;
 * 4) A "Request Payment" button should be present that leads to a mobile phone number input UI.
 * The last is an alternative way for the user to pay in case of difficulties while scanning
 * the QR code.
 *
 * @see PaymentIntentApi.createMeezaPaymentQrCode
 * @see PaymentIntentApi.sendMeezaRequestToPay
 */
class SampleMeezaQrPaymentActivity : BaseSampleActivity<ActivitySampleMeezaBinding>() {

    override fun createBinding(layoutInflater: LayoutInflater): ActivitySampleMeezaBinding {
        return ActivitySampleMeezaBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(binding) {
            setResult(Activity.RESULT_CANCELED)
            setSupportActionBar(includeAppBar.toolbar)
            title = "Meeza QR payment sample"

            val qrRequest: CreateMeezaPaymentIntentRequest = requireNotNull(intent.getParcelableExtra(EXTRA_QR_REQUEST)) {
                "Missing $EXTRA_QR_REQUEST"
            }
            currencyTextView.text = qrRequest.currency

            require(qrRequest.amount.signum() == 1) { "Positive amount required" }

            // The integral part of the amount formatted as a String
            val integerAmountString = qrRequest.amount.toBigInteger().toString()

            val myFormatter = DecimalFormat("#0.00")
            val fractionalPart = qrRequest.amount.remainder(BigDecimal.ONE)
            // Remove the zero before floating point
            val fractionAmountString = myFormatter.format(fractionalPart).substring(1)

            amountIntegerPartTextView.text = integerAmountString
            amountFractionPartTextView.text = fractionAmountString

            if (savedInstanceState == null || sCachedQrBitmapDrawable == null) {
                createQrCodeImage(qrRequest)
            } else {
                // We have an already generated and cached bitmap. Re-use it.
                showQrImage(sCachedQrBitmapDrawable)
                startPaymentIntentPolling(sPaymentIntentId!!, ::handlePaymentIntent)
            }

            requestToPayButton.setOnClickListener(::onRequestPaymentButtonClicked)
        }
    }

    override fun setupUi(merchantConfig: MerchantConfigurationResponse) = with(binding) {
        merchantNameTextView.text = merchantConfig.merchantName
    }

    private fun createQrCodeImage(request: CreateMeezaPaymentIntentRequest) {
        sCachedQrBitmapDrawable = null
        sMeezaMessage = null
        lifecycleScope.launch {
            when (val qrResult = PaymentIntentApi.createMeezaPaymentQrCode(request)) {
                is GeideaResult.Success<MeezaQrImageResponse> -> {
                    sMeezaMessage = qrResult.data.message!!
                    sCachedQrBitmapDrawable = decodeQrImage(qrResult.data.image!!)
                    sPaymentIntentId = requireNotNull(qrResult.data.paymentIntentId)
                    showQrImage(sCachedQrBitmapDrawable)
                    startPaymentIntentPolling(sPaymentIntentId!!, ::handlePaymentIntent)
                }
                is GeideaResult.Error -> {
                    showErrorResult(qrResult.toJson(pretty = true))
                    showQrImage(null)
                }
                is GeideaResult.Cancelled -> {
                    snack("Cancelled.")
                    showQrImage(null)
                }
            }
        }
    }

    private fun decodeQrImage(qrCodeImageBase64: String): BitmapDrawable {
        val imageBytes = Base64.decode(qrCodeImageBase64, Base64.DEFAULT)
        val decodedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        return BitmapDrawable(resources, decodedBitmap)
    }

    private fun showQrImage(qrCodeImage: BitmapDrawable?) = with(binding) {
        TransitionManager.beginDelayedTransition(root)
        qrCodeImageView.setImageDrawable(qrCodeImage)
        qrCodeImageView.isInvisible = qrCodeImage == null
        if (qrCodeImage == null) {
            qrPlaceholderTextView.text = "Error creating QR code!"
        } else {
            qrPlaceholderTextView.isVisible = false
        }
        requestToPayButton.isEnabled = qrCodeImage != null
    }

    /**
     * Start periodically checking if the payment intent status has become "Paid" and call
     * [onSuccess] with the response.
     */
    private fun startPaymentIntentPolling(paymentIntentId: String, onSuccess: (PaymentIntent) -> Unit) {
        lifecycleScope.launch {
            // Do a maximum of 200 requests - once every 3 seconds
            repeat(200) {
                when (val pollResult = PaymentIntentApi.getPaymentIntent(paymentIntentId)) {
                    is GeideaResult.Success<PaymentIntentResponse> -> {
                        onSuccess(pollResult.data.paymentIntent)
                    }
                    is GeideaResult.Error -> {
                        showErrorResult(pollResult.toJson(pretty = true))
                    }
                    is GeideaResult.Cancelled -> {
                        snack("Cancelled.")
                    }
                }
                delay(3_000)
            }
        }
    }

    private fun handlePaymentIntent(paymentIntent: PaymentIntent) {
        val status: String? = paymentIntent.status
        when (status) {
            PaymentIntentStatus.PAID -> {
                Toast.makeText(this@SampleMeezaQrPaymentActivity, "Successfully paid.", Toast.LENGTH_LONG).show()
            }
            PaymentIntentStatus.EXPIRED -> {
                Toast.makeText(this@SampleMeezaQrPaymentActivity, "QR code expired!", Toast.LENGTH_LONG).show()
            }
        }
        if (status != PaymentIntentStatus.CREATED) {
            val orderId = paymentIntent.orders?.last()?.orderId
            if (orderId != null) {
                lifecycleScope.launch {
                    val order = GatewayApi.getOrder(orderId)
                    setResult(RESULT_OK, Intent().apply { putExtra("result", order)})
                    finish()
                }
            }
        }
    }

    private fun onRequestPaymentButtonClicked(view: View) {
        inputEgyptMobileNumber(this@SampleMeezaQrPaymentActivity) { receiverId, dialog ->
            val request = MeezaPaymentRequest {
                this.receiverId = receiverId
                this.qrCodeMessage = sMeezaMessage!!
            }
            when (val result: GeideaResult<MeezaPaymentResponse> = PaymentIntentApi.sendMeezaRequestToPay(request)) {
                is GeideaResult.Success -> {
                    dialog.dismiss()
                }
                is GeideaResult.Error -> {
                    // Dialog is not dismissed on error
                    showObjectAsJson(result)
                }
                is GeideaResult.Cancelled -> {
                    dialog.dismiss()
                    snack("Cancelled")
                }
            }
        }
    }

    private fun inputEgyptMobileNumber(activity: Activity, block: suspend (normalizedReceiverId: String, DialogInterface) -> Unit) {
        // Inflate the custom view
        val binding = DialogMeezaPhoneBinding.inflate(activity.layoutInflater).apply {
            receiverIdEditText.setValidator(MeezaMobileNumberOrDigitalIdValidator)
            receiverIdEditText.setOnErrorListener(TextInputErrorListener(receiverIdInputLayout))
            receiverIdEditText.setOnInvalidStatusListener { value: String?, status: ValidationStatus.Invalid ->
                if (!value.isNullOrEmpty()) {
                    receiverIdEditText.updateErrorMessage()
                }
            }
        }

        val dialog = MaterialAlertDialogBuilder(activity)
                .setTitle("Receiver")
                .setView(binding.root)
                .setCancelable(false)
                .setPositiveButton("Send", null)
                .setNegativeButton(android.R.string.cancel, null)
                .create()

        dialog.show()

        dialog.getButton(BUTTON_POSITIVE).setOnClickListener {
            with(binding) {
                receiverIdEditText.updateErrorMessage()
                if (receiverIdEditText.validationStatus == ValidationStatus.Valid) {
                    val receiverId: String = MeezaMobileNumberOrDigitalIdValidator.getNormalizedReceiverId(receiverIdEditText.textOrNull)!!

                    // Temporarily change the UI to show the progress
                    progressLinearLayout.isVisible = true
                    receiverIdEditText.clearFocus()
                    receiverIdEditText.isEnabled = false
                    val positiveButton = dialog.getButton(BUTTON_POSITIVE).apply { isEnabled = false }
                    val negativeButton = dialog.getButton(BUTTON_NEGATIVE).apply { isEnabled = false }
                    hideKeyboard(binding.root)

                    lifecycleScope.launch {
                        // Do the network call and handle the response
                        block(receiverId, dialog)

                        // Re-enable the UI to accept input again. After an error user could retry.
                        receiverIdEditText.isEnabled = true
                        receiverIdEditText.requestFocus()
                        positiveButton.isEnabled = true
                        negativeButton.isEnabled = true
                        progressLinearLayout.isVisible = false
                    }
                }
            }
        }
    }

    private fun snack(message: CharSequence, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(binding.root, message, duration).show()
    }

    companion object {
        /**
         * Parameter of type [CreateMeezaPaymentIntentRequest], required.
         */
        private const val EXTRA_QR_REQUEST = "EXTRA_QR_REQUEST"

        // Statically cached data from the Meeza response, used to keep state across config changes.
        // In a real app those should be kept in the ViewModel.
        private var sCachedQrBitmapDrawable: BitmapDrawable? = null
        private var sMeezaMessage: String? = null
        private var sPaymentIntentId: String? = null

        fun createIntent(context: Context, request: CreateMeezaPaymentIntentRequest): Intent {
            return Intent(context, SampleMeezaQrPaymentActivity::class.java).apply {
                putExtra(EXTRA_QR_REQUEST, request)
            }
        }
    }
}