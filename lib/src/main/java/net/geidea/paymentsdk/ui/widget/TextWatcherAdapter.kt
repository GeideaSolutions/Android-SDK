package net.geidea.paymentsdk.ui.widget

import android.text.Editable
import android.text.TextWatcher

/**
 * Implementation of [TextWatcher] with empty method implementations.
 */
open class TextWatcherAdapter : TextWatcher {
    override fun afterTextChanged(s: Editable) {
        // Implementation in sub-classes
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        // Implementation in sub-classes
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        // Implementation in sub-classes
    }
}