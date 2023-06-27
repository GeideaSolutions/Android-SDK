package net.geidea.paymentsdk.model.auth.v4

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import java.util.*

@Serializable
class DeviceIdentificationRequest internal constructor(
    val browser: String?,
    val customerIp: String?,
    val colorDepth: Int?,
    val language: String?,
    val timezoneOffset: Int?,
    val screenHeight: Int?,
    val screenWidth: Int?,
    val javaEnabled: Boolean?,
    val javaScriptEnabled: Boolean?,
    val providerDeviceId: String?,
    val acceptHeaders: String?,
    @SerialName("3DSecureChallengeWindowSize")
    val threeDSecureChallengeWindowSize: String?
) : GeideaJsonObject {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeviceIdentificationRequest

        if (browser != other.browser) return false
        if (customerIp != other.customerIp) return false
        if (colorDepth != other.colorDepth) return false
        if (language != other.language) return false
        if (timezoneOffset != other.timezoneOffset) return false
        if (screenHeight != other.screenHeight) return false
        if (screenWidth != other.screenWidth) return false
        if (javaEnabled != other.javaEnabled) return false
        if (javaScriptEnabled != other.javaScriptEnabled) return false
        if (providerDeviceId != other.providerDeviceId) return false
        if (acceptHeaders != other.acceptHeaders) return false
        if (threeDSecureChallengeWindowSize != other.threeDSecureChallengeWindowSize) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(
            browser,
            customerIp,
            colorDepth,
            language,
            timezoneOffset,
            screenHeight,
            screenWidth,
            javaEnabled,
            javaScriptEnabled,
            providerDeviceId,
            acceptHeaders,
            threeDSecureChallengeWindowSize,
        )
    }

    override fun toString(): String {
        return "DeviceIdentificationRequest(browser=$browser, customerIp=$customerIp, colorDepth=$colorDepth, language=$language, timezoneOffset=$timezoneOffset, screenHeight=$screenHeight, screenWidth=$screenWidth, javaEnabled=$javaEnabled, javaScriptEnabled=$javaScriptEnabled, providerDeviceId=$providerDeviceId, acceptHeaders=$acceptHeaders, threeDSecureChallengeWindowSize=$threeDSecureChallengeWindowSize)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): DeviceIdentificationRequest = decodeFromJson(json)
    }

    /**
     * Builder for [DeviceIdentificationRequest]
     */
    class Builder() {

        @set:JvmSynthetic // Hide 'void' setter from Java
        var browser: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var customerIp: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var colorDepth: Int? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var language: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var timezoneOffset: Int? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var screenHeight: Int? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var screenWidth: Int? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var javaEnabled: Boolean? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var javaScriptEnabled: Boolean? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var providerDeviceId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var acceptHeaders: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var threeDSecureChallengeWindowSize: String? = null

        fun build() = DeviceIdentificationRequest(
            browser = this.browser,
            customerIp = this.customerIp,
            colorDepth = this.colorDepth,
            language = this.language,
            timezoneOffset = this.timezoneOffset,
            screenHeight = this.screenHeight,
            screenWidth = this.screenWidth,
            javaEnabled = this.javaEnabled,
            javaScriptEnabled = this.javaScriptEnabled,
            providerDeviceId = this.providerDeviceId,
            acceptHeaders = this.acceptHeaders,
            threeDSecureChallengeWindowSize = this.threeDSecureChallengeWindowSize,
        )
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun DeviceIdentificationRequest(initializer: DeviceIdentificationRequest.Builder.() -> Unit): DeviceIdentificationRequest {
    return DeviceIdentificationRequest.Builder().apply(initializer).build()
}
