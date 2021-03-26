package net.geidea.paymentsdk.sampleapp

import android.app.Application
import net.geidea.paymentsdk.GeideaPaymentAPI
import net.geidea.paymentsdk.ServerEnvironment

class SampleApplication : Application() {
    companion object {
        lateinit var INSTANCE: SampleApplication
    }

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()

        GeideaPaymentAPI.serverEnvironment = if (BuildConfig.DEBUG) {
            ServerEnvironment.Test
        } else {
            ServerEnvironment.Prod
        }
    }
}