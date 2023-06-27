package net.geidea.paymentsdk.model.transaction

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.ExpiryDate
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import java.util.*

@Parcelize
@Serializable
class Platform

@GeideaSdkInternal
internal constructor(
        val integrationType: String? = null,
        val name: String? = null,
        val version: String? = null,
        val pluginVersion: String? = null,
        val partnerId: ExpiryDate? = null
) : GeideaJsonObject, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Platform

        if (integrationType != other.integrationType) return false
        if (name != other.name) return false
        if (version != other.version) return false
        if (pluginVersion != other.pluginVersion) return false
        if (partnerId != other.partnerId) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                integrationType,
                name,
                version,
                pluginVersion,
                partnerId
        )
    }

    // GENERATED
    override fun toString(): String {
        return "Platform(integrationType=$integrationType, name=$name, version=$version, pluginVersion=$pluginVersion, partnerId=$partnerId)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): Platform = decodeFromJson(json)
    }
}