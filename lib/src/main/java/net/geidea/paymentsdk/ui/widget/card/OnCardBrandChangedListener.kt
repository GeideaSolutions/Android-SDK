package net.geidea.paymentsdk.ui.widget.card

import net.geidea.paymentsdk.model.CardBrand

/**
 * Listener called when a [card brand][CardBrand] is recognized.
 */
fun interface OnCardBrandChangedListener {

    /**
     * Called when card brand is recognized from the first few digits.
     *
     * @see CardBrand.prefixes
     */
    fun onCardBrandChanged(cardBrand: CardBrand)
}