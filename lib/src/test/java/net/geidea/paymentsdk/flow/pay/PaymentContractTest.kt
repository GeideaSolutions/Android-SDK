package net.geidea.paymentsdk.flow.pay

import android.app.Activity.*
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import io.mockk.*
import io.mockk.impl.annotations.MockK
import net.geidea.paymentsdk.flow.*
import net.geidea.paymentsdk.flow.pay.PaymentActivity.Companion.EXTRA_PAYMENT_DATA
import net.geidea.paymentsdk.flow.pay.PaymentActivity.Companion.EXTRA_RESULT
import net.geidea.paymentsdk.model.exception.SdkException
import net.geidea.paymentsdk.model.order.Order
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class PaymentContractTest {

    @MockK
    lateinit var mockIntent: Intent

    init {
        MockKAnnotations.init(this)
    }

    @Test
    fun readPaymentDataOrThrow_withMissingExtra_throwsError() {
        // Given
        every { mockIntent.hasExtra(eq(EXTRA_PAYMENT_DATA)) }
                .returns(false)
        every { mockIntent.setExtrasClassLoader(any()) } just Runs

        // When, Then
        val sdkEx = assertFailsWith<SdkException> { readPaymentDataOrThrow(mockIntent) }
        assertEquals(ERR_EXTRA_PAYMENT_DATA_MISSING, sdkEx.errorCode)
    }

    @Test
    fun readPaymentDataOrThrow_withNullExtra_throwsError() {
        // Given
        every { mockIntent.hasExtra(eq(EXTRA_PAYMENT_DATA)) }
                .returns(true)
        every { mockIntent.setExtrasClassLoader(any()) } just Runs
        every { mockIntent.getParcelableExtra<PaymentData?>(eq(EXTRA_PAYMENT_DATA)) }
                .returns(null)

        // When, Then
        val sdkEx = assertFailsWith<SdkException> { readPaymentDataOrThrow(mockIntent) }
        assertEquals(ERR_EXTRA_PAYMENT_DATA_NULL, sdkEx.errorCode)
    }

    @Test
    fun readPaymentDataOrThrow_withUnexpectedTypeOfExtra_throwsError() {
        // Given
        every { mockIntent.hasExtra(eq(EXTRA_PAYMENT_DATA)) }
                .returns(true)
        every { mockIntent.setExtrasClassLoader(any()) } just Runs
        every { mockIntent.getParcelableExtra<Parcelable?>(eq(EXTRA_PAYMENT_DATA)) }
                .returns(Bundle())   // Use Bundle just as a wrong class different from the expected [GeideaResult] type

        // When, Then
        val sdkEx = assertFailsWith<SdkException> { readPaymentDataOrThrow(mockIntent) }
        assertEquals(ERR_EXTRA_PAYMENT_DATA_UNEXPECTED_TYPE, sdkEx.errorCode)
    }

    @Test
    fun readPaymentDataOrThrow_withCorrectExtra_returnsPaymentData() {
        // Given
        every { mockIntent.hasExtra(eq(EXTRA_PAYMENT_DATA)) }
                .returns(true)
        every { mockIntent.setExtrasClassLoader(any()) } just Runs
        every { mockIntent.getParcelableExtra<PaymentData?>(eq(EXTRA_PAYMENT_DATA)) }
                .returns(mockk())

        // When
        val actual: PaymentData = readPaymentDataOrThrow(mockIntent)

        // Then
        assertNotNull(actual)
    }

    @Test
    fun parseResult_withResultCodeInvalid_returnsError() {
        val contract = PaymentContract()
        var actualResult = contract.parseResult(resultCode = RESULT_FIRST_USER, Intent())
        assertEquals(GeideaResult.SdkError(ERR_INTENT_RESULT_CODE_UNEXPECTED), actualResult)

        actualResult = contract.parseResult(resultCode = -2, Intent())
        assertEquals(GeideaResult.SdkError(ERR_INTENT_RESULT_CODE_UNEXPECTED), actualResult)
    }

    @Test
    fun parseResult_withResultCodeCancelled_returnsCancelled() {
        every { mockIntent.hasExtra(eq(EXTRA_RESULT)) }
                .returns(true)
        every { mockIntent.setExtrasClassLoader(any()) } just Runs
        val resultCancelled = GeideaResult.Cancelled()
        every { mockIntent.getParcelableExtra<Parcelable>(eq(EXTRA_RESULT)) }
                .returns(resultCancelled)

        val contract = PaymentContract()
        val actualResult = contract.parseResult(resultCode = RESULT_CANCELED, mockIntent)
        assertEquals(resultCancelled, actualResult)
    }

    @Test
    fun parseResult_withNullIntent_returnsError() {
        // Given
        val contract = PaymentContract()

        // When
        val actualResult = contract.parseResult(
                resultCode = RESULT_OK,
                resultIntent = null
        )

        // Then
        assertEquals(GeideaResult.SdkError(ERR_INTENT_NULL), actualResult)
    }

    @Test
    fun parseResult_withMissingResultExtra_returnsError() {
        // Given
        val contract = PaymentContract()

        every { mockIntent.hasExtra(eq(EXTRA_RESULT)) }
                .returns(false)
        every { mockIntent.setExtrasClassLoader(any()) } just Runs

        // When
        val actualResult = contract.parseResult(
                resultCode = RESULT_OK,
                resultIntent = mockIntent
        )

        // Then
        assertEquals(GeideaResult.SdkError(ERR_EXTRA_RESULT_MISSING), actualResult)
    }

    @Test
    fun parseResult_withNullResultExtra_returnsError() {
        // Given
        val contract = PaymentContract()

        every { mockIntent.hasExtra(eq(EXTRA_RESULT)) }
                .returns(true)
        every { mockIntent.setExtrasClassLoader(any()) } just Runs
        every { mockIntent.getParcelableExtra<GeideaResult<Order>?>(eq(EXTRA_RESULT)) }
                .returns(null)

        // When
        val actualResult = contract.parseResult(
                resultCode = RESULT_OK,
                resultIntent = mockIntent
        )

        // Then
        assertEquals(GeideaResult.SdkError(ERR_EXTRA_RESULT_NULL), actualResult)
    }

    @Test
    fun parseResult_withUnexpectedTypeOfResultExtra_returnsError() {
        // Given
        val contract = PaymentContract()

        every { mockIntent.hasExtra(eq(EXTRA_RESULT)) }
                .returns(true)
        every { mockIntent.setExtrasClassLoader(any()) } just Runs
        every { mockIntent.getParcelableExtra<Parcelable>(eq(EXTRA_RESULT)) }
                .returns(Bundle())   // Use Bundle just as a wrong class different from the expected [GeideaResult] type

        // When
        val actualResult = contract.parseResult(
                resultCode = RESULT_OK,
                resultIntent = mockIntent
        )

        // Then
        assertEquals(GeideaResult.SdkError(ERR_EXTRA_RESULT_UNEXPECTED_TYPE), actualResult)
    }

    @Test
    fun parseResult_withCorrectResult_returnsSuccess() {
        // Given
        val contract = PaymentContract()
        val mockOrder = mockk<Order>()

        every { mockIntent.hasExtra(eq(EXTRA_RESULT)) }
                .returns(true)
        every { mockIntent.setExtrasClassLoader(any()) } just Runs
        every { mockIntent.getParcelableExtra<Parcelable>(eq(EXTRA_RESULT)) }
                .returns(GeideaResult.Success(mockOrder))

        // When
        val actualResult = contract.parseResult(
                resultCode = RESULT_OK,
                resultIntent = mockIntent
        )

        // Then
        assertEquals(GeideaResult.Success(mockOrder), actualResult)
    }
}