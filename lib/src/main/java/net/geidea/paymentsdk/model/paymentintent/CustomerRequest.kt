package net.geidea.paymentsdk.model.paymentintent

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import java.util.*

/**
 * Either email or phone number should be set.
 */
@Parcelize
@Serializable
class CustomerRequest(
    val email: String? = null,
    val phoneNumber: String? = null,
    val phoneCountryCode: String? = null,
    val name: String? = null,
) : GeideaJsonObject, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CustomerRequest

        if (email != other.email) return false
        if (phoneNumber != other.phoneNumber) return false
        if (phoneCountryCode != other.phoneCountryCode) return false
        if (name != other.name) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                email,
                phoneNumber,
                phoneCountryCode,
                name
        )
    }

    // GENERATED
    override fun toString(): String {
        return "CustomerRequest(email=$email, phoneNumber=$phoneNumber, phoneCountryCode=$phoneCountryCode, name=$name)"
    }

    /**
     * Builder for [CustomerRequest]
     */
    class Builder() {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var email: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var phoneNumber: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var phoneCountryCode: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var name: String? = null

        fun setEmail(email: String?): Builder = apply { this.email = email }
        fun setPhoneNumber(phoneNumber: String?): Builder = apply { this.phoneNumber = phoneNumber }
        fun setPhoneCountryCode(phoneCountryCode: String?): Builder = apply { this.phoneCountryCode = phoneCountryCode }
        fun setName(name: String?): Builder = apply { this.name = name }

        fun build(): CustomerRequest {
            return CustomerRequest(
                    email = this.email,
                    phoneNumber = this.phoneNumber,
                    phoneCountryCode = this.phoneCountryCode,
                    name = this.name,
            )
        }
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): CustomerRequest = decodeFromJson(json)
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun CustomerRequest(initializer: CustomerRequest.Builder.() -> Unit): CustomerRequest {
    return CustomerRequest.Builder().apply(initializer).build()
}