@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.transaction

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import java.util.*

@Parcelize
@Serializable
class StatementDescriptor

internal constructor(
        val name: String,
        val phone: String? = null,
) : GeideaJsonObject, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StatementDescriptor

        if (name != other.name) return false
        if (phone != other.phone) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                name,
                phone,
        )
    }

    // GENERATED
    override fun toString(): String {
        return "StatementDescriptor(name='$name', phone=$phone)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): StatementDescriptor = decodeFromJson(json)
    }
}