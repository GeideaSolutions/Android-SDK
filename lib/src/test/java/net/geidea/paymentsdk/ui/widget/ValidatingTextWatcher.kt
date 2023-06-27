package net.geidea.paymentsdk.ui.widget

import android.text.Editable

class ValidatingTextWatcher(private val formEditText: FormEditText) : TextWatcherAdapter() {
    override fun afterTextChanged(s: Editable) {
        formEditText.validate()
    }
}