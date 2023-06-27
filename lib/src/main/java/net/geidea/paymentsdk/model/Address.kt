package net.geidea.paymentsdk.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import java.util.*

@Parcelize
@Serializable
class Address(
        /**
         * Country code in ISO 3166 – alpha-3 format.
         */
        val countryCode: String? = null,

        /**
         * City name.
         */
        val city: String? = null,

        /**
         * Street name.
         */
        val street: String? = null,

        /**
         * Post code
         */
        val postCode: String? = null
) : GeideaJsonObject, Parcelable {

    fun copy(
            countryCode: String? = null,
            city: String? = null,
            street: String? = null,
            postCode: String? = null,
    ) = Address(
            countryCode = countryCode ?: this.countryCode,
            city = city ?: this.city,
            street = street ?: this.street,
            postCode = postCode ?: this.postCode,
    )

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Address

        if (countryCode != other.countryCode) return false
        if (city != other.city) return false
        if (street != other.street) return false
        if (postCode != other.postCode) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                countryCode,
                city,
                street,
                postCode
        )
    }

    // GENERATED
    override fun toString(): String {
        return "Address(countryCode=$countryCode, city=$city, street=$street, postCode=$postCode)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): Address = decodeFromJson(json)
    }
}

fun Address?.isNullOrEmpty(): Boolean {
    return if (this != null) {
        countryCode.isNullOrEmpty() &&
        city.isNullOrEmpty() &&
        street.isNullOrEmpty() &&
        postCode.isNullOrEmpty()
    } else {
        true
    }
}

fun Address?.equalsIgnoreCase(other: Address?): Boolean {
    return when {
        this === null && other === null -> true
        this !== null && other !== null -> {
            val equalCountryCode = this.countryCode.isNullOrEmpty() && other.countryCode.isNullOrEmpty()
                    || this.countryCode.equals(other.countryCode, ignoreCase = true)

            val equalStreet = this.street.isNullOrEmpty() && other.street.isNullOrEmpty()
                    || this.street.equals(other.street, ignoreCase = true)

            val equalCity = this.city.isNullOrEmpty() && other.city.isNullOrEmpty()
                    || this.city.equals(other.city, ignoreCase = true)

            val equalPostCode = this.postCode.isNullOrEmpty() && other.postCode.isNullOrEmpty()
                    || this.postCode.equals(other.postCode, ignoreCase = true)

            equalCountryCode && equalStreet && equalCity && equalPostCode
        }
        else -> false
    }
}