package net.geidea.paymentsdk.model.paymentintent

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import java.util.*

@Parcelize
@Serializable
class CustomerResponse

@GeideaSdkInternal
internal constructor(
        val customerId: String? = null,
        val email: String? = null,
        val phone: String? = null,
        val name: String? = null,
) : Parcelable, GeideaJsonObject {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CustomerResponse

        if (customerId != other.customerId) return false
        if (email != other.email) return false
        if (phone != other.phone) return false
        if (name != other.name) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                customerId,
                email,
                phone,
                name,
        )
    }

    // GENERATED
    override fun toString(): String {
        return "CustomerResponse(customerId=$customerId, email=$email, phone=$phone, name=$name)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): CustomerResponse = decodeFromJson(json)
    }
}