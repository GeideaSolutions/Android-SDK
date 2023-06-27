package net.geidea.paymentsdk.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.internal.util.Validations.luhnCheck
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import java.util.*

/**
 * Data class representing debit/credit card details.
 *
 * @see PaymentMethod
 */
@Parcelize
@Serializable
class Card

@GeideaSdkInternal
internal constructor(
        var cardNumber: String,
        var cardHolderName: String,
        var owner: String? = null,
        var cvv: String,
        var expiryDate: ExpiryDate
) : GeideaJsonObject, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Card

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
        return "Card(cardNumber=<MASKED>, cardHolderName=$cardHolderName, owner=$owner, cvv=<MASKED>, expiryDate=$expiryDate)"
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
         * Builds [Card].
         *
         * @throws IllegalArgumentException if a mandatory property is not set, blank or invalid
         */
        fun build(): Card {
            cardHolderName.run {
                requireNotNull(this) { "Missing card holder name" }
                require(isNotEmpty()) { "Invalid card holder name: must not be empty" }
                require(length <= 255) { "Invalid card holder name: exceeds max length of 255"}
            }

            cardNumber.run {
                requireNotNull(this) { "Missing card number" }
                require(isNotEmpty()) { "Invalid card number: must not be empty" }
                require(luhnCheck(cardNumber)) { "Invalid card number: Luhn verification failed" }
            }

            expiryDate.run {
                requireNotNull(this) { "Missing expiry date" }
                require(month in 1..12) { "Invalid expiry month: must tbe between 01 and 12" }
            }
            expiryDate.run {
                requireNotNull(this) { "Missing expiry date" }
                require(year in 1..99) { "Invalid expiry year: must be between 01 and 99" }
            }

            cvv.run {
                requireNotNull(this) { "Missing CVV" }
                require(length in 3..4) { "Invalid CVV: must be 3 or 4 characters" }
                if (CardBrand.AmericanExpress == CardBrand.fromCardNumberPrefix(cardNumber!!)) {
                    require(length == 4) { "Invalid CVV: 4-digit CVV required for amex" }
                }
            }

            return Card(
                    cardNumber = cardNumber!!,
                    cardHolderName = cardHolderName!!,
                    owner = owner,
                    cvv = cvv!!,
                    expiryDate = expiryDate!!
            )
        }
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): Card = decodeFromJson(json)
    }
}

/**
 * Builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun Card(initializer: Card.Builder.() -> Unit): Card {
    return Card.Builder().apply(initializer).build()
}