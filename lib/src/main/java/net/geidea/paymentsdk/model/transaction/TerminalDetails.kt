@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.transaction

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import java.util.*

@Parcelize
@Serializable
class TerminalDetails

internal constructor(
        val tid: String? = null,
        val mid: String? = null,
        val transactionNumber: String? = null,
        val transactionCreateDateTime: String? = null,
        val merchantReferenceId: String? = null,
        val transactionType: String? = null,
        val transactionOutcome: String? = null,
        val providerGateId: String? = null,
        val paymentWay: String? = null,
        val reconciliationKey: String? = null,
        val transactionReceiveDateTime: String? = null,
        val transactionSentDateTime: String? = null,
        val status: String? = null,
        val message: String? = null,
        val approvalCode: String? = null,
        val responseCode: String? = null,
) : GeideaJsonObject, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TerminalDetails

        if (tid != other.tid) return false
        if (mid != other.mid) return false
        if (transactionNumber != other.transactionNumber) return false
        if (transactionCreateDateTime != other.transactionCreateDateTime) return false
        if (merchantReferenceId != other.merchantReferenceId) return false
        if (transactionType != other.transactionType) return false
        if (transactionOutcome != other.transactionOutcome) return false
        if (providerGateId != other.providerGateId) return false
        if (paymentWay != other.paymentWay) return false
        if (reconciliationKey != other.reconciliationKey) return false
        if (transactionReceiveDateTime != other.transactionReceiveDateTime) return false
        if (transactionSentDateTime != other.transactionSentDateTime) return false
        if (status != other.status) return false
        if (message != other.message) return false
        if (approvalCode != other.approvalCode) return false
        if (responseCode != other.responseCode) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                tid,
                mid,
                transactionNumber,
                transactionCreateDateTime,
                merchantReferenceId,
                transactionType,
                transactionOutcome,
                providerGateId,
                paymentWay,
                reconciliationKey,
                transactionReceiveDateTime,
                transactionSentDateTime,
                status,
                message,
                approvalCode,
                responseCode,
        )
    }

    // GENERATED
    override fun toString(): String {
        return "TerminalDetails(tid=$tid, mid=$mid, transactionNumber=$transactionNumber, transactionCreateDateTime=$transactionCreateDateTime, merchantReferenceId=$merchantReferenceId, transactionType=$transactionType, transactionOutcome=$transactionOutcome, providerGateId=$providerGateId, paymentWay=$paymentWay, reconciliationKey=$reconciliationKey, transactionReceiveDateTime=$transactionReceiveDateTime, transactionSentDateTime=$transactionSentDateTime, status=$status, message=$message, approvalCode=$approvalCode, responseCode=$responseCode)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): TerminalDetails = decodeFromJson(json)
    }
}