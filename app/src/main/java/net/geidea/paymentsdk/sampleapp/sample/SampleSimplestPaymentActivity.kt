package net.geidea.paymentsdk.sampleapp.sample

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.ActivityResultLauncher
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.pay.PaymentContract
import net.geidea.paymentsdk.flow.pay.PaymentIntent
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.model.Order
import net.geidea.paymentsdk.sampleapp.databinding.ActivitySampleSimplestPaymentBinding
import net.geidea.paymentsdk.sampleapp.snack

class SampleSimplestPaymentActivity : BaseSampleActivity<ActivitySampleSimplestPaymentBinding>() {

    private lateinit var paymentIntent: PaymentIntent

    override fun createBinding(layoutInflater: LayoutInflater): ActivitySampleSimplestPaymentBinding {
        return ActivitySampleSimplestPaymentBinding.inflate(layoutInflater)
    }

    private lateinit var paymentLauncher: ActivityResultLauncher<PaymentIntent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        paymentLauncher = registerForActivityResult(PaymentContract(), ::onOrderResult)

        with(binding) {
            setSupportActionBar(includeAppBar.toolbar)
            title = "Simplest payment sample"
        }

        paymentIntent = createPaymentIntent()
    }

    @SuppressLint("SetTextI18n")
    override fun setupUi(merchantConfig: MerchantConfigurationResponse) = with(binding) {
        paymentDetailsTextView.text = """PaymentIntent {
        |    amount = "123.45"
        |    currency = "SAR"
        |}""".trimMargin()

        payButton.setOnClickListener {
            paymentLauncher.launch(paymentIntent)
        }
    }

    private fun createPaymentIntent(): PaymentIntent {
        return PaymentIntent {
            amount = "123.45".toBigDecimal()
            currency = "SAR"
        }
    }

    private fun onOrderResult(orderResult: GeideaResult<Order>) = with (binding) {
        when (orderResult) {
            is GeideaResult.Success<*> -> snack("Success")
            is GeideaResult.Error -> snack("Error")
            is GeideaResult.Cancelled -> snack("Cancelled")
        }
    }
}