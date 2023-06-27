package net.geidea.paymentsdk.model.bnpl.souhoola

import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import net.geidea.paymentsdk.model.common.GeideaResponse
import java.util.*

@Parcelize
@Serializable
class SelectInstallmentPlanResponse

@GeideaSdkInternal
internal constructor(
        override val responseCode: String? = null,
        override val responseMessage: String? = null,
        override val detailedResponseCode: String? = null,
        override val detailedResponseMessage: String? = null,
        override val language: String? = null,
        val orderId: String? = null,
        val souhoolaTransactionId: String? = null,
        val nextStep: String,
) : GeideaJsonObject, GeideaResponse {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SelectInstallmentPlanResponse

        if (responseCode != other.responseCode) return false
        if (responseMessage != other.responseMessage) return false
        if (detailedResponseCode != other.detailedResponseCode) return false
        if (detailedResponseMessage != other.detailedResponseMessage) return false
        if (language != other.language) return false
        if (orderId != other.orderId) return false
        if (souhoolaTransactionId != other.souhoolaTransactionId) return false
        if (nextStep != other.nextStep) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                responseCode,
                responseMessage,
                detailedResponseCode,
                detailedResponseMessage,
                language,
                orderId,
                souhoolaTransactionId,
                nextStep,
        )
    }

    override fun toString(): String {
        return "InstallmentPlanSelectedResponse(responseCode=$responseCode, responseMessage=$responseMessage, detailedResponseCode=$detailedResponseCode, detailedResponseMessage=$detailedResponseMessage, language=$language, orderId=$orderId, souhoolaTransactionId=$souhoolaTransactionId, nextStep=$nextStep)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): SelectInstallmentPlanResponse = decodeFromJson(json)

        public const val NextStepProceedWithBnpl = "proceedWithBNPL"
        public const val NextStepProceedWithDownPayment = "proceedWithDownPayment"
    }
}