package net.geidea.paymentsdk.ui.widget

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import androidx.transition.TransitionManager
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.theme.overlay.MaterialThemeOverlay
import kotlinx.parcelize.Parcelize
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.internal.util.dp
import net.geidea.paymentsdk.internal.util.textOrNull
import net.geidea.paymentsdk.model.*
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.email.validator.EmailValidator
import net.geidea.paymentsdk.ui.widget.address.AddressInputAdapter
import net.geidea.paymentsdk.ui.widget.address.AddressInputView
import net.geidea.paymentsdk.ui.widget.address.DefaultCountryDropDownAdapter
import net.geidea.paymentsdk.ui.widget.card.CardInputAdapter
import net.geidea.paymentsdk.ui.widget.card.CardInputListener
import net.geidea.paymentsdk.ui.widget.card.CardInputView
import net.geidea.paymentsdk.ui.widget.email.EmailEditText

/**
 * Composite view for entering and validation of debit/credit card credentials and customer data -
 * email, billing/shipping address(es).
 *
 * **Usage**
 *
 *
 * ***Embed the form into your XML layout***
 * ```xml
 * <net.geidea.paymentsdk.ui.widget.PaymentFormView
 *     android:id="@+id/paymentFormView"
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 * />
 * ```
 *
 * ***Initialize the payment form***
 * ```kotlin
 * // Setup the payment form with your accepted card brands, supported countries, etc.
 * paymentFormView.configure(GatewayApi.getMerchantConfiguration())
 *
 * // Listen for validation events that signal if all input data in the form is valid or not.
 * paymentFormView.setOnValidityChangedListener { data, valid ->
 *     payButton.isEnabled = valid
 * }
 * ```
 *
 * ***Pre-populate customer details (optional)***
 * ```kotlin
 * paymentFormView.showCustomerEmail = true
 * paymentFormView.customerEmail = "email@noreply.test"
 * paymentFormView.showAddress = true
 * paymentFormView.billingAddress = Address {
 *     countryCode = "SAU"
 *     street = "My Street, 12"
 *     city = "My city"
 *     postCode = "1234"
 * }
 * ```
 *
 * ***Read the data from the form and start payment***
 * It is important that you read the values only of the fields which are currently shown!
 * ```kotlin
 * payButton.setOnClickListener { payButton ->
 *     // Start the Payment flow
 *     paymentLauncher.launch(
 *         PaymentData {
 *             // Use the initial intent as a default
 *             paymentOperation = initialPaymentIntent.paymentOperation
 *             amount = initialPaymentIntent.amount
 *             currency = initialPaymentIntent.currency
 *             merchantReferenceId = initialPaymentIntent.merchantReferenceId
 *             callbackUrl = initialPaymentIntent.callbackUrl
 *             showCustomerEmail = initialPaymentIntent.showCustomerEmail
 *             showAddress = initialPaymentIntent.showAddress
 *             cardOnFile = initialPaymentIntent.cardOnFile
 *             initiatedBy = initialPaymentIntent.initiatedBy
 *             agreementId = initialPaymentIntent.agreementId
 *             agreementType = initialPaymentIntent.agreementType
 *             bundle = initialPaymentIntent.bundle
 *
 *             val card: Card? = paymentFormView.card
 *             paymentMethod = PaymentMethod {
 *                 cardHolderName = card?.cardHolderName
 *                 cardNumber = card?.cardNumber
 *                 expiryDate = card?.expiryDate
 *                 cvv = card?.cvv
 *             }
 *
 *             customerEmail = if (paymentFormView.showCustomerEmail) {
 *                 paymentFormView.customerEmail
 *             } else {
 *                 initialPaymentIntent.customerEmail
 *             }
 *
 *             billingAddress = if (paymentFormView.showAddress) {
 *                 paymentFormView.billingAddress
 *             } else {
 *                 initialPaymentIntent.billingAddress
 *             }
 *
 *             shippingAddress = if (paymentFormView.showAddress) {
 *                 if (paymentFormView.isSameAddressChecked) {
 *                     paymentFormView.billingAddress
 *                 } else {
 *                     paymentFormView.shippingAddress
 *                 }
 *             } else {
 *                 initialPaymentIntent.shippingAddress
 *             }
 *         }
 *     )
 * }
 * ```
 *
 * **Form structure**
 *
 * The form is a vertical LinearLayout composed of several child views/layouts:
 *
 * ***Card brand logos***
 *
 * Horizontal linear layout of small card brand logos. Shows only the brands that are currently
 * accepted by this merchant. If the first few digits of a valid card number are entered then the
 * recognized brand logo becomes highlighted with a rounded rectangle around it. Use either the
 * [configure] method or set the brands with [acceptedCardBrands].
 *
 * ***Card input view***
 *
 * A [CardInputView] is used to input card details. They are mandatory.
 *
 * ***Card brand logos***
 *
 * If [configure] method was called then on top of the form are shown the accepted card brand logos.
 * When the user enters the first few digits of a card number and its brand is recognized then the
 * respective brand logo is highlighted with a rounded rectangle around it.
 *
 * ***Customer e-mail***
 *
 * An email input field that can be pre-populated and read with the [customerEmail] variable.
 * It shown when the [showCustomerEmail] variable is set to true.
 *
 * ***Billing and shipping addresses***
 *
 * Billing and shipping address input fields are shown when [showAddress] variable is set to true.
 * However, the visibility of the shipping address fields depends on the checkbox.
 * The address fields are not mandatory but those fields that are shown must be valid in order for
 * [isValid] to return true.
 *
 * ***"Shipping address is same as Billing address" Checkbox***
 *
 * When the checkbox is checked shipping address fields become hidden.
 *
 * **Validation**
 *
 * Use [isValid] variable to check for the validity status of the form input.
 * Only the fields that are currently visible are checked for validity.
 * The [setOnValidityChangedListener] can be used to register for validation status changes.
 *
 * @see PaymentFormData
 * @see net.geidea.paymentsdk.flow.pay.PaymentData
 * @see net.geidea.paymentsdk.flow.pay.PaymentContract
 */
open class PaymentFormView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : LinearLayout(MaterialThemeOverlay.wrap(context, attrs, defStyleAttr, defStyleRes), attrs, defStyleAttr, defStyleRes) {

    private val cardBrandLogosLinearLayout: LinearLayout
    @get:JvmName("-cardInputName")
    internal val cardInputView: CardInputView
    private val customerEmailInputLayout: TextInputLayout
    private val customerEmailEditText: EmailEditText
    private val addressesLinearLayout: LinearLayout
    private val billingAddressLabel: TextView
    private val billingAddressInputView: AddressInputView
    private val shippingAddressLabel: TextView
    private val shippingAddressInputView: AddressInputView
    private val sameAddressCheckBox: CheckBox

    private var onValidationChangedListener: OnValidationChangedListener<PaymentFormData>? = null

    // When true any UI changes must be non-animated
    private var isRestoringSavedState: Boolean = false

    /**
     * Applies your merchant configuration (e.g. accepted card brands, supported countries) to
     * initialize this form view.
     */
    open fun configure(config: MerchantConfigurationResponse) {
        val countries = config.countries?.toList() ?: emptyList()

        // Setup the accepted countries list adapters
        val billingCountriesAdapter = DefaultCountryDropDownAdapter(context, countries)
        setBillingCountryDropDownAdapter(billingCountriesAdapter)
        val shippingCountriesAdapter = DefaultCountryDropDownAdapter(context, countries)
        setShippingCountryDropDownAdapter(shippingCountriesAdapter)

        // Setup filtering for card brands
        acceptedCardBrands = (config.paymentMethods ?: emptySet())
                .map(CardBrand::fromBrandName)
                .toSet()
                .minus(CardBrand.Unknown)
    }

    /**
     * Returns the input contents of the whole form. Fields with invalid data might be null.
     */
    val data: PaymentFormData
        get() = PaymentFormData(
                card = card,
                customerEmail = if (customerEmailEditText.isShown) customerEmailEditText.text?.toString() else null,
                billingAddress = if (billingAddressInputView.isShown) billingAddressInputView.address else null,
                shippingAddress = if (shippingAddressInputView.isShown) shippingAddressInputView.address else null,
                isSameAddress = sameAddressCheckBox.isChecked,
        )

    // Card data

    /**
     * The [card][Card] details in this form or null if any card details are invalid.
     */
    var card: Card?
        /**
         * Get the [card][Card] details in this form or null if any card details are invalid.
         */
        get() = cardInputView.card
        /**
         * Set [card][Card] details or null to clear them.
         */
        set(value) {
            with(cardInputView) {
                setCardHolder(value?.cardHolderName ?: "")
                setCardNumber(value?.cardNumber ?: "")
                setCardExpiryDate(value?.expiryDate?.toDisplayString() ?: "")
                setCardSecurityCode(value?.cvv ?: "")
            }
        }

    /**
     * Card number string.
     */
    var cardNumber: String
        /**
         * Get card number from the text input field (with removed digit group separators).
         */
        get() = cardInputView.getCardNumber()
        /**
         * Sets card number into the card number field.
         *
         * @param value the card number to be set, might be with or without digit group separators.
         */
        set(value) {
            cardInputView.setCardNumber(value ?: "")
        }

    /**
     * Card expiry date string.
     */
    var cardExpiryDate: String
        /**
         * Get card expiry date from the text input field.
         */
        get() = cardInputView.getCardExpiryDate()
        /**
         * Set card expiry date into the text input field.
         *
         * @param value a 2-digit expiry date separated by slash. E.g. 03/25.
         */
        set(value) {
            cardInputView.setCardExpiryDate(value ?: "")
        }

    /**
     * Card security code (CVV) string.
     */
    var cardSecurityCode: String
        /**
         * Get a card security code (CVV) from the text input field.
         */
        get() = cardInputView.getCardSecurityCode()
        /**
         * Set a security code (CVV) into the text input field.
         *
         * @param value 3- or 4-digit code depending on card brand.
         * For [American Express][CardBrand.AmericanExpress] should be 4-digit.
         *
         * @see CardBrand
         */
        set(value) {
            cardInputView.setCardSecurityCode(value ?: "")
        }

    /**
     * Card holder from the text input field. Length is between 3 and 255 characters.
     */
    var cardHolder: String
        /**
         * Get a card holder from the text input field.
         */
        get() = cardInputView.getCardHolder()
        /**
         * Set a card holder into the text input field. Should be between 3 and 255 characters long.
         */
        set(value) {
            cardInputView.setCardHolder(value ?: "")
        }

    /**
     * Customer e-mail or null if no e-mail is set or invalid. [showCustomerEmail] must be set for
     * this value to be shown.
     */
    var customerEmail: String?
        /**
         * Get a customer e-mail or null if no e-mail is set or invalid.
         */
        get() = customerEmailEditText.textOrNull
        /**
         * Set a customer e-mail or null to clear it.
         */
        set(value) {
            customerEmailEditText.setText(value)
        }

    /**
     * Flag for the customer email input field visibility.
     */
    var showCustomerEmail: Boolean
        /**
         * Get a flag for the customer email input field visibility.
         */
        get() = customerEmailInputLayout.isVisible
        /**
         * Set a flag for the customer email input field visibility.
         */
        set(value) {
            customerEmailInputLayout.isVisible = value
            isValid = areAllFieldsValid()
        }

    // Addresses

    /**
     * Flag for the address input fields visibility.
     */
    var showAddress: Boolean
        /**
         * Get a flag for the address input fields visibility.
         */
        get() = addressesLinearLayout.isVisible
        /**
         * Set a flag for the address input fields visibility.
         */
        set(value) {
            addressesLinearLayout.isVisible = value
            isValid = areAllFieldsValid()
        }

    /**
     * The billing address.
     */
    var billingAddress: Address
        /**
         * Get the billing address set in this form.
         */
        get() = billingAddressInputView.address
        /**
         * Set the billing address in this form.
         */
        set(value) {
            billingAddressInputView.address = value
        }

    /**
     * Billing address country code.
     *
     * @see Country
     * @see MerchantConfigurationResponse.countries
     */
    var billingAddressCountryCode: String?
        /**
         * Get billing address country code set in this form.
         */
        get() = billingAddressInputView.countryCode
        /**
         * Set billing address country code in this form.
         */
        set(value) {
            billingAddressInputView.countryCode = value
        }

    /**
     * Billing address street.
     */
    var billingAddressStreet: String?
        /**
         * Get billing address street set in this form.
         */
        get() = billingAddressInputView.street
        /**
         * Set billing address street in this form.
         */
        set(value) {
            billingAddressInputView.street = value
        }

    /**
     * Billing address city.
     */
    var billingAddressCity: String?
        /**
         * Get billing address city set in this form.
         */
        get() = billingAddressInputView.city
        /**
         * Set billing address city in this form.
         */
        set(value) {
            billingAddressInputView.city = value
        }

    /**
     * Billing address postcode.
     */
    var billingAddressPostCode: String?
        /**
         * Get billing address postcode set in this form.
         */
        get() = billingAddressInputView.postCode
        /**
         * Set billing address postcode in this form.
         */
        set(value) {
            billingAddressInputView.postCode = value
        }

    /**
     * Shipping address.
     */
    var shippingAddress: Address
        /**
         * Get the shipping address set in this form.
         */
        get() = shippingAddressInputView.address
        /**
         * Set the shipping address in this form.
         */
        set(value) {
            shippingAddressInputView.address = value
            isValid = areAllFieldsValid()
        }

    /**
     * The shipping address country code.
     *
     * @see Country
     * @see MerchantConfigurationResponse.countries
     */
    var shippingAddressCountryCode: String?
        /**
         * Get shipping address country code set in this form.
         */
        get() = shippingAddressInputView.countryCode
        /**
         * Set shipping address country code in this form.
         */
        set(value) {
            shippingAddressInputView.countryCode = value
        }

    /**
     * Shipping address street.
     */
    var shippingAddressStreet: String?
        /**
         * Get shipping address street set in this form.
         */
        get() = shippingAddressInputView.street
        /**
         * Set shipping address street in this form.
         */
        set(value) {
            shippingAddressInputView.street = value
        }

    /**
     * Shipping address city.
     */
    var shippingAddressCity: String?
        /**
         * Get shipping address city set in this form.
         */
        get() = shippingAddressInputView.city
        /**
         * Set shipping address city in this form.
         */
        set(value) {
            shippingAddressInputView.city = value
        }

    /**
     * Shipping address postcode.
     */
    var shippingAddressPostCode: String?
        /**
         * Get shipping address postcode set in this form.
         */
        get() = shippingAddressInputView.postCode
        /**
         * Set shipping address postcode in this form.
         */
        set(value) {
            shippingAddressInputView.postCode = value
        }

    /**
     * The state of the "Billing address is same as shipping address"
     *
     * Boolean indicating that only the billing address will be used and the
     * shipping address will not. If true the shipping
     * address fields become hidden. If false address fields become visible. In the last case
     * if the shipping address is not pre-specified by you in the
     * [net.geidea.paymentsdk.flow.pay.PaymentData] then a default country will be set.
     */
    var isSameAddressChecked: Boolean
        /**
         * Get a Boolean indicating that only the billing address will be used and the
         * shipping address will not.
         */
        get() = sameAddressCheckBox.isChecked
        /**
         * Get a Boolean indicating that shipping address will not be used. If true the shipping
         * address fields become hidden. If false address fields become visible. In the last case
         * if the shipping address is not pre-specified by you in the
         * [net.geidea.paymentsdk.flow.pay.PaymentData] then a default country will be set.
         */
        set(value) {
            sameAddressCheckBox.isChecked = value
            isValid = areAllFieldsValid()
        }

    /**
     * Enable or disable automatic animated transitions. E.g. should the address input fields
     * show/hide with fade animation when the "Same address" CheckBox is clicked.
     */
    var areTransitionsEnabled: Boolean = false

    /**
     * The accepted card brands. If not empty of card number filter will be used and the logo
     * icons will visualize them accordingly. If not set then no brand filtering is used and all
     * brands supported by the SDK will be considered valid.
     *
     * Note: Prefer the [configure] method which sets the accepted brands for you.
     */
    var acceptedCardBrands: Set<CardBrand> = CardBrand.allSupportedBrands
        /**
         * Set of accepted card brands. If not empty of card number filter will be used and the logo
         * icons will visualize them accordingly. If not set then no brand filtering is used and all
         * brands supported by the SDK will be considered valid.
         *
         * Note: Prefer the [configure] method which sets the accepted brands for you.
         */
        set(newValue) {
            // TODO save/restore as part of SavedState
            val acceptedBrands = newValue.minus(CardBrand.Unknown)
            if (field != acceptedBrands) {
                field = acceptedBrands
                onAcceptedCardBrandsChanged()
            }
        }

    private fun onAcceptedCardBrandsChanged() {
        cardInputView.setCardBrandFilter(acceptedCardBrands::contains)

        // Remove all logo images
        cardBrandLogosLinearLayout.removeAllViews()

        // Re-populate logo images
        acceptedCardBrands.ifEmpty { CardBrand.allSupportedBrands }
                .map(::createBrandImageView)
                .onEach(cardBrandLogosLinearLayout::addView)
    }

    private fun createBrandImageView(brand: CardBrand): ImageView {
        return ImageView(context).apply {
            id = View.generateViewId()
            tag = brand.name
            setImageResource(brand.logo)
            scaleType = ImageView.ScaleType.CENTER
            background = ContextCompat.getDrawable(context, R.drawable.gd_selector_outline_rounded)
            contentDescription = context.getString(R.string.gd_content_desc_card_brand_logo, brand.name)
            layoutParams = LinearLayout.LayoutParams(36.dp, 36.dp, 0f).apply {
                gravity = Gravity.CENTER_VERTICAL
                setMargins(2.dp)
            }
        }
    }

    /**
     * Clear the card input fields while leaving all other fields untouched.
     */
    fun clearCard() {
        this.cardInputView.clear()
    }

    /**
     * Set country list adapter. Used to initialize the supported countries.
     *
     * Note: Prefer the [configure] method which sets a default adapter for you.
     */
    fun <T> setBillingCountryDropDownAdapter(adapter: T) where T : ListAdapter, T : Filterable {
        billingAddressInputView.setCountryDropDownAdapter(adapter)
    }

    /**
     * Set country list adapter. Used to initialize the supported countries.
     *
     * Note: Prefer the [configure] method which sets a default adapter for you.
     */
    fun <T> setShippingCountryDropDownAdapter(adapter: T) where T : ListAdapter, T : Filterable {
        shippingAddressInputView.setCountryDropDownAdapter(adapter)
    }

    /**
     * Set listener to be notified for validation status changes.
     */
    fun setOnValidityChangedListener(listener: OnValidationChangedListener<PaymentFormData>?) {
        if (onValidationChangedListener !== listener) {
            onValidationChangedListener = listener
            onValidationChangedListener?.onValidationChanged(data, isValid)
        }
    }

    /**
     * Add card input listener.
     */
    fun addCardInputListener(listener: CardInputListener) {
        this.cardInputView.addCardInputListener(listener)
    }

    /**
     * Remove existing card input listener.
     */
    fun removeCardInputListener(listener: CardInputListener) {
        this.cardInputView.removeCardInputListener(listener)
    }

    /**
     * Returns true if the currently data in the visible input fields is valid or false otherwise.
     */
    var isValid: Boolean = false
        private set(value) {
            if (field != value) {
                field = value
                onValidationChangedListener?.onValidationChanged(data, value)
            }
        }

    private fun areAllFieldsValid(): Boolean {
        return cardInputView.isValid &&
                (!customerEmailEditText.isShown || customerEmailEditText.isValid) &&
                (!billingAddressInputView.isShown || billingAddressInputView.isValid) &&
                (!shippingAddressInputView.isShown || shippingAddressInputView.isValid)
    }

    init {
        val view = inflate(context, R.layout.gd_view_payment_form, this)

        cardBrandLogosLinearLayout = view.findViewById(R.id.cardBrandLogosLinearLayout)
        cardInputView = view.findViewById(R.id.cardInputView)
        customerEmailInputLayout = view.findViewById(R.id.customerEmailInputLayout)
        customerEmailEditText = view.findViewById(R.id.customerEmailEditText)
        addressesLinearLayout = view.findViewById(R.id.addressesLinearLayout)
        billingAddressLabel = view.findViewById(R.id.billingAddressLabel)
        billingAddressInputView = view.findViewById(R.id.billingAddressInputView)
        shippingAddressLabel = view.findViewById(R.id.shippingAddressLabel)
        shippingAddressInputView = view.findViewById(R.id.shippingAddressInputView)
        sameAddressCheckBox = view.findViewById(R.id.sameAddressCheckbox)

        // Initialize logos visibility
        onAcceptedCardBrandsChanged()

        cardInputView.addCardInputListener(object : CardInputAdapter() {
            override fun onCardValidationChanged(cardValid: Boolean) {
                isValid = areAllFieldsValid()
            }
        })

        cardInputView.addOnCardBrandChangedListener { brand ->
            cardBrandLogosLinearLayout.forEach { brandLogoImageView ->
                brandLogoImageView.isSelected = brandLogoImageView.tag == brand.name
            }
        }

        customerEmailEditText.setValidator(EmailValidator)
        customerEmailEditText.setOnErrorListener(TextInputErrorListener(customerEmailInputLayout))
        customerEmailEditText.setOnValidStatusListener {
            isValid = areAllFieldsValid()
        }
        customerEmailEditText.setOnInvalidStatusListener { value: String?, status: ValidationStatus.Invalid ->
            isValid = areAllFieldsValid()
            if (!value.isNullOrEmpty()) {
                customerEmailEditText.updateErrorMessage()
            }
        }

        billingAddressInputView.setAddressInputListener(object : AddressInputAdapter() {
            override fun onAddressValidationChanged(address: Address, valid: Boolean) {
                isValid = areAllFieldsValid()
            }
        })

        shippingAddressInputView.setAddressInputListener(object : AddressInputAdapter() {
            override fun onAddressValidationChanged(address: Address, valid: Boolean) {
                isValid = areAllFieldsValid()
            }
        })

        sameAddressCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (areTransitionsEnabled && !isRestoringSavedState) {
                TransitionManager.beginDelayedTransition(this)
            }
            shippingAddressLabel.isVisible = !isChecked
            shippingAddressInputView.isVisible = !isChecked
        }
    }

    // View state saving / restoration

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        return SavedState(
                superState,
                showCustomerEmail = showCustomerEmail,
                showAddress = showAddress,
                sameAddress = sameAddressCheckBox.isChecked,
                areTransitionsEnabled = areTransitionsEnabled,
        )
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val myState = state as SavedState
        super.onRestoreInstanceState(myState.superSaveState)

        customerEmailInputLayout.isVisible = myState.showCustomerEmail
        addressesLinearLayout.isVisible = myState.showAddress
        sameAddressCheckBox.isChecked = myState.sameAddress
        areTransitionsEnabled = myState.areTransitionsEnabled

        requestLayout()
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable?>) {
        isRestoringSavedState = true
        super.dispatchRestoreInstanceState(container)
        isRestoringSavedState = false
    }

    @Parcelize
    protected open class SavedState(
            val superSaveState: Parcelable?,
            var showCustomerEmail: Boolean,
            var showAddress: Boolean,
            var sameAddress: Boolean,
            var areTransitionsEnabled: Boolean,
    ) : BaseSavedState(superSaveState)
}