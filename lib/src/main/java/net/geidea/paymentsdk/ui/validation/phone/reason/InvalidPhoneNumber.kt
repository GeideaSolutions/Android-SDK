package net.geidea.paymentsdk.ui.validation.phone.reason

import android.content.Context
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.ui.validation.InvalidationReason

object InvalidPhoneNumber : InvalidationReason {
    override fun getMessage(context: Context): CharSequence {
        return context.getString(R.string.gd_invalid_phone_number)
    }
}