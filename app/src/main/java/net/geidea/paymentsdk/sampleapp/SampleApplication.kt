package net.geidea.paymentsdk.sampleapp

import android.app.Application
import net.geidea.paymentsdk.GeideaPaymentSdk
import net.geidea.paymentsdk.ServerEnvironment
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.util.LogLevel

class SampleApplication : Application() {
    companion object {
        lateinit var INSTANCE: SampleApplication
    }

    // Merchant config cached throughout the app lifetime
    var merchantConfiguration: MerchantConfigurationResponse? = null

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            GeideaPaymentSdk.serverEnvironment = ServerEnvironment.UAE_PREPROD
            GeideaPaymentSdk.setLogLevel(LogLevel.VERBOSE)
        } else {
            GeideaPaymentSdk.serverEnvironment = ServerEnvironment.UAE_PROD
        }
    }
}