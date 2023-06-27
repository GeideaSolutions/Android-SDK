package net.geidea.paymentsdk.ui.validation.card.validator

import net.geidea.paymentsdk.internal.util.Validations.validateExpiryDate
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.Validator

/**
 * Validator for [card expiration date][net.geidea.paymentsdk.model.Card.expiryDate].
 *
 * Possible invalidation reasons:
 * - [InvalidExpiryDate][net.geidea.paymentsdk.ui.validation.card.reason.InvalidExpiryDate]
 * - [InvalidExpiryMonth][net.geidea.paymentsdk.ui.validation.card.reason.InvalidExpiryMonth]
 * - [InvalidExpiryYear][net.geidea.paymentsdk.ui.validation.card.reason.InvalidExpiryYear]
 * - [ExpiredCard][net.geidea.paymentsdk.ui.validation.card.reason.ExpiredCard]
 */
object ExpiryDateValidator : Validator<String> {

    override fun validate(value: String?): ValidationStatus {
        return validateExpiryDate(value)
    }
}