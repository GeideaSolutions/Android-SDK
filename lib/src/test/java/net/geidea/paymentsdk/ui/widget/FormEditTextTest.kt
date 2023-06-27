package net.geidea.paymentsdk.ui.widget

import io.mockk.*
import io.mockk.impl.annotations.MockK
import net.geidea.paymentsdk.ui.validation.*
import net.geidea.paymentsdk.ui.widget.card.CardInputTestActivity
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class FormEditTextTest {

    @MockK
    internal lateinit var mockValidator: Validator<String>

    @MockK(relaxed = true)
    internal lateinit var mockOnErrorListener: OnErrorListener

    @MockK(relaxed = true)
    internal lateinit var mockOnValidStatusListener: OnValidStatusListener<String>

    @MockK(relaxed = true)
    internal lateinit var mockOnInvalidStatusListener: OnInvalidStatusListener<String>

    // SUT
    private lateinit var formEditText: FormEditText

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { mockValidator.validate("") } returns ValidationStatus.Valid
        every { mockValidator.validate("valid") } returns ValidationStatus.Valid
        every { mockValidator.validate("invalid") } returns ValidationStatus.Invalid(reason = dummyReason)

        val activity = Robolectric
                .buildActivity(CardInputTestActivity::class.java)
                .create()
                .start()
                .resume()
                .get()
        formEditText = activity.formEditText
    }

    @Test
    fun isValid_byDefault_isFalse() {
        assertFalse(formEditText.isValid)
    }

    @Test
    fun init_errorListener_isNotCalled() {
        verify { mockOnErrorListener wasNot Called }
    }

    @Test
    fun getErrorMessage_byDefault_isNull() {
        assertNull(formEditText.errorMessage)
    }

    @Test
    fun setErrorMessage_withMessage_callsOnShowErrorWithIt() {
        // Given
        formEditText.setOnErrorListener(mockOnErrorListener)

        // When
        // Initially is null
        formEditText.errorMessage = "error"

        // Then
        verify(exactly = 1) { mockOnErrorListener.onShowError(eq("error")) }
    }

    @Test
    fun setValidator_callsValidateWithExistingText() {
        // When
        formEditText.setValidator(mockValidator)

        // Then
        verify { mockValidator.validate(eq("")) }
    }

    @Test
    fun setText_withNewValue_callsValidate() {
        // Given
        formEditText.setValidator(mockValidator)
        formEditText.addTextChangedListener(ValidatingTextWatcher(formEditText))
        clearMocks(mockValidator, answers = false)

        // When
        // Initial is ""
        formEditText.setText("valid")

        // Then
        verify { mockValidator.validate(eq("valid")) }
    }

    @Test
    fun setText_withValid_updatesValidationStatus() {
        // Given
        formEditText.setValidator(mockValidator)
        formEditText.addTextChangedListener(ValidatingTextWatcher(formEditText))

        // When
        formEditText.setText("valid")

        // Then
        assertEquals(ValidationStatus.Valid, formEditText.validationStatus)
    }

    @Test
    fun setText_withInvalid_updatesValidationStatus() {
        // Given
        formEditText.setValidator(mockValidator)
        formEditText.addTextChangedListener(ValidatingTextWatcher(formEditText))
        formEditText.setText("valid")

        // When
        formEditText.setText("invalid")

        // Then
        assertTrue(formEditText.validationStatus is ValidationStatus.Invalid)
    }

    @Test
    fun getValidationStatus_byDefault_isUndefined() {
        assertEquals(ValidationStatus.Undefined, formEditText.validationStatus)
    }

    @Test(expected = IllegalArgumentException::class)
    fun setValidationStatus_withUndefined_throwsIAE() {
        // Validation status must have Undefined value only after initialization and before setting the first value
        formEditText.validationStatus = ValidationStatus.Undefined
    }

    @Test
    fun setOnValidStatusListener_whenExistingTextIsValid_callsOnValidStatus() {
        // Given
        formEditText.setValidator(mockValidator)
        formEditText.setText("valid")
        clearMocks(mockOnValidStatusListener, answers = false)   // clear the invocation of mockOnValidStatusListener

        // When
        formEditText.setOnValidStatusListener(mockOnValidStatusListener)

        verify(exactly = 1) { mockOnValidStatusListener.onValidStatus(eq("valid")) }
    }

    @Test
    fun setOnInvalidStatusListener_whenExistingTextIsInvalid_callsOnInvalidStatus() {
        // Given
        formEditText.setValidator(mockValidator)
        formEditText.addTextChangedListener(ValidatingTextWatcher(formEditText))
        formEditText.setText("invalid")
        clearMocks(mockOnInvalidStatusListener, answers = false) // clear the invocation of mockOnInvalidStatusListener

        // When
        formEditText.setOnInvalidStatusListener(mockOnInvalidStatusListener)

        // Then
        verify(exactly = 1) { mockOnInvalidStatusListener.onInvalidStatus(eq("invalid"), any()) }
    }
}