package net.geidea.paymentsdk.sampleapp.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.GeideaPaymentAPI
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.model.*
import net.geidea.paymentsdk.sampleapp.copyToClipboard
import net.geidea.paymentsdk.sampleapp.databinding.ActivitySampleEinvoiceBinding
import net.geidea.paymentsdk.sampleapp.sample.EInvoiceDialogs.inputCreateRequest
import net.geidea.paymentsdk.sampleapp.sample.EInvoiceDialogs.inputEInvoiceId
import net.geidea.paymentsdk.sampleapp.sample.EInvoiceDialogs.inputUpdateRequest
import net.geidea.paymentsdk.sampleapp.snack
import java.util.*

class SampleEInvoiceActivity : BaseSampleActivity<ActivitySampleEinvoiceBinding>() {

    private var eInvoice: EInvoice? = null

    override fun createBinding(layoutInflater: LayoutInflater): ActivitySampleEinvoiceBinding {
        return ActivitySampleEinvoiceBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(binding) {
            setSupportActionBar(includeAppBar.toolbar)
            title = "e-Invoice sample"
        }
    }

    override fun setupUi(merchantConfig: MerchantConfigurationResponse) = with(binding) {
        createEInvoiceButton.setOnClickListener(::onCreateEInvoiceButtonClicked)
        getEInvoiceButton.setOnClickListener(::onGetEInvoiceButtonClicked)
        updateEInvoiceButton.setOnClickListener(::onUpdateEInvoiceButtonClicked)
        deleteEInvoiceButton.setOnClickListener(::onDeleteEInvoiceButtonClicked)
    }

    // CRUD operations

    private fun onCreateEInvoiceButtonClicked(view: View) = with(binding) {
        lifecycleScope.launch {
            val request: CreateEInvoiceRequest? = inputCreateRequest()
            if (request != null) {
                GeideaPaymentAPI.createEInvoice(request).show()
            }
        }
    }

    private fun onGetEInvoiceButtonClicked(view: View) = with(binding) {
        lifecycleScope.launch {
            inputEInvoiceId(title = "Get", eInvoice?.eInvoiceId)?.let { eInvoiceId ->
                GeideaPaymentAPI.getEInvoice(eInvoiceId = eInvoiceId).show()
            }
        }
    }

    private fun onUpdateEInvoiceButtonClicked(view: View) = with(binding) {
        lifecycleScope.launch {
            val request: UpdateEInvoiceRequest? = inputUpdateRequest(eInvoice?.eInvoiceId)
            if (request != null) {
                GeideaPaymentAPI.updateEInvoice(request).show()
            }
        }
    }

    private fun onDeleteEInvoiceButtonClicked(view: View) = with(binding) {
        lifecycleScope.launch {
            inputEInvoiceId(title = "Delete", eInvoice?.eInvoiceId)?.let {
                GeideaPaymentAPI.deleteEInvoice(eInvoiceId = it).show()
            }
        }
    }

    private fun GeideaResult<EInvoiceResponse>.show() = with(binding) {
        val json = when (this@show) {
            is GeideaResult.Success<EInvoiceResponse> -> {
                eInvoice = data.eInvoice
                data.eInvoice?.eInvoiceId?.let { id ->
                    copyToClipboard(id, "eInvoiceId")
                    snack("eInvoiceId copied to clipboard.")
                }
                data.toJson(pretty = true)
            }
            is GeideaResult.Error -> {
                eInvoice = null
                snack("Operation failed!")
                toJson(pretty = true)
            }
            is GeideaResult.Cancelled -> {
                eInvoice = null
                toJson(pretty = true)
            }
        }
        scrollableJsonContainer.textView.setText(json)
    }
}