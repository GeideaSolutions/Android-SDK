package net.geidea.paymentsdk.sampleapp.sample

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.ActivityResultLauncher
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.pay.PaymentContract
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.model.order.Order
import net.geidea.paymentsdk.sampleapp.databinding.ActivitySampleSimplestPaymentBinding
import net.geidea.paymentsdk.sampleapp.snack

class SampleSimplestPaymentActivity : BaseSampleActivity<ActivitySampleSimplestPaymentBinding>() {

    private lateinit var paymentData: PaymentData

    override fun createBinding(layoutInflater: LayoutInflater): ActivitySampleSimplestPaymentBinding {
        return ActivitySampleSimplestPaymentBinding.inflate(layoutInflater)
    }

    private lateinit var paymentLauncher: ActivityResultLauncher<PaymentData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        paymentLauncher = registerForActivityResult(PaymentContract(), ::onPaymentResult)

        with(binding) {
            setSupportActionBar(includeAppBar.toolbar)
            title = "Simplest payment sample"
        }

        paymentData = createPaymentData()
    }

    @SuppressLint("SetTextI18n")
    override fun setupUi(merchantConfig: MerchantConfigurationResponse) = with(binding) {
        paymentDetailsTextView.text = """PaymentIntent {
        |    amount = "123.45"
        |    currency = "SAR"
        |}""".trimMargin()

        payButton.setOnClickListener {
            paymentLauncher.launch(paymentData)
        }
    }

    private fun createPaymentData(): PaymentData {
        return PaymentData {
            amount = "123.45".toBigDecimal()
            currency = "SAR"
        }
    }

    private fun onPaymentResult(paymentResult: GeideaResult<Order>) = with (binding) {
        when (paymentResult) {
            is GeideaResult.Success<*> -> snack("Success")
            is GeideaResult.Error -> snack("Error")
            is GeideaResult.Cancelled -> snack("Cancelled")
        }
    }
}