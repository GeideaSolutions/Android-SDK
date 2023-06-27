package net.geidea.paymentsdk.ui.widget.otp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.TypedArray
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.KEYCODE_DEL
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.view.inputmethod.EditorInfo.IME_ACTION_NEXT
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.theme.overlay.MaterialThemeOverlay
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.internal.util.dp
import net.geidea.paymentsdk.internal.util.hideKeyboard
import net.geidea.paymentsdk.ui.validation.card.validator.AlphanumericFilter
import net.geidea.paymentsdk.ui.widget.TextWatcherAdapter


/**
 * A linear layout that manages the input of one-time password (OTP). Default length is six.
 * It consists of single-character [TextInputEditText]s laid out horizontally which are acting as one
 * text input field.
 * Jumping to next or previous character input field on input or back-press
 * is handled automatically. Pasting is supported on each of the edit fields and it will result in
 * pasting the clipboard text on the entire sequence of character input fields.
 */
internal open class OtpInputView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : LinearLayout(MaterialThemeOverlay.wrap(context, attrs, defStyleAttr, defStyleRes), attrs, defStyleAttr, defStyleRes) {

    private var isLengthSet = false

    /**
     * The expected length of the [otp]. Value in 3..12.
     */
    var expectedLength: Int = DefaultOtpLength
        private set(value) {
            require(!isLengthSet) { "OTP length already set and cannot be changed" }
            require(value in 3..12) { "OTP length must be in 3..12" }
            field = value
            isLengthSet = true
        }

    init {
        layoutDirection = LAYOUT_DIRECTION_LTR
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.OtpInputView)
        expectedLength = a.getInt(R.styleable.OtpInputView_gd_expectedLength, DefaultOtpLength)
        a.recycle()
    }

    private val charEditTexts: List<SingleCharEditText> =
            (0 until expectedLength).map { position ->
                val materialContext = MaterialThemeOverlay.wrap(context, attrs, defStyleAttr, defStyleRes)
                SingleCharEditText(materialContext).apply {
                    layoutParams = LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                        if (position < expectedLength - 1) {
                            marginEnd = 8.dp
                        }
                    }
                    gravity = Gravity.CENTER
                    setSingleLine()
                    maxLines = 1
                    filters = arrayOf(
                            InputFilter.LengthFilter(1),
                            AlphanumericFilter
                    )
                    inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                    val isLast = position == expectedLength - 1
                    imeOptions = if (isLast) IME_ACTION_DONE else IME_ACTION_NEXT

                    // TODO Customizable attributes
                    //isAllCaps = true
                    textSize = 24f
                    background = AppCompatResources.getDrawable(materialContext, R.drawable.gd_rectangle_rounded_8dp)
                    //isCursorVisible = false
                }
            }

    init {
        orientation = HORIZONTAL
        charEditTexts.forEachIndexed { position, editText ->
            if (position < charEditTexts.size - 1) {
                val nextEditText = charEditTexts[position + 1]
                editText.addTextChangedListener(IfNonEmptyJumpToNext(nextEditText))
            }
            editText.addTextChangedListener(NotifyingTextWatcher())

            val previousEditText: EditText? = if (position > 0) charEditTexts[position - 1] else null
            editText.setOnKeyListener(IfEmptyDeletePrevious(editText, previousEditText))
        }
        charEditTexts.forEach(::addView)
    }

    /**
     * Set the input type for each character input field.
     */
    fun setInputType(inputType: Int) {
        charEditTexts.forEach { it.inputType = inputType }
    }

    /**
     * Clear all character input fields.
     */
    fun clear() {
        notifyListenerAfter { charEditTexts.forEach { it.setText("") } }
    }

    /**
     * The OTP string.
     */
    var otp: String
        /**
         * Returns the concatenation of characters in the character input fields.
         */
        get() = charEditTexts.joinToString(separator = "") { it.text?.toString() ?: "" }

        /**
         * Set the [value] in the individual character input field.
         *
         * @throws IllegalArgumentException if [value] length is not equal to the [expectedLength] of this view.
         */
        set(value) {
            val size = charEditTexts.size
            require (value.length == size) { "Must be $size characters long" }

            notifyListenerAfter {
                charEditTexts.forEachIndexed { index, editText ->
                    editText.setText(value[index].toString())
                }
            }
        }

    private var onOtpChangedListener: OnOtpChangedListener? = null

    /**
     * Set listener to be notified when [otp] characters are added/deleted.
     */
    fun setOnOtpChangedListener(listener: OnOtpChangedListener?) {
        this.onOtpChangedListener = listener
    }

    /**
     * Set editor action listener on the **last** character input field.
     */
    fun setOnEditorActionListener(listener: TextView.OnEditorActionListener?) {
        charEditTexts.last().setOnEditorActionListener(listener)
    }

    /**
     * True when all character input fields are filled.
     */
    val isFilled: Boolean
        get() { return isOtpLengthCorrect(otp) }

    /**
     * Attempt to fill the OTP from the clipboard contents. The contents length must be equal to
     * this view [expectedLength].
     *
     * @return true if successfully populated from clipboard or false otherwise
     */
    fun prepopulateFromClipboard(): Boolean {
        val clipboard: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val item: ClipData.Item? = clipboard.primaryClip?.getItemAt(0)
        if (item != null) {
            val pasteData: String = item.text.toString()
            if (isOtpLengthCorrect(pasteData)) {
                otp = pasteData
                return true
            }
        }

        return false
    }

    protected fun isOtpLengthCorrect(otp: String): Boolean = otp.length == expectedLength

    private fun notifyListenerAfter(block: () -> Unit) {
        val savedListener = this.onOtpChangedListener
        this.onOtpChangedListener = null
        block()
        this.onOtpChangedListener = savedListener
        this.onOtpChangedListener?.onOtpChanged(otp, isFilled)
    }

    private inner class NotifyingTextWatcher : TextWatcherAdapter() {
        override fun afterTextChanged(s: Editable) {
            onOtpChangedListener?.onOtpChanged(otp, isFilled)
        }
    }

    private class IfEmptyDeletePrevious(
            private val currentView: EditText,
            private val previousView: EditText?
    ) : OnKeyListener{
        override fun onKey(view: View?, keyCode: Int, event: KeyEvent): Boolean {
            if (event.action == ACTION_DOWN && keyCode == KEYCODE_DEL &&
                    previousView != null &&
                    currentView.text.isEmpty()) {
                previousView.text = null
                previousView.requestFocus()
                return true
            }
            return false
        }
    }

    private class IfNonEmptyJumpToNext(private val nextEditText: EditText) : TextWatcherAdapter() {
        override fun afterTextChanged(s: Editable) {
            if (s.isNotBlank()) {
                nextEditText.requestFocus()
            }
        }
    }

    private inner class SingleCharEditText @JvmOverloads constructor(
            context: Context, attrs: AttributeSet? = null
    ) : TextInputEditText(context, attrs) {

        override fun onTextContextMenuItem(id: Int): Boolean {
            if (id == android.R.id.paste) {
                val clipboard: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val item: ClipData.Item? = clipboard.primaryClip?.getItemAt(0)
                if (item != null) {
                    val pasteData: String = item.text.toString()
                    if (isOtpLengthCorrect(pasteData)) {
                        this@OtpInputView.otp = pasteData
                        this@OtpInputView.clearFocus()
                        hideKeyboard(this@OtpInputView)
                    }
                }
            }
            return super.onTextContextMenuItem(id)
        }
    }
}

private const val DefaultOtpLength = 6