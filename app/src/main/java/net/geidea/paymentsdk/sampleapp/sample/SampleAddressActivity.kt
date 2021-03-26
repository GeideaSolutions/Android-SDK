package net.geidea.paymentsdk.sampleapp.sample

import android.os.Bundle
import android.view.LayoutInflater
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.sampleapp.databinding.ActivitySampleAddressBinding
import net.geidea.paymentsdk.sampleapp.showObjectAsJson
import net.geidea.paymentsdk.sampleapp.snack
import net.geidea.paymentsdk.ui.widget.address.AddressFieldType
import net.geidea.paymentsdk.ui.widget.address.AddressInputAdapter
import net.geidea.paymentsdk.ui.widget.address.DefaultCountryDropDownAdapter

class SampleAddressActivity : BaseSampleActivity<ActivitySampleAddressBinding>() {

    override fun createBinding(layoutInflater: LayoutInflater): ActivitySampleAddressBinding {
        return ActivitySampleAddressBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(binding) {
            setSupportActionBar(includeAppBar.toolbar)
            title = "AddressInputView sample"
        }
    }

    override fun setupUi(merchantConfig: MerchantConfigurationResponse) = with(binding) {
        val countryList = merchantConfig.countries?.toList() ?: emptyList()
        val countriesAdapter = DefaultCountryDropDownAdapter(this@SampleAddressActivity, countryList)
        addressInputView.setCountryDropDownAdapter(countriesAdapter)

        addressInputView.setAddressInputListener(object : AddressInputAdapter() {
            override fun onFocusChange(focusField: AddressFieldType) {
                snack("Focus changed to $focusField")
            }
        })

        clearButton.setOnClickListener {
            addressInputView.clear()
            updateButtons()
        }

        enableButton.setOnClickListener {
            addressInputView.isEnabled = !addressInputView.isEnabled
            updateButtons()
        }

        getAddressButton.setOnClickListener {
            updateButtons()
            showObjectAsJson(requireNotNull(addressInputView.address)) {
                setTitle("Address (as JSON)")
            }
        }

        updateButtons()
    }

    private fun updateButtons() = with(binding) {
        enableButton.text = if (addressInputView.isEnabled) "Disable" else "Enable"
        getAddressButton.isEnabled = addressInputView.isValid
    }
}