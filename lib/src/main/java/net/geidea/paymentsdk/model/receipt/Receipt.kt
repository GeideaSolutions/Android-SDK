@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.receipt

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.model.bnpl.BnplDetails
import net.geidea.paymentsdk.model.meezaqr.MeezaDetails
import net.geidea.paymentsdk.model.paymentintent.CustomerResponse
import net.geidea.paymentsdk.model.paymentintent.EInvoiceDetails
import net.geidea.paymentsdk.model.transaction.PaymentMethodInfo
import java.math.BigDecimal

@Parcelize
@Serializable
data class Receipt(
    val orderId: String,
    val paymentDate: String? = null,
    val amount: BigDecimal,
    val currency: String,
    val customerEmail: String? = null,
    val paymentIntentType: String? = null,
    val paymentOperation: String? = null,
    val paymentMethod: PaymentMethodInfo? = null,
    val merchant: Merchant? = null,
    val eInvoiceCustomer: CustomerResponse? = null,
    val eInvoice: EInvoiceDetails? = null,
    val bnplDetails: BnplDetails? = null,
    val meezaDetails: MeezaDetails? = null,
) : Parcelable