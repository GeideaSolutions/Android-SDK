package net.geidea.paymentsdk.sampleapp.sample.paymentintents

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.GeideaPaymentAPI
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.model.*
import net.geidea.paymentsdk.sampleapp.*
import net.geidea.paymentsdk.sampleapp.databinding.ActivitySamplePaymentIntentsBinding
import net.geidea.paymentsdk.sampleapp.sample.BaseSampleActivity
import net.geidea.paymentsdk.sampleapp.sample.PaymentIntentDialogs.inputCreateEInvoiceRequest
import net.geidea.paymentsdk.sampleapp.sample.PaymentIntentDialogs.inputMeezaCreateRequest
import net.geidea.paymentsdk.sampleapp.sample.PaymentIntentDialogs.inputUpdateEInvoiceRequest

class SamplePaymentIntentsActivity : BaseSampleActivity<ActivitySamplePaymentIntentsBinding>() {

    private var savedInstanceState: Bundle? = null

    override fun createBinding(layoutInflater: LayoutInflater): ActivitySamplePaymentIntentsBinding {
        return ActivitySamplePaymentIntentsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.savedInstanceState = savedInstanceState

        with(binding) {
            setSupportActionBar(includeAppBar.toolbar)
            title = "Payment intents sample"
        }
    }

    override fun setupUi(merchantConfig: MerchantConfigurationResponse) {
        with(binding) {
            createButton.setOnClickListener {
                paymentIntentIdInputLayout.isVisible = false

                val typeChoices = mutableListOf<CharSequence>()
                typeChoices.add("e-Invoice")
                if (merchantConfig.isMeezaQrEnabled == true) {
                    typeChoices.add("Meeza QR")
                }

                lifecycleScope.launch {
                    val choice = singleChoiceDialog(
                            title = "Payment Intent Type",
                            choices = typeChoices
                    )
                    when (choice) {
                        0 -> onCreateEInvoiceSelected()
                        1 -> onCreateMeezaQrSelected()
                    }
                }
            }

            updateButton.setOnClickListener {
                paymentIntentIdInputLayout.isVisible = true
            }

            getButton.setOnClickListener {
                paymentIntentIdInputLayout.isVisible = true
            }

            deleteButton.setOnClickListener {
                paymentIntentIdInputLayout.isVisible = true
            }

            goButton.setOnClickListener {
                val paymentIntentId = paymentIntentEditText.textOrNull
                if (paymentIntentId != null) {
                    val checkedButtonId = operationButtonToggleGroup.checkedButtonId
                    when (checkedButtonId) {
                        R.id.updateButton -> updatePaymentIntent(paymentIntentId)
                        R.id.getButton -> getPaymentIntent(paymentIntentId)
                        R.id.deleteButton -> deletePaymentIntent(paymentIntentId)
                        else -> {}
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_QR) {
            when (val result = data?.getParcelableExtra<GeideaResult<Order>>("result")) {
                is GeideaResult.Success<Order> -> showObjectAsJson(result.data)
                is GeideaResult.Error -> showObjectAsJson(result)
                is GeideaResult.Cancelled -> showObjectAsJson(result)
            }
        }
    }

    private fun onCreateMeezaQrSelected() {
        if (SampleApplication.INSTANCE.merchantConfiguration?.isMeezaQrEnabled == true)
            lifecycleScope.launch {
                val request: CreateMeezaPaymentIntentRequest? = inputMeezaCreateRequest()
                if (request != null) {
                    val intent = SampleMeezaQrPaymentActivity.createIntent(this@SamplePaymentIntentsActivity, request)
                    startActivityForResult(intent, REQUEST_CODE_QR)
                }
            } else {
            binding.snack("Meeza QR payments not enabled!")
        }
    }

    private fun onCreateEInvoiceSelected() {
        lifecycleScope.launch {
            val request: CreatePaymentIntentRequest? = inputCreateEInvoiceRequest()
            if (request != null) {
                onEInvoiceResult(GeideaPaymentAPI.createEInvoice(request))
            }
        }
    }

    private fun updatePaymentIntent(paymentIntentId: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            // Get the existing e-Invoice to prepopulate the input form with its data
            val eInvoiceResult: GeideaResult<EInvoiceOrdersResponse> = GeideaPaymentAPI.getEInvoice(paymentIntentId)
            if (eInvoiceResult is GeideaResult.Success) {
                val updateRequest: UpdateEInvoiceRequest? = inputUpdateEInvoiceRequest(eInvoiceResult.data.paymentIntent!!)
                if (updateRequest != null) {
                    onEInvoiceResult(GeideaPaymentAPI.updateEInvoice(updateRequest))
                }
            } else {
                onEInvoiceResult(eInvoiceResult)
            }
        }
    }

    private fun getPaymentIntent(paymentIntentId: String) {
        lifecycleScope.launch {
            onEInvoiceResult(GeideaPaymentAPI.getEInvoice(paymentIntentId))
        }
    }

    private fun deletePaymentIntent(paymentIntentId: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            onEInvoiceResult(GeideaPaymentAPI.deletePaymentIntent(paymentIntentId))
        }
    }

    private fun onEInvoiceResult(result: GeideaResult<EInvoiceOrdersResponse>) = with(binding) {
        when (result) {
            is GeideaResult.Success -> {
                val paymentIntent = result.data.paymentIntent
                if (paymentIntent != null) {
                    showEInvoice(paymentIntent, ::updatePaymentIntent, ::deletePaymentIntent)
                    paymentIntentEditText.setText(paymentIntent.paymentIntentId)
                } else {
                    showObjectAsJson(result.data)
                }
            }
            is GeideaResult.Error -> {
                showErrorResult(result.toJson(pretty = true))
                paymentIntentEditText.setText(null)
            }
            is GeideaResult.Cancelled -> {
                snack("Cancelled.")
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_QR = 9478
    }
}