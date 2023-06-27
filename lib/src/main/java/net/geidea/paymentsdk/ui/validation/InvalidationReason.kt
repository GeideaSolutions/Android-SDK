package net.geidea.paymentsdk.ui.validation

import android.content.Context

/**
 * Descriptor interface that specifies the exact reason for a value to not pass a validation check.
 *
 * @see Validator
 */
interface InvalidationReason {
    /**
     * Returns a localized message describing the exact reason for a value
     * to not pass a validation check.
     */
    fun getMessage(context: Context): CharSequence
}