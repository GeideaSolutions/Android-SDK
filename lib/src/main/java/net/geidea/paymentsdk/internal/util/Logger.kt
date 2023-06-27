package net.geidea.paymentsdk.internal.util

import android.util.Log
import net.geidea.paymentsdk.util.LogLevel

/**
 * Logger utility class.
 *
 * Has no effect in production build (release).
 */
internal object Logger {

    internal const val LOG_TAG = "Geidea"

    /**
     * Logging verbosity level. Default value: [LogLevel.DEBUG].
     */
    @JvmField
    var logLevel: LogLevel = LogLevel.DEBUG

    @JvmStatic
    fun logv(message: String) {
        log(LogLevel.VERBOSE, message)
    }

    @JvmStatic
    fun logd(message: String) {
        log(LogLevel.DEBUG, message)
    }

    @JvmStatic
    fun logi(message: String) {
        log(LogLevel.INFO, message)
    }

    @JvmStatic
    fun logw(message: String) {
        log(LogLevel.WARN, message)
    }

    @JvmStatic
    fun loge(message: String) {
        log(LogLevel.ERROR, message)
    }

    private fun log(level: LogLevel, message: String) {
        if (logLevel.value <= level.value) {
            when (level) {
                LogLevel.VERBOSE -> Log.v(LOG_TAG, message)
                LogLevel.DEBUG -> Log.d(LOG_TAG, message)
                LogLevel.INFO -> Log.i(LOG_TAG, message)
                LogLevel.WARN -> Log.w(LOG_TAG, message)
                LogLevel.ERROR -> Log.e(LOG_TAG, message)
            }
        }
    }
}