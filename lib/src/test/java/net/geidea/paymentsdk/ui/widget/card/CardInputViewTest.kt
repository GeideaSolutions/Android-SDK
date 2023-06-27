package net.geidea.paymentsdk.ui.widget.card

import com.google.android.material.textfield.TextInputLayout
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.internal.util.*
import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.model.ExpiryDate
import net.geidea.paymentsdk.ui.widget.FormEditText
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class CardInputViewTest {

    private lateinit var cardInputView: CardInputView
    private lateinit var childViews: ChildViews

    @MockK(relaxUnitFun = true)
    internal lateinit var mockCardListener: CardInputAdapter

    @MockK(relaxUnitFun = true)
    internal lateinit var mockCardBrandChangedListener: OnCardBrandChangedListener

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        val activityController = Robolectric
                .buildActivity(CardInputTestActivity::class.java)
                .create()
                .start()
                .resume()
        cardInputView = activityController.get().cardInputView
        childViews = ChildViews(cardInputView)
        childViews.cardNumberEditText.setText("")
    }

    @Test
    fun testExistence() {
        assertNotNull(cardInputView)
        assertNotNull(childViews.cardHolderEditText)
        assertNotNull(childViews.cardNumberEditText)
        assertNotNull(childViews.expiryDateEditText)
        assertNotNull(childViews.securityCodeEditText)
    }

    @Test
    fun getCard_whenValidVisa_returnsCard() {
        childViews.cardHolderEditText.setText("John Doe")
        childViews.cardNumberEditText.setText(VALID_VISA_WITH_SPACES)
        childViews.expiryDateEditText.append("12")
        childViews.expiryDateEditText.append("34")
        childViews.securityCodeEditText.append("123")

        val card = cardInputView.card
        assertNotNull(card)
        assertEquals(VALID_VISA, card!!.cardNumber)
        assertEquals(ExpiryDate(12, 34), card.expiryDate)
        assertEquals("123", card.cvv)
        //assertTrue(card.validate())
    }

    @Test
    fun getCard_whenValidAmexAnd4DigitSecCode_returnsCard() {
        childViews.cardHolderEditText.setText("John Doe")
        childViews.cardNumberEditText.setText(VALID_AMEX_WITH_SPACES)
        childViews.expiryDateEditText.append("12")
        childViews.expiryDateEditText.append("34")
        childViews.securityCodeEditText.append("1234")  // Amex requires 4-digit sec.code
        val card = cardInputView.card
        assertNotNull(card)
        card!!
        assertEquals(VALID_AMEX, card.cardNumber)
        assertEquals(ExpiryDate(12, 34), card.expiryDate)
        assertEquals("1234", card.cvv)
        //assertTrue(card.validate())
    }

    @Test
    fun getCard_whenValidAmexAndValidHolder_returnsCard() {
        childViews.cardHolderEditText.setText("John Doe")
        childViews.cardNumberEditText.setText(VALID_AMEX_WITH_SPACES)
        childViews.expiryDateEditText.append("12")
        childViews.expiryDateEditText.append("34")
        childViews.securityCodeEditText.append("1234")
        val card = cardInputView.card
        assertNotNull(card)
    }

    @Test
    fun clear_whenAllFieldsEntered_clearsAllFields() {
        with(childViews) {
            cardHolderEditText.setText("John Doe")
            cardNumberEditText.setText(VALID_VISA_WITH_SPACES)
            expiryDateEditText.append("12")
            expiryDateEditText.append("34")
            securityCodeEditText.append("123")
        }

        cardInputView.clear()

        assertEquals("", childViews.cardHolderEditText.text.toString())
        assertEquals("", childViews.cardNumberEditText.text.toString())
        assertEquals("", childViews.expiryDateEditText.text.toString())
        assertEquals("", childViews.securityCodeEditText.text.toString())
    }

    @Test
    fun clear_whenFieldsInErrorState_clearsFieldsAndHidesErrors() {
        with(childViews) {
            cardNumberEditText.errorMessage = "error"
            expiryDateEditText.errorMessage = "error"
            securityCodeEditText.errorMessage = "error"
            cardHolderEditText.errorMessage = "error"

            cardInputView.clear()

            assertNull(cardNumberEditText.errorMessage)
            assertNull(expiryDateEditText.errorMessage)
            assertNull(securityCodeEditText.errorMessage)
            assertNull(cardHolderEditText.errorMessage)

            assertEquals("", cardNumberEditText.text?.toString())
            assertEquals("", expiryDateEditText.text?.toString())
            assertEquals("", securityCodeEditText.text?.toString())
            assertEquals("", cardHolderEditText.text?.toString())
        }
    }

    @Test
    fun onCompleteCardNumber_whenVisaAndValidNumber_shiftsFocusToExpiryDate() {
        cardInputView.addCardInputListener(mockCardListener)

        childViews.cardNumberEditText.setText(VALID_VISA_WITH_SPACES)
        verify(exactly = 1) { mockCardListener.onFocusChange(eq(CardFieldType.EXPIRY_DATE)) }
        assertTrue(childViews.expiryDateEditText.hasFocus())
    }

    @Test
    fun onFieldValidStatus_withValidNumber_isCalled() {
        cardInputView.addCardInputListener(mockCardListener)

        childViews.cardNumberEditText.setText(VALID_VISA_WITH_SPACES)

        verify(exactly = 1) { mockCardListener.onFieldValidStatus(eq(CardFieldType.NUMBER)) }
    }

    @Test
    fun onFieldInvalidStatus_withValidNumberThenInvalid_isCalled() {
        cardInputView.addCardInputListener(mockCardListener)

        childViews.cardNumberEditText.setText(VALID_VISA_WITH_SPACES)
        childViews.cardNumberEditText.setText(INVALID_VISA_LAST_CHAR_WRONG)

        verify(exactly = 1) { mockCardListener.onFieldInvalidStatus(eq(CardFieldType.NUMBER), any()) }
    }

    @Test
    fun onFieldValidStatus_withInvalidNumberThenValid_isCalled() {
        cardInputView.addCardInputListener(mockCardListener)

        childViews.cardNumberEditText.setText(INVALID_VISA_LAST_CHAR_WRONG)
        childViews.cardNumberEditText.setText(VALID_VISA_WITH_SPACES)

        verify(exactly = 1) { mockCardListener.onFieldValidStatus(eq(CardFieldType.NUMBER)) }
    }

    @Test
    fun onFieldValidStatus_withValidExpiry_isCalled() {
        cardInputView.addCardInputListener(mockCardListener)

        childViews.expiryDateEditText.setText("01/55")

        verify(exactly = 1) { mockCardListener.onFieldValidStatus(eq(CardFieldType.EXPIRY_DATE)) }
    }

    @Test
    fun onFieldInvalidStatus_withValidExpiryThenInvalid_isCalled() {
        cardInputView.addCardInputListener(mockCardListener)

        childViews.expiryDateEditText.setText("01/55")
        childViews.expiryDateEditText.setText("88/55")

        verify(exactly = 1) { mockCardListener.onFieldInvalidStatus(eq(CardFieldType.EXPIRY_DATE), any()) }
    }

    @Test
    fun onFieldValidStatus_withInvalidExpiryThenValid_isCalled() {
        cardInputView.addCardInputListener(mockCardListener)

        childViews.expiryDateEditText.setText("88/55")
        childViews.expiryDateEditText.setText("01/55")

        verify(exactly = 1) { mockCardListener.onFieldValidStatus(eq(CardFieldType.EXPIRY_DATE)) }
    }

    @Test
    fun onFieldValidStatus_withValidSecCode_isCalled() {
        cardInputView.addCardInputListener(mockCardListener)

        childViews.securityCodeEditText.setText("123")

        verify(exactly = 1) { mockCardListener.onFieldValidStatus(eq(CardFieldType.SECURITY_CODE)) }
    }

    @Test
    fun onFieldInvalidStatus_withValidSecCodeThenInvalid_isCalled() {
        cardInputView.addCardInputListener(mockCardListener)

        childViews.securityCodeEditText.setText("123")
        childViews.securityCodeEditText.setText("12")

        verify(exactly = 1) { mockCardListener.onFieldInvalidStatus(eq(CardFieldType.SECURITY_CODE), any()) }
    }

    @Test
    fun onFieldValidStatus_withInvalidSecCodeThenValid_isCalled() {
        cardInputView.addCardInputListener(mockCardListener)

        childViews.securityCodeEditText.setText("12")
        childViews.securityCodeEditText.append("3")

        verify(exactly = 1) { mockCardListener.onFieldValidStatus(eq(CardFieldType.SECURITY_CODE)) }
    }

    @Test
    fun onFieldValidStatus_withValidHolder_isCalled() {
        cardInputView.addCardInputListener(mockCardListener)

        childViews.cardHolderEditText.setText("John Doe")

        verify(exactly = 1) { mockCardListener.onFieldValidStatus(eq(CardFieldType.HOLDER)) }
    }

    @Test
    fun onFieldInvalidStatus_withValidHolderThenInvalid_isCalled() {
        cardInputView.addCardInputListener(mockCardListener)

        childViews.cardHolderEditText.setText("John Doe")
        childViews.cardHolderEditText.setText("")

        verify(exactly = 1) { mockCardListener.onFieldInvalidStatus(eq(CardFieldType.HOLDER), any()) }
    }

    @Test
    fun onFieldValidStatus_withInvalidHolderThenValid_isCalled() {
        cardInputView.addCardInputListener(mockCardListener)

        childViews.cardHolderEditText.setText("")
        childViews.cardHolderEditText.setText("John Doe")

        verify(exactly = 1) { mockCardListener.onFieldValidStatus(eq(CardFieldType.HOLDER)) }
    }

    @Test
    fun onCardValidationChanged_withValidInput_isCalledWithTrue() {
        cardInputView.addCardInputListener(mockCardListener)

        childViews.cardHolderEditText.setText("John Doe")
        childViews.cardNumberEditText.setText(VALID_VISA)
        childViews.expiryDateEditText.setText("01/55")
        childViews.securityCodeEditText.setText("123")

        verify(exactly = 1) { mockCardListener.onCardValidationChanged(eq(true)) }
    }

    @Test
    fun onCardValidationChanged_withValidCardThenInvalidNumber_isCalledWithFalse() {
        cardInputView.addCardInputListener(mockCardListener)

        childViews.cardHolderEditText.setText("John Doe")
        childViews.cardNumberEditText.setText(VALID_VISA)
        childViews.expiryDateEditText.setText("01/55")
        childViews.securityCodeEditText.setText("123")

        childViews.cardNumberEditText.setText(INVALID_VISA_LAST_CHAR_WRONG)

        verify(exactly = 1) { mockCardListener.onCardValidationChanged(eq(false)) }
    }

    @Test
    fun onCardValidationChanged_withValidCardThenInvalidExpiry_isCalledWithFalse() {
        cardInputView.addCardInputListener(mockCardListener)

        childViews.cardHolderEditText.setText("John Doe")
        childViews.cardNumberEditText.setText(VALID_VISA)
        childViews.expiryDateEditText.setText("01/55")
        childViews.securityCodeEditText.setText("123")

        childViews.expiryDateEditText.setText("88/55")

        verify(exactly = 1) { mockCardListener.onCardValidationChanged(eq(false)) }
    }

    @Test
    fun onCardValidationChanged_withValidCardThenInvalidSecCode_isCalledWithFalse() {
        cardInputView.addCardInputListener(mockCardListener)

        childViews.cardHolderEditText.setText("John Doe")
        childViews.cardNumberEditText.setText(VALID_VISA)
        childViews.expiryDateEditText.setText("01/55")
        childViews.securityCodeEditText.setText("123")

        childViews.securityCodeEditText.setText("12")

        verify(exactly = 1) { mockCardListener.onCardValidationChanged(eq(false)) }
    }

    @Test
    fun onCardValidationChanged_withVisa3DigitThenAmex_isCalledWithFalse() {
        cardInputView.addCardInputListener(mockCardListener)

        childViews.cardHolderEditText.setText("John Doe")
        childViews.cardNumberEditText.setText(VALID_VISA)
        childViews.expiryDateEditText.setText("01/55")
        childViews.securityCodeEditText.setText("123")

        // changing number from visa to amex makes the sec.code invalid as 4 digits are required now
        childViews.cardNumberEditText.setText(VALID_AMEX)

        verify(exactly = 1) { mockCardListener.onCardValidationChanged(eq(false)) }
    }

    @Test
    fun onCardValidationChanged_withAmex4DigitThenVisa_isCalledWithFalse() {
        cardInputView.addCardInputListener(mockCardListener)

        childViews.cardHolderEditText.setText("John Doe")
        childViews.cardNumberEditText.setText(VALID_AMEX)
        childViews.expiryDateEditText.setText("01/55")
        childViews.securityCodeEditText.setText("1234")

        // changing number from amex to visa makes the sec.code invalid as 3 digits are required now
        childViews.cardNumberEditText.setText(VALID_VISA)

        verify(exactly = 1) { mockCardListener.onCardValidationChanged(eq(false)) }
    }

    @Test
    fun onCompleteExpiry_whenValid_shiftsFocusToSecCode() {
        cardInputView.addCardInputListener(mockCardListener)

        childViews.expiryDateEditText.append("12")
        childViews.expiryDateEditText.append("34")
        verify(exactly = 1) { mockCardListener.onFocusChange(eq(CardFieldType.SECURITY_CODE)) }
        assertTrue(childViews.securityCodeEditText.hasFocus())
    }

    @Test
    fun setCardNumber_whenHasNoSpaces_canCreateValidCard() {
        cardInputView.setCardNumber(VALID_VISA)
        childViews.cardHolderEditText.append("John Doe")
        childViews.expiryDateEditText.append("12")
        childViews.expiryDateEditText.append("34")
        childViews.securityCodeEditText.append("123")

        val card = cardInputView.card

        assertNotNull(card)
        assertEquals(VALID_VISA, card!!.cardNumber)
    }

    @Test
    fun setCardNumber_whenHasSpaces_canCreateValidCard() {
        cardInputView.setCardNumber(VALID_VISA_WITH_SPACES)
        childViews.cardHolderEditText.append("John Doe")
        childViews.expiryDateEditText.append("12")
        childViews.expiryDateEditText.append("34")
        childViews.securityCodeEditText.append("123")

        val card = cardInputView.card

        assertNotNull(card)
        assertEquals(VALID_VISA, card!!.cardNumber)
    }

    @Test
    fun setCardNumber_withBrandPrefix_callsBrandListeners() {
        // Clear the verification of the setText() in setup
        clearMocks(mockCardBrandChangedListener, answers = false)
        cardInputView.addOnCardBrandChangedListener(mockCardBrandChangedListener)
        childViews.cardNumberEditText.setText("400861")
        assertEquals(CardBrand.Mada, cardInputView.cardBrand)
        verify(exactly = 1) { mockCardBrandChangedListener.onCardBrandChanged(eq(CardBrand.Mada)) }
    }

    @Test
    fun setEnabled_setsEnabledOnAllChildren() {
        assertTrue(cardInputView.isEnabled)
        assertTrue(childViews.cardHolderInputLayout.isEnabled)
        assertTrue(childViews.cardNumberInputLayout.isEnabled)
        assertTrue(childViews.expiryInputLayout.isEnabled)
        assertTrue(childViews.securityCodeInputLayout.isEnabled)
        assertTrue(childViews.cardHolderEditText.isEnabled)
        assertTrue(childViews.expiryDateEditText.isEnabled)
        assertTrue(childViews.cardNumberEditText.isEnabled)
        assertTrue(childViews.securityCodeEditText.isEnabled)

        cardInputView.isEnabled = false

        assertFalse(cardInputView.isEnabled)
        assertFalse(childViews.cardHolderInputLayout.isEnabled)
        assertFalse(childViews.cardNumberInputLayout.isEnabled)
        assertFalse(childViews.expiryInputLayout.isEnabled)
        assertFalse(childViews.securityCodeInputLayout.isEnabled)
        assertFalse(childViews.cardHolderEditText.isEnabled)
        assertFalse(childViews.cardNumberEditText.isEnabled)
        assertFalse(childViews.expiryDateEditText.isEnabled)
        assertFalse(childViews.securityCodeEditText.isEnabled)

        cardInputView.isEnabled = true

        assertTrue(cardInputView.isEnabled)
        assertTrue(childViews.cardHolderInputLayout.isEnabled)
        assertTrue(childViews.cardNumberInputLayout.isEnabled)
        assertTrue(childViews.expiryInputLayout.isEnabled)
        assertTrue(childViews.securityCodeInputLayout.isEnabled)
        assertTrue(childViews.cardHolderEditText.isEnabled)
        assertTrue(childViews.cardNumberEditText.isEnabled)
        assertTrue(childViews.expiryDateEditText.isEnabled)
        assertTrue(childViews.securityCodeEditText.isEnabled)
    }

    private class ChildViews(parent: CardInputView) {
        val cardHolderInputLayout: TextInputLayout = parent.findViewById(R.id.cardHolderInputLayout)
        val cardNumberInputLayout: TextInputLayout = parent.findViewById(R.id.cardNumberInputLayout)
        val expiryInputLayout: TextInputLayout = parent.findViewById(R.id.cardExpiryDateInputLayout)
        val securityCodeInputLayout: TextInputLayout = parent.findViewById(R.id.cardSecurityCodeInputLayout)

        val cardHolderEditText: CardHolderEditText = parent.findViewById(R.id.cardHolderEditText)
        val cardNumberEditText: CardNumberEditText = parent.findViewById(R.id.cardNumberEditText)
        val expiryDateEditText: CardExpiryDateEditText = parent.findViewById(R.id.cardExpiryDateEditText)
        val securityCodeEditText: FormEditText = parent.findViewById(R.id.cardSecurityCodeEditText)
    }
}