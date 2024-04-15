package net.geidea.paymentsdk

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import kotlinx.coroutines.*
import net.geidea.paymentsdk.flow.ERR_MERCHANT_KEY_OR_PASS_MISSING
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.util.Logger
import net.geidea.paymentsdk.model.SdkLanguage
import net.geidea.paymentsdk.model.exception.SdkException
import net.geidea.paymentsdk.util.LogLevel

/**
 * The Geidea Payment SDK main class. Provides methods to initialize the SDK.
 *
 * Example - initializing the SDK:
 * ```
 * if (!GeideaPaymentSdk.hasCredentials) {
 *     GeideaPaymentSdk.setCredentials(<MERCHANT_ID>, <MERCHANT_PASSWORD>)
 * }
 * ```
 *
 * @see net.geidea.paymentsdk.flow.pay.PaymentContract
 */
object GeideaPaymentSdk {

    private val supervisorJob: Job = SupervisorJob()
    private val supervisorScope: CoroutineScope = CoroutineScope(SdkComponent.dispatchersProvider.unconfined + supervisorJob)

    /**
     * The Geidea server environment which the SDK will connect to.
     */
    @JvmStatic
    var serverEnvironment: ServerEnvironment = ServerEnvironment.UAE_PROD

    internal lateinit var applicationContext: Context

    private const val PREF_MERCHANT_KEY = "a"
    private const val PREF_MERCHANT_PASSWORD = "b"

    /**
     * Returns true if your Merchant credentials are already persisted on the device with the
     * [setCredentials] method.
     */
    @get:JvmName("hasCredentials")
    @JvmStatic
    val hasCredentials: Boolean get() {
        val prefs = getSharedPreferences()
        return prefs.contains(PREF_MERCHANT_KEY) && prefs.contains(PREF_MERCHANT_PASSWORD)
    }

    /**
     * Persist your Merchant credentials securely encrypted on the device for re-use in future.
     * Required by the SDK to connect to server.
     *
     * This method should be called at least once before first use of the SDK. Once credentials are
     * persisted it is not necessary to call it on each app session.
     *
     * If no credentials are stored but payment flow is started then a
     * [net.geidea.paymentsdk.flow.GeideaResult.NetworkError] with response code 002
     * will be returned as an activity result.
     *
     * IMPORTANT: *Do not hard-code your merchant credentials* in the APK. Instead it is recommended
     * to obtain them dynamically from a backend endpoint or other secure way.
     *
     * @param merchantKey your Merchant ID assigned to you by Geidea
     * @param merchantPassword your Merchant password
     *
     * @throws IllegalArgumentException if [merchantKey] or [merchantPassword] are blank.
     */
    @JvmStatic
    fun setCredentials(merchantKey: String, merchantPassword: String) {
        if (merchantKey.isBlank()) {
            throw IllegalArgumentException("Invalid merchant key")
        }
        if (merchantPassword.isBlank()) {
            throw IllegalArgumentException("Invalid merchant password")
        }
        SdkComponent.merchantsService.clearCachedMerchantConfiguration()
        getSharedPreferences().edit()
                .putString(PREF_MERCHANT_KEY, merchantKey)
                .putString(PREF_MERCHANT_PASSWORD, merchantPassword)
                .apply()
    }

    /**
     * Deletes the Merchant credentials (if already persisted) from the local secure storage.
     */
    @JvmStatic
    fun clearCredentials() {
        SdkComponent.merchantsService.clearCachedMerchantConfiguration()
        getSharedPreferences().edit()
                .remove(PREF_MERCHANT_KEY)
                .remove(PREF_MERCHANT_PASSWORD)
                .apply()
    }

    /**
     * The language to be applied on the SDK UI and network error messages. A locale with this
     * language will be set for all UI of the [payment flow][net.geidea.paymentsdk.flow.pay.PaymentContract].
     *
     * NOTE: when calling the REST API methods of [GeideaPaymentSdk] you are responsible to set
     * optional value for the 'language' property found in all network request classes:
     *
     * ```kotlin
     * val authRequest = AuthenticationRequest {
     *     // Set this to receive error messages translated in Arabic
     *     language = "ar"
     * }
     *
     * ```
     */
    @JvmStatic
    var language: SdkLanguage? = null

    /**
     * Clean up the SDK and cancel all active network requests.
     */
    @JvmStatic
    fun cleanup() {
        // TODO cancellation is not yet supported by HttpsClient
        supervisorScope.cancel(CancellationException("cleanup"))
    }

    /**
     * Set logging verbosity level. Default value: [LogLevel.DEBUG].
     *
     * Has no effect in production build (release) since logging code is removed.
     */
    @JvmStatic
    fun setLogLevel(logLevel: LogLevel) {
        Logger.logLevel = logLevel
    }

    // Privates

    @JvmName("-sp")
    private fun getSharedPreferences(): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
                "GeideaPreferences",
                masterKeyAlias,
                this.applicationContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @get:JvmName("-a")
    @GeideaSdkInternal
    internal val merchantKey: String
        get() {
            return getSharedPreferences().getString(PREF_MERCHANT_KEY, null)
                    ?: throw SdkException(errorCode = ERR_MERCHANT_KEY_OR_PASS_MISSING)
        }

    @get:JvmName("-b")
    @GeideaSdkInternal
    internal val merchantPassword: String
        get() {
            return getSharedPreferences().getString(PREF_MERCHANT_PASSWORD, null)
                    ?: throw SdkException(errorCode = ERR_MERCHANT_KEY_OR_PASS_MISSING)
        }
}