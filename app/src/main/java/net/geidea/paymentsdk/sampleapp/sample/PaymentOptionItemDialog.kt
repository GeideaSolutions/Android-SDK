package net.geidea.paymentsdk.sampleapp.sample

import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.sampleapp.R
import net.geidea.paymentsdk.sampleapp.customViewDialog
import net.geidea.paymentsdk.sampleapp.databinding.DialogPaymentOptionBinding
import net.geidea.paymentsdk.sampleapp.databinding.ItemPaymentOptionBinding
import net.geidea.paymentsdk.sampleapp.showErrorResult
import net.geidea.paymentsdk.sampleapp.textOrNull
import net.geidea.paymentsdk.ui.model.BnplPaymentMethodDescriptor
import net.geidea.paymentsdk.ui.model.PaymentMethodDescriptor

object PaymentOptionItemDialog {

    suspend fun FragmentActivity.choosePaymentOption(config: MerchantConfigurationResponse): PaymentOption? {
        return with(DialogPaymentOptionBinding.inflate(layoutInflater)) {
            val configPaymentMethods = config.paymentMethods.orEmpty()

            // Enable only the configured card brands
            getCardBrandCheckboxes().forEach { brandCheckBox ->
                // Each checkbox tag has its brand name hardcoded in the XML
                if (!configPaymentMethods.contains(brandCheckBox.tag)) {
                    brandCheckBox.text = "${brandCheckBox.text} (disabled)"
                }
                brandCheckBox.setOnCheckedChangeListener { _, _ ->
                    cardRadioButton.isChecked = true
                }
            }

            if (config.isMeezaQrEnabled != true) {
                meezaQrRadioButton.text = "${meezaQrRadioButton.text} (disabled)"
            }

            // BNPL

            if (config.isValuBnplEnabled != true) {
                valuRadioButton.text = "${valuRadioButton.text} (disabled)"
            }
            if (config.isShahryCnpBnplEnabled != true) {
                shahryRadioButton.text = "${shahryRadioButton.text} (disabled)"
            }
            if (config.isSouhoolaCnpBnplEnabled != true) {
                souhoolaRadioButton.text = "${souhoolaRadioButton.text} (disabled)"
            }

            try {
                customViewDialog(root, "Add payment option") {
                    val checkedRadioButtonId = paymentOptionsRadioGroup.checkedRadioButtonId

                    if (checkedRadioButtonId != -1) {
                        PaymentOption(
                            label = labelEditText.textOrNull,
                            paymentMethod = when (checkedRadioButtonId) {
                                R.id.cardRadioButton -> {
                                    PaymentMethodDescriptor.Card(
                                        acceptedBrands = getCardBrandCheckboxes()
                                            .filter { checkBox -> checkBox.isEnabled && checkBox.isChecked }
                                            .map { checkBox -> checkBox.tag as String }
                                            .map(CardBrand::fromBrandName)
                                            .toSet()
                                    )
                                }
                                R.id.meezaQrRadioButton -> {
                                    PaymentMethodDescriptor.MeezaQr
                                }
                                R.id.valuRadioButton -> {
                                    BnplPaymentMethodDescriptor.ValuInstallments
                                }
                                R.id.shahryRadioButton -> {
                                    BnplPaymentMethodDescriptor.ShahryInstallments
                                }
                                R.id.souhoolaRadioButton -> {
                                    BnplPaymentMethodDescriptor.SouhoolaInstallments
                                }
                                else -> error("Should not reach here")
                            }
                        )
                    } else {
                        // No option selected
                        null
                    }
                }
            } catch (e: Exception) {
                showErrorResult(e.message ?: "Error")
                null
            }
        }
    }

    private fun DialogPaymentOptionBinding.getCardBrandCheckboxes(): List<CheckBox> = listOf(
        visaCheckBox,
        mastercardCheckBox,
        americanExpressCheckBox,
        madaCheckBox,
        meezaCheckBox,
    )

    fun ItemPaymentOptionBinding.init(paymentOption: PaymentOption): ItemPaymentOptionBinding {
        if (root.tag !== paymentOption) {
            root.tag = paymentOption

            paymentMethodTextView.text = paymentOption.paymentMethod.name
                .replaceFirstChar { it.uppercase() }

            if (paymentOption.paymentMethod is PaymentMethodDescriptor.Card) {
                brandsTextView.text =
                    paymentOption.paymentMethod.acceptedBrands
                        .joinToString(separator = ", ", transform = CardBrand::name)
            }


            val hasLabel = paymentOption.label?.isNotBlank() ?: false
            labelTextView.text = paymentOption.label
            labelTextView.isVisible = hasLabel
            labelLabelTextView.isVisible = hasLabel

            deleteItemButton.setOnClickListener { deleteItem(root) }
        }

        return this
    }

    fun collectOptions(optionItemsViewGroup: ViewGroup): List<PaymentOption>  {
        // Gather the items which are already stored as tags in each of the item views
        return optionItemsViewGroup.children
                .map { view -> view.tag }
                .filterIsInstance<PaymentOption>()
                .toList()
    }

    private fun deleteItem(itemView: View) {
        (itemView.parent as ViewGroup).removeView(itemView)
    }
}

data class PaymentOption(
    val label: CharSequence? = null,
    val paymentMethod: PaymentMethodDescriptor,
)