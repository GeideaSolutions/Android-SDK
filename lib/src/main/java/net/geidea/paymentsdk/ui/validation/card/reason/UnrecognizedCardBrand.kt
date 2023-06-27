package net.geidea.paymentsdk.ui.validation.card.reason

import android.content.Context
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.ui.validation.InvalidationReason

object UnrecognizedCardBrand : InvalidationReason {
    override fun getMessage(context: Context): CharSequence {
        return context.getString(R.string.gd_unrecognized_card_brand)
    }
}