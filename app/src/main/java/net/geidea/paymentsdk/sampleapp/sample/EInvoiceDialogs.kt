package net.geidea.paymentsdk.sampleapp.sample

import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import net.geidea.paymentsdk.model.CreateEInvoiceRequest
import net.geidea.paymentsdk.model.CustomerRequest
import net.geidea.paymentsdk.model.UpdateEInvoiceRequest
import net.geidea.paymentsdk.sampleapp.customViewDialog
import net.geidea.paymentsdk.sampleapp.databinding.DialogEinvoiceBinding
import net.geidea.paymentsdk.sampleapp.getNextMonthUtc
import net.geidea.paymentsdk.sampleapp.showErrorResult
import net.geidea.paymentsdk.sampleapp.textOrNull
import java.text.SimpleDateFormat

object EInvoiceDialogs {

    private val EXPIRY_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")

    suspend fun FragmentActivity.inputCreateRequest(): CreateEInvoiceRequest? {
        return with(DialogEinvoiceBinding.inflate(layoutInflater)) {
            eInvoiceIdInputLayout.isVisible = false
            amountInputLayout.hint = "${amountInputLayout.hint} *"        // Signify as required
            currencyInputLayout.hint = "${currencyInputLayout.hint} *"    // Signify as required
            setupFutureDateEditText(expiryDateEditText)
            try {
                customViewDialog(root, "Create e-Invoice") {
                    CreateEInvoiceRequest {
                        amount = amountEditText.textOrNull?.toBigDecimal()
                        currency = currencyEditText.textOrNull
                        customer = CustomerRequest {
                            name = customerNameEditText.textOrNull
                            email = customerEmailEditText.textOrNull
                            phone = customerPhoneEditText.textOrNull
                        }
                        expiryDate = expiryDateEditText.textOrNull
                    }
                }
            } catch (e: Exception) {
                showErrorResult(e.message ?: "Error")
                null
            }
        }
    }

    suspend fun FragmentActivity.inputEInvoiceId(title: CharSequence, eInvoiceId: String? = null): String? {
        return with(DialogEinvoiceBinding.inflate(layoutInflater)) {
            eInvoiceIdEditText.setText(eInvoiceId)   // Prepopulate with the id from last response
            amountInputLayout.isVisible = false
            currencyInputLayout.isVisible = false
            customerLinearLayout.isVisible = false
            expiryDateInputLayout.isVisible = false
            try {
                customViewDialog(root, title) {
                    requireNotNull(eInvoiceIdEditText.textOrNull) { "Missing eInvoiceId" }
                }
            } catch (e: Exception) {
                showErrorResult(e.message ?: "Error")
                null
            }
        }
    }

    suspend fun FragmentActivity.inputUpdateRequest(eInvoiceId: String? = null): UpdateEInvoiceRequest? {
        return with(DialogEinvoiceBinding.inflate(layoutInflater)) {
            eInvoiceIdEditText.setText(eInvoiceId)   // Prepopulate with the id from last response
            setupFutureDateEditText(expiryDateEditText)
            try {
                customViewDialog(root, "Update") {
                    UpdateEInvoiceRequest {
                        this.eInvoiceId = eInvoiceIdEditText.textOrNull
                        this.amount = amountEditText.textOrNull?.toBigDecimal()
                        this.currency = currencyEditText.textOrNull
                        this.customer = CustomerRequest {
                            this.name = customerNameEditText.textOrNull
                            this.email = customerEmailEditText.textOrNull
                            this.phone = customerPhoneEditText.textOrNull
                        }
                        this.expiryDate = expiryDateEditText.textOrNull
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
                    .setCalendarConstraints(CalendarConstraints.Builder()
                            .setValidator(DateValidatorPointForward.now())
                            .build()
                    )
                    .build()
            datePicker.addOnPositiveButtonClickListener {
                dateEditText.setText(EXPIRY_DATE_FORMAT.format(datePicker.selection))
            }
            datePicker.show(supportFragmentManager, datePicker.toString())
        }
    }
}