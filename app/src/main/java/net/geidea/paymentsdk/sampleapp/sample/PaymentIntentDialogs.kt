package net.geidea.paymentsdk.sampleapp.sample

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.model.common.Source
import net.geidea.paymentsdk.model.meezaqr.CreateMeezaPaymentIntentRequest
import net.geidea.paymentsdk.model.paymentintent.*
import net.geidea.paymentsdk.sampleapp.*
import net.geidea.paymentsdk.sampleapp.databinding.DialogEinvoiceBinding
import net.geidea.paymentsdk.sampleapp.databinding.DialogEinvoiceItemBinding
import net.geidea.paymentsdk.sampleapp.databinding.DialogMeezaBinding
import net.geidea.paymentsdk.sampleapp.databinding.ItemEinvoiceItemBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object PaymentIntentDialogs {

    @SuppressLint("NewApi")
    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private val PRICE_FORMAT = DecimalFormat("#0.00")

    suspend fun FragmentActivity.inputCreateEInvoiceRequest(): CreatePaymentIntentRequest? {
        return with(DialogEinvoiceBinding.inflate(layoutInflater)) {
            paymentIntentIdInputLayout.isVisible = false
            statusInputLayout.isVisible = false
            amountInputLayout.hint = "${amountInputLayout.hint} *"        // Signify as required
            currencyInputLayout.hint = "${currencyInputLayout.hint} *"    // Signify as required
            setupFutureDateEditText(expiryDateEditText)
            setupFutureDateEditText(activationDateEditText)
            eInvoiceDetailsCheckBox.setOnCheckedChangeListener { _, isChecked ->
                eInvoiceDetailsLinearLayout.isVisible = isChecked
                addItemButton.isVisible = isChecked
            }
            addItemButton.setOnClickListener {
                lifecycleScope.launch {
                    val eInvoiceItem = inputEInvoiceItem()
                    if (eInvoiceItem != null) {
                        val itemBinding = ItemEinvoiceItemBinding
                                .inflate(layoutInflater)
                                .init(this@inputCreateEInvoiceRequest, eInvoiceItem)

                        eInvoiceItemsLinearLayout.addView(itemBinding.root)
                    }
                }
            }
            try {
                customViewDialog(root, "Create e-Invoice") {
                    CreateEInvoiceRequest {
                        amount = amountEditText.textOrNull?.toBigDecimal()
                        currency = currencyEditText.textOrNull
                        customer = CustomerRequest {
                            name = customerNameEditText.textOrNull
                            email = customerEmailEditText.textOrNull
                            phoneNumber = customerPhoneEditText.textOrNull
                        }
                        expiryDate = expiryDateEditText.textOrNull
                        activationDate = activationDateEditText.textOrNull

                        if (eInvoiceDetailsCheckBox.isChecked) {
                            eInvoiceDetails = EInvoiceDetails {
                                // Gather the items which are already stored as tags in each of the item views
                                val items: List<EInvoiceItem> = eInvoiceItemsLinearLayout.children
                                        .map { view -> view.tag }
                                        .filterIsInstance<EInvoiceItem>()
                                        .toList()
                                eInvoiceItems = items
                                merchantReferenceId = merchantReferenceIdEditText.textOrNull
                                subTotal = subTotalEditText.textOrNull?.toBigDecimal()
                                extraCharges = extraChargesEditText.textOrNull?.toBigDecimal()
                                if (extraCharges != null) {
                                    extraChargesType = extraChargesTypeEditText.textOrNull
                                }
                                chargeDescription = chargeDescriptionEditText.textOrNull
                                invoiceDiscount = discountEditText.textOrNull?.toBigDecimal()
                                if (invoiceDiscount != null) {
                                    invoiceDiscountType = discountTypeEditText.textOrNull
                                }
                                grandTotal = grandTotalEditText.textOrNull?.toBigDecimal()
                                collectCustomersBillingShippingAddress = collectBillingShippingAddressSwitch.isChecked
                                preAuthorizeAmount = preauthorizeAmountSwitch.isChecked
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                showErrorResult(e.message ?: "Error")
                null
            }
        }
    }

    suspend fun FragmentActivity.inputEInvoiceItem(existingItem: EInvoiceItem? = null): EInvoiceItem? {
        return with(DialogEinvoiceItemBinding.inflate(layoutInflater)) {
            // Prepopulate form
            existingItem?.apply {
                skuEditText.setText(sku)
                priceEditText.setText(price?.toString())
                quantityEditText.setText(quantity?.toString())
                discountEditText.setText(itemDiscount?.toString())
                discountTypeEditText.setText(itemDiscountType)
                taxEditText.setText(tax?.toString())
                taxTypeEditText.setText(taxType)
                totalEditText.setText(total?.toString())
                descriptionEditText.setText(description)
            }

            try {
                customViewDialog(root, "Create e-Invoice Item") {
                    EInvoiceItem {
                        sku = skuEditText.textOrNull
                        price = priceEditText.textOrNull?.toBigDecimal()
                        quantity = quantityEditText.textOrNull?.toInt()
                        itemDiscount = discountEditText.textOrNull?.toBigDecimal()
                        if (itemDiscount != null) {
                            itemDiscountType = discountTypeEditText.textOrNull
                        }
                        tax = taxEditText.textOrNull?.toBigDecimal()
                        if (tax != null) {
                            taxType = taxTypeEditText.textOrNull
                        }
                        total = totalEditText.textOrNull?.toBigDecimal()
                        description = descriptionEditText.textOrNull
                    }
                }
            } catch (e: Exception) {
                showErrorResult(e.message ?: "Error")
                null
            }
        }
    }

    suspend fun FragmentActivity.inputUpdateEInvoiceRequest(eInvoice: EInvoicePaymentIntent): UpdateEInvoiceRequest? {
        return with(DialogEinvoiceBinding.inflate(layoutInflater)) {
            // Prepopulate with data from last response
            paymentIntentIdEditText.setText(eInvoice.paymentIntentId)
            amountEditText.setText(eInvoice.amount?.toString())
            currencyEditText.setText(eInvoice.currency)
            expiryDateEditText.setText(eInvoice.expiryDate)
            activationDateEditText.setText(eInvoice.activationDate)
            customerNameEditText.setText(eInvoice.customer?.name)
            customerEmailEditText.setText(eInvoice.customer?.email)
            customerPhoneEditText.setText(eInvoice.customer?.phone)

            val isEInvoice = PaymentIntentType.EINVOICE == eInvoice.type
            val hasDetails = isEInvoice && eInvoice.eInvoiceDetails != null

            eInvoiceDetailsCheckBox.isVisible = isEInvoice
            eInvoiceDetailsCheckBox.isChecked = hasDetails
            eInvoiceDetailsCheckBox.setOnCheckedChangeListener { _, isChecked ->
                eInvoiceDetailsLinearLayout.isVisible = isChecked
                addItemButton.isVisible = isChecked
            }

            eInvoiceDetailsLinearLayout.isVisible = hasDetails
            addItemButton.isVisible = hasDetails
            addItemButton.setOnClickListener {
                lifecycleScope.launch {
                    val newItem = inputEInvoiceItem()
                    if (newItem != null) {
                        val itemBinding = ItemEinvoiceItemBinding
                                .inflate(layoutInflater)
                                .init(this@inputUpdateEInvoiceRequest, newItem)

                        eInvoiceItemsLinearLayout.addView(itemBinding.root)
                    }
                }
            }

            eInvoice.eInvoiceDetails?.apply {
                // Inflate, setup and add a view for each item
                eInvoiceItems
                        ?.map { item -> ItemEinvoiceItemBinding
                                .inflate(layoutInflater)
                                .init(this@inputUpdateEInvoiceRequest, item) }
                        ?.map { itemBinding -> itemBinding.root }
                        ?.forEach(eInvoiceItemsLinearLayout::addView)

                merchantReferenceIdEditText.setText(merchantReferenceId)
                subTotalEditText.setText(subTotal?.toString())
                chargeDescriptionEditText.setText(chargeDescription)
                extraChargesEditText.setText(extraCharges?.toString())
                extraChargesTypeEditText.setText(extraChargesType)
                discountEditText.setText(invoiceDiscount?.toString())
                discountTypeEditText.setText(invoiceDiscountType)
                grandTotalEditText.setText(grandTotal?.toString())
                collectBillingShippingAddressSwitch.isChecked = collectCustomersBillingShippingAddress == true
                preauthorizeAmountSwitch.isChecked = preAuthorizeAmount == true
            }

            setupFutureDateEditText(expiryDateEditText)
            setupFutureDateEditText(activationDateEditText)
            try {
                customViewDialog(root, "Update") {
                    UpdateEInvoiceRequest {
                        this.paymentIntentId = paymentIntentIdEditText.textOrNull
                        this.amount = amountEditText.textOrNull?.toBigDecimal()
                        this.currency = currencyEditText.textOrNull
                        this.customer = CustomerRequest {
                            this.name = customerNameEditText.textOrNull
                            this.email = customerEmailEditText.textOrNull
                            this.phoneNumber = customerPhoneEditText.textOrNull
                        }
                        this.expiryDate = expiryDateEditText.textOrNull
                        this.activationDate = activationDateEditText.textOrNull

                        if (eInvoiceDetailsCheckBox.isChecked) {
                            eInvoiceDetails = EInvoiceDetails {
                                // Gather the items which are already stored as tags in each of the item views
                                val items: List<EInvoiceItem> = eInvoiceItemsLinearLayout.children
                                        .map { view -> view.tag }
                                        .filterIsInstance<EInvoiceItem>()
                                        .toList()
                                eInvoiceItems = items
                                merchantReferenceId = merchantReferenceIdEditText.textOrNull
                                subTotal = subTotalEditText.textOrNull?.toBigDecimal()
                                extraCharges = extraChargesEditText.textOrNull?.toBigDecimal()
                                if (extraCharges != null) {
                                    extraChargesType = extraChargesTypeEditText.textOrNull
                                }
                                chargeDescription = chargeDescriptionEditText.textOrNull
                                invoiceDiscount = discountEditText.textOrNull?.toBigDecimal()
                                if (invoiceDiscount != null) {
                                    invoiceDiscountType = discountTypeEditText.textOrNull
                                }
                                grandTotal = grandTotalEditText.textOrNull?.toBigDecimal()
                                collectCustomersBillingShippingAddress = collectBillingShippingAddressSwitch.isChecked
                                preAuthorizeAmount = preauthorizeAmountSwitch.isChecked
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                showErrorResult(e.message ?: "Error")
                null
            }
        }
    }

    private fun ItemEinvoiceItemBinding.init(
            activity: FragmentActivity,
            eInvoiceItem: EInvoiceItem
    ): ItemEinvoiceItemBinding {
        if (root.tag !== eInvoiceItem) {
            root.tag = eInvoiceItem
            priceTextView.text = eInvoiceItem.price?.let(PRICE_FORMAT::format)
            quantityTextView.text = eInvoiceItem.quantity?.toString()
            descriptionTextView.text = eInvoiceItem.description

            editItemButton.setOnClickListener { activity.editItem(root) }
            deleteItemButton.setOnClickListener { deleteItem(root) }
        }

        return this
    }

    private fun FragmentActivity.editItem(itemView: View) {
        lifecycleScope.launch {
            val updatedItem = inputEInvoiceItem(itemView.tag as EInvoiceItem)
            if (updatedItem != null) {
                ItemEinvoiceItemBinding
                        .bind(itemView)
                        .init(this@editItem, updatedItem)
            }
        }
    }

    private fun deleteItem(itemView: View) {
        (itemView.parent as ViewGroup).removeView(itemView)
    }

    /**
     * Show an input dialog that collects data to populate a [CreateMeezaPaymentIntentRequest].
     */
    suspend fun FragmentActivity.inputMeezaCreateRequest(): CreateMeezaPaymentIntentRequest? {
        return with(DialogMeezaBinding.inflate(layoutInflater)) {
            setupFutureDateEditText(expiryDateEditText)
            setupFutureDateEditText(activationDateEditText)
            try {
                customViewDialog(root, "Create Meeza QR code") {
                    CreateMeezaPaymentIntentRequest {
                        amount = amountEditText.textOrNull?.toBigDecimal()
                        currency = currencyEditText.textOrNull
                        customer = CustomerRequest {
                            name = customerNameEditText.textOrNull
                            email = customerEmailEditText.textOrNull
                            phoneNumber = customerPhoneEditText.textOrNull
                        }
                        expiryDate = expiryDateEditText.textOrNull
                        activationDate = activationDateEditText.textOrNull
                        merchantPublicKey = merchantPublicKeyEditText.textOrNull
                        source = Source.MOBILE_APP
                    }
                }
            } catch (e: Exception) {
                showErrorResult(e.message ?: "Error")
                null
            }
        }
    }

    private fun FragmentActivity.setupFutureDateEditText(dateEditText: EditText) {
        dateEditText.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder
                    .datePicker()
                    .setSelection(getNextMonthUtc())
                    .setCalendarConstraints(validUntil(Calendar.YEAR, 5))
                    .build()
            datePicker.addOnPositiveButtonClickListener {
                dateEditText.setText(DATE_FORMAT.format(datePicker.selection))
            }
            datePicker.show(supportFragmentManager, datePicker.toString())
        }
    }
}