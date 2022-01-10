package net.geidea.paymentsdk.sampleapp.sample

import android.os.Bundle
import android.view.LayoutInflater
import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.sampleapp.databinding.ActivitySampleCardBinding
import net.geidea.paymentsdk.sampleapp.showObjectAsJson
import net.geidea.paymentsdk.sampleapp.snack
import net.geidea.paymentsdk.ui.widget.card.CardFieldType
import net.geidea.paymentsdk.ui.widget.card.CardInputAdapter

class SampleCardActivity : BaseSampleActivity<ActivitySampleCardBinding>() {

    override fun createBinding(layoutInflater: LayoutInflater): ActivitySampleCardBinding {
        return ActivitySampleCardBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(binding) {
            setSupportActionBar(includeAppBar.toolbar)
            title = "CardInputView sample"
        }
    }

    override fun setupUi(merchantConfig: MerchantConfigurationResponse) = with(binding) {
        // Setup filtering for card brands
        val acceptedCardBrands = (merchantConfig.paymentMethods ?: emptySet())
                .map(CardBrand::fromBrandName)
                .toSet()
                .minus(CardBrand.Unknown)
        cardInputView.setCardBrandFilter(acceptedCardBrands::contains)
        cardInputView.addOnCardBrandChangedListener { cardBrand ->
            if (cardBrand != CardBrand.Unknown) {
                snack("Card brand recognized: ${cardBrand.name}")
            }
        }
        cardInputView.addCardInputListener(object : CardInputAdapter() {
            override fun onFocusChange(focusField: CardFieldType) {
                snack("Focus changed to $focusField")
            }

            override fun onCardValidationChanged(valid: Boolean) {
                getCardButton.isEnabled = valid
            }

            override fun onCardInputComplete() {
                snack("Card input complete")
            }
        })

        cardInputView.setSecurityCodeEndIconClickListener {
            snack("CVV end icon clicked")
        }

        clearButton.setOnClickListener {
            cardInputView.clear()
            updateButtons()
        }

        enableButton.setOnClickListener {
            cardInputView.isEnabled = !cardInputView.isEnabled
            updateButtons()
        }

        getCardButton.setOnClickListener {
            updateButtons()
            showObjectAsJson(requireNotNull(cardInputView.card)) {
                setTitle("Card (as JSON)")
            }
        }

        updateButtons()
    }

    private fun updateButtons() = with(binding) {
        enableButton.text = if (cardInputView.isEnabled) "Disable" else "Enable"
        getCardButton.isEnabled = cardInputView.isValid
    }
}