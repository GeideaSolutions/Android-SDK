package net.geidea.paymentsdk.internal.util

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import net.geidea.paymentsdk.GeideaPaymentSdk
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.model.DEFAULT_LANGUAGE
import java.util.*

@GeideaSdkInternal
internal object LocaleUtils {

    fun Context.withSdkLocale(): Context {
        val languageCode = GeideaPaymentSdk.language?.code ?: DEFAULT_LANGUAGE
        return withLocale(languageCode)
    }

    fun Context.withLocale(language: String): Context {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(this, language)
        } else {
            updateResourcesLegacy(this, language)
        }
    }

    val Context.localeLanguage: String get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0).language ?: "en"
        } else {
            resources.configuration.locale.language
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = context.resources.configuration.apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
        return context.createConfigurationContext(configuration)
    }

    private fun updateResourcesLegacy(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        configuration.setLayoutDirection(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }
}