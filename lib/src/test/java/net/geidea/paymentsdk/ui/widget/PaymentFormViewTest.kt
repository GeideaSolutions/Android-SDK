package net.geidea.paymentsdk.ui.widget

import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.model.*
import net.geidea.paymentsdk.model.common.AgreementType
import net.geidea.paymentsdk.model.common.InitiatingSource
import net.geidea.paymentsdk.model.transaction.PaymentOperation
import net.geidea.paymentsdk.ui.widget.address.*
import net.geidea.paymentsdk.ui.widget.card.*
import net.geidea.paymentsdk.ui.widget.email.EmailEditText
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PaymentFormViewTest {

    private lateinit var paymentFormView: PaymentFormView
    private lateinit var childViews: ChildViews

    @RelaxedMockK
    private lateinit var mockOnValidationChangedListener: OnValidationChangedListener<PaymentFormData>

    private lateinit var validCountriesAdapter: DefaultCountryDropDownAdapter

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        val activityController = Robolectric
                .buildActivity(PaymentFormViewTestActivity::class.java)
                .create()
                .start()
                .resume()
        paymentFormView = activityController.get().paymentFormView
        paymentFormView.setOnValidityChangedListener(mockOnValidationChangedListener)
        childViews = ChildViews(paymentFormView)

        validCountriesAdapter = DefaultCountryDropDownAdapter(paymentFormView.context, listOf(SAUDI_ARABIA, FRANCE))
    }

    class ChildViews(paymentFormView: PaymentFormView) {
        // Card brand logos
        val cardBrandLogosLinearLayout: LinearLayout = paymentFormView.findViewById(R.id.cardBrandLogosLinearLayout)

        // Card
        val cardInputView: CardInputView = paymentFormView.findViewById(R.id.cardInputView)
        val cardNumberEditText: CardNumberEditText = paymentFormView.findViewById(R.id.cardNumberEditText)
        val cardExpiryDateEditText: CardExpiryDateEditText = paymentFormView.findViewById(R.id.cardExpiryDateEditText)
        val cardSecurityCodeEditText: CardSecurityCodeEditText = paymentFormView.findViewById(R.id.cardSecurityCodeEditText)
        val cardHolderEditText: CardHolderEditText = paymentFormView.findViewById(R.id.cardHolderEditText)

        // E-mail
        val customerEmailInputLayout: TextInputLayout = paymentFormView.findViewById(R.id.customerEmailInputLayout)
        val customerEmailEditText: EmailEditText = paymentFormView.findViewById(R.id.customerEmailEditText)

        // Addresses
        val addressesViewGroup: ViewGroup = paymentFormView.findViewById(R.id.addressesLinearLayout)
        val billingAddressLabel: TextView = paymentFormView.findViewById(R.id.billingAddressLabel)
        val billingAddressInputView: AddressInputView = paymentFormView.findViewById(R.id.billingAddressInputView)
        val billingCountryView: TextView = billingAddressInputView.findViewById(R.id.countryAutoCompleteTextView)
        val billingStreetEditText: StreetEditText = billingAddressInputView.findViewById(R.id.streetEditText)
        val billingCityEditText: CityEditText = billingAddressInputView.findViewById(R.id.cityEditText)
        val billingPostCodeEditText: PostCodeEditText = billingAddressInputView.findViewById(R.id.postCodeEditText)
        
        val sameAddressCheckBox: CheckBox = paymentFormView.findViewById(R.id.sameAddressCheckbox)
        val shippingAddressLabel: TextView = paymentFormView.findViewById(R.id.shippingAddressLabel)
        val shippingAddressInputView: AddressInputView = paymentFormView.findViewById(R.id.shippingAddressInputView)
        val shippingCountryView: TextView = shippingAddressInputView.findViewById(R.id.countryAutoCompleteTextView)
        val shippingStreetEditText: StreetEditText = shippingAddressInputView.findViewById(R.id.streetEditText)
        val shippingCityEditText: CityEditText = shippingAddressInputView.findViewById(R.id.cityEditText)
        val shippingPostCodeEditText: PostCodeEditText = shippingAddressInputView.findViewById(R.id.postCodeEditText)
    }

    @Test
    fun testDefaultChildViewsVisibility() {
        assertTrue(childViews.cardBrandLogosLinearLayout.isVisible)
        // By default acceptedCardBrands is null and we assume all brands are accepted
        assertEquals(CardBrand.allSupportedBrands.size, childViews.cardBrandLogosLinearLayout.childCount)
        CardBrand.allSupportedBrands.onEach { brand ->
            val brandLogoImageView = childViews.cardBrandLogosLinearLayout.findViewWithTag<ImageView>(brand.name)
            assertTrue(brandLogoImageView.isVisible)
        }

        assertTrue(childViews.cardInputView.isVisible)
        assertTrue(childViews.customerEmailInputLayout.isGone)

        // The entire addresses view group must be gone
        assertTrue(childViews.addressesViewGroup.isGone)
        assertTrue(childViews.billingAddressLabel.isVisible)
        assertTrue(childViews.billingAddressInputView.isVisible)
        assertTrue(childViews.sameAddressCheckBox.isVisible)
        assertTrue(childViews.shippingAddressLabel.isVisible)
        assertTrue(childViews.shippingAddressInputView.isVisible)
    }

    @Test
    fun sameAddressCheckBox_byDefault_isNotChecked() {
        assertFalse(childViews.sameAddressCheckBox.isChecked)
    }

    @Test
    fun sameAddressCheckBox_whenChanged_updatesShippingAddressVisibility() {
        assertFalse(childViews.sameAddressCheckBox.isChecked)

        childViews.sameAddressCheckBox.performClick()       // Check

        assertTrue(childViews.shippingAddressLabel.isGone)
        assertTrue(childViews.shippingAddressInputView.isGone)

        childViews.sameAddressCheckBox.performClick()       // Uncheck

        assertTrue(childViews.shippingAddressLabel.isVisible)
        assertTrue(childViews.shippingAddressInputView.isVisible)
    }

    @Test
    fun setAcceptedCardBrands_withSingleBrand_onlyThisBrandLogoIsVisible() {
        paymentFormView.acceptedCardBrands = setOf(CardBrand.Visa)
        assertEquals(1, childViews.cardBrandLogosLinearLayout.childCount)
        assertTrue(childViews.cardBrandLogosLinearLayout.findViewWithTag<ImageView>(CardBrand.Visa.name).isVisible)
    }

    @Test
    fun setAcceptedCardBrands_withOrderedSet_logosAreDisplayedInSameOrder() {
        paymentFormView.acceptedCardBrands = setOf(CardBrand.Visa, CardBrand.Mada, CardBrand.Mastercard)
        assertEquals(3, childViews.cardBrandLogosLinearLayout.childCount)
        val children: List<ImageView> = childViews.cardBrandLogosLinearLayout.children.toList().map { it as ImageView }

        assertEquals(CardBrand.Visa.name, children[0].tag)
        assertEquals(CardBrand.Mada.name, children[1].tag)
        assertEquals(CardBrand.Mastercard.name, children[2].tag)
    }

    // Setters/getters tests

    @Test
    fun setCard_thenGetCard_returnsTheSame() {
        paymentFormView.card = VALID_CARD
        assertEquals(VALID_CARD, paymentFormView.card)
    }

    @Test
    fun setCustomerEmail_withValid_populates() {
        paymentFormView.customerEmail = "email@noreply.test"
        assertEquals("email@noreply.test", childViews.customerEmailEditText.text?.toString())
    }

    @Test
    fun setCustomerEmail_withNull_clears() {
        paymentFormView.customerEmail = null
        assertEquals("", childViews.customerEmailEditText.text?.toString())
    }

    @Test
    fun setBillingAddress_thenGet_returnsTheSame() {
        paymentFormView.setBillingCountryDropDownAdapter(validCountriesAdapter)
        paymentFormView.billingAddress = VALID_BILLING_ADDRESS
        assertEquals(VALID_BILLING_ADDRESS, paymentFormView.billingAddress)
    }

    @Test
    fun setShippingAddress_thenGet_returnsTheSame() {
        paymentFormView.setShippingCountryDropDownAdapter(validCountriesAdapter)
        paymentFormView.shippingAddress = VALID_SHIPPING_ADDRESS
        assertEquals(VALID_SHIPPING_ADDRESS, paymentFormView.shippingAddress)
    }

    @Test
    fun showAddress_whenChanged_addressesViewGroupVisibilityChanges() {
        paymentFormView.showAddress = true
        assertTrue(childViews.addressesViewGroup.isVisible)

        paymentFormView.showAddress = false
        assertTrue(childViews.addressesViewGroup.isGone)
    }

    @Test
    fun isSameAddressChecked_whenChanged_checkBoxChecked() {
        paymentFormView.isSameAddressChecked = true
        assertTrue(childViews.sameAddressCheckBox.isChecked)

        paymentFormView.isSameAddressChecked = false
        assertFalse(childViews.sameAddressCheckBox.isChecked)
    }

    // Validation tests

    @Test
    fun setCard_withValidThenNull_isValidUpdatesCorrectly() {
        assertFalse(paymentFormView.isValid)

        paymentFormView.card = VALID_CARD
        assertTrue(paymentFormView.isValid)
        verify { mockOnValidationChangedListener.onValidationChanged(withArg { assertNotNull(it.card) }, eq(true)) }

        paymentFormView.card = null
        assertFalse(paymentFormView.isValid)
        verify { mockOnValidationChangedListener.onValidationChanged(withArg { assertNull(it.card) }, eq(false)) }
    }

    // TODO Save/restore tests

    companion object {
        val VALID_CARD = Card {
            cardNumber = "5123456789012346"
            expiryDate = ExpiryDate(month = 1, year = 39)
            cvv = "100"
            cardHolderName = "John Doe"
        }
        val VALID_PAYMENT_METHOD = PaymentMethod {
            cardNumber = "4111111111111111"
            expiryDate = ExpiryDate(month = 12, year = 22)
            cvv = "222"
            cardHolderName = "John Doe"
        }
        val SAUDI_ARABIA = Country {
            nameEn = "Saudi Arabia"
            key3 = "SAU"
            numericCode = 1
            isSupported = true
        }
        val FRANCE = Country {
            nameEn = "France"
            key3 = "FRA"
            numericCode = 2
            isSupported = true
        }

        val VALID_BILLING_ADDRESS = Address(
                countryCode = "SAU",
                street = "bstreet",
                city = "bcity",
                postCode = "bpostcode",
        )

        val VALID_SHIPPING_ADDRESS = Address(
                countryCode = "FRA",
                street = "sstreet",
                city = "scity",
                postCode = "spostcode",
        )

        val MINIMAL_PAYMENT_DATA = PaymentData {
            amount = "123.45".toBigDecimal()
            currency = "SAR"
            paymentMethod = VALID_PAYMENT_METHOD
        }

        val FULL_PAYMENT_DATA = PaymentData {
            amount = "123.45".toBigDecimal()
            currency = "SAR"
            paymentOperation = PaymentOperation.PREAUTHORIZE
            merchantReferenceId = "mref"
            callbackUrl = "https://callback.com"
            paymentMethod = PaymentMethod {
                cardNumber = "4111111111111111"
                expiryDate = ExpiryDate(month = 12, year = 22)
                cvv = "222"
                cardHolderName = "John Doe"
            }
            showCustomerEmail = true
            customerEmail = "email@noreply.test"
            showAddress = true
            billingAddress = Address(
                    countryCode = "SAU",
                    street = "bstreet",
                    city = "bcity",
                    postCode = "bpostcode"
            )
            shippingAddress = Address(
                    countryCode = "FRA",
                    street = "sstreet",
                    city = "scity",
                    postCode = "spostcode"
            )
            initiatedBy = InitiatingSource.MERCHANT
            agreementType = AgreementType.UNSCHEDULED
            agreementId = "12345"
            cardOnFile = true
            bundle = bundleOf("key" to "value")
        }
    }
}