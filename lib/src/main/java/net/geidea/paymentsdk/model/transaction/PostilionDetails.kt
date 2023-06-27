package net.geidea.paymentsdk.model.transaction

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import java.util.*

@Parcelize
@Serializable
class PostilionDetails

internal constructor(
        val stan: Int? = null,
        val switchKey: String? = null,
        val originalKey: String? = null,
) : GeideaJsonObject, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PostilionDetails

        if (stan != other.stan) return false
        if (switchKey != other.switchKey) return false
        if (originalKey != other.originalKey) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                stan,
                switchKey,
                originalKey,
        )
    }

    // GENERATED
    override fun toString(): String {
        return "PostilionDetails(stan='$stan', switchKey=$switchKey, originalKey=$originalKey)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): PostilionDetails = decodeFromJson(json)
    }
}