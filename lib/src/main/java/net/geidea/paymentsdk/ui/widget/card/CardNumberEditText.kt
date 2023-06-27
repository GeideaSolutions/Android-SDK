package net.geidea.paymentsdk.ui.widget.card

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.model.CardBrand.Companion.fromCardNumberPrefix
import net.geidea.paymentsdk.model.CardBrand.Companion.possibleBrands
import net.geidea.paymentsdk.ui.widget.FormEditText

/**
 * Text input field for [card][net.geidea.paymentsdk.model.Card] number.
 *
 * @see CardInputView
 */
open class CardNumberEditText @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = R.attr.editTextStyle
) : FormEditText(context, attrs, defStyleAttr) {

    private var maxLength: Int = CardBrand.MAX_LENGTH_STANDARD
    private val textWatcher = NumberTextWatcher()

    /**
     * Returns the recognized card brand or [CardBrand.Unknown] if the CardBrand cannot be
     * recognized or the number is invalid.
     */
    var cardBrand: CardBrand = CardBrand.Unknown
        private set

    /**
     * Returns the card number or null if invalid.
     *
     * Note: Returns only the digits cleared of separators and whitespace.
     */
    val cardNumber: String?
        get() {
            return if (isValid)
                CardBrand.removeCardNumberSpaces(text?.toString() ?: "")
            else
                null
        }

    private var onCardBrandChangedListeners = mutableListOf<OnCardBrandChangedListener>()

    init {
        textAlignment = TEXT_ALIGNMENT_VIEW_START
        textDirection = TEXT_DIRECTION_LTR
        filters += arrayOf(InputFilter.LengthFilter(maxLength))
        addTextChangedListener(textWatcher)
    }

    /**
     * Add a listener to be notified when card brand is recognized.
     */
    fun addOnCardBrandChangedListener(listener: OnCardBrandChangedListener) {
        if (!onCardBrandChangedListeners.contains(listener)) {
            onCardBrandChangedListeners.add(listener)
            // Immediately notify the new listener in case a brand is already known
            listener.onCardBrandChanged(cardBrand)
        }
    }

    /**
     * Remove a card brand change listener.
     */
    fun removeOnCardBrandChangedListener(listener: OnCardBrandChangedListener) {
        onCardBrandChangedListeners.remove(listener)
    }

    private fun updateCardBrand(cardBrand: CardBrand) {
        if (this.cardBrand != cardBrand) {
            this.cardBrand = cardBrand
            onCardBrandChangedListeners.forEach { it.onCardBrandChanged(cardBrand) }

            val oldLength = maxLength
            if (oldLength != maxLength) {
                // Update max length for the new brand
                maxLength = cardBrand.maxLength
                updateLengthFilter()
            }
        }
    }

    private fun updateCardBrandFrom(partialCardNumber: String) {
        val possibleBrands = possibleBrands(partialCardNumber)
        val brand = if (possibleBrands.size == 1) {
            fromCardNumberPrefix(partialCardNumber)
        } else {
            CardBrand.Unknown
        }

        updateCardBrand(brand)
    }

    private fun updateLengthFilter() {
        filters = arrayOf(InputFilter.LengthFilter(maxLength))
    }

    private fun format(cardNumber: String): String {
        return cardBrand.separateCardNumber(CardBrand.removeCardNumberSpaces(cardNumber))
    }

    /**
     * Calculates the new cursor position (selection index) after a text change.
     */
    private fun updateSelectionIndex(newLength: Int, start: Int, addition: Int): Int {
        var newPosition: Int
        var gapsJumped = 0
        val gapPositions = cardBrand.gapPositions
        var skipBack = false
        for (gap in gapPositions) {
            if (start <= gap && start + addition > gap) {
                gapsJumped++
            }

            if (addition == 0 && start == gap + 1) {
                skipBack = true
            }
        }

        newPosition = start + addition + gapsJumped
        if (skipBack && newPosition > 0) {
            newPosition--
        }

        return if (newPosition <= newLength) newPosition else newLength
    }

    private inner class NumberTextWatcher : TextWatcher {

        private var textChangeInProgress = false
        private var latestChangeStart: Int = 0
        private var latestInsertionSize: Int = 0

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            if (!textChangeInProgress) {
                latestChangeStart = start
                latestInsertionSize = after
            }
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (textChangeInProgress) {
                return
            }

            if (start < CardBrand.LONGEST_PREFIX + 1) {
                updateCardBrandFrom(s.toString())
            }

            // Beyond maximal length, no need to format
            if (start > 16) {
                return
            }

            if (s.isBlank()) {
                return
            }

            val formatted = format(s.toString())
            val cursorPosition = updateSelectionIndex(
                    formatted.length,
                    latestChangeStart,
                    latestInsertionSize
            )

            if (formatted != text.toString()) {
                textChangeInProgress = true
                setText(formatted)
                setSelection(cursorPosition)
                textChangeInProgress = false
            }
        }

        override fun afterTextChanged(editable: Editable) {
            validate()
        }
    }
}