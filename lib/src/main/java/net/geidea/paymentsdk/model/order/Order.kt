@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.order

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.flow.pay.OrderItem
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.Address
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import net.geidea.paymentsdk.model.paymentintent.PaymentIntent
import net.geidea.paymentsdk.model.transaction.MultiCurrency
import net.geidea.paymentsdk.model.transaction.PaymentMethodInfo
import net.geidea.paymentsdk.model.transaction.StatementDescriptor
import net.geidea.paymentsdk.model.transaction.Transaction
import java.math.BigDecimal
import java.util.*

@Parcelize
@Serializable
class Order

/**
 * For SDK internal usage only!
 */
@GeideaSdkInternal
internal constructor(
    val orderId: String,
    val parentOrderId: String? = null,
    val createdDate: String? = null,
    val createdBy: String? = null,
    val updatedDate: String? = null,
    val updatedBy: String? = null,
    val amount: BigDecimal? = null,
    val totalAmount: BigDecimal? = null,
    val settleAmount: BigDecimal? = null,
    val tipAmount: BigDecimal? = null,
    val convenienceFeeAmount: BigDecimal? = null,
    val currency: String? = null,
    val language: String? = null,
    val detailedStatus: String? = null,
    val status: String? = null,
    val threeDSecureId: String? = null,
    val merchantId: String? = null,
    val merchantPublicKey: String? = null,
    val merchantReferenceId: String? = null,
    val mcc: String? = null,
    val callbackUrl: String? = null,
    val customerEmail: String? = null,
    val billingAddress: Address? = null,
    val shippingAddress: Address? = null,
    val returnUrl: String? = null,
    val cardOnFile: Boolean = false,
    val tokenId: String? = null,
    val initiatedBy: String? = null,
    val agreementId: String? = null,
    val agreementType: String? = null,
    val paymentOperation: String? = null,
    val paymentMethod: PaymentMethodInfo? = null,
    val paymentMethods: Set<String>? = null,
    val restrictPaymentMethods: Boolean? = false,

    val totalAuthorizedAmount: BigDecimal? = null,
    val totalCapturedAmount: BigDecimal? = null,
    val totalRefundedAmount: BigDecimal? = null,

    val paymentIntent: PaymentIntent? = null,
    val transactions: List<Transaction>? = null,
    val statementDescriptor: StatementDescriptor? = null,
    val description: String? = null,
    val orderSource: String? = null,
    val orderItems: List<OrderItem>? = null,
    val cashOnDelivery: Boolean? = null,
    val amountToCollect: BigDecimal? = BigDecimal.ZERO,
    val multiCurrency: MultiCurrency? = null,
) : GeideaJsonObject, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Order

        if (orderId != other.orderId) return false
        if (parentOrderId != other.parentOrderId) return false
        if (createdDate != other.createdDate) return false
        if (createdBy != other.createdBy) return false
        if (updatedDate != other.updatedDate) return false
        if (updatedBy != other.updatedBy) return false
        if (amount != other.amount) return false
        if (totalAmount != other.totalAmount) return false
        if (settleAmount != other.settleAmount) return false
        if (tipAmount != other.tipAmount) return false
        if (convenienceFeeAmount != other.convenienceFeeAmount) return false
        if (currency != other.currency) return false
        if (language != other.language) return false
        if (detailedStatus != other.detailedStatus) return false
        if (status != other.status) return false
        if (threeDSecureId != other.threeDSecureId) return false
        if (merchantId != other.merchantId) return false
        if (merchantPublicKey != other.merchantPublicKey) return false
        if (merchantReferenceId != other.merchantReferenceId) return false
        if (mcc != other.mcc) return false
        if (callbackUrl != other.callbackUrl) return false
        if (customerEmail != other.customerEmail) return false
        if (billingAddress != other.billingAddress) return false
        if (shippingAddress != other.shippingAddress) return false
        if (returnUrl != other.returnUrl) return false
        if (cardOnFile != other.cardOnFile) return false
        if (tokenId != other.tokenId) return false
        if (initiatedBy != other.initiatedBy) return false
        if (agreementId != other.agreementId) return false
        if (agreementType != other.agreementType) return false
        if (paymentOperation != other.paymentOperation) return false
        if (paymentMethod != other.paymentMethod) return false
        if (paymentMethods != other.paymentMethods) return false
        if (restrictPaymentMethods != other.restrictPaymentMethods) return false
        if (totalAuthorizedAmount != other.totalAuthorizedAmount) return false
        if (totalCapturedAmount != other.totalCapturedAmount) return false
        if (totalRefundedAmount != other.totalRefundedAmount) return false
        if (paymentIntent != other.paymentIntent) return false
        if (transactions != other.transactions) return false
        if (statementDescriptor != other.statementDescriptor) return false
        if (description != other.description) return false
        if (orderSource != other.orderSource) return false
        if (orderItems != other.orderItems) return false
        if (cashOnDelivery != other.cashOnDelivery) return false
        if (amountToCollect != other.amountToCollect) return false
        if (multiCurrency != other.multiCurrency) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                orderId,
                parentOrderId,
                createdDate,
                createdBy,
                updatedDate,
                updatedBy,
                amount,
                totalAmount,
                settleAmount,
                tipAmount,
                convenienceFeeAmount,
                currency,
                language,
                detailedStatus,
                status,
                threeDSecureId,
                merchantId,
                merchantPublicKey,
                merchantReferenceId,
                mcc,
                callbackUrl,
                customerEmail,
                billingAddress,
                shippingAddress,
                returnUrl,
                cardOnFile,
                tokenId,
                initiatedBy,
                agreementId,
                agreementType,
                paymentOperation,
                paymentMethod,
                paymentMethods,
                restrictPaymentMethods,
                totalAuthorizedAmount,
                totalCapturedAmount,
                totalRefundedAmount,
                paymentIntent,
                transactions,
                statementDescriptor,
                description,
                orderSource,
                orderItems,
                cashOnDelivery,
                amountToCollect,
                multiCurrency,
        )
    }

    // GENERATED
    override fun toString(): String {
        return """Order(
            orderId='$orderId',
            parentOrderId=$parentOrderId,
            createdDate=$createdDate, 
            createdBy=$createdBy, 
            updatedDate=$updatedDate, 
            updatedBy=$updatedBy, 
            amount=$amount,
            totalAmount=$totalAmount,
            settleAmount=$settleAmount,
            tipAmount=$tipAmount,
            convenienceFeeAmount=$convenienceFeeAmount,
            currency=$currency,
            language=$language,
            detailedStatus=$detailedStatus, 
            status=$status, 
            threeDSecureId=$threeDSecureId, 
            merchantId='$merchantId', 
            merchantPublicKey='$merchantPublicKey', 
            merchantReferenceId=$merchantReferenceId, 
            mcc=$mcc,
            callbackUrl=$callbackUrl, 
            customerEmail=$customerEmail, 
            billingAddress=$billingAddress, 
            shippingAddress=$shippingAddress, 
            returnUrl=$returnUrl, 
            cardOnFile=$cardOnFile, 
            tokenId=$tokenId,
            initiatedBy=$initiatedBy,
            agreementId=$agreementId,        
            agreementType=$agreementType,
            paymentOperation=$paymentOperation,
            paymentMethod=$paymentMethod,
            paymentMethods=$paymentMethods,
            restrictPaymentMethods=$restrictPaymentMethods,
            totalAuthorizedAmount=$totalAuthorizedAmount, 
            totalCapturedAmount=$totalCapturedAmount, 
            totalRefundedAmount=$totalRefundedAmount, 
            paymentIntent=$paymentIntent,
            transactions=$transactions,
            statementDescriptor=$statementDescriptor,
            description=$description,
            orderSource=$orderSource,
            orderItems=$orderItems,
            cashOnDelivery=$cashOnDelivery,
            amountToCollect=$amountToCollect,
            multiCurrency=$multiCurrency,
            )""".replace("\n", "").trimIndent()
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): Order = decodeFromJson(json)
    }
}