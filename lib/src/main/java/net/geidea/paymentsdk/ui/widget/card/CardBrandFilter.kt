package net.geidea.paymentsdk.ui.widget.card

import net.geidea.paymentsdk.model.CardBrand

/**
 * Filter which can take decision if a given card brand is accepted or not.
 */
fun interface CardBrandFilter {

    /**
     * Return true to accept the given card brand or false to reject it.
     */
    fun accept(cardBrand: CardBrand): Boolean
}