package net.geidea.paymentsdk.sampleapp.sample

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import net.geidea.paymentsdk.model.Card
import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.sampleapp.databinding.ActivitySampleCardFieldsBinding
import net.geidea.paymentsdk.sampleapp.showObjectAsJson
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.card.validator.CardHolderValidator
import net.geidea.paymentsdk.ui.validation.card.validator.CvvValidator
import net.geidea.paymentsdk.ui.validation.card.validator.DefaultCardNumberValidator
import net.geidea.paymentsdk.ui.validation.card.validator.ExpiryDateValidator
import net.geidea.paymentsdk.ui.widget.TextInputErrorListener
import net.geidea.paymentsdk.ui.widget.card.CardExpiryDateEditText

class SampleCardFieldsActivity : BaseSampleActivity<ActivitySampleCardFieldsBinding>() {

    override fun createBinding(layoutInflater: LayoutInflater): ActivitySampleCardFieldsBinding {
        return ActivitySampleCardFieldsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(binding) {
            setSupportActionBar(includeAppBar.toolbar)
            title = "Card input fields sample"
        }
    }

    override fun setupUi(merchantConfig: MerchantConfigurationResponse) = with(binding) {

        // Card number

        val acceptedBrands = (merchantConfig.paymentMethods ?: emptySet())
                .map(CardBrand::fromBrandName)
                .toSet()
                .minus(CardBrand.Unknown)

        cardNumberInputLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
        val brandFilter = acceptedBrands::contains
        cardNumberEditText.setValidator(DefaultCardNumberValidator(brandFilter))
        cardNumberEditText.addOnCardBrandChangedListener { cardBrand ->
            cardNumberInputLayout.endIconDrawable = ContextCompat.getDrawable(this@SampleCardFieldsActivity, cardBrand.logo)
        }
        cardNumberEditText.setOnErrorListener(TextInputErrorListener(cardNumberInputLayout))
        cardNumberEditText.setOnValidStatusListener() { _ ->
            updateValidationStatus(cardNumberStatusTextView, ValidationStatus.Valid)
            cardExpiryDateEditText.requestFocus()
        }
        cardNumberEditText.setOnInvalidStatusListener { value: String?, status: ValidationStatus.Invalid ->
            // Show errors only when reached the maximal length
            if (value?.length == CardBrand.MAX_LENGTH_STANDARD) {
                cardNumberEditText.updateErrorMessage()
            }
            updateValidationStatus(cardNumberStatusTextView, status)
        }

        // Expiry date

        cardExpiryDateEditText.setValidator(ExpiryDateValidator)
        cardExpiryDateEditText.setOnErrorListener(TextInputErrorListener(cardExpiryDateInputLayout))
        cardExpiryDateEditText.setOnValidStatusListener() { _ ->
            updateValidationStatus(expiryStatusTextView, ValidationStatus.Valid)
            cardSecurityCodeEditText.requestFocus()
        }
        cardExpiryDateEditText.setOnInvalidStatusListener { value: String?, status: ValidationStatus.Invalid ->
            if (value != null && value.length == CardExpiryDateEditText.MAX_INPUT_LENGTH) {
                cardExpiryDateEditText.updateErrorMessage()
            }
            updateValidationStatus(expiryStatusTextView, status)
        }

        // CVV

        cardSecurityCodeEditText.setValidator(CvvValidator())
        cardNumberEditText.addOnCardBrandChangedListener(cardSecurityCodeEditText)
        cardSecurityCodeEditText.setOnErrorListener(TextInputErrorListener(cardSecurityCodeInputLayout))
        cardSecurityCodeEditText.setOnValidStatusListener {
            updateValidationStatus(cvvStatusTextView, ValidationStatus.Valid)
            cardHolderEditText.requestFocus()
        }
        cardSecurityCodeEditText.setOnInvalidStatusListener { value: String?, status: ValidationStatus.Invalid ->
            if (value != null && value.length == cardSecurityCodeEditText.maxLength) {
                cardSecurityCodeEditText.updateErrorMessage()
            }
            updateValidationStatus(cvvStatusTextView, status)
        }

        // Card holder

        cardHolderEditText.setValidator(CardHolderValidator)
        cardHolderEditText.setOnErrorListener(TextInputErrorListener(cardHolderInputLayout))
        cardHolderEditText.setOnValidStatusListener {
            updateValidationStatus(holderStatusTextView, ValidationStatus.Valid)
        }
        cardHolderEditText.setOnInvalidStatusListener { value: String?, status: ValidationStatus.Invalid ->
            if (!value.isNullOrEmpty()) {
                cardHolderEditText.updateErrorMessage()
            }
            updateValidationStatus(holderStatusTextView, status)
        }

        // Get card button

        getCardButton.setOnClickListener {
            showObjectAsJson(getCard()) {
                setTitle("Card (as JSON)")
            }
        }
    }

    private fun getCard(): Card = with(binding) {
        Card {
            cardNumber = cardNumberEditText.cardNumber
            expiryDate = cardExpiryDateEditText.expiryDate
            cvv = cardSecurityCodeEditText.text?.toString() ?: ""
            cardHolderName = cardHolderEditText.text?.toString() ?: ""
        }
    }

    /**
     * Function that displays a validation status in a TextView for test/demo/debug purposes.
     */
    @SuppressLint("SetTextI18n")
    private fun updateValidationStatus(statusTextView: TextView, status: ValidationStatus) = with(binding) {
        statusTextView.text = when (status) {
            ValidationStatus.Undefined -> "Undefined"
            ValidationStatus.Valid -> "Valid"
            is ValidationStatus.Invalid -> status.reason.javaClass.simpleName
        }

        getCardButton.isEnabled = allFieldsValid()
    }

    private fun allFieldsValid(): Boolean = with(binding) {
        return cardNumberEditText.isValid &&
                cardExpiryDateEditText.isValid &&
                cardSecurityCodeEditText.isValid &&
                cardHolderEditText.isValid
    }
}