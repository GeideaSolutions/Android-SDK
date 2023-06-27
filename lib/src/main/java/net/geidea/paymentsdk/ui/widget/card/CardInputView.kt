package net.geidea.paymentsdk.ui.widget.card

import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View.OnFocusChangeListener
import android.widget.LinearLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.theme.overlay.MaterialThemeOverlay
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.model.Card
import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.card.validator.*
import net.geidea.paymentsdk.ui.widget.TextInputErrorListener

/**
 * Composite view for entering and validation of debit/credit [card][Card] data.
 *
 * Consists of 4 child views for input of the respective [Card] properties where each of the
 * fields is wrapped inside [TextInputLayout]:
 *
 * 1. [CardNumberEditText]
 * 2. [CardExpiryDateEditText]
 * 3. [CardSecurityCodeEditText]
 * 4. [CardHolderEditText]
 *
 * All the fields are mandatory and are restricted to 255 characters.
 *
 * @see Card
 */
open class CardInputView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : LinearLayout(MaterialThemeOverlay.wrap(context, attrs, defStyleAttr, defStyleRes), attrs, defStyleAttr, defStyleRes) {

    private lateinit var cardNumberInputLayout: TextInputLayout
    private lateinit var cardNumberEditText: CardNumberEditText
    private lateinit var cardHolderInputLayout: TextInputLayout
    private lateinit var cardHolderEditText: CardHolderEditText
    private lateinit var cardExpiryDateInputLayout: TextInputLayout
    private lateinit var cardExpiryDateEditText: CardExpiryDateEditText
    private lateinit var cardSecurityCodeInputLayout: TextInputLayout
    private lateinit var cardSecurityCodeEditText: CardSecurityCodeEditText

    private val cardNumberValidator: CardNumberValidator = DefaultCardNumberValidator(cardBrandFilter = null)

    /**
     * Listener for card input events.
     */
    private var cardInputListeners: List<CardInputListener> = emptyList()

    init {
        initView()
    }

    /**
     * Responsible for view inflation and initialization of child views.
     */
    private fun initView() {
        val view = inflate(context, R.layout.gd_view_card, this)

        orientation = VERTICAL

        cardNumberInputLayout = view.findViewById(R.id.cardNumberInputLayout)
        cardNumberEditText = view.findViewById(R.id.cardNumberEditText)

        cardHolderInputLayout = view.findViewById(R.id.cardHolderInputLayout)
        cardHolderEditText = view.findViewById(R.id.cardHolderEditText)

        cardExpiryDateInputLayout = view.findViewById(R.id.cardExpiryDateInputLayout)
        cardExpiryDateEditText = view.findViewById(R.id.cardExpiryDateEditText)

        cardSecurityCodeInputLayout = view.findViewById(R.id.cardSecurityCodeInputLayout)
        cardSecurityCodeEditText = view.findViewById(R.id.cardSecurityCodeEditText)

        cardNumberEditText.setOnErrorListener(TextInputErrorListener(cardNumberInputLayout))
        cardExpiryDateEditText.setOnErrorListener(TextInputErrorListener(cardExpiryDateInputLayout))
        cardSecurityCodeEditText.setOnErrorListener(TextInputErrorListener(cardSecurityCodeInputLayout))
        cardHolderEditText.setOnErrorListener(TextInputErrorListener(cardHolderInputLayout))

        cardNumberEditText.addOnCardBrandChangedListener(cardSecurityCodeEditText)

        cardNumberEditText.setValidator(cardNumberValidator)
        cardExpiryDateEditText.setValidator(ExpiryDateValidator)
        cardSecurityCodeEditText.setValidator(CvvValidator())
        cardHolderEditText.setValidator(CardHolderValidator)

        initFocusChangeListeners()
        initValidationListeners()

        // Fix for layout editor exception
        if (!isInEditMode) {
            cardNumberEditText.addOnCardBrandChangedListener(::updateBrand)
        }

        setSecurityCodeEndIconClickListener {
            MaterialAlertDialogBuilder(context)
                    .setIcon(R.drawable.gd_ic_cvv_hint)
                    .setTitle(R.string.gd_dlg_title_cvv_help)
                    .setMessage(R.string.gd_dlg_msg_cvv_help)
                    .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
                    .show()
        }
    }

    private fun initFocusChangeListeners() {
        cardHolderEditText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                cardInputListeners.forEach { it.onFocusChange(CardFieldType.HOLDER) }
            }
        }

        cardNumberEditText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                cardInputListeners.forEach { it.onFocusChange(CardFieldType.NUMBER) }
            }
        }

        cardExpiryDateEditText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                cardInputListeners.forEach { it.onFocusChange(CardFieldType.EXPIRY_DATE) }
            }
        }

        cardSecurityCodeEditText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                cardInputListeners.forEach { it.onFocusChange(CardFieldType.SECURITY_CODE) }
            } else {
                updateBrand(cardBrand)
            }
        }
    }

    private fun initValidationListeners() {

        // Card number

        cardNumberEditText.setOnValidStatusListener {
            validate()
            cardInputListeners.forEach { it.onFieldValidStatus(CardFieldType.NUMBER) }
            cardExpiryDateEditText.requestFocus()
        }
        cardNumberEditText.setOnInvalidStatusListener { value: String?, status: ValidationStatus.Invalid ->
            validate()
            // Show errors only when reached the maximal length
            if (value?.length == CardBrand.MAX_LENGTH_STANDARD) {
                cardNumberEditText.updateErrorMessage()
            }
            cardInputListeners.forEach { it.onFieldInvalidStatus(CardFieldType.NUMBER, status) }
        }

        // Expiry date

        cardExpiryDateEditText.setOnValidStatusListener {
            validate()
            cardInputListeners.forEach { it.onFieldValidStatus(CardFieldType.EXPIRY_DATE) }
            cardSecurityCodeEditText.requestFocus()
        }
        cardExpiryDateEditText.setOnInvalidStatusListener { value: String?, status: ValidationStatus.Invalid ->
            validate()
            if (value != null && value.length == 5) {
                cardExpiryDateEditText.updateErrorMessage()
            }
            cardInputListeners.forEach { it.onFieldInvalidStatus(CardFieldType.EXPIRY_DATE, status) }
        }

        // CVV

        cardSecurityCodeEditText.setOnValidStatusListener {
            validate()
            cardInputListeners.forEach { it.onFieldValidStatus(CardFieldType.SECURITY_CODE) }
            if (cardBrand != CardBrand.Unknown && cardBrand.securityCodeLengthRange.first == cardBrand.securityCodeLengthRange.last) {
                cardHolderEditText.requestFocus()
            }
        }
        cardSecurityCodeEditText.setOnInvalidStatusListener { value: String?, status: ValidationStatus.Invalid ->
            validate()
            if (value != null && value.length == cardSecurityCodeEditText.maxLength) {
                cardSecurityCodeEditText.updateErrorMessage()
            }
            cardInputListeners.forEach { it.onFieldInvalidStatus(CardFieldType.SECURITY_CODE, status) }
        }

        // Card holder

        cardHolderEditText.setOnValidStatusListener {
            validate()
            cardInputListeners.forEach { it.onFieldValidStatus(CardFieldType.HOLDER) }
        }
        cardHolderEditText.setOnInvalidStatusListener { value: String?, status: ValidationStatus.Invalid ->
            validate()
            if (!value.isNullOrEmpty()) {
                cardHolderEditText.updateErrorMessage()
            }
            cardInputListeners.forEach { it.onFieldInvalidStatus(CardFieldType.HOLDER, status) }
        }
    }

    /**
     * Returns true if the currently input card data is valid or false otherwise.
     */
    var isValid: Boolean = false
        private set(value) {
            if (field != value) {
                field = value
                cardInputListeners.forEach { it.onCardValidationChanged(value) }
                if (value) {
                    cardInputListeners.forEach { it.onCardInputComplete() }
                }
            }
        }

    /**
     * The currently recognized card brand or [CardBrand.Unknown] if not recognized or
     * card number is invalid.
     */
    val cardBrand: CardBrand get() = cardNumberEditText.cardBrand

    /**
     * A [card][Card] containing the current input or null if not valid.
     */
    val card: Card?
        get() {
            return try {
                Card {
                    cardNumber = cardNumberEditText.cardNumber
                    cardHolderName = cardHolderEditText.text?.toString()
                    expiryDate = requireNotNull(cardExpiryDateEditText.expiryDate)
                    cvv = cardSecurityCodeEditText.text?.toString()
                }
            } catch (e: IllegalArgumentException) {
                null
            }
        }

    /**
     * Clears the input fields and error messages.
     */
    fun clear() {
        cardHolderEditText.setText("")
        cardNumberEditText.setText("")
        cardExpiryDateEditText.setText("")
        cardSecurityCodeEditText.setText("")
        cardHolderEditText.errorMessage = null
        cardNumberEditText.errorMessage = null
        cardExpiryDateEditText.errorMessage = null
        cardSecurityCodeEditText.errorMessage = null
    }

    /**
     * Enables or disables this view and all of its children.
     *
     * @param enabled flag indicating if this view should be enabled.
     */
    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)

        cardHolderInputLayout.isEnabled = enabled
        cardNumberInputLayout.isEnabled = enabled
        cardExpiryDateInputLayout.isEnabled = enabled
        cardSecurityCodeInputLayout.isEnabled = enabled

        cardHolderEditText.isEnabled = enabled
        cardNumberEditText.isEnabled = enabled
        cardExpiryDateEditText.isEnabled = enabled
        cardSecurityCodeEditText.isEnabled = enabled
    }

    /**
     * Sets a filter which decides whether a given card number is accepted or rejected.
     * If not set then all possible supported card brands will be accepted.
     */
    fun setCardBrandFilter(cardBrandFilter: CardBrandFilter?) {
        cardNumberValidator.cardBrandFilter = cardBrandFilter
        validate()      // Re-validating only card number and CVV should be enough
    }

    /**
     * Add a listener to be notified when card brand is recognized.
     */
    fun addOnCardBrandChangedListener(listener: OnCardBrandChangedListener) {
        cardNumberEditText.addOnCardBrandChangedListener(listener)
    }

    /**
     * Remove a card brand change listener.
     */
    fun removeOnCardBrandChangedListener(listener: OnCardBrandChangedListener) {
        cardNumberEditText.removeOnCardBrandChangedListener(listener)
    }

    /**
     * Add a listener for card input events.
     */
    fun addCardInputListener(listener: CardInputListener) {
        if (!this.cardInputListeners.contains(listener)) {
            this.cardInputListeners += listener
        }
    }

    /**
     * Remove an existing listener for card input events.
     */
    fun removeCardInputListener(listener: CardInputListener) {
        this.cardInputListeners -= listener
    }

    /**
     * Get card number from the text input field (with removed digit group separators).
     */
    fun getCardNumber(): String {
        val groupedCardNumber = cardNumberEditText.text?.toString() ?: ""
        return CardBrand.removeCardNumberSpaces(groupedCardNumber)
    }

    /**
     * Sets card number into the card number field.
     *
     * @param cardNumber the card number to be set, might be with or without digit group separators.
     */
    fun setCardNumber(cardNumber: String) {
        cardNumberEditText.setText(cardNumber)
    }

    /**
     * Get card expiry date from the text input field.
     */
    fun getCardExpiryDate(): String {
        return cardExpiryDateEditText.text?.toString() ?: ""
    }

    /**
     * Set card expiry date.
     *
     * @param cardExpiryDate a 2-digit expiry date separated by slash. E.g. 03/25.
     *
     * @see net.geidea.paymentsdk.model.ExpiryDate
     * @see net.geidea.paymentsdk.model.ExpiryDate.toDisplayString
     */
    fun setCardExpiryDate(cardExpiryDate: String) {
        cardExpiryDateEditText.setText(cardExpiryDate)
    }

    /**
     * Get card holder from the text input field.
     */
    fun getCardHolder(): String {
        return cardHolderEditText.text?.toString() ?: ""
    }

    /**
     * Set card holder name.
     */
    fun setCardHolder(cardHolder: String) {
        cardHolderEditText.setText(cardHolder)
    }

    /**
     * Get card security code from the text input field.
     */
    fun getCardSecurityCode(): String {
        return cardSecurityCodeEditText.text?.toString() ?: ""
    }

    /**
     * Set security code (CVV).
     *
     * @param securityCode 3- or 4-digit code depending on card brand.
     * For [American Express][CardBrand.AmericanExpress] should be 4-digit.
     *
     * @see CardBrand
     */
    fun setCardSecurityCode(securityCode: String) {
        cardSecurityCodeEditText.setText(securityCode)
    }

    /**
     * Add a text watcher to the Card Holder input field.
     */
    fun addCardHolderTextWatcher(cardHolderTextWatcher: TextWatcher) {
        cardHolderEditText.addTextChangedListener(cardHolderTextWatcher)
    }

    /**
     * Remove a text watcher from the Card Holder input field.
     */
    fun removeCardHolderTextWatcher(cardHolderTextWatcher: TextWatcher) {
        cardHolderEditText.removeTextChangedListener(cardHolderTextWatcher)
    }

    /**
     * Add a text watcher to the Card Number input field.
     */
    fun addCardNumberTextWatcher(cardNumberTextWatcher: TextWatcher) {
        cardNumberEditText.addTextChangedListener(cardNumberTextWatcher)
    }

    /**
     * Remove a text watcher from the Card Number input field.
     */
    fun removeCardNumberTextWatcher(cardNumberTextWatcher: TextWatcher) {
        cardNumberEditText.removeTextChangedListener(cardNumberTextWatcher)
    }

    /**
     * Add a text watcher to the Expiry Date input field.
     */
    fun addExpiryDateTextWatcher(expiryDateTextWatcher: TextWatcher) {
        cardExpiryDateEditText.addTextChangedListener(expiryDateTextWatcher)
    }

    /**
     * Remove a text watcher from the Expiry Date input field.
     */
    fun removeExpiryDateTextWatcher(expiryDateTextWatcher: TextWatcher) {
        cardExpiryDateEditText.removeTextChangedListener(expiryDateTextWatcher)
    }

    /**
     * Add a text watcher to the Security Code (CVV) input field.
     */
    fun addSecurityCodeTextWatcher(securityCodeTextWatcher: TextWatcher) {
        cardSecurityCodeEditText.addTextChangedListener(securityCodeTextWatcher)
    }

    /**
     * Remove a text watcher from the Security Code (CVV) input field.
     */
    fun removeSecurityCodeTextWatcher(securityCodeTextWatcher: TextWatcher) {
        cardSecurityCodeEditText.removeTextChangedListener(securityCodeTextWatcher)
    }

    /**
     * Sets listener for Security Code (CVV) help icon clicks.
     */
    fun setSecurityCodeEndIconClickListener(listener: OnClickListener?) {
        cardSecurityCodeInputLayout.setEndIconOnClickListener(listener)
    }

    /**
     * Update the validation status of this composite view derived from the validation statuses of
     * all its children input fields.
     */
    private fun validate() {
        isValid = allFieldsValid()
    }

    private fun forceValidate() {
        cardNumberEditText.validate()
        cardExpiryDateEditText.validate()
        cardSecurityCodeEditText.validate()
        cardHolderEditText.validate()
        validate()
    }

    private fun allFieldsValid(): Boolean {
        return cardNumberEditText.isValid &&
                cardExpiryDateEditText.isValid &&
                cardSecurityCodeEditText.isValid &&
                cardHolderEditText.isValid
    }

    internal fun showValidationStatus() {
        cardNumberEditText.updateErrorMessage()
        cardExpiryDateEditText.updateErrorMessage()
        cardSecurityCodeEditText.updateErrorMessage()
        cardHolderEditText.updateErrorMessage()
    }

    /**
     * Updates fields that depend on the card brand.
     */
    private fun updateBrand(cardBrand: CardBrand) {
        if (!cardSecurityCodeEditText.text.isNullOrBlank() && cardSecurityCodeEditText.isValid) {
            cardSecurityCodeInputLayout.helperText = getSecurityCodeHelperText(cardBrand)
        }
    }

    private fun getSecurityCodeHelperText(cardBrand: CardBrand): CharSequence {
        return if (cardBrand == CardBrand.AmericanExpress) {
            context.getString(R.string.gd_help_cvv_4digits)
        } else {
            context.getString(R.string.gd_help_cvv)
        }
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)

        if (hasWindowFocus) {
            updateBrand(cardBrand)
        }
    }
}