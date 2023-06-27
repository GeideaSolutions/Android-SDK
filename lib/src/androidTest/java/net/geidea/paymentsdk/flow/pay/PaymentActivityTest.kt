package net.geidea.paymentsdk.flow.pay

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import androidx.test.espresso.web.webdriver.DriverAtoms.webClick
import androidx.test.espresso.web.webdriver.Locator
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import net.geidea.paymentsdk.GeideaPaymentSdk
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.ServerEnvironment
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.model.ExpiryDate
import net.geidea.paymentsdk.model.PaymentMethod
import net.geidea.paymentsdk.model.order.Order
import net.geidea.paymentsdk.util.waitUntilViewIsDisplayed
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@Ignore
@RunWith(AndroidJUnit4::class)
class PaymentActivityTest {

    private val appContext: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun testReturnsOk() {
        // Given

        GeideaPaymentSdk.serverEnvironment = ServerEnvironment.Test
        GeideaPaymentSdk.setCredentials(
                merchantKey = "5daf56c7-0249-409b-8650-e4ef6c4596c2",
                merchantPassword = "8555ee4e-6ac9-49ae-9ed3-73ddb4ae97fd"
        )

        // When

        val intent: Intent = PaymentData {
            amount = BigDecimal("100.0")
            currency = "EGP"
            paymentMethod = PaymentMethod(
                    cardNumber = "5123450000000008",
                    cardHolderName = "John Doe",
                    cvv = "111",
                    expiryDate = ExpiryDate(month = 12, year = 55)
            )
        }.toIntent(appContext)

        val scenario = ActivityScenario.launch<PaymentActivity>(intent)

        scenario.onActivity {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        waitUntilViewIsDisplayed(withId(R.id.webView))

        // Select "Authentication result"
        onWebView()
                .withElement(findElement(Locator.XPATH, "//select[@id='selectAuthResult']"))
                .perform(webClick())

        // Option - Success
        onWebView()
                .withElement(findElement(Locator.XPATH, "//option[@id='AUTHENTICATED']"))
                .perform(webClick())

        // Button "Submit"
        onWebView()
                .withElement(findElement(Locator.XPATH, "//input[@type='submit']"))
                .perform(webClick())

        val activityResult = scenario.result

        // Then

        assertThat(activityResult.resultCode).isEqualTo(Activity.RESULT_OK)
        assertThat(activityResult.resultData).isNotNull()
        assertThat(activityResult.resultData.extras).isNotNull()

        activityResult.resultData.setExtrasClassLoader(Order::class.java.classLoader)
        val result: GeideaResult<Order>? = activityResult.resultData.getParcelableExtra(PaymentActivity.EXTRA_RESULT)
        assertNotNull(result)
        assertTrue(result is GeideaResult.Success)
        val orderStatus: String? = (result as GeideaResult.Success).data.status
        assertEquals("Success", orderStatus)
    }
}