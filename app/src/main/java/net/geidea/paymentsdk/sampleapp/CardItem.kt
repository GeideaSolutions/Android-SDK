package net.geidea.paymentsdk.sampleapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.geidea.paymentsdk.model.transaction.PaymentMethodInfo

@Parcelize
@Serializable
data class CardItem(
        val tokenId: String,
        val cardholderName: String,
        val maskedCardNumber: String,
        val expiryDate: String,
        val brand: String?
) : Parcelable {
    constructor(tokenId: String, card: PaymentMethodInfo) : this(
            tokenId = tokenId,
            cardholderName = card.cardholderName!!,
            maskedCardNumber = card.maskedCardNumber!!,
            expiryDate = "${card.expiryDate!!.month} / ${card.expiryDate!!.year}",
            brand = card.brand
    )

    companion object {
        fun fromJson(json: String): CardItem = Json.decodeFromString(json)
    }
}