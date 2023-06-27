package net.geidea.paymentsdk.ui.widget.card

import io.mockk.*
import io.mockk.impl.annotations.MockK
import net.geidea.paymentsdk.internal.util.VALID_VISA_WITH_SPACES
import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.ui.validation.InvalidationReason
import net.geidea.paymentsdk.ui.validation.OnInvalidStatusListener
import net.geidea.paymentsdk.ui.validation.OnValidStatusListener
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.card.validator.CardNumberValidator
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CardNumberEditTextTest {

    @MockK(relaxed = true)
    internal lateinit var mockCardBrandChangeListener: OnCardBrandChangedListener

    @MockK
    internal lateinit var mockValidator: CardNumberValidator

    @MockK(relaxed = true)
    internal lateinit var mockOnValidStatusListener: OnValidStatusListener<String>

    @MockK(relaxed = true)
    internal lateinit var mockOnInvalidStatusListener: OnInvalidStatusListener<String>

    @MockK
    internal lateinit var mockInvalidationReason: InvalidationReason

    @MockK
    internal lateinit var mockUnacceptedBrand: InvalidationReason

    lateinit var cardNumberEditText: CardNumberEditText

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        every { mockValidator.validate(eq("")) } returns ValidationStatus.Invalid(mockInvalidationReason)
        every { mockValidator.validate(isNull()) } returns ValidationStatus.Invalid(mockInvalidationReason)
        every { mockValidator.validate(eq("123")) } returns ValidationStatus.Invalid(mockInvalidationReason)
        every { mockValidator.validate(eq("456")) } returns ValidationStatus.Invalid(mockUnacceptedBrand)
        every { mockValidator.validate(eq(VALID_VISA_WITH_SPACES)) } returns ValidationStatus.Valid

        val activityController = Robolectric
                .buildActivity(CardInputTestActivity::class.java)
                .create()
                .start()
                .resume()

        cardNumberEditText = activityController.get().cardNumberEditText
        cardNumberEditText.setValidator(mockValidator)
    }

    @Test
    fun setValidator_withNotEqual_callsValidate() {
        // setValidator is called in setup()
        verify(exactly = 1) { mockValidator.validate(eq("")) }
    }

    @Test
    fun setOnValidStatusListener_whenExistsValidText_callsOnValidStatusWithText() {
        // Given
        cardNumberEditText.setText(VALID_VISA_WITH_SPACES)

        // When
        cardNumberEditText.setOnValidStatusListener(mockOnValidStatusListener)

        // Then
        verify(exactly = 1) { mockOnValidStatusListener.onValidStatus(eq(VALID_VISA_WITH_SPACES)) }
        verify { mockOnInvalidStatusListener wasNot Called }
    }

    @Test
    fun setOnInvalidStatusListener_whenExistsInvalidText_callsOnInvalidStatusWithText() {
        // When
        cardNumberEditText.setOnInvalidStatusListener(mockOnInvalidStatusListener)

        // Then
        verify(exactly = 1) { mockOnInvalidStatusListener.onInvalidStatus(eq(""),
                eq(ValidationStatus.Invalid(mockInvalidationReason))) }
        verify { mockOnValidStatusListener wasNot Called }
    }

    @Test
    fun setText_withValid() {
        // Given
        cardNumberEditText.setOnValidStatusListener(mockOnValidStatusListener)
        clearMocks(mockOnValidStatusListener)

        // When
        cardNumberEditText.setText(VALID_VISA_WITH_SPACES)

        // Then
        assertTrue(cardNumberEditText.isValid)
        assertEquals(ValidationStatus.Valid, cardNumberEditText.validationStatus)
        verify(exactly = 1) { mockOnValidStatusListener.onValidStatus(eq(VALID_VISA_WITH_SPACES)) }
        verify { mockOnInvalidStatusListener wasNot Called }
    }

    @Test
    fun setText_withInvalid() {
        // Given
        cardNumberEditText.setOnInvalidStatusListener(mockOnInvalidStatusListener)
        cardNumberEditText.setText(VALID_VISA_WITH_SPACES)
        clearMocks(mockOnInvalidStatusListener)
        clearMocks(mockOnValidStatusListener)

        // When
        cardNumberEditText.setText("123")      // Invalid

        // Then
        assertFalse(cardNumberEditText.isValid)
        assertEquals(ValidationStatus.Invalid(mockInvalidationReason), cardNumberEditText.validationStatus)
        verify(exactly = 1) { mockOnInvalidStatusListener.onInvalidStatus(eq("123"),
                eq(ValidationStatus.Invalid(mockInvalidationReason))) }
        verify { mockOnValidStatusListener wasNot Called }
    }

    @Test
    fun setText_withPrefix_cursorAtEnd() {
        every { mockValidator.validate(any()) }.returns(ValidationStatus.Valid)
        "400861".forEach { c -> cardNumberEditText.append(c.toString()) }

        val selStart = cardNumberEditText.selectionStart
        val selEnd = cardNumberEditText.selectionEnd
        assertEquals(selStart, selEnd)
        assertEquals(7, selStart)   // one space must appear in between
    }

    /*@Test
    fun setText_whenTextChangesFromValidToInvalid_setsNotValid() {
        cardNumberEditText.setText(VALID_VISA)
        // setText interacts with this mock
        clearMocks(mockCompleteListener)

        var mutable = cardNumberEditText.text.toString()
        // Make it invalid by removing a character
        mutable = mutable.substring(0, mutable.length - 1)
        cardNumberEditText.setText(mutable)

        assertFalse(cardNumberEditText.isValid)
        verify { mockCompleteListener wasNot Called }
    }

    @Test
    fun setText_whenTextIsInvalid_doesNotCallListener() {
        cardNumberEditText.setText(INVALID_VISA_LAST_CHAR_WRONG)

        assertFalse(cardNumberEditText.isValid)
        verify { mockCompleteListener wasNot Called }
    }

    @Test
    fun setText_whenTextIsInvalidTooLong_doesNotCallListener() {
        cardNumberEditText.setText("4111 1111 1111 1112 111111111111111111")

        assertFalse(cardNumberEditText.isValid)
        verify { mockCompleteListener wasNot Called }
    }*/

    @Test
    fun addCardBrandChangeListener_callsCardBrandChanged() {
        cardNumberEditText.addOnCardBrandChangedListener(mockCardBrandChangeListener)
        verify { mockCardBrandChangeListener.onCardBrandChanged(eq(CardBrand.Unknown)) }
    }

    @Test
    fun setText_withValidPrefix_callsBrandListener() {
        // Given
        every { mockValidator.validate(eq("4111 11")) } returns
                ValidationStatus.Invalid(mockInvalidationReason)
        cardNumberEditText.addOnCardBrandChangedListener(mockCardBrandChangeListener)
        clearMocks(mockCardBrandChangeListener)

        // When
        cardNumberEditText.setText("411111")

        // Then
        assertEquals(CardBrand.Visa, cardNumberEditText.cardBrand)
        verify { mockCardBrandChangeListener.onCardBrandChanged(eq(CardBrand.Visa)) }
    }
}