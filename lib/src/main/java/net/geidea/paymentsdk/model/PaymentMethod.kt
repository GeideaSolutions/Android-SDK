package net.geidea.paymentsdk.model

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
class PaymentMethod

@GeideaSdkInternal
internal constructor(
        var cardNumber: String? = null,
        var cardHolderName: String? = null,
        var owner: String? = null,
        var cvv: String? = null,
        var expiryDate: ExpiryDate? = null
) : GeideaJsonObject, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PaymentMethod

        if (cardNumber != other.cardNumber) return false
        if (cardHolderName != other.cardHolderName) return false
        if (owner != other.owner) return false
        if (cvv != other.cvv) return false
        if (expiryDate != other.expiryDate) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                cardNumber,
                cardHolderName,
                owner,
                cvv,
                expiryDate
        )
    }

    // GENERATED
    override fun toString(): String {
        return "PaymentMethod(cardNumber=<MASKED>, cardHolderName=<MASKED>, owner=<MASKED>, cvv=<MASKED>, expiryDate=$expiryDate)"
    }

    class Builder {
        /**
         * Card number (mandatory).
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var cardNumber: String? = null

        /**
         * Card holder name (mandatory).
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var cardHolderName: String? = null

        /**
         * Owner (optional).
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var owner: String? = null

        /**
         * 3- or 4-digit CVV (mandatory).
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var cvv: String? = null

        /**
         * Expiry date (mandatory).
         */
        @set:JvmSynthetic // Hide 'void' setter from Java
        var expiryDate: ExpiryDate? = null

        /**
         * Set card number (mandatory).
         */
        fun setCardNumber(cardNumber: String?): Builder = apply { this.cardNumber = cardNumber }

        /**
         * Set card holder name (mandatory).
         */
        fun setCardHolderName(cardHolderName: String?): Builder = apply { this.cardHolderName = cardHolderName }

        /**
         * Set owner (optional).
         */
        fun setOwner(owner: String?): Builder = apply { this.owner = owner }

        /**
         * Set 3- or 4-digit CVV (mandatory).
         */
        fun setCvv(cvv: String?): Builder = apply { this.cvv = cvv }

        /**
         * Set expiry date (mandatory).
         */
        fun setExpiryDate(expiryDate: ExpiryDate?): Builder = apply { this.expiryDate = expiryDate }

        /**
         * Builds [PaymentMethod].
         *
         * @throws IllegalArgumentException if a mandatory property is not set, blank or invalid
         */
        fun build(): PaymentMethod {
            cardHolderName.run {
                requireNotNull(this) { "Missing card holder name" }
                require(isNotEmpty()) { "Invalid card holder name: must not be empty" }
                require(length <= 255) { "Invalid card holder name: exceeds max length of 255"}
            }

            cardNumber.run {
                requireNotNull(this) { "Missing card number" }
                require(isNotEmpty()) { "Invalid card number: must not be empty" }
            }

            expiryDate.run {
                requireNotNull(this) { "Missing expiry date" }
                require(month in 1..12) { "Invalid expiry month: must be 1..12" }
                require(year in 1..99) { "Invalid expiry year: must be 1..99" }
            }

            cvv.run {
                requireNotNull(this) { "Missing CVV" }
                require(length in 3..4) { "Invalid CVV: must be 3 or 4 characters" }
            }

            return PaymentMethod(
                    cardNumber = cardNumber,
                    cardHolderName = cardHolderName,
                    owner = owner,
                    cvv = cvv,
                    expiryDate = expiryDate
            )
        }
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): PaymentMethod = decodeFromJson(json)
    }
}

/**
 * Builder function.
 */
@JvmSynthetic // Hide from Java callers who should use Builder.
fun PaymentMethod(initializer: PaymentMethod.Builder.() -> Unit): PaymentMethod {
    return PaymentMethod.Builder().apply(initializer).build()
}