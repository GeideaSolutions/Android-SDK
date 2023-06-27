package net.geidea.paymentsdk.ui.validation.card.validator

import net.geidea.paymentsdk.ui.validation.SimpleCharFilter

/**
 * Input filter for [CVV][net.geidea.paymentsdk.model.Card.cvv].
 */
object CvvFilter : SimpleCharFilter(
        filterFunc = { c -> c in '0'..'9' }
)