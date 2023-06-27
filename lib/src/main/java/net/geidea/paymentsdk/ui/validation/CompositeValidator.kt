package net.geidea.paymentsdk.ui.validation

/**
 * Validator composed of sequences of other validators which are executed sequentially on the same
 * value. The first validator to return a non-valid status decides the overall result. That means
 * the order of validators matters.
 *
 * @param operator logical operation - all / any
 * @param validators validators
 */
class CompositeValidator<T> private constructor(
        private val operator: Operator,
        private val validators: List<Validator<T>>,
): Validator<T> {

    override fun validate(value: T?): ValidationStatus {
        return operator.validate(validators, value)
    }

    private sealed interface Operator {
        fun <T> validate(validators: List<Validator<T>>, value: T?): ValidationStatus
    }

    object AllOperator : Operator {
        override fun <T> validate(validators: List<Validator<T>>, value: T?): ValidationStatus {
            val statuses = validators.map { it.validate(value) }
            return if (statuses.all { it == ValidationStatus.Valid }) {
                ValidationStatus.Valid
            } else {
                statuses.first { it != ValidationStatus.Valid }
            }
        }
    }

    object AnyOperator : Operator {
        override fun <T> validate(validators: List<Validator<T>>, value: T?): ValidationStatus {
            val statuses = validators.map { it.validate(value) }
            return if (statuses.any { it == ValidationStatus.Valid }) {
                ValidationStatus.Valid
            } else {
                statuses.first { it != ValidationStatus.Valid }
            }
        }
    }

    companion object {
        @JvmStatic
        public fun <T> allOf(vararg validators: Validator<T>): CompositeValidator<T> =
                CompositeValidator(AllOperator, validators.toList())

        @JvmStatic
        public fun <T> anyOf(vararg validators: Validator<T>): CompositeValidator<T> =
                CompositeValidator(AnyOperator, validators.toList())
    }
}