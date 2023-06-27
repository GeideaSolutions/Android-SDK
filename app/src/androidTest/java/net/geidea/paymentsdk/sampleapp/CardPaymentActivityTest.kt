package net.geidea.paymentsdk.sampleapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class CardPaymentActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(CardPaymentActivity::class.java)

    @Test
    fun empty_amount_should_show_error() {

        onView(withId(R.id.amountEditText))
            .check(matches(ViewMatchers.isDisplayed()))

        onView(withId(R.id.buttonPay))
            .check(matches(ViewMatchers.isDisplayed()))

        onView(withId(R.id.bySdkRadioButton))
            .check(matches(ViewMatchers.isChecked()))
        onView(withId(R.id.buttonPay))
            .perform(ViewActions.click())
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("Please enter amount to pay")))
    }
}