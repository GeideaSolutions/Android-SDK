package net.geidea.paymentsdk.ui.widget

import com.google.android.material.textfield.TextInputLayout

/**
 * Helper text input error listener that relays (sets/clears)
 * an error message to the parent [textInputLayout].
 *
 * @property textInputLayout the parent layout that serves to show the error.
 */
class TextInputErrorListener(private val textInputLayout: TextInputLayout) : OnErrorListener {
    override fun onShowError(errorMessage: CharSequence?) {
        textInputLayout.error = errorMessage
    }
}