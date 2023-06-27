package net.geidea.paymentsdk.sampleapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Created by nama on 21,June,2023
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class LauncherActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(LauncherActivity::class.java)

    @Test
    fun config_is_visible() {
        onView(withId(R.id.item_1)).check(matches(isDisplayed()))
    }

    @Test
    fun card_payment_is_visible() {
        onView(withId(R.id.item_2)).check(matches(isDisplayed()))
    }

    @Test
    fun test_config_click_attempt() {
        Intents.init()
        onView(withId(R.id.item_1)).perform(ViewActions.click())
        intended(hasComponent(MainActivity::class.java!!.name))
        Intents.release()
    }

    @Test
    fun test_payment_click_attempt() {
        Intents.init()
        onView(withId(R.id.item_2)).perform(ViewActions.click())
        intended(hasComponent(CardPaymentActivity::class.java!!.name))
        Intents.release()
    }
}