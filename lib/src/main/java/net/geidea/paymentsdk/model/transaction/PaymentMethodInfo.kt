package net.geidea.paymentsdk.model.transaction

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.ExpiryDate
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import java.util.*

@Parcelize
@Serializable
class PaymentMethodInfo

internal constructor(
    val type: String? = null,
    val cardholderName: String? = null,
    val maskedCardNumber: String? = null,
    val expiryDate: ExpiryDate? = null,
    val brand: String? = null,
    val wallet: String? = null,
    val meezaTransactionId: String? = null,
    val meezaSenderId: String? = null,
) : GeideaJsonObject, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PaymentMethodInfo

        if (type != other.type) return false
        if (cardholderName != other.cardholderName) return false
        if (maskedCardNumber != other.maskedCardNumber) return false
        if (expiryDate != other.expiryDate) return false
        if (brand != other.brand) return false
        if (wallet != other.wallet) return false
        if (meezaTransactionId != other.meezaTransactionId) return false
        if (meezaSenderId != other.meezaSenderId) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                type,
                cardholderName,
                maskedCardNumber,
                expiryDate,
                brand,
                wallet,
                meezaTransactionId,
                meezaSenderId
        )
    }

    // GENERATED
    override fun toString(): String {
        return "PaymentMethodInfo(type=$type, cardholderName=$cardholderName, maskedCardNumber=$maskedCardNumber, expiryDate=$expiryDate, brand=$brand, wallet=$wallet, meezaTransactionId=$meezaTransactionId, meezaSenderId=$meezaSenderId)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): PaymentMethodInfo = decodeFromJson(json)
    }
}