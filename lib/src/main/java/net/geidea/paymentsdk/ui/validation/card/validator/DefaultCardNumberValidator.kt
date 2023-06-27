package net.geidea.paymentsdk.ui.validation.card.validator

import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.ui.validation.CompositeValidator
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.widget.card.CardBrandFilter

/**
 * Validator for card numbers that performs all required checks in the following order - Luhn
 * checksum validation, length validation and merchant acceptance by card brand.
 *
 * Possible invalidation reasons:
 * - [InvalidLuhnChecksum][net.geidea.paymentsdk.ui.validation.card.reason.InvalidLuhnChecksum]
 * - [InvalidCardNumberLength][net.geidea.paymentsdk.ui.validation.card.reason.InvalidCardNumberLength]
 * - [UnrecognizedCardBrand][net.geidea.paymentsdk.ui.validation.card.reason.UnrecognizedCardBrand]
 * - [UnacceptedCardBrand][net.geidea.paymentsdk.ui.validation.card.reason.UnacceptedCardBrand]
 *
 * @param cardBrandFilter predicate that decides which brand is accepted. If no filter is set [all
 * brands supported by the SDK][CardBrand.allSupportedBrands] are considered acceptable.
 */
class DefaultCardNumberValidator(cardBrandFilter: CardBrandFilter?) : CardNumberValidator {

    override var cardBrandFilter: CardBrandFilter?
        get() = brandValidator.cardBrandFilter
        set(value) {
            brandValidator.cardBrandFilter = value
        }

    private val luhnValidator = LuhnValidator
    private val lengthValidator = CardNumberLengthValidator
    private val brandValidator = AcceptedCardBrandValidator(cardBrandFilter)

    private val compositeValidator = CompositeValidator.allOf(
            luhnValidator,
            lengthValidator,
            brandValidator
    )

    init {
        this.cardBrandFilter = cardBrandFilter
    }

    override fun validate(value: String?): ValidationStatus {
        return compositeValidator.validate(value)
    }
}