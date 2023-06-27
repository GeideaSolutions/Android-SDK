package net.geidea.paymentsdk.ui.validation

import android.content.Context

internal val dummyReason = object : InvalidationReason {
    override fun getMessage(context: Context): CharSequence {
        return "dummyReason"
    }
}