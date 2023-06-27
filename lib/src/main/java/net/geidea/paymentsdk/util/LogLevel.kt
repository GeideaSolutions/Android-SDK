package net.geidea.paymentsdk.util

import android.util.Log

public enum class LogLevel(internal val value: Int) {
    VERBOSE(Log.VERBOSE),
    DEBUG(Log.DEBUG),
    INFO(Log.INFO),
    WARN(Log.WARN),
    ERROR(Log.ERROR),
}