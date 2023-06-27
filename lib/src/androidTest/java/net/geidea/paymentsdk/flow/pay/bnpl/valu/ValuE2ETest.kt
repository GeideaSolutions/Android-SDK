package net.geidea.paymentsdk.flow.pay.bnpl.valu

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.geidea.paymentsdk.GeideaPaymentSdk
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.ServerEnvironment
import net.geidea.paymentsdk.flow.pay.PaymentActivity
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.flow.pay.assertSuccessOrderResult
import net.geidea.paymentsdk.util.hasTextInputLayoutErrorText
import net.geidea.paymentsdk.util.waitForView
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
class ValuE2ETest {

    private val appContext: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setUp() {
        // Merchant configured to return ValU adminFees=0. Zero admin fees are required to test
        // the path without down payment.
        GeideaPaymentSdk.serverEnvironment = ServerEnvironment.Test
        GeideaPaymentSdk.setCredentials(
            merchantKey = "5daf56c7-0249-409b-8650-e4ef6c4596c2",
            merchantPassword = "8555ee4e-6ac9-49ae-9ed3-73ddb4ae97fd"
        )
    }

    @Test
    fun testHappyPath_noDownPayment() {
        val intent: Intent = PaymentData {
            amount = BigDecimal("1200.0")
            currency = "EGP"
            merchantReferenceId = "mref123"
            showReceipt = true
        }.toIntent(appContext)

        val scenario = ActivityScenario.launch<PaymentActivity>(intent)

        // Payment Options screen

        val valuInstallments = "ValU Installments"
        onView(isRoot())
            .perform(waitForView(withText(valuInstallments), 20_000))
        onView(withText(valuInstallments))
            .perform(click())
        onView(withId(R.id.nextButton))
            .check(matches(isEnabled()))
            .perform(click())

        // ValU Verify phone number screen

        onView(withId(R.id.backImageButton))
            .check(matches(isClickable()))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.textView))
            .check(matches(withText("Confirm your phone number")))
        onView(withId(R.id.progressTextView))
            .check(matches(withText("1 / 3")))
        onView(withId(R.id.nextButton))
            .check(matches(isNotEnabled()))
        onView(withId(R.id.phoneNumberEditText))
            .check(matches(withText("")))
            .perform(typeText("1201297772"), closeSoftKeyboard())    // Triggers success
        onView(withId(R.id.phoneNumberInputLayout))
            .check(matches(hasTextInputLayoutErrorText(null)))
        onView(withId(R.id.nextButton))
            .check(matches(isEnabled()))
            .perform(click())

        // ValU Installment Plan screen

        onView(withId(R.id.backImageButton))
            .check(matches(isClickable()))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(isRoot())
            .perform(waitForView(withText("Choose your preferred installment plan"), 20_000))
        onView(withId(R.id.progressTextView))
            .check(matches(withText("2 / 3")))
        onView(isRoot())
            .perform(waitForView(allOf(withId(R.id.installmentPlanGridLayout), hasMinimumChildCount(1)), 20_000))
        onView(withId(R.id.nextButton))
            .check(matches(isNotEnabled()))
        // Click on 1st installment plan
        val matchFirstInstallmentPlan = allOf(withParent(withId(R.id.installmentPlanGridLayout)), withParentIndex(0))
        onView(matchFirstInstallmentPlan)
            .perform(click())
            .check(matches(isChecked()))
        onView(withId(R.id.totalAmountTextView))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            .check(matches(withText("1,200 EGP")))
        onView(withId(R.id.financedAmountTextView))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            .check(matches(withText("1,200 EGP")))
        onView(withId(R.id.adminFeesTextView))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            .check(matches(withText("0 EGP")))      // This assertion relies on the Merchant being configured for zero adminFees
        onView(withId(R.id.downPaymentTextView))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            .check(matches(withText("0 EGP")))
        onView(withId(R.id.totalUpfrontTextView))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            .check(matches(withText("0 EGP")))      // This assertion relies on the Merchant being configured for zero adminFees
        onView(withId(R.id.nestedScrollView))
            .perform(swipeUp())
        sleep(500)
        onView(withText("Next"))
            .check(matches(isEnabled()))
            .perform(click())

        // ValU OTP screen

        onView(isRoot())
            .perform(waitForView(withText("Fill in the One-Time Password"), 20_000))
        onView(withId(R.id.progressTextView))
            .check(matches(withText("3 / 3")))
        onView(withId(R.id.purchaseButton))
            .check(matches(isNotEnabled()))
        // Click on the first child of OtpInputView
        onView(allOf(withParent(withId(R.id.otpInputView)), withParentIndex(0)))
            .perform(typeText("123456"), closeSoftKeyboard())
        onView(withId(R.id.purchaseButton))
            .check(matches(isEnabled()))
            .perform(click())

        // Receipt screen

        val goToButtonId = R.id.returnButton
        onView(isRoot())
            .perform(waitForView(withId(goToButtonId), 20_000))
        sleep(1_000)
        onView(hasSibling(withText("Geidea Order ID")))
            .check(matches(withText(not(isEmptyOrNullString()))))
        onView(hasSibling(withText("Total Amount")))
            .check(matches(withText("1,200 EGP")))
        onView(hasSibling(withText("Financed Amount")))
            .check(matches(withText("1,200 EGP")))
        onView(hasSibling(withText("Installment Amount")))
            .check(matches(withText(not(isEmptyOrNullString()))))
        onView(hasSibling(withText("Tenure")))
            .check(matches(withText(not(isEmptyOrNullString()))))
        onView(hasSibling(withText("Down Payment")))
            .check(matches(withText("—")))
        onView(hasSibling(withText("To-U Balance")))
            .check(matches(withText("—")))
        onView(hasSibling(withText("Cashback Amount")))
            .check(matches(withText("—")))
        onView(hasSibling(withText("Purchase Fees")))
            .check(matches(withText("0 EGP")))  // relies on a Merchant configured with zero adminFees
        onView(hasSibling(withText("Reference number")))
            .check(matches(withText(not(isEmptyOrNullString()))))

        assertSuccessOrderResult(scenario.result)
    }

    /**
     * This test relies on Meeza Mock service been activated on the Test environment.
     * More info: [Meeza Mock Service](https://confluence.endava.com/display/GDADV005/Meeza+Mock+Service)
     */
    @Test
    fun testHappyPath_withDownPayment_meezaQr() {

        val intent: Intent = PaymentData {
            amount = BigDecimal("1200.0")
            currency = "EGP"
            merchantReferenceId = "mref123"
            showReceipt = true
        }.toIntent(appContext)

        val scenario = ActivityScenario.launch<PaymentActivity>(intent)

        // Payment Options screen (tested in noDownPayment case)

        val valuInstallments = "ValU Installments"
        onView(isRoot())
            .perform(waitForView(withText(valuInstallments), 20_000))
        onView(withText(valuInstallments))
            .perform(click())
        onView(withId(R.id.nextButton))
            .perform(click())

        // ValU Verify phone number screen (tested in noDownPayment case)

        onView(withId(R.id.phoneNumberEditText))
            .perform(typeText("1201297772"), closeSoftKeyboard())    // Triggers success
        onView(withId(R.id.nextButton))
            .perform(click())

        // ValU Installment Plan screen

        onView(isRoot())
            .perform(waitForView(withText("Choose your preferred installment plan"), 20_000))
        onView(withId(R.id.downPaymentAmountEditText))
            .perform(typeText("100"), closeSoftKeyboard())
        onView(withId(R.id.giftCardAmountEditText))
            .perform(typeText("200"), closeSoftKeyboard())
        onView(withId(R.id.campaignAmountEditText))
            .perform(typeText("300"), closeSoftKeyboard())
        sleep(3_000)
        onView(isRoot())
            .perform(waitForView(allOf(withId(R.id.installmentPlanGridLayout), hasMinimumChildCount(1)), 20_000))
        // Click on 1st installment plan
        val matchFirstInstallmentPlan = allOf(withParent(withId(R.id.installmentPlanGridLayout)), withParentIndex(0))
        onView(matchFirstInstallmentPlan)
            .perform(click())
            .check(matches(isChecked()))
        onView(withId(R.id.totalAmountTextView))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            .check(matches(withText("1,200 EGP")))
        onView(withId(R.id.financedAmountTextView))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            .check(matches(withText("600 EGP")))
        onView(withId(R.id.adminFeesTextView))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            .check(matches(withText("0 EGP")))      // This assertion relies on the Merchant being configured for zero adminFees
        onView(withId(R.id.downPaymentTextView))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            .check(matches(withText("100 EGP")))
        onView(withId(R.id.totalUpfrontTextView))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            .check(matches(withText("100 EGP")))      // This assertion relies on the Merchant being configured for zero adminFees
        onView(withId(R.id.nestedScrollView))
            .perform(swipeUp())
        sleep(2_000)        // Wait for latest installmentPlans response
        onView(withText("Next"))
            .check(matches(isEnabled()))
            .perform(click())

        // Payment options screen

        // Test only non-BNPL payment methods are shown
        sleep(200)
        bnplPaymentMethodItemTexts.forEach { itemText ->
            onView(withText(itemText))
                .check(doesNotExist())
        }
        val qrCode = "QR code"
        onView(isRoot())
            .perform(waitForView(withText(qrCode), 20_000))
        onView(withText(qrCode))
            .perform(click())
        onView(withId(R.id.nextButton))
            .perform(click())

        // Meeza QR screen - wait few seconds for Meeza Mock service to return response

        onView(withId(R.id.backImageButton))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withText("Process Down Payment"))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.progressTextView))
            .check(matches(withText("3 / 4")))
        onView(withId(R.id.currencyTextView))
            .check(matches(withText("EGP")))
        onView(withId(R.id.amountIntegerPartTextView))
            .check(matches(withText("100")))
        onView(withId(R.id.amountFractionPartTextView))
            .check(matches(withText(".00")))

        // ValU OTP screen

        onView(isRoot())
            .perform(waitForView(withText("Fill in the One-Time Password"), 20_000))
        onView(withId(R.id.progressTextView))
            .check(matches(withText("4 / 4")))
        onView(withId(R.id.purchaseButton))
            .check(matches(isNotEnabled()))
        // Click on the first child of OtpInputView
        onView(allOf(withParent(withId(R.id.otpInputView)), withParentIndex(0)))
            .perform(typeText("123456"), closeSoftKeyboard())
        onView(withId(R.id.purchaseButton))
            .check(matches(isEnabled()))
            .perform(click())

        // Receipt screen

        val goToButtonId = R.id.returnButton
        onView(isRoot())
            .perform(waitForView(withId(goToButtonId), 20_000))
        sleep(1_000)
        onView(hasSibling(withText("Down Payment")))
            .check(matches(withText("100 EGP")))
        onView(hasSibling(withText("To-U Balance")))
            .check(matches(withText("200 EGP")))
        onView(hasSibling(withText("Cashback Amount")))
            .check(matches(withText("300 EGP")))

        onView(withId(R.id.nestedScrollView))
            .perform(swipeUp())
        sleep(1_000)
        onView(withId(goToButtonId))
            .perform(swipeUp(), click())

        assertSuccessOrderResult(scenario.result)
    }

    companion object {
        // BNPL PM titles in Payment Options screen
        private val bnplPaymentMethodItemTexts = arrayOf("ValU Installments", "Shahry", "Souhoola")
    }
}