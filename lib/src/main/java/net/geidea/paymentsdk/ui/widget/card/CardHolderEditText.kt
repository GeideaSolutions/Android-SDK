package net.geidea.paymentsdk.ui.widget.card

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.ui.validation.card.validator.CardHolderValidator
import net.geidea.paymentsdk.ui.widget.FormEditText
import net.geidea.paymentsdk.ui.widget.TextWatcherAdapter

/**
 * Text input field for entering and validation of [card][net.geidea.paymentsdk.model.Card]
 * holder name.
 *
 * The length should be in 3..255.
 *
 * @see CardInputView
 */
open class CardHolderEditText @JvmOverloads constructor(
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
        filters += arrayOf(CardHolderValidator.cardHolderCharFilter)
    }
}