package net.geidea.paymentsdk.ui.validation.card.reason

import android.content.Context
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.ui.validation.InvalidationReason

object InvalidCardHolderCharacters : InvalidationReason {
    override fun getMessage(context: Context): CharSequence = context.getString(R.string.gd_invalid_card_holder_characters)
}