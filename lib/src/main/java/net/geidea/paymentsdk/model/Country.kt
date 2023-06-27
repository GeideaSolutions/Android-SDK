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
class Country private constructor(
        val key2: String? = null,
        val key3: String? = null,
        val numericCode: Int = 0,
        val nameEn: String? = null,
        val nameAr: String? = null,
        val isSupported: Boolean = false
) : GeideaJsonObject, Parcelable {

    companion object {
        @JvmStatic
        fun fromJson(json: String): Country = decodeFromJson(json)
    }

    fun getLocalizedName(languageCode: String): String? {
        return when (languageCode) {
            "en" -> nameEn
            "ar" -> nameAr
            else -> nameEn
        }
    }

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Country

        if (key2 != other.key2) return false
        if (key3 != other.key3) return false
        if (numericCode != other.numericCode) return false
        if (nameEn != other.nameEn) return false
        if (nameAr != other.nameAr) return false
        if (isSupported != other.isSupported) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                key2,
                key3,
                numericCode,
                nameEn,
                nameAr,
                isSupported,
        )
    }

    // GENERATED
    override fun toString(): String {
        return "Country(key2=$key2, key3=$key3, numericCode=$numericCode, nameEn=$nameEn, nameAr=$nameAr, isSupported=$isSupported)"
    }

    class Builder() {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var key2: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var key3: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var numericCode: Int? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var nameEn: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var nameAr: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var isSupported: Boolean = false

        fun build(): Country {
            return Country(
                    key2 = key2,
                    key3 = key3,
                    numericCode = requireNotNull(numericCode) { "Missing numericCode" },
                    nameEn = nameEn,
                    nameAr = nameAr,
                    isSupported = isSupported,
            )
        }
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun Country(initializer: Country.Builder.() -> Unit): Country {
    return Country.Builder().apply(initializer).build()
}