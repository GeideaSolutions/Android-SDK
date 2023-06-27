package net.geidea.paymentsdk.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.internal.util.Validations.EXPIRY_SEPARATOR_REGEX
import net.geidea.paymentsdk.internal.util.Validations.parseExpiryMonthAndYear
import net.geidea.paymentsdk.internal.util.Validations.splitExpiryDate
import net.geidea.paymentsdk.internal.util.Validations.validateExpiryDate
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import java.util.*

@Parcelize
@Serializable
class ExpiryDate(
        /**
         * Expiry month (1..12).
         */
        val month: Int,

        /**
         * 2-digit expiry year (1..99).
         */
        val year: Int
) : GeideaJsonObject, Parcelable {

    @IgnoredOnParcel
    public val calendar: Calendar by lazy {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
        }
    }

    val isValid: Boolean get() = validateExpiryDate(month, year) == ValidationStatus.Valid

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExpiryDate

        if (month != other.month) return false
        if (year != other.year) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int = Objects.hash(month, year)

    // GENERATED
    override fun toString(): String {
        return "ExpiryDate(month=$month, year=$year)"
    }

    /**
     * Returns this expiry date as a formatted string in the form 03/05.
     */
    fun toDisplayString(): String {
        return String.format(FORMAT, month, year)
    }

    /**
     * Returns this expiry date value in UTC milliseconds from the epoch.
     */
    fun toTimeMillis(): Long = calendar.timeInMillis

    companion object {
        @JvmStatic
        fun fromString(expiryWithSeparator: String): ExpiryDate? {
            val expiryNotSeparated = EXPIRY_SEPARATOR_REGEX.replace(expiryWithSeparator, "")
            val parts = splitExpiryDate(expiryNotSeparated)
            val (month, year) = parseExpiryMonthAndYear(parts[0], parts[1]) ?:
                return null
            return ExpiryDate(month, year)
        }

        @JvmStatic
        fun fromJson(json: String): ExpiryDate = decodeFromJson(json)

        private const val FORMAT = "%02d/%02d"
    }
}