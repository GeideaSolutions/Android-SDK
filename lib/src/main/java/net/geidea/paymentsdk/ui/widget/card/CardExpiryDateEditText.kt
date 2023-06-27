package net.geidea.paymentsdk.ui.widget.card

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.autofill.AutofillValue
import androidx.annotation.RequiresApi
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.internal.util.Validations
import net.geidea.paymentsdk.model.ExpiryDate
import net.geidea.paymentsdk.ui.widget.FormEditText
import java.util.*
import kotlin.math.min

/**
 * Text input field for entering and validation of [card][net.geidea.paymentsdk.model.Card] expiry date.
 *
 * The expiry is entered as a 2-digit numbers and adding the '/' separator is done automatically.
 *
 * @see ExpiryDate
 * @see CardInputView
 */
open class CardExpiryDateEditText @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = R.attr.editTextStyle
) : FormEditText(context, attrs, defStyleAttr) {

    /**
     * Returns the current input as [ExpiryDate] instance. Month number is in the range [1-12]. Year
     * is 2-digit in the range of [1..99].
     */
    var expiryDate: ExpiryDate?
        get() = ExpiryDate.fromString(text?.toString() ?: "")
        set(value) {
            val newText = value?.toDisplayString() ?: ""
            if (text?.toString() ?: "" != newText) {
                setText(newText)
            }
        }

    init {
        textAlignment = TEXT_ALIGNMENT_VIEW_START
        textDirection = TEXT_DIRECTION_LTR
        addTextChangedListener(CardTextWatcher())
    }

    internal fun updateSelectionIndex(newLength: Int, start: Int, addition: Int, maxInputLength: Int): Int {
        var newPosition: Int
        var gapsJumped = 0

        var skipBack = false
        if (start <= 2 && start + addition >= 2) {
            gapsJumped = 1
        }

        if (addition == 0 && start == 3) {
            skipBack = true
        }

        newPosition = start + addition + gapsJumped
        if (skipBack && newPosition > 0) {
            newPosition--
        }
        val unTruncatedPosition = if (newPosition <= newLength) newPosition else newLength
        return min(maxInputLength, unTruncatedPosition)
    }

    private inner class CardTextWatcher : TextWatcher {
        var parts = arrayOf("", "")
        var textChangeInProgress = false
        var latestChangeStart: Int = 0
        var latestInsertionSize: Int = 0

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            if (textChangeInProgress) {
                return
            }
            latestChangeStart = start
            latestInsertionSize = after
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (textChangeInProgress) {
                return
            }

            var inErrorState = false

            var notSeparated = Validations.EXPIRY_SEPARATOR_REGEX.replace(s, "")

            if (notSeparated.length == 1
                    && latestChangeStart == 0
                    && latestInsertionSize == 1
            ) {
                val first = notSeparated[0]
                if (!(first == '0' || first == '1')) {
                    notSeparated = "0$notSeparated"
                    latestInsertionSize++
                }
            } else if (notSeparated.length == 2
                    && latestChangeStart == 2
                    && latestInsertionSize == 0
            ) {
                notSeparated = notSeparated.substring(0, 1)
            }

            parts = Validations.splitExpiryDate(notSeparated)

            if (!Validations.validateExpiryMonth(parts[0])) {
                inErrorState = true
            }

            val formattedDateBuilder = StringBuilder()
            formattedDateBuilder.append(parts[0])
            if (parts[0].length == 2 && latestInsertionSize > 0 && !inErrorState || notSeparated.length > 2) {
                formattedDateBuilder.append("/")
            }
            formattedDateBuilder.append(parts[1])

            val formattedDate = formattedDateBuilder.toString()
            val cursorPosition = updateSelectionIndex(
                    formattedDate.length,
                    latestChangeStart,
                    latestInsertionSize,
                    MAX_INPUT_LENGTH
            )

            textChangeInProgress = true
            setText(formattedDate)
            setSelection(cursorPosition)
            textChangeInProgress = false
        }

        override fun afterTextChanged(s: Editable?) {
            validate()
        }
    }

    // Autofill support

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAutofillType(): Int {
        return AUTOFILL_TYPE_DATE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAutofillValue(): AutofillValue? {
        return expiryDate?.let {
            AutofillValue.forDate(it.toTimeMillis())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun autofill(value: AutofillValue?) {
        // TODO attempt to consume type "text"?
        if (value == null || !value.isDate) {
            return
        }
        val time: Long = value.dateValue
        val tempCalendar = Calendar.getInstance().apply { timeInMillis = time }
        var year: Int = tempCalendar.get(Calendar.YEAR)
        if (year > 2000) {
            year -= 2000
        }
        val month: Int = tempCalendar.get(Calendar.MONTH) + 1
        expiryDate = ExpiryDate(month = month, year = year)
    }

    override fun getAutofillHints(): Array<String> {
        return AUTOFILL_HINT
    }

    companion object {
        const val MAX_INPUT_LENGTH = 5
        private val AUTOFILL_HINT = arrayOf(AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DATE)
    }
}