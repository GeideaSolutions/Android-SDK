package net.geidea.paymentsdk.ui.validation

/**
 * Responsible to analyze a value and perform one or more validation checks to compute
 * the [validation status][ValidationStatus] of a given input value of type [T].
 */
fun interface Validator<T> {

    /**
     * Analyzes the value and decides if it is valid or not.
     *
     * @param value the current value to be validated
     * @return the [validation status][ValidationStatus] of the value.
     * Must never return [ValidationStatus.Undefined].
     */
    fun validate(value: T?): ValidationStatus
}