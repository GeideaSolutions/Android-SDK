package net.geidea.paymentsdk.sampleapp.sample

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.ActivityResultLauncher
import androidx.core.os.bundleOf
import net.geidea.paymentsdk.flow.GeideaContract
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.pay.PaymentContract
import net.geidea.paymentsdk.flow.pay.PaymentIntent
import net.geidea.paymentsdk.model.InitiatingSource
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.model.Order
import net.geidea.paymentsdk.sampleapp.*
import net.geidea.paymentsdk.sampleapp.databinding.ActivitySampleCustomThemeBinding

class SampleThemeActivity : BaseSampleActivity<ActivitySampleCustomThemeBinding>() {

    private var savedInstanceState: Bundle? = null
    private lateinit var paymentIntent: PaymentIntent

    override fun createBinding(layoutInflater: LayoutInflater): ActivitySampleCustomThemeBinding {
        return ActivitySampleCustomThemeBinding.inflate(layoutInflater)
    }

    private lateinit var paymentLauncher: ActivityResultLauncher<PaymentIntent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.savedInstanceState = savedInstanceState

        paymentLauncher = registerForActivityResult(PaymentContract(), ::onOrderResult)

        with(binding) {
            setSupportActionBar(includeAppBar.toolbar)
            title = "Custom theme sample"

            payButton.setOnClickListener {
                paymentIntent = createPaymentIntent()
                paymentLauncher.launch(paymentIntent)
            }
        }
    }

    override fun setupUi(merchantConfig: MerchantConfigurationResponse) = with(binding) {
        paymentDetailsTextView.text = "Payment flow with custom theme"

        val paymentOperationsAdapter = DropDownAdapter(this@SampleThemeActivity, THEMES)
        themeAutoCompleteTextView.setAdapter(paymentOperationsAdapter)
        if (savedInstanceState == null) {
            themeAutoCompleteTextView.setText(THEMES[0].text, false)
        }
    }

    private fun createPaymentIntent(): PaymentIntent = with(binding) {
        return PaymentIntent {
            amount = "123.45".toBigDecimal()
            currency = "SAR"
            initiatedBy = InitiatingSource.INTERNET
            showCustomerEmail = true
            showAddress = true
            bundle = bundleOf(
                    GeideaContract.PARAM_THEME to THEMES.findValueByText(themeAutoCompleteTextView.textOrNull),
                    GeideaContract.PARAM_TITLE to screenTitleEditText.textOrNull
            )
        }
    }

    private fun onOrderResult(orderResult: GeideaResult<Order>) {
        // Not handled as the purpose of this sample is to just demonstrate theme customization
    }

    companion object {
        val THEMES = listOf(
                DropDownItem(R.style.Gd_Theme_DayNight_NoActionBar, "Gd.Theme.DayNight.NoActionBar"),
                DropDownItem(R.style.Theme_MaterialComponents_Light_NoActionBar, "Theme.MaterialComponents.Light.NoActionBar (test-only)"),
                DropDownItem(R.style.Theme_MaterialComponents_DayNight_NoActionBar, "Theme.MaterialComponents.DayNight.NoActionBar (test-only)"),
                DropDownItem(R.style.Theme_App_Rounded, "Theme.App.Rounded"),
                DropDownItem(R.style.Theme_App_Filled, "Theme.App.Filled"),
        )
    }
}