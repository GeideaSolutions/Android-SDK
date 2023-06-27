package net.geidea.paymentsdk.ui.validation.card.validator

import net.geidea.paymentsdk.ui.validation.Validator
import net.geidea.paymentsdk.ui.widget.card.CardBrandFilter

/**
 * Complete validator for card numbers.
 */
interface CardNumberValidator : Validator<String> {
    var cardBrandFilter: CardBrandFilter?
}