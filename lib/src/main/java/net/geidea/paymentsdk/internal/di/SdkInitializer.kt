package net.geidea.paymentsdk.internal.di

import android.content.Context
import androidx.startup.Initializer
import net.geidea.paymentsdk.GeideaPaymentSdk

internal class SdkInitializer : Initializer<GeideaPaymentSdk> {
    override fun create(context: Context): GeideaPaymentSdk {
        GeideaPaymentSdk.applicationContext = context.applicationContext
        return GeideaPaymentSdk
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}