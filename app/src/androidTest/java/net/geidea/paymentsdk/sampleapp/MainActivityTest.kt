package net.geidea.paymentsdk.sampleapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import net.geidea.paymentsdk.GeideaPaymentSdk
import org.hamcrest.CoreMatchers.containsString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        GeideaPaymentSdk.clearCredentials()
    }

    @Test
    fun merchant_key_and_password_are_empty() {
        onView(ViewMatchers.withId(R.id.merchantKeyEditText))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.merchantKeyEditText))
            .check(matches(withText(containsString(""))))

        onView(ViewMatchers.withId(R.id.merchantPasswordEditText))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.merchantPasswordEditText))
            .check(matches(withText(containsString(""))))
    }

    @Test
    fun merchant_credentials_are_not_displayed_after_store() {
        onView(ViewMatchers.withId(R.id.merchantKeyEditText))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.merchantPasswordEditText))
            .check(matches(ViewMatchers.isDisplayed()))

        onView(ViewMatchers.withId(R.id.merchantKeyEditText))
            .perform(ViewActions.typeText("test_merchant_key"))
        onView(ViewMatchers.withId(R.id.merchantPasswordEditText))
            .perform(ViewActions.typeText("test_merchant_password"))

        onView(ViewMatchers.withId(R.id.storeCredentialsButton))
            .check(matches(ViewMatchers.isDisplayed()))

        onView(ViewMatchers.withId(R.id.storeCredentialsButton))
            .perform(ViewActions.click())

        onView(ViewMatchers.withId(R.id.clearCredentialsButton))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.merchantKeyEditText))
            .check(matches(ViewMatchers.isNotEnabled()))
        onView(ViewMatchers.withId(R.id.merchantPasswordEditText))
            .check(matches(ViewMatchers.isNotEnabled()))

        onView(ViewMatchers.withId(R.id.merchantKeyEditText))
            .check(matches(withText(containsString("*****"))))
        onView(ViewMatchers.withId(R.id.merchantPasswordEditText))
            .check(matches(withText(containsString("*****"))))

        onView(ViewMatchers.withId(R.id.clearCredentialsButton))
            .perform(ViewActions.click())
    }


}