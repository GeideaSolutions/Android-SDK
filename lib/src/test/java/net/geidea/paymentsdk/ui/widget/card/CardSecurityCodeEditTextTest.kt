package net.geidea.paymentsdk.ui.widget.card

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.ui.validation.OnValidStatusListener
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.Validator
import net.geidea.paymentsdk.ui.validation.dummyReason
import net.geidea.paymentsdk.ui.widget.ValidatingTextWatcher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class CardSecurityCodeEditTextTest {

    @MockK internal lateinit var mock3DigitValidator: Validator<String>
    @MockK internal lateinit var mock4DigitValidator: Validator<String>
    @RelaxedMockK internal lateinit var mockValidStatusListener: OnValidStatusListener<String>

    private lateinit var securityCodeEditText: CardSecurityCodeEditText

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        with(mock3DigitValidator) {
            every { validate(eq("")) } returns ValidationStatus.Invalid(dummyReason)
            every { validate(eq("12")) } returns ValidationStatus.Invalid(dummyReason)
            every { validate(eq("123")) } returns ValidationStatus.Valid
            every { validate(eq("1234")) } returns ValidationStatus.Invalid(dummyReason)
        }
        with(mock4DigitValidator) {
            every { validate(eq("")) } returns ValidationStatus.Invalid(dummyReason)
            every { validate(eq("12")) } returns ValidationStatus.Invalid(dummyReason)
            every { validate(eq("123")) } returns ValidationStatus.Invalid(dummyReason)
            every { validate(eq("1234")) } returns ValidationStatus.Valid
        }

        val activityController = Robolectric
                .buildActivity(CardInputTestActivity::class.java)
                .create()
                .start()

        securityCodeEditText = activityController.get().cardSecurityCodeEditText
        securityCodeEditText.setText("")
        securityCodeEditText.addTextChangedListener(ValidatingTextWatcher(securityCodeEditText))
        securityCodeEditText.setValidator(mock3DigitValidator)
        securityCodeEditText.setOnValidStatusListener(mockValidStatusListener)
    }

    @Test
    fun maxLength_4digitsByDefault() {
        //assertEquals(3, securityCodeEditText.maxLength)
        // Changed from 3 to 4 for more semblance with web
        assertEquals(4, securityCodeEditText.maxLength)
    }

    @Test
    fun maxLength_with4Digits_isValidTrue() {
        securityCodeEditText.setValidator(mock4DigitValidator)
        securityCodeEditText.onCardBrandChanged(CardBrand.AmericanExpress)
        securityCodeEditText.setText("1234")

        assertTrue(securityCodeEditText.isValid)
        verify { mockValidStatusListener.onValidStatus("1234") }
    }

    @Test(expected = IllegalArgumentException::class)
    fun maxLength_withLessThan3_throws() {
        securityCodeEditText.maxLength = 2
    }

    @Test(expected = IllegalArgumentException::class)
    fun maxLength_withGreaterThan4_throws() {
        securityCodeEditText.maxLength = 5
    }

    @Test
    fun maxLength_with3DigitsThan4_switchesCorrectly() {
        securityCodeEditText.setText("123")

        securityCodeEditText.setValidator(mock4DigitValidator)
        securityCodeEditText.onCardBrandChanged(CardBrand.AmericanExpress) // Amex requires 4 digits
        securityCodeEditText.setText("1234")

        assertTrue(securityCodeEditText.isValid)
        assertEquals("1234", securityCodeEditText.text?.toString())
        verify { mockValidStatusListener.onValidStatus(eq("1234")) }
    }

    @Test
    fun setText_with3Digits_isValidTrue() {
        securityCodeEditText.setText("123")

        assertTrue(securityCodeEditText.isValid)
    }

    @Test
    fun setText_with3Digits_callsOnCompleteListener() {
        securityCodeEditText.setText("123")

        verify { mockValidStatusListener.onValidStatus(eq("123")) }
    }

    @Test
    fun setText_with3DigitsThan2_isValidFalse() {
        securityCodeEditText.setText("123")
        securityCodeEditText.setText("12")

        assertFalse(securityCodeEditText.isValid)
    }

    @Test
    fun setText_with3DigitsThan2Than3_isValidTrue() {
        securityCodeEditText.setText("123")
        securityCodeEditText.setText("12")
        securityCodeEditText.setText("123")

        assertTrue(securityCodeEditText.isValid)
    }

    @Test
    fun setText_withLongerThanMaxLength_trimsCorrectly() {
        securityCodeEditText.setText("12345")

        // With unknown card accepts 3 or 4
        assertEquals("1234", securityCodeEditText.text?.toString())
    }

    @Test
    fun setText_withNonDigits_filtersOut() {
        securityCodeEditText.setText("abcABC(!@#$%^&*()12345_-+=<>./?")

        // With unknown card accepts 3 or 4
        assertEquals("1234", securityCodeEditText.text?.toString())
    }
}