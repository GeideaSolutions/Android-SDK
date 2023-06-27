@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.bnpl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import java.math.BigDecimal

@Parcelize
@Serializable
class BnplDetails(
    val provider: String? = null,
    val transactionId: String? = null,
    val bnplOrderId: String? = null,
    val providerTransactionId: String? = null,
    val tenure: Int? = null,
    val currency: String? = null,
    val totalAmount: BigDecimal? = null,
    val financedAmount: BigDecimal? = null,
    val downPayment: BigDecimal? = null,
    val installmentAmount: BigDecimal? = null,
    val giftCardAmount: BigDecimal? = null,
    val campaignAmount: BigDecimal? = null,
    val adminFees: BigDecimal? = null,
    val interestTotalAmount: BigDecimal? = null,
    val firstInstallmentDate: String? = null,
    val lastInstallmentDate: String? = null,
    val providerResponseCode: String? = null,
    val providerResponseDescription: String? = null,
    val monthlyInterestRate: BigDecimal? = null,
    val otherFees: BigDecimal? = null,
    val amountToCollect: BigDecimal? = null,
    val token: String? = null,
    val annualInterestRate: BigDecimal? = null,
    val borrowerName: String? = null,
    val borrowerNationalId: String? = null,
    val borrowerAddress: String? = null,
    val applicationId: String? = null,
    val applicationCreated: String? = null,
    val orderCreated: String? = null,
    val createdDate: String? = null,
    val createdBy: String? = null,
    val updatedDate: String? = null,
    val updatedBy: String? = null
) : Parcelable