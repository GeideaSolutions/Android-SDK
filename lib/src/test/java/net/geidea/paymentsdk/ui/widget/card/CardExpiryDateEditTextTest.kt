package net.geidea.paymentsdk.ui.widget.card

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import net.geidea.paymentsdk.model.ExpiryDate
import net.geidea.paymentsdk.ui.validation.OnInvalidStatusListener
import net.geidea.paymentsdk.ui.validation.OnValidStatusListener
import net.geidea.paymentsdk.ui.validation.card.validator.ExpiryDateValidator
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class CardExpiryDateEditTextTest {

    @MockK(relaxed = true)
    internal lateinit var mockOnValidStatusListener: OnValidStatusListener<String>

    @MockK(relaxed = true)
    internal lateinit var mockOnInvalidStatusListener: OnInvalidStatusListener<String>

    private lateinit var expiryDateEditText: CardExpiryDateEditText

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        val activityController = Robolectric
            .buildActivity(CardInputTestActivity::class.java)
            .create()
            .start()

        expiryDateEditText = activityController.get().cardExpiryDateEditText
        expiryDateEditText.setText("")
        // Use non-mocked validator
        expiryDateEditText.setValidator(ExpiryDateValidator)
        expiryDateEditText.setOnValidStatusListener(mockOnValidStatusListener)
        expiryDateEditText.setOnInvalidStatusListener(mockOnInvalidStatusListener)
    }

    @Test
    fun inputTwoDigits_textEndsWithSlash_appendsSlash() {
        expiryDateEditText.append("0")
        expiryDateEditText.append("1")

        assertEquals("01/", expiryDateEditText.text.toString())
        assertEquals(3, expiryDateEditText.selectionStart)
    }

    @Test
    fun inputSingleDigit_whenDigitIsGreaterThanOne_prependsZero() {
        expiryDateEditText.append("2")

        assertEquals("02/", expiryDateEditText.text.toString())
        assertEquals(3, expiryDateEditText.selectionStart)
    }

    @Test
    fun inputSingleDigit_whenAtFirstCharacterButTextNotEmpty_doesNotPrependZero() {
        expiryDateEditText.append("1")

        expiryDateEditText.setSelection(0)
        expiryDateEditText.editableText.replace(0, 0, "3", 0, 1)

        assertEquals("31", expiryDateEditText.text.toString())
        assertEquals(1, expiryDateEditText.selectionStart)
    }

    @Test
    fun inputMultipleValidDigits_whenEmpty_doesNotPrependZeroOrShowErrorState() {
        expiryDateEditText.append("11")

        val text = expiryDateEditText.text.toString()
        assertEquals("11/", text)
        assertNull(expiryDateEditText.errorMessage)
        assertEquals(3, expiryDateEditText.selectionStart)
    }

    @Test
    fun addFinalDigit_withValidDigit_callsListener() {
        expiryDateEditText.append("1")
        expiryDateEditText.append("2")
        expiryDateEditText.append("5")
        expiryDateEditText.append("9")

        assertTrue(expiryDateEditText.isValid)
        verify { mockOnValidStatusListener.onValidStatus(eq("12/59")) }
    }

    @Test
    fun updateSelectionIndex_whenMovingAcrossTheGap_movesToEnd() {
        assertEquals(3, expiryDateEditText.updateSelectionIndex(3, 1, 1, 5))
    }

    @Test
    fun updateSelectionIndex_atStart_onlyMovesForwardByOne() {
        assertEquals(1, expiryDateEditText.updateSelectionIndex(1, 0, 1, 5))
    }

    @Test
    fun updateSelectionIndex_whenDeletingAcrossTheGap_staysAtEnd() {
        assertEquals(2, expiryDateEditText.updateSelectionIndex(2, 4, 0, 5))
    }

    @Test
    fun updateSelectionIndex_whenInputVeryLong_respectMaxInputLength() {
        assertEquals(5, expiryDateEditText.updateSelectionIndex(6, 4, 2, 5))
    }

    @Test
    fun inputZero_whenEmpty_doesNotShowErrorState() {
        expiryDateEditText.append("0")
        assertEquals("0", expiryDateEditText.text.toString())
    }

    @Test
    fun inputOne_whenEmpty_doesNotShowErrorState() {
        expiryDateEditText.append("1")
        assertEquals("1", expiryDateEditText.text.toString())
    }

    @Test
    fun inputTwoDigitMonth_whenInvalid_showsErrorAndDoesNotAddSlash() {
        expiryDateEditText.append("14")
        assertEquals("14", expiryDateEditText.text.toString())
    }

    @Test
    fun inputThreeDigits_whenInvalid_showsErrorAndDoesAddSlash() {
        expiryDateEditText.append("143")
        assertEquals("14/3", expiryDateEditText.text.toString())
    }

    @Test
    fun expiryDate_whenDataIsValid_returnsExpectedValues() {
        expiryDateEditText.append("12")
        expiryDateEditText.append("34")

        val retrievedDate = expiryDateEditText.expiryDate
        assertNotNull(retrievedDate)
        assertEquals(12, retrievedDate!!.month)
        assertEquals(34, retrievedDate.year)
    }

    @Test
    fun expiryDate_whenDateIsValidButExpired_returns() {
        expiryDateEditText.append("12")
        expiryDateEditText.append("01")

        assertEquals(ExpiryDate(month = 12, year = 1), expiryDateEditText.expiryDate)
    }

    @Test
    fun expiryDate_whenDateIsIncomplete_returnsNull() {
        expiryDateEditText.append("4")
        assertNull(expiryDateEditText.expiryDate)
    }
}