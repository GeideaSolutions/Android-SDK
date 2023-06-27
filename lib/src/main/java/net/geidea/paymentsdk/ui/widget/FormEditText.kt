package net.geidea.paymentsdk.ui.widget

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.theme.overlay.MaterialThemeOverlay
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.ui.validation.*

/**
 * Base class for all text input fields of type [TextInputEditText].
 *
 * **Validation**
 *
 * This class provides common mechanism to perform validation on every input change through
 * pluggable [validators][setValidator]. The [validity status][ValidationStatus]
 * of the currently input text can be read with [validationStatus] and [isValid] properties.
 *
 * **Errors**
 *
 * This class maintains its current validation status but the actual visualization (e.g. inline
 * error message below the input field or other) of it is left for the consumer.
 * Use [errorMessage] to set a validation error message or clear it out. After assigning a new
 * error message the error listener will be called with its value for the error to be actually
 * shown or hidden.
 *
 * @see OnErrorListener
 */
open class FormEditText @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : TextInputEditText(
        MaterialThemeOverlay.wrap(context, attrs, defStyleAttr, 0), attrs, defStyleAttr
), Validatable<String, String> {

    protected var _validator: Validator<String>? = null
        private set(value) {
            if (field != value) {
                field = value
                if (value != null) {
                    validate()
                }
            }
        }

    /**
     * Returns true if the value in this field is valid or false otherwise.
     */
    override val isValid: Boolean get() = validationStatus == ValidationStatus.Valid

    /**
     * Set a validator. It is queried on every text change and the result of [Validator.validate]
     * is set to [validationStatus] and [isValid].
     */
    fun setValidator(validator: Validator<String>) {
        _validator = validator
    }

    override fun validate() {
        _validator?.let {
            validationStatus = it.validate(text?.toString() ?: "")
        }
    }

    /**
     * The current validation error message associated with this text input field.
     * Could be null if the field is in a valid state.
     */
    var errorMessage: CharSequence? = null
        set(value) {
            if (field != value) {
                field = value
                onErrorListener?.onShowError(value)
            }
        }

    private var onErrorListener: OnErrorListener? = null
    private var focusChangeListener: OnFocusChangeListener? = null

    override var validationStatus: ValidationStatus = ValidationStatus.Undefined
        internal set(status) {
            require(status !is ValidationStatus.Undefined) { "Cannot assign ValidationStatus.Undefined" }
            if (field != status) {
                field = status
                val newValue = text?.toString() ?: ""
                when (status) {
                    ValidationStatus.Valid -> {
                        errorMessage = null
                        onValidStatusListener?.onValidStatus(newValue)
                    }
                    is ValidationStatus.Invalid -> onInvalidStatusListener?.onInvalidStatus(newValue, status)
                    else -> { /* Should never reach here */ }
                }
            }
        }

    private var onValidStatusListener: OnValidStatusListener<String>? = null
    private var onInvalidStatusListener: OnInvalidStatusListener<String>? = null

    init {
        super.setOnFocusChangeListener { view, hasFocus ->
            if (isErrorShownOnFocusLost && !hasFocus) {
                updateErrorMessage()
            }
            focusChangeListener?.onFocusChange(view, hasFocus)
        }
    }

    override fun setOnValidStatusListener(listener: OnValidStatusListener<String>?) {
        if (onValidStatusListener != listener) {
            onValidStatusListener = listener
            if (validationStatus == ValidationStatus.Valid) {
                onValidStatusListener?.onValidStatus(text?.toString() ?: "")
            }
        }
    }

    override fun setOnInvalidStatusListener(listener: OnInvalidStatusListener<String>?) {
        if (onInvalidStatusListener != listener) {
            onInvalidStatusListener = listener
            val status = validationStatus
            if (status is ValidationStatus.Invalid) {
                onInvalidStatusListener?.onInvalidStatus(text?.toString(), status)
            }
        }
    }

    override var isErrorShownOnFocusLost: Boolean = true

    override fun updateErrorMessage() {
        val status = validationStatus
        errorMessage = if (status is ValidationStatus.Invalid)
            status.reason.getMessage(context)
        else
            null
    }

    /**
     * Set listener responsible for displaying the field error message.
     */
    override fun setOnErrorListener(listener: OnErrorListener) {
        this.onErrorListener = listener
    }

    override fun setOnFocusChangeListener(listener: OnFocusChangeListener?) {
        this.focusChangeListener = listener
    }

    companion object {
        const val MAX_FIELD_LENGTH = PaymentData.MAX_FIELD_LENGTH
    }
}