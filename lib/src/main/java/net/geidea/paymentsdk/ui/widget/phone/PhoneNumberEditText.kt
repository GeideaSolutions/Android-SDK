package net.geidea.paymentsdk.ui.widget.phone

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.ui.widget.FormEditText
import net.geidea.paymentsdk.ui.widget.TextWatcherAdapter

/**
 * Text input field for entering a phone number.
 */
class PhoneNumberEditText @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = R.attr.editTextStyle
) : FormEditText(context, attrs, defStyleAttr) {

    init {
        addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable) {
                validate()
            }
        })
    }
}