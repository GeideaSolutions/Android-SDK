package net.geidea.paymentsdk.ui.validation

/**
 * Represent the result of a validation check of a value.
 *
 * @see Validator
 * @see Validatable
 */
sealed class ValidationStatus {
    /**
     * Special type that is used only to signify that the validation check is not yet performed.
     */
    object Undefined : ValidationStatus()

    /**
     * Signifies that a value passed the validation check.
     */
    object Valid : ValidationStatus()

    /**
     * Signifies that a value has NOT passed the validation check.
     *
     * @param reason A concrete explanation for the invalidity.
     */
    data class Invalid(
            /**
             * A concrete explanation for the invalidity.
             */
            val reason: InvalidationReason
    ) : ValidationStatus()
}