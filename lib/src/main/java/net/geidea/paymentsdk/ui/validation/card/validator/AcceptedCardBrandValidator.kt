package net.geidea.paymentsdk.ui.validation.card.validator

import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.Validator
import net.geidea.paymentsdk.ui.validation.card.reason.UnacceptedCardBrand
import net.geidea.paymentsdk.ui.validation.card.reason.UnrecognizedCardBrand
import net.geidea.paymentsdk.ui.widget.card.CardBrandFilter

/**
 * Card number validator that checks if the prefix is of a card brand that is accepted.
 *
 * Possible invalidation reasons:
 * - [UnrecognizedCardBrand] - if cannot recognize any card brand by the number prefix;
 * - [UnacceptedCardBrand] - if card brand is recognized but it is filtered out by [cardBrandFilter].
 *
 * @param cardBrandFilter predicate that decides which brand is accepted. If no filter is set [all
 * brands supported by the SDK][CardBrand.allSupportedBrands] are considered acceptable.
 *
 * @see net.geidea.paymentsdk.model.MerchantConfigurationResponse.paymentMethods
 * @see CardBrand.allSupportedBrands
 * @see CardBrand.prefixes
 */
class AcceptedCardBrandValidator(
        /**
         * Predicate that decides which brand is accepted. If no filter is set [all
         * brands supported by the SDK][CardBrand.allSupportedBrands] are considered acceptable.
         */
        var cardBrandFilter: CardBrandFilter?
) : Validator<String> {

    override fun validate(value: String?): ValidationStatus {
        val brand: CardBrand = if (value != null) {
            val cardNumber: String = value.replace(" ", "")
            CardBrand.fromCardNumberPrefix(cardNumber)
        } else {
            return ValidationStatus.Invalid(UnrecognizedCardBrand)
        }

        val filter = cardBrandFilter
        val isAccepted = filter?.accept(brand) ?: CardBrand.allSupportedBrands.contains(brand)
        if (isAccepted) {
            return ValidationStatus.Valid
        }

        return ValidationStatus.Invalid(UnacceptedCardBrand)
    }
}