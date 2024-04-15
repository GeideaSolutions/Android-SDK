package net.geidea.paymentsdk.model.auth.v6

import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import java.util.Objects

@Serializable
class DeviceIdentificationRequest internal constructor(
    val language: String?,
    val providerDeviceId: String?,
    val userAgent: String?
) : GeideaJsonObject {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeviceIdentificationRequest

        if (language != other.language) return false
        if (providerDeviceId != other.providerDeviceId) return false
        return userAgent == other.userAgent
    }

    override fun hashCode(): Int {
        return Objects.hash(
            language,
            providerDeviceId,
            userAgent
        )
    }

    override fun toString(): String {
        return "DeviceIdentificationRequest(language=$language, providerDeviceId=$providerDeviceId, userAgent=$userAgent)"
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
        var language: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var providerDeviceId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var userAgent: String? = null


        fun build() = DeviceIdentificationRequest(
            language = this.language,
            providerDeviceId = this.providerDeviceId,
            userAgent = this.userAgent
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
