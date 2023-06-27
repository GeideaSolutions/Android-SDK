@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.transaction

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.auth.AuthenticationDetails
import net.geidea.paymentsdk.model.bnpl.BnplDetails
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import net.geidea.paymentsdk.model.meezaqr.MeezaDetails
import java.math.BigDecimal
import java.util.*

@Parcelize
@Serializable
class Transaction

internal constructor(
    val transactionId: String,
    val createdDate: String? = null,
    val createdBy: String? = null,
    val updatedDate: String? = null,
    val updatedBy: String? = null,
    val type: String,
    val status: String,
    val amount: BigDecimal,
    val currency: String? = null,
    val source: String? = null,
    val authorizationCode: String? = null,
    val rrn: String? = null,
    val paymentMethod: PaymentMethodInfo,
    val codes: TransactionCodes? = null,
    val authenticationDetails: AuthenticationDetails? = null,
    val postilionDetails: PostilionDetails? = null,
    val terminalDetails: TerminalDetails? = null,
    val meezaDetails: MeezaDetails? = null,
    val bnplDetails: BnplDetails? = null,
    val correlationId: String? = null,
) : GeideaJsonObject, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Transaction

        if (transactionId != other.transactionId) return false
        if (createdDate != other.createdDate) return false
        if (createdBy != other.createdBy) return false
        if (updatedDate != other.updatedDate) return false
        if (updatedBy != other.updatedBy) return false
        if (type != other.type) return false
        if (status != other.status) return false
        if (amount != other.amount) return false
        if (currency != other.currency) return false
        if (source != other.source) return false
        if (authorizationCode != other.authorizationCode) return false
        if (rrn != other.rrn) return false
        if (paymentMethod != other.paymentMethod) return false
        if (codes != other.codes) return false
        if (authenticationDetails != other.authenticationDetails) return false
        if (postilionDetails != other.postilionDetails) return false
        if (terminalDetails != other.terminalDetails) return false
        if (meezaDetails != other.meezaDetails) return false
        if (bnplDetails != other.bnplDetails) return false
        if (correlationId != other.correlationId) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                transactionId,
                createdDate,
                createdBy,
                updatedDate,
                updatedBy,
                type,
                status,
                amount,
                currency,
                source,
                authorizationCode,
                rrn,
                paymentMethod,
                codes,
                authenticationDetails,
                postilionDetails,
                terminalDetails,
                meezaDetails,
                bnplDetails,
                correlationId
        )
    }

    // GENERATED
    override fun toString(): String {
        return "Transaction(transactionId='$transactionId', createdDate=$createdDate, createdBy=$createdBy, updatedDate=$updatedDate, updatedBy=$updatedBy, type='$type', status='$status', amount=$amount, currency=$currency, source=$source, authorizationCode=$authorizationCode, rrn=$rrn, paymentMethod=$paymentMethod, codes=$codes, authenticationDetails=$authenticationDetails, postilionDetails=$postilionDetails, meezaDetails=$meezaDetails, terminalDetails=$terminalDetails, bnplDetails=$bnplDetails, correlationId=$correlationId)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): Transaction = decodeFromJson(json)
    }
}