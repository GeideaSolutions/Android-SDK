package net.geidea.paymentsdk.ui.widget.card

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.util.AttributeSet
import android.util.LayoutDirection
import android.view.Gravity
import androidx.core.text.layoutDirection
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.ui.validation.card.validator.CvvFilter
import net.geidea.paymentsdk.ui.widget.FormEditText
import net.geidea.paymentsdk.ui.widget.TextWatcherAdapter
import java.util.*

/**
 * Text input field for [card][net.geidea.paymentsdk.model.Card] security code (CVV).
 *
 * @see CardInputView
 */
open class CardSecurityCodeEditText @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = R.attr.editTextStyle
) : FormEditText(context, attrs, defStyleAttr), OnCardBrandChangedListener {

    /**
     * Returns the currently set length limit. Depending on the card brand it should be set to 3 or 4.
     */
    var maxLength: Int = CardBrand.Unknown.securityCodeLengthRange.last
        internal set(value) {
            require(value in 3..4)
            field = value
            filters = arrayOf(CvvFilter, InputFilter.LengthFilter(value))
        }

    init {
        gravity = if (Locale.getDefault().layoutDirection == LayoutDirection.RTL)
            Gravity.END
        else
            Gravity.START

        textAlignment = TEXT_ALIGNMENT_VIEW_START
        textDirection = TEXT_DIRECTION_LTR
        addTextChangedListener(NumberTextWatcher())
    }

    override fun onCardBrandChanged(cardBrand: CardBrand) {
        maxLength = cardBrand.securityCodeLengthRange.last
        (_validator as? OnCardBrandChangedListener)?.also {
            it.onCardBrandChanged(cardBrand)
        }
        validate()
        if (!text?.toString().isNullOrEmpty()) {
            updateErrorMessage()
        }
    }

    private inner class NumberTextWatcher : TextWatcherAdapter() {
        override fun afterTextChanged(editable: Editable) {
            validate()
        }
    }
}