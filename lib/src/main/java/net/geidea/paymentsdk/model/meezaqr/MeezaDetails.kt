@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.meezaqr

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import java.math.BigDecimal
import java.util.*

@Parcelize
@Serializable
class MeezaDetails

internal constructor(
        val transactionId: String? = null,
        val meezaTransactionId: String? = null,
        val type: String? = null,
        val transactionTimeStamp: String? = null,
        val adviceId: String? = null,
        val senderId: String? = null,
        val senderName: String? = null,
        val senderAddress: String? = null,
        val receiverId: String? = null,
        val receiverName: String? = null,
        val receiverAddress: String? = null,
        val amount: BigDecimal? = null,
        val currency: String? = null,
        val description: String? = null,
        val responseCode: String? = null,
        val responseDescription: String? = null,
        val interchange: BigDecimal? = null,
        val interchangeAction: String? = null,
        val reference1: String? = null,
        val reference2: String? = null,
        val tips: BigDecimal? = null,
        val convenienceFee: BigDecimal? = null,
) : GeideaJsonObject, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MeezaDetails

        if (transactionId != other.transactionId) return false
        if (meezaTransactionId != other.meezaTransactionId) return false
        if (type != other.type) return false
        if (transactionTimeStamp != other.transactionTimeStamp) return false
        if (adviceId != other.adviceId) return false
        if (senderId != other.senderId) return false
        if (senderName != other.senderName) return false
        if (senderAddress != other.senderAddress) return false
        if (receiverId != other.receiverId) return false
        if (receiverName != other.receiverName) return false
        if (receiverAddress != other.receiverAddress) return false
        if (amount != other.amount) return false
        if (currency != other.currency) return false
        if (description != other.description) return false
        if (responseCode != other.responseCode) return false
        if (responseDescription != other.responseDescription) return false
        if (interchange != other.interchange) return false
        if (interchangeAction != other.interchangeAction) return false
        if (reference1 != other.reference1) return false
        if (reference2 != other.reference2) return false
        if (tips != other.tips) return false
        if (convenienceFee != other.convenienceFee) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                transactionId,
                meezaTransactionId,
                type,
                transactionTimeStamp,
                adviceId,
                senderId,
                senderName,
                senderAddress,
                receiverId,
                receiverName,
                receiverAddress,
                amount,
                currency,
                description,
                responseCode,
                responseDescription,
                interchange,
                interchangeAction,
                reference1,
                reference2,
                tips,
                convenienceFee,
        )
    }

    override fun toString(): String {
        return "MeezaDetails(transactionId=$transactionId, meezaTransactionId=$meezaTransactionId, type=$type, transactionTimeStamp=$transactionTimeStamp, adviceId=$adviceId, senderId=$senderId, senderName=$senderName, senderAddress=$senderAddress, receiverId=$receiverId, receiverName=$receiverName, receiverAddress=$receiverAddress, amount=$amount, currency=$currency, description=$description, responseCode=$responseCode, responseDescription=$responseDescription, interchange=$interchange, interchangeAction=$interchangeAction, reference1=$reference1, reference2=$reference2, tips=$tips, convenienceFee=$convenienceFee)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): MeezaDetails = decodeFromJson(json)
    }
}