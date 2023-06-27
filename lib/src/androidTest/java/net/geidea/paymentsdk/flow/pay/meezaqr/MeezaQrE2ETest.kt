package net.geidea.paymentsdk.flow.pay.meezaqr

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.geidea.paymentsdk.GeideaPaymentSdk
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.ServerEnvironment
import net.geidea.paymentsdk.flow.pay.PaymentActivity
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.flow.pay.assertSuccessOrderResult
import net.geidea.paymentsdk.internal.ui.widget.Stepper
import net.geidea.paymentsdk.util.waitForView
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.hamcrest.text.IsEmptyString.isEmptyOrNullString
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

/**
 * This test relies on Meeza Mock service been activated on the Test environment.
 * More info: [Meeza Mock Service](https://confluence.endava.com/display/GDADV005/Meeza+Mock+Service)
 */
@RunWith(AndroidJUnit4::class)
class MeezaQrE2ETest {

    private val appContext: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setUp() {
        GeideaPaymentSdk.serverEnvironment = ServerEnvironment.Test
        GeideaPaymentSdk.setCredentials(
            merchantKey = "01b20d43-d004-431a-aef9-a098ed4b4d26",
            merchantPassword = "944c2844-687e-4dc3-922c-11721adfb357"
        )
    }

    @Test
    fun testHappyPath() {
        val intent: Intent = PaymentData {
            amount = BigDecimal("100.0")
            currency = "EGP"
            merchantReferenceId = "mref123"
            showReceipt = true
        }.toIntent(appContext)

        val scenario = ActivityScenario.launch<PaymentActivity>(intent)

        // Payment Options screen

        val qrCodeText = "QR code"
        onView(isRoot())
            .perform(waitForView(withText(qrCodeText), 20_000))
        onView(withText(qrCodeText))
            .perform(click())
        onView(withId(R.id.nextButton))
            .check(matches(isEnabled()))
            .perform(click())

        // Meeza QR screen - wait few seconds for Meeza Mock service to return response

        // Stepper must be visible for BNPL down payment mode only
        onView(allOf(instanceOf(Stepper::class.java)))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.currencyTextView))
            .check(matches(withText("EGP")))
        onView(withId(R.id.amountIntegerPartTextView))
            .check(matches(withText("100")))
        onView(withId(R.id.amountFractionPartTextView))
            .check(matches(withText(".00")))
        onView(isRoot())
            .perform(waitForView(allOf(withId(R.id.qrCodeImageView), isDisplayed()), 20_000))
        onView(withId(R.id.meezaLogoImageView))
            .check(matches(isDisplayed()))
        onView(withId(R.id.requestPaymentButton))
            .check(matches(isEnabled()))

        // Receipt screen

        onView(isRoot())
            .perform(waitForView(withText("Transaction approved"), 20_000))
        onView(hasSibling(withText("Date/Time")))
            .check(matches(withText(Matchers.not(isEmptyOrNullString()))))
        onView(hasSibling(withText("Username")))
            .check(matches(withText(Matchers.not(isEmptyOrNullString()))))
        onView(hasSibling(withText("Geidea Order ID")))
            .check(matches(withText(Matchers.not(isEmptyOrNullString()))))
        onView(hasSibling(withText("Total")))
            .check(matches(withText("100 EGP")))
        // TODO merchantReferenceId must be added to CreateMeezaPaymentIntentRequest
        /*onView(hasSibling(withText("Merchant Reference ID")))
            .check(matches(withText(Matchers.not(isEmptyOrNullString()))))*/

        // TODO click "Go To Merchant App"

        assertSuccessOrderResult(scenario.result)
    }
}