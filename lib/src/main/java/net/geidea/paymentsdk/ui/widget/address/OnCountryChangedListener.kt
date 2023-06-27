package net.geidea.paymentsdk.ui.widget.address

import net.geidea.paymentsdk.model.Country

/**
 * Listener called when an billing or shipping country has changed.
 */
fun interface OnCountryChangedListener {

    /**
     * Called when the country value has changed.
     */
    fun onCountryChanged(country: Country?)
}