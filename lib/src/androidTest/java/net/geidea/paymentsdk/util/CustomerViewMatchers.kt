package net.geidea.paymentsdk.util

import android.view.View
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

internal fun hasTextInputLayoutErrorText(expectedErrorText: String?): Matcher<View> = object : TypeSafeMatcher<View>() {

    override fun describeTo(description: Description?) {
    }

    override fun matchesSafely(view: View): Boolean {
        if (view !is TextInputLayout) return false
        return expectedErrorText == view.error
    }
}